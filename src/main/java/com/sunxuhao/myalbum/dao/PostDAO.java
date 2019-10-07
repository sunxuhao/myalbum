package com.sunxuhao.myalbum.dao;

import com.sunxuhao.myalbum.pojo.Post;
import com.sunxuhao.myalbum.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer> {
    Page<Post> findByStatusAndUserOrderByCreateDateDescIdDesc(String status, User user, Pageable pageable);

    List<Post> findByStatusAndUserAndTitleLikeAndTextLike(String status, User user, String title, String text);

    List<Post> findByUser(User user);
}
