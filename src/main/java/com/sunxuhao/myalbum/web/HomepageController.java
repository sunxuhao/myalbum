package com.sunxuhao.myalbum.web;

import java.util.Date;
import java.util.List;

import com.sunxuhao.myalbum.pojo.Post;
import com.sunxuhao.myalbum.pojo.User;
import com.sunxuhao.myalbum.service.PictureService;
import com.sunxuhao.myalbum.service.PostService;
import com.sunxuhao.myalbum.service.UserService;
import com.sunxuhao.myalbum.util.Page4Navigator;
import com.sunxuhao.myalbum.util.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;

@RestController
public class HomepageController {
    @Autowired
    PostService postService;
    @Autowired
    PictureService pictureService;
    @Autowired
    UserService userService;

    @GetMapping("/homepageData")
    public Page4Navigator<Post> homepageData(@RequestParam(value = "start", defaultValue = "0") int start,
                                             @RequestParam(value = "size", defaultValue = "5") int size, HttpSession session) throws Exception {
        start = start < 0 ? 0 : start;
        User user = (User) session.getAttribute("user");
        Page4Navigator<Post> page = postService.list(PostService.status_normal, user, start, size, 5);
        List<Post> posts = page.getContent();
        for (Post post : posts) {
            pictureService.fill(post);
        }
        return page;
    }

    @GetMapping("/username")
    public String getUsername(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user.getName();
    }

    @PutMapping("/login")
    public Result login(@RequestBody User user, HttpSession session) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        return tryLogin(user, session);
    }

    @PutMapping("/register")
    public Object register(@RequestBody User user, HttpSession session) {
        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);
        if (exist) {
            String message = "用户名已被使用";
            return Result.fail(message);
        }

        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";
        String encodePassword = new SimpleHash(algorithmName, password, salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodePassword);
        userService.add(user);
        insertDefaultPost(user);
        session.setAttribute("user", user);
        return tryLogin(new User(name, password), session);
    }

    private Result tryLogin(User user, HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());
        try {
            subject.login(token);
            user = userService.getByName(user.getName());
            user.setPassword("");
            user.setSalt("");
            session.setAttribute("user", user);
            return Result.success();
        } catch (AuthenticationException e) {
            String message = "账号密码错误";
            return Result.fail(message);
        }
    }

    private void insertDefaultPost(User user) {
        final String[] titles = {"人物", "风景", "山水", "花鸟"};
        for (String title : titles) {
            Post post = new Post();
            post.setUser(user);
            post.setStatus(PostService.status_normal);
            post.setCreateDate(new Date());
            post.setTitle(title);
            post.setText("系统默认类别");
            postService.add(post);
        }
    }
}
