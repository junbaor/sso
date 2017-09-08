package com.junbaor.sso.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private Map<String, Object> currentLoginUser = new ConcurrentHashMap<>(); //最好设置过期时间, 放置到 redis 中

    private final String CURRENT_LOGIN_USER_TOKEN = "CURRENT_LOGIN_USER_TOKEN";
    private final String CALLBACK_URL = "callbackUrl";
    private final String SYSTEM_NAME = "systemName";


    @GetMapping("/login")
    public String login(String systemName, String callbackUrl, Model model, HttpSession session) {
        log.info("请求登录地址 systemName: {}  callbackUrl: {}", systemName, callbackUrl);

        Object token = session.getAttribute(CURRENT_LOGIN_USER_TOKEN);

        model.addAttribute(CALLBACK_URL, callbackUrl);
        model.addAttribute(SYSTEM_NAME, systemName);

        if (token == null) {
            return "index";
        } else {
            if (systemName.equals("adm")) {
                return "redirect:http://adm.com:8081/login?callbackUrl=" + callbackUrl + "&token=" + String.valueOf(token);
            } else {
                return "redirect:http://user.com:8083/login?callbackUrl=" + callbackUrl + "&token=" + String.valueOf(token);
            }
        }
    }


    @PostMapping("/login")
    public String doLogin(String callbackUrl, String systemName, String name, String pass, HttpSession session) {
        log.info("请求登录 systemName: {}  callbackUrl: {}  name: {} pass: {}", systemName, callbackUrl, name, pass);

        if ("admin".equals(name) && "pass".equals(pass)) {
            String token = UUID.randomUUID().toString();
            session.setAttribute(CURRENT_LOGIN_USER_TOKEN, token);
            currentLoginUser.put(token, new LoginUserInfo(name, pass));

            if (systemName.equals("adm")) {
                return "redirect:http://adm.com:8081/login?token=" + token + "&callbackUrl=" + callbackUrl;
            } else {
                return "redirect:http://user.com:8083/login?token=" + token + "&callbackUrl=" + callbackUrl;
            }
        } else {
            return "redirect:/login?systemName=" + systemName + "&callbackUrl=" + callbackUrl;
        }
    }


    @PostMapping("/logout")
    public void logout(String systemName, HttpSession session) {
        log.info("注销请求 systemName:{} ", systemName);
        session.removeAttribute(CURRENT_LOGIN_USER_TOKEN);
    }


    @PostMapping("/valid")
    @ResponseBody
    public Object valid(String systemName, String token) {
        log.info("验证请求systemName :{}  token: {}", systemName, token);
        Object loginUser = currentLoginUser.get(token);
        log.info("当前登录用户信息:{}", loginUser);
        return loginUser;
    }


    class LoginUserInfo {
        public LoginUserInfo(String name, String pass) {
            this.name = name;
            this.pass = pass;
        }

        public LoginUserInfo() {
        }

        private String name;
        private String pass;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("LoginUserInfo{");
            sb.append("name='").append(name).append('\'');
            sb.append(", pass='").append(pass).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

}
