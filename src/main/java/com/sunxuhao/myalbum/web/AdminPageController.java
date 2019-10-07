package com.sunxuhao.myalbum.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {
    @GetMapping(value = "/admin")
    public String admin() {
        return "redirect:admin_post_list";
    }

    @GetMapping(value = "/")
    public String index() {
        return "redirect:homepage";
    }

    @GetMapping(value = "/admin_post_list")
    public String listPost() {
        return "admin/listPost";
    }

    @GetMapping(value = "/admin_post_deleted_list")
    public String listPostDeleted() {
        return "admin/listPostDeleted";
    }

    @GetMapping(value = "/admin_picture_list")
    public String listPicture() {
        return "admin/listPicture";
    }

    @GetMapping(value = "/admin_picture_search")
    public String searchPicture() {
        return "admin/searchPicture";
    }

    @GetMapping(value = "/admin_register")
    public String admin_register() {
        return "admin/register";
    }

    @GetMapping(value = "/admin_login")
    public String admin_login() {
        return "admin/login";
    }

    @GetMapping(value = "/admin_logout")
    public String admin_logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:admin_login";
    }

    @GetMapping(value = "/homepage")
    public String homepage() {
        return "fore/homepage";
    }
}
