package com.sunxuhao.myalbum.service;

import com.sunxuhao.myalbum.dao.PictureDAO;
import com.sunxuhao.myalbum.dao.PostDAO;
import com.sunxuhao.myalbum.pojo.Picture;
import com.sunxuhao.myalbum.pojo.Post;
import com.sunxuhao.myalbum.pojo.User;
import com.sunxuhao.myalbum.util.Page4Navigator;
import com.sunxuhao.myalbum.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "picture")
public class PictureService {
    public final static String status_delete = "delete";
    public final static String status_normal = "normal";

    @Autowired
    PostService postService;
    @Autowired
    PictureDAO pictureDAO;

    @Cacheable(key = "'pictures-page-'+#p0+'-'+#p1")
    public List<Picture> list(String status, int pid) {
        Post post = postService.get(pid);
        List<Picture> ps = pictureDAO.findByStatusAndPostOrderByIndexAsc(status, post);
        return ps;
    }

    @CacheEvict(allEntries = true)
    public void add(Picture bean) {
        pictureDAO.save(bean);
    }

    //    @Cacheable(key = "'picture-'+#p0")
    public Picture get(int id) {
        Picture p = pictureDAO.getOne(id);
        return p;
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        pictureDAO.delete(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Picture bean) {
        pictureDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void adjust(int id, int index) {
        Picture p = pictureDAO.getOne(id);
        p.setIndex(index);
        pictureDAO.save(p);
    }

    public void fillAll(Post post) {
        PictureService pictureService = SpringContextUtil.getBean(PictureService.class);
        List<Picture> beansNormal = pictureService.list(status_normal, post.getId());
        List<Picture> beansDelete = pictureService.list(status_delete, post.getId());
        List<Picture> beans = new ArrayList<>();
        beans.addAll(beansNormal);
        beans.addAll(beansDelete);

        post.setPictures(beans);
    }

    public void fill(Post post) {
//        PictureService pictureService = SpringContextUtil.getBean(PictureService.class);
//        List<Picture> beans = pictureService.list(status_normal, post.getId());
        List<Picture> beans = list(status_normal, post.getId());
        removePost(beans);
        post.setPictures(beans);
    }

    public void fillAll(List<Post> posts) {
        for (Post post : posts) {
            fill(post);
        }
    }

    public int getMaxIndex(String status) {
        return pictureDAO.findFirst1ByStatusOrderByIndexDesc(status).getIndex();
    }

    public Picture getByMd5(String md5) {
        return pictureDAO.findFirst1ByMd5(md5);
    }

    public void removePost(Picture p) {
        p.setPost(null);
    }

    public void removePost(List<Picture> ps) {
        for (Picture p : ps) {
            removePost(p);
        }
    }

    public List<Picture> search(User user, String postitle, String postext, String picname, Date d1, Date d2, int w1, int w2, int h1, int h2, long s1, long s2) {
        postitle = "%" + (postitle != null ? postitle : "") + "%";
        postext = "%" + (postext != null ? postext : "") + "%";
        picname = "%" + (picname != null ? picname : "") + "%";
        List<Picture> pictures = new ArrayList<>();
        for (Post post : postService.search(user, postitle, postext)) {
//            System.out.println(post);
            pictures.addAll(pictureDAO.findByStatusAndPostAndNameLikeAndCreateDateBetweenAndWidthBetweenAndHeightBetweenAndSizeBetweenOrderByIndexAsc(PictureService.status_normal, post, picname, d1, d2, w1, w2, h1, h2, s1, s2));
        }
//        for(Picture p:pictures){
//            System.out.println(p);
//        }
        return pictures;
    }

    public Picture getByPostsAndMd5(List<Post> posts, String md5) {
        for (Post p : posts) {
            Picture picture = pictureDAO.findFirst1ByPostAndMd5(p, md5);
            if (picture != null)
                return picture;
        }
        return null;
    }
}
