package com.fxiaoke.dataplatform.crm.cleantool.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

/**
 * 首页
 */
@Controller
public class HomeController {

    @Value("${casServerUrlPrefix}")
    private String casServerUrlPrefix = "/cas";

    @RequestMapping("/logout")
    public String logout(RedirectAttributes r) {
        SecurityUtils.getSubject().logout();
        r.addFlashAttribute("message", "您已经安全退出");
        String redirectUrl = casServerUrlPrefix.substring(0, casServerUrlPrefix.length() - 4);
        return "redirect:" + casServerUrlPrefix + "/logout?service=" + redirectUrl;
    }

    @RequestMapping("/unauthorized")
    public String unauthorized() {
        return "unauthorized";
    }

    @RequestMapping("/")
    public String home(Model model) {
        return "home";
    }

    @RequestMapping("/init")
    public String init(Model model) {
        return "init";
    }

    @RequestMapping("/username")
    @ResponseBody
    public String username() {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        return username;
    }

}
