package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.unilife.common.result.Result;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.LogDTO;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.entity.User;
import com.unilife.model.vo.LogVO;
import com.unilife.model.vo.LoginVO;
import com.unilife.service.UserService;
import com.unilife.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;



    @Override
    public Result register(LoginDTO loginDTO) {
        if(loginDTO.getEmail().equals("") || loginDTO.getPassword().equals("")) {
            return Result.error(400,"邮箱或密码不能为空");
        }
        User user = new User();
        BeanUtil.copyProperties(loginDTO,user);
        userMapper.insert(user);
        LoginVO loginVO = new LoginVO(Math.toIntExact(user.getId()),user.getUsername(),user.getNickname());
        return Result.success(loginVO);
    }

    @Override
    public Result login(LogDTO logDTO) {
        User user = new User();
        BeanUtil.copyProperties(logDTO,user);//将登录的前端传来的消息拷贝给这个user
        User getuser = userMapper.FindByEmail(user.getEmail(),user.getPassword());
        if(getuser == null)
        {
            return Result.error(logDTO,"用户不存在，登录失败!");
        }
        if(!user.getPassword().equals(getuser.getPassword()))
        {
            return Result.error(logDTO,"密码错误，登录失败!");
        }
        LogVO logVO = new LogVO(Math.toIntExact(getuser.getId()), getuser.getUsername(), getuser.getNickname(),
                getuser.getAvatar(), getuser.getRole(), getuser.getIsVerified(), getuser.getStatus());
        return Result.success(logVO);
    }

    @Override
    public Result sendVerificationCode(String email) {
        //1.校验邮箱是否合法
        boolean emailInvalid = RegexUtils.isEmailInvalid(email);
        if(emailInvalid){
            return Result.error(400,"邮箱格式不正确");
        }

        //2.生成随机验证码
        String code = RandomUtil.randomNumbers(6);
        log.debug("成功生成验证码,邮箱{},验证码{}", email, code);



        //3.发送验证码到邮箱

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


        //4.存储随机产生的验证码
        //TODO


        return Result.success(200,"验证码已发送");


    }
}
