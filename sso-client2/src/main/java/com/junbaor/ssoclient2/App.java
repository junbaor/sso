package com.junbaor.ssoclient2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SpringBootApplication
public class App extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
                HttpSession session = httpServletRequest.getSession();

                Object loginUser = session.getAttribute("CLIENT_LOGIN_USER");
                if (loginUser == null) {
                    httpServletResponse.sendRedirect("http://sso.com:8080/login?systemName=user&callbackUrl=" + httpServletRequest.getRequestURI());
                    return false;
                }

                return true;
            }

            @Override
            public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

            }

            @Override
            public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

            }
        }).addPathPatterns("/**").excludePathPatterns("/login").excludePathPatterns("/error");
        super.addInterceptors(registry);
    }
}
