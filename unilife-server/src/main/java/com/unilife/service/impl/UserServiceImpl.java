package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.unilife.common.constant.RedisConstant;
import com.unilife.common.result.Result;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.model.dto.RegisterDTO;
import com.unilife.model.dto.UpdateEmailDTO;
import com.unilife.model.dto.UpdatePasswordDTO;
import com.unilife.model.dto.UpdateProfileDTO;
import com.unilife.model.entity.User;
import com.unilife.model.vo.LoginVO;
import com.unilife.model.vo.RegisterVO;
import com.unilife.service.IPLocationService;
import com.unilife.service.UserService;
import com.unilife.utils.JwtUtil;
import com.unilife.utils.RegexUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.unilife.common.constant.RedisConstant.LOGIN_EMAIL_KEY;

@Slf4j
@Component
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private IPLocationService ipLocationService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.mail.username}")
    private String from;

    final int CODE_EXPIRE_MINUTES = 10;
    final int LIMIT_SECONDS=60;


    @Override
    public Result register(RegisterDTO registerDTO, HttpServletRequest request) {
        String email=registerDTO.getEmail();
        String password=registerDTO.getPassword();
        String code=registerDTO.getCode();

        if (StringUtils.isAnyEmpty(email, password, code)) {
            return Result.error(400, "邮箱、密码和验证码不能为空");
        }
        if (password.length() < 6) {
            return Result.error(400, "密码长度至少为6位");
        }
        if (RegexUtils.isEmailInvalid(email)) { // 假设你有这个正则工具类
            return Result.error(400, "邮箱格式不正确");
        }

        String cacheCodeKey=RedisConstant.LOGIN_EMAIL_KEY + email;
        String cacheCode=stringRedisTemplate.opsForValue().get(cacheCodeKey);

        if(cacheCode==null){
            return Result.error(400,"验证码已过期或未发送，请重新获取");
        }
        if(!cacheCode.equals(code)){
            return Result.error(400,"验证码错误");
        }
        User getuser = userMapper.findByEmail(registerDTO.getEmail());
        if(getuser != null) {
            return Result.error(400,"该邮箱已被注册");
        }

        stringRedisTemplate.delete(cacheCodeKey);


        User user=new User();
        user.setEmail(email);
        user.setPassword(registerDTO.getPassword());
        // 设置昵称、用户名 (可以提供默认值)
        String nickname = StringUtils.isNotEmpty(registerDTO.getNickname()) ? registerDTO.getNickname() : "用户" + RandomUtil.randomString(6);
        user.setNickname(nickname);
        // 避免用户名太长，或者你可以让用户在 DTO 中提供
        String username = StringUtils.isNotEmpty(registerDTO.getUsername()) ? registerDTO.getUsername() : email.substring(0, Math.min(email.indexOf('@'), 10)) + "_" + RandomUtil.randomString(4);
        user.setUsername(username);

        // 设置其他默认值
        user.setRole((byte) 0);      // 普通用户
        user.setStatus((byte) 1);    // 启用状态
        user.setIsVerified((byte) 1); // 邮箱已通过验证码验证
        user.setPoints(0);
        user.setGender((byte) 0);    // 默认性别，或从 DTO 获取

        // 记录注册IP
        String currentIp = ipLocationService.getClientIP(request);
        String ipLocation = ipLocationService.getIPLocation(currentIp);
        user.setLoginIp(ipLocation);    // 首次登录IP设为注册IP
        user.setLoginTime(LocalDateTime.now()); // 记录首次登录时间
        // 插入数据库
        try {
            userMapper.insert(user); // user 对象现在应该有 id 了 (如果配置了主键返回)
            log.info("新用户注册成功: {}", user.getEmail());
        } catch (Exception e) {
            log.error("数据库插入用户失败: {}", email, e);
            return Result.error(500, "注册失败，服务器内部错误");
        }
        // 注册成功后直接登录：生成 Token 并返回 LoginVO
        LoginVO loginVO = new LoginVO();
        BeanUtil.copyProperties(user, loginVO); // 复制基本信息
        // 确保 user.getId() 能获取到刚插入的 ID
        if (user.getId() == null) {
            log.error("无法获取新注册用户的 ID: {}", email);
            return Result.error(500, "注册成功但登录失败，请稍后重试");
        }
        String token = jwtUtil.generateToken(user.getId());
        loginVO.setToken(token);
        loginVO.setId(user.getId());

        return Result.success(loginVO, "注册成功并已登录");
    }

    @Override
    public Result login(LoginDTO loginDTO,HttpServletRequest request) {
        if(loginDTO==null|| StringUtils.isEmpty(loginDTO.getEmail())||StringUtils.isEmpty(loginDTO.getPassword())){
            return Result.error(400,"邮箱或密码不能为空");
        }

        User user = userMapper.findByEmail(loginDTO.getEmail());
        if (user == null) {
            return Result.error(400, "账号或密码错误");
        }

        if (!loginDTO.getPassword().equals(user.getPassword())) {
            return Result.error(400, "账号或密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.error(403, "账号已被禁用，请联系管理员");
        }


        String LastLogIpLocation = user.getLoginIp();
        String currentIp = ipLocationService.getClientIP(request);
        String ipLocation = ipLocationService.getIPLocation(currentIp);
        user.setLoginIp(ipLocation);
        user.setLoginTime(LocalDateTime.now());
        userMapper.updateLoginInfo(user.getId(),ipLocation,new Date());

        LoginVO loginVO = new LoginVO();
        BeanUtil.copyProperties(user,loginVO);
        String message = StringUtils.isEmpty(LastLogIpLocation) ? "首次登录" : "上次登录IP归属地为" + LastLogIpLocation;
        return Result.success(loginVO, message);

    }

    @Override
    public Result sendVerificationCode(String email,HttpServletRequest request) {
        //1.校验邮箱是否合法
        boolean emailInvalid = RegexUtils.isEmailInvalid(email);
        if(emailInvalid){
            return Result.error(400,"邮箱格式不正确");
        }

        //2.防止频繁发送验证码
        String countKey = RedisConstant.LOGIN_EMAIL_LIMIT_KEY + email;
        Boolean setSuccess = stringRedisTemplate.opsForValue().setIfAbsent(
                countKey,
                "1",
                Duration.ofSeconds(LIMIT_SECONDS)
        );

        if (Boolean.FALSE.equals(setSuccess)) {
            return Result.error(null, "请求过于频繁，请稍后再试");
        }


        //3.生成随机验证码
        String code = RandomUtil.randomNumbers(6);
        log.debug("成功生成验证码,邮箱{},验证码{}", email, code);


        //4.发送验证码到邮箱
        try {
            //构建邮件
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("UniLife - 登录验证码");

            String IPAddress = ipLocationService.getClientIP(request);
            String Location = ipLocationService.getIPLocation(IPAddress);
            String content = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 5px;\">" +
                    "<h2 style=\"color: #333;\">您好！</h2>" +
                    "<p>感谢您使用UniLife平台,本次登录地为" + Location + "您的验证码是：</p>" +
                    "<div style=\"background-color: #f5f5f5; padding: 10px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; margin: 20px 0;\">" +
                    code +
                    "</div>" +
                    "<p>此验证码将在10分钟内有效。</p>" +
                    "<p>如果您没有请求此验证码，请忽略此邮件。</p>" +
                    "<p style=\"margin-top: 30px; font-size: 12px; color: #888;\">" +
                    "这是一封自动生成的邮件，请勿直接回复。" +
                    "</p></div>";

            helper.setText(content, true);

            //4.发送邮件
            mailSender.send(message);
        }catch (MessagingException e){
            log.error("邮件发送失败");
            return Result.error(400,"邮件发送失败");
        }


        //5.存储随机产生的验证码,设置有效期为十分钟
        stringRedisTemplate.opsForValue().set(LOGIN_EMAIL_KEY + email, code, Duration.ofMinutes(CODE_EXPIRE_MINUTES));

        return Result.success(200,"验证码已发送");
    }

    @Override
    public Result loginWithEmail(LoginEmailDTO loginEmailDTO,HttpServletRequest request) {
        String email=loginEmailDTO.getEmail();

        if(RegexUtils.isEmailInvalid(email)){
            return Result.error(400,"请输入正确的邮箱");
        }

        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_EMAIL_KEY + email);
        if (cacheCode == null) {
            return Result.error(400, "验证码已过期或未发送，请重新获取");
        }

        // 3. 校验验证码是否正确
        String code = loginEmailDTO.getCode();
        if (!cacheCode.equals(code)) {
            return Result.error(400, "验证码错误");
        }

        // 4. 验证通过，删除验证码
        stringRedisTemplate.delete(RedisConstant.LOGIN_EMAIL_KEY + email);

        // 5. 查询用户是否存在
        User user=userMapper.findByEmail(email);
        if(user == null){
            user = createUserWithEmail(email,request);
        }

        // 更新登录信息
        String currentIp = ipLocationService.getClientIP(request);
        String ipLocation = ipLocationService.getIPLocation(currentIp);
        user.setLoginIp(ipLocation);
        user.setLoginTime(LocalDateTime.now());
        userMapper.updateLoginInfo(user.getId(), ipLocation, new Date());

        // 创建LoginVO对象，与普通登录保持一致的返回格式
        LoginVO loginVO = new LoginVO();
        BeanUtil.copyProperties(user, loginVO);

        // 返回结果，不在这里生成token，由Controller统一处理
        String message = "邮箱验证码登录成功";
        return Result.success(loginVO, message);
    }

    /**
     * 使用邮箱信息创建新用户
     */
    private User createUserWithEmail(String email,HttpServletRequest request) {
        User user = new User();
        user.setEmail(email);
        user.setNickname("用户" + RandomUtil.randomString(6)); // 生成随机昵称
        String username = email.split("@")[0]+"_"+ RandomUtil.randomString(4); // 使用@前面的部分作为用户名
        user.setUsername(username);

        String password = RandomUtil.randomString(6);
        String IPAddress = ipLocationService.getClientIP(request);
        String Location = ipLocationService.getIPLocation(IPAddress);
        user.setPassword(password);

        user.setRole((byte)0);      // 普通用户角色
        user.setStatus((byte)1);    // 正常状态
        user.setIsVerified((byte)0); // 未验证
        user.setPoints(0);          // 初始积分
        user.setGender((byte)0);
        user.setLoginIp(Location);//注册地IP
        // 保存用户
        try {
            userMapper.insert(user);
        }catch (Exception e){
            log.error("用户创建失败");
        }

        return user;
    }

    @Override
    public Result getUserProfile(Long userId) {
        // 根据用户ID获取用户信息
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 出于安全考虑，不返回密码字段
        user.setPassword(null);

        return Result.success(user);
    }

    @Override
    public Result updateUserProfile(Long userId, UpdateProfileDTO profileDTO) {
        User currentUser = userMapper.getUserById(userId); // Changed from findById to getUserById based on UserMapper.java
        if (currentUser == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查用户名是否更改以及是否重复
        if (StringUtils.isNotEmpty(profileDTO.getUsername()) && !profileDTO.getUsername().equals(currentUser.getUsername())) {
            User existingUserWithNewUsername = userMapper.findByUsername(profileDTO.getUsername());
            if (existingUserWithNewUsername != null) {
                return Result.error(409, "用户名已被占用，请选择其他用户名"); // 409 Conflict
            }
            currentUser.setUsername(profileDTO.getUsername());
        }

        // 更新用户信息
        // 注意：这里应该只更新profileDTO中存在的字段，且要考虑空值情况
        // if (StringUtils.isNotEmpty(profileDTO.getNickname())) { // Commented out as nickname is removed from DTO
        //     user.setNickname(profileDTO.getNickname());
        // }
        if (StringUtils.isNotEmpty(profileDTO.getBio())) {
            currentUser.setBio(profileDTO.getBio()); // Changed user to currentUser
        }
        if (profileDTO.getGender() != null) {
            currentUser.setGender(profileDTO.getGender()); // Changed user to currentUser
        }
        if (StringUtils.isNotEmpty(profileDTO.getDepartment())) {
            currentUser.setDepartment(profileDTO.getDepartment()); // Changed user to currentUser
        }
        if (StringUtils.isNotEmpty(profileDTO.getMajor())) {
            currentUser.setMajor(profileDTO.getMajor()); // Changed user to currentUser
        }
        if (StringUtils.isNotEmpty(profileDTO.getGrade())) {
            currentUser.setGrade(profileDTO.getGrade()); // Changed user to currentUser
        }

        try {
            userMapper.updateUserProfile(currentUser); // Call void method
            log.info("用户 {} 的个人资料更新成功", userId);
            return Result.success("个人资料更新成功");
        } catch (Exception e) {
            log.error("用户 {} 的个人资料更新时发生数据库错误: {}", userId, e.getMessage());
            return Result.error(500, "个人资料更新失败，服务器内部错误");
        }
    }

    @Override
    public Result updatePassword(Long userId, UpdatePasswordDTO passwordDTO) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 验证验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_EMAIL_KEY + user.getEmail());
        if (cacheCode == null) {
            return Result.error(400, "验证码已过期或未发送，请重新获取");
        }

        if (!cacheCode.equals(passwordDTO.getCode())) {
            return Result.error(400, "验证码错误");
        }

        // 验证通过，删除验证码
        stringRedisTemplate.delete(RedisConstant.LOGIN_EMAIL_KEY + user.getEmail());

        // 更新密码
        userMapper.updatePassword(userId, passwordDTO.getNewPassword());

        return Result.success(null, "密码修改成功");
    }

    @Override
    public Result updateAvatar(Long userId, MultipartFile file) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "只能上传图片文件");
        }

        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = "avatar_" + userId + "_" + System.currentTimeMillis() + suffix;

            // TODO: 实际项目中应该将文件保存到云存储或服务器指定目录
            // 这里简化处理，假设保存成功并返回URL
            String avatarUrl = "https://example.com/avatars/" + filename;

            // 更新用户头像URL
            userMapper.updateAvatar(userId, avatarUrl);

            Map<String, String> data = new HashMap<>();
            data.put("avatar", avatarUrl);

            return Result.success(data, "头像上传成功");
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error(500, "头像上传失败");
        }
    }

    @Override
    public Result updateEmail(Long userId, UpdateEmailDTO emailDTO) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查邮箱格式
        String email = emailDTO.getEmail();
        if (RegexUtils.isEmailInvalid(email)) {
            return Result.error(400, "邮箱格式不正确");
        }

        // 检查邮箱是否已被使用
        User existingUser = userMapper.findByEmail(email);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return Result.error(400, "该邮箱已被其他用户使用");
        }

        // 验证验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_EMAIL_KEY + email);
        if (cacheCode == null) {
            return Result.error(400, "验证码已过期或未发送，请重新获取");
        }

        if (!cacheCode.equals(emailDTO.getCode())) {
            return Result.error(400, "验证码错误");
        }

        // 验证通过，删除验证码
        stringRedisTemplate.delete(RedisConstant.LOGIN_EMAIL_KEY + email);

        // 更新邮箱
        userMapper.updateEmail(userId, email);

        return Result.success(null, "邮箱更新成功");
    }
}
