package com.unilife.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 测试配置类
 * 为测试环境提供特定的Bean配置
 */
@TestConfiguration
public class TestConfig {

    /**
     * 测试用的Redis连接工厂
     */
    @Bean
    @Primary
    public RedisConnectionFactory testRedisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory("localhost", 6379);
        factory.setDatabase(1); // 使用数据库1进行测试
        return factory;
    }

    /**
     * 测试用的RedisTemplate
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> testRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    /**
     * 测试用的StringRedisTemplate
     */
    @Bean
    @Primary
    public StringRedisTemplate testStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * 测试用的邮件发送器
     */
    @Bean
    @Primary
    public JavaMailSender testJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.example.com");
        mailSender.setPort(587);
        mailSender.setUsername("test@example.com");
        mailSender.setPassword("testpassword");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false"); // 测试时关闭debug

        return mailSender;
    }
} 