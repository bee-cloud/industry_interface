package com.fxiaoke.dataplatform.crm.cleantool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页
 */
@Controller
public class HomeController {
    @RequestMapping("/")
    public String home(Model model) {
        return "home";
    }

}
