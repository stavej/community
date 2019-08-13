package com.jzy.community.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jzy
 * @create 2019-08-12-16:28
 */
@Configuration

public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionIntercetor sessionIntercetor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionIntercetor).addPathPatterns("/**");
    }
}
