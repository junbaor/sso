package com.junbaor.ssoclient2;

import com.alibaba.fastjson.JSONObject;
import net.dongliu.requests.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/login")
    public String login(String token, String callbackUrl, HttpSession httpSession) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("systemName", "user");
        String readToText = Requests.post("http://sso.com:8080/valid").params(map).send().readToText();
        log.info("sso server response:{}", readToText);

        JSONObject jsonObject = JSONObject.parseObject(readToText);

        String name = jsonObject.getString("name");
        httpSession.setAttribute("CLIENT_LOGIN_USER", name);
        return "redirect:" + callbackUrl;
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("CLIENT_LOGIN_USER");
        return "redirect:" + "http://sso.com:8080/logout";
    }

    @GetMapping("/home")
    @ResponseBody
    public Object login(HttpSession session) {
        return "当前登录用户：" + session.getAttribute("CLIENT_LOGIN_USER");
    }
}
