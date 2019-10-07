package com.sunxuhao.myalbum.web;

import cn.hutool.core.date.DateUtil;
import com.sunxuhao.myalbum.pojo.Post;
import com.sunxuhao.myalbum.pojo.User;
import com.sunxuhao.myalbum.service.PictureService;
import com.sunxuhao.myalbum.service.PostService;
import com.sunxuhao.myalbum.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    PictureService pictureService;

    @RequestMapping("/posts")
    public Page4Navigator<Post> list(@RequestParam(value = "start", defaultValue = "0") int start,
                                     @RequestParam(value = "size", defaultValue = "5") int size, HttpSession session) {
        start = start < 0 ? 0 : start;
        User user = (User) session.getAttribute("user");
        Page4Navigator<Post> page = postService.list(PostService.status_normal, user, start, size, 5);
        List<Post> ps = page.getContent();
        pictureService.fillAll(ps);
        return page;
    }

    @GetMapping("/posts/deleted")
    public Page4Navigator<Post> listDeleted(@RequestParam(value = "start", defaultValue = "0") int start,
                                            @RequestParam(value = "size", defaultValue = "5") int size, HttpSession session) {
        start = start < 0 ? 0 : start;
        User user = (User) session.getAttribute("user");
        Page4Navigator<Post> page = postService.list(PostService.status_delete, user, start, size, 5);

        List<Post> ps = page.getContent();
        pictureService.fillAll(ps);
        return page;
    }

    @PostMapping("/posts")
    public Object add(Post bean, HttpSession session) {
        if (bean.getCreateDate().getTime() > System.currentTimeMillis())
            return null;
        User user = (User) session.getAttribute("user");
        bean.setStatus(PostService.status_normal);
        bean.setUser(user);
        postService.add(bean);
        return bean;
    }


    @DeleteMapping("/posts/{id}")
    public String delete(@PathVariable("id") int id) {
        Post post = postService.get(id);
        post.setStatus(PostService.status_delete);
        postService.update(post);
        return null;
    }

    @DeleteMapping("/posts/delete/{id}")
    public String m_delete(@PathVariable("id") int id) {
        postService.delete(id);
        return null;
    }

    @GetMapping("/posts/recover/{id}")
    public String recover(@PathVariable("id") int id) {
        Post post = postService.get(id);
        post.setStatus(PostService.status_normal);
        postService.update(post);
        return null;
    }

    @GetMapping("/posts/{id}")
    public Post get(@PathVariable("id") int id) throws Exception {
        Post bean = postService.get(id);
        return bean;
    }

    @PutMapping("/posts/{id}")
    public Object update(@PathVariable("id") int id, MultipartFile image, HttpServletRequest request) throws Exception {

        String title = request.getParameter("title");
        String text = request.getParameter("text");
        String createDate = request.getParameter("createDate");
        Post post = postService.get(id);
        post.setTitle(title);
        post.setText(text);
        post.setCreateDate(DateUtil.parse(createDate));
        postService.update(post);
        return post;
    }
}
