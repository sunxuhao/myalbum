package com.sunxuhao.myalbum.dao;

import com.sunxuhao.myalbum.pojo.Picture;
import com.sunxuhao.myalbum.pojo.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PictureDAO extends JpaRepository<Picture, Integer> {
    List<Picture> findByStatusAndPostOrderByIndexAsc(String status, Post post);

    Picture findFirst1ByStatusOrderByIndexDesc(String status);

    Picture findFirst1ByMd5(String md5);

    Picture findFirst1ByPostAndMd5(Post post, String md5);

    List<Picture> findByStatusAndPostAndNameLikeAndCreateDateBetweenAndWidthBetweenAndHeightBetweenAndSizeBetweenOrderByIndexAsc(String status, Post post, String name, Date d1, Date d2, int w1, int w2, int h1, int h2, long s1, long s2);
}
