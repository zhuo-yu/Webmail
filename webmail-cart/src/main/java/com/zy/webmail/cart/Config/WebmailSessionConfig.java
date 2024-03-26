package com.zy.webmail.cart.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class WebmailSessionConfig {

    /**
     * 设置分布式cookie规则
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        //cookie作用域设置为公共域名,这样所有子域名才能共享cookie
        cookieSerializer.setDomainName("webmail.com");
        //修改cookie名称
        cookieSerializer.setCookieName("WEBMAILSESSION");
        return cookieSerializer;
    }

    /**
     * 将springSession存入redis的数据格式转换为json
     * @return
     */
    @Bean
    public RedisSerializer redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
