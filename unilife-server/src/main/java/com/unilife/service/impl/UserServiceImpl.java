package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.unilife.common.constant.RedisConstant;
import com.unilife.common.result.Result;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.model.dto.RegisterDTO;
import com.unilife.model.entity.User;
import com.unilife.model.vo.LoginVO;
import com.unilife.model.vo.RegisterVO;
import com.unilife.service.UserService;
import com.unilife.utils.JwtUtil;
import com.unilife.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.unilife.common.constant.RedisConstant.LOGIN_EMAIL_KEY;

@Slf4j
@Component
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String from;

    final int CODE_EXPIRE_MINUTES = 10;
    final int LIMIT_SECONDS=60;
    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Result register(RegisterDTO registerDTO) {
        if(registerDTO.getEmail().isEmpty() || registerDTO.getPassword().isEmpty()) {
            return Result.error(400,"邮箱或密码不能为空");
        }
        User user = new User();
        BeanUtil.copyProperties(registerDTO,user);
        userMapper.insert(user);
        RegisterVO registerVO = new RegisterVO(user.getId(),user.getUsername(),user.getNickname());
        return Result.success(registerVO);
    }

    @Override
    public Result login(LoginDTO loginDTO) {
        User user = new User();
        BeanUtil.copyProperties(loginDTO,user);//将登录的前端传来的消息拷贝给这个user
        User getuser = userMapper.FindByEmail(user.getEmail(),user.getPassword());
        if(getuser == null)
        {
            return Result.error(loginDTO,"用户不存在，登录失败!");
        }
        if(!user.getPassword().equals(getuser.getPassword()))
        {
            return Result.error(loginDTO,"密码错误，登录失败!");
        }
        LoginVO loginVO=new LoginVO();
        BeanUtil.copyProperties(getuser,loginVO);
        return Result.success(loginVO);
    }

    @Override
    public Result sendVerificationCode(String email) {
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

            String content = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 5px;\">" +
                    "<h2 style=\"color: #333;\">您好！</h2>" +
                    "<p>感谢您使用UniLife平台。您的验证码是：</p>" +
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
    public Result loginWithEmail(LoginEmailDTO loginEmailDTO) {
        String email=loginEmailDTO.getEmail();

        if(RegexUtils.isEmailInvalid(email)){
            return Result.error(null,"请输入正确的邮箱");
        }

        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_EMAIL_KEY + email);
        if (cacheCode == null) {
            return Result.error(null, "验证码已过期或未发送，请重新获取");
        }

        // 3. 校验验证码是否正确
        String code = loginEmailDTO.getCode();
        if (!cacheCode.equals(code)) {
            return Result.error(null, "验证码错误");
        }

        // 4. 验证通过，删除验证码
        stringRedisTemplate.delete(RedisConstant.LOGIN_EMAIL_KEY + email);

        // 5. 查询用户是否存在
        User user=userMapper.getUserByEmail(email);
        if(user == null){
            user=createUserWithEmail(email);
        }

        //6.生成登录凭证
        String token=jwtUtil.generateToken(user.getId());

        // 8. 返回用户信息和登录凭证
        Map<String, Object> userInfo = new HashMap<>();
        //HashMap userInfo.put("token", token);
        userInfo.put("user", user);
        userInfo.put("token", token);

        return Result.success(userInfo);
    }

    /**
     * 使用邮箱信息创建新用户
     */
    private User createUserWithEmail(String email) {
        User user = new User();
        user.setEmail(email);
        user.setNickname("用户" + RandomUtil.randomString(6)); // 生成随机昵称
        String username = email.split("@")[0]+"_"+ RandomUtil.randomString(4); // 使用@前面的部分作为用户名
        user.setUsername(username);

        String password=RandomUtil.randomString(6);
        user.setPassword(password);

        user.setRole((byte)0);      // 普通用户角色
        user.setStatus((byte)1);    // 正常状态
        user.setIsVerified((byte)0); // 未验证
        user.setPoints(0);          // 初始积分
        user.setGender((byte)0);

        // 保存用户
        try {
            userMapper.insert(user);
        }catch (Exception e){
            log.error("用户创建失败");
        }

        return user;
    }

}
