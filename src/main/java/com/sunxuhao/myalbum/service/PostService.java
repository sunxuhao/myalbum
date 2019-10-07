package com.sunxuhao.myalbum.service;

import com.sunxuhao.myalbum.dao.PostDAO;
import com.sunxuhao.myalbum.pojo.Post;
import com.sunxuhao.myalbum.pojo.User;
import com.sunxuhao.myalbum.util.Page4Navigator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "post")
public class PostService {
    public final static String status_delete = "delete";
    public final static String status_normal = "normal";

    @Autowired
    private PostDAO postDAO;

    @Cacheable(key = "'posts-page-'+#p0+'-'+#p1.id+'-'+#p2")
    public Page4Navigator<Post> list(String status, User user, int start, int size, int navigatePages) {
        Pageable pageable = new PageRequest(start, size);
        Page<Post> pageFromJpa = postDAO.findByStatusAndUserOrderByCreateDateDescIdDesc(status, user, pageable);

        return new Page4Navigator<>(pageFromJpa, navigatePages);
    }

    @CacheEvict(allEntries = true)
    public void add(Post bean) {
        postDAO.save(bean);
    }

    //    @Cacheable(key = "'post-'+#p0")
    public Post get(int id) {
        Post p = postDAO.getOne(id);
        p.setPictures(null);
        return p;
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        postDAO.delete(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Post bean) {
        postDAO.save(bean);
    }

    public List<Post> search(User user, String title, String text) {
        return postDAO.findByStatusAndUserAndTitleLikeAndTextLike(PostService.status_normal, user, title, text);
    }

    public List<Post> getByUser(User user) {
        return postDAO.findByUser(user);
    }

}
