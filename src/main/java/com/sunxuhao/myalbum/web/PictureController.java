package com.sunxuhao.myalbum.web;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sunxuhao.myalbum.pojo.Picture;
import com.sunxuhao.myalbum.pojo.Post;
import com.sunxuhao.myalbum.pojo.User;
import com.sunxuhao.myalbum.service.PictureService;
import com.sunxuhao.myalbum.service.PostService;
import com.sunxuhao.myalbum.util.ImageUtil;
import com.sunxuhao.myalbum.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@RestController
public class PictureController {
    @Autowired
    PictureService pictureService;
    @Autowired
    PostService postService;

    @GetMapping("/posts/{pid}/pictures")
    public List<Picture> list(@PathVariable("pid") int pid) {
        List<Picture> ps = pictureService.list(PictureService.status_normal, pid);
        return ps;
    }

    @GetMapping("/posts/{pid}/deleted/pictures")
    public List<Picture> listDeleted(@PathVariable("pid") int pid) {
        List<Picture> ps = pictureService.list(PictureService.status_delete, pid);
        return ps;
    }

    @PostMapping("/pictures")
    public synchronized Result add(@RequestParam("pid") int pid,
                                   @RequestParam("image") MultipartFile images[],
                                   HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        for (MultipartFile image : images) {
            Picture bean = new Picture();
            bean.setName("");
            bean.setStatus(PictureService.status_normal);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            bean.setCreateDate(calendar.getTime());
            Post post = postService.get(pid);
            bean.setPost(post);
            try {
                Picture p = saveOrUpdateImageFile(bean, image, request, user);
                if (p != null)
                    return Result.fail("图片重复，重复图片名称：" + p.getName() + "；" + "类别：" + p.getPost().getText());
            } catch (IOException e) {
                e.printStackTrace();
                return Result.fail("图片上传失败");
            }
        }
        return Result.success();
    }

    public Picture saveOrUpdateImageFile(Picture bean, MultipartFile image, HttpServletRequest request, User user)
            throws IOException {
        String md5 = SecureUtil.md5(image.getInputStream());

        File imageFolder = new File(request.getServletContext().getRealPath("uploaded/picture"));
        File file = new File(imageFolder, md5 + ".jpg");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        bean.setMd5(md5);

        Picture samePic = pictureService.getByMd5(md5);

        if (samePic == null) {
            BufferedImage img = ImageUtil.change2jpg(image.getBytes());
            //格式转换发生错误
            if (img == null) {
                throw new IOException();
            }
            ImageIO.write(img, "jpg", file);
            bean.setSize(file.length());
            bean.setWidth(img.getWidth());
            bean.setHeight(img.getHeight());

//            File middleFile = new File(file.getParentFile().getParentFile(), "middlePicture/" + file.getName());
//            int middleWidth = 500;
//            int middleHeight = (int) (((float) img.getHeight() * middleWidth) / img.getWidth());
//            ImageUtil.resizeImage(img, middleWidth, middleHeight, middleFile);

            File smallFile = new File(file.getParentFile().getParentFile(), "smallPicture/" + file.getName());
            int smallWidth = 100;
            int smallHeight = (int) (((float) img.getHeight() * smallWidth) / img.getWidth());
            ImageUtil.resizeImage(img, smallWidth, smallHeight, smallFile);
        } else {
            Picture p = pictureService.getByPostsAndMd5(postService.getByUser(user), md5);
            if (p != null)
                return p;
            bean.setSize(samePic.getSize());
            bean.setHeight(samePic.getHeight());
            bean.setWidth(samePic.getWidth());
        }

        pictureService.add(bean);
        if (bean.getName().length() < 1) {
            bean.setName(bean.getPost().getTitle() + "-" + bean.getId());
            pictureService.update(bean);
        }
        return null;
    }

    @PostMapping("/pictures/search")
    public List<Picture> search(String postitle, String postext, String picname, Date d1, Date d2, Integer w1, Integer w2, Integer h1, Integer h2, Long s1, Long s2, HttpSession session) {
//        if(w1==null)w1=0;
//        if(w2==null)w2=10000;
//        if(h1==null)h1=0;
//        if(h2==null)h2=10000;
//        if(s1==null)s1=0L;
//        if(s2==null)s2=100000000L;
        User user = (User) session.getAttribute("user");
        List<Picture> pictures = pictureService.search(user, postitle, postext, picname, d1, d2, w1, w2, h1, h2, s1, s2);
        return pictures;
    }

    @DeleteMapping("/pictures/{id}")
    public String delete(@PathVariable("id") int id) {
        Picture picture = pictureService.get(id);
        picture.setStatus(PictureService.status_delete);
        pictureService.update(picture);
        return null;
    }

    @DeleteMapping("/pictures/delete/{id}")
    public String m_delete(@PathVariable("id") int id) {
        pictureService.delete(id);
        return null;
    }

    @GetMapping("/pictures/{id}")
    public Picture get(@PathVariable("id") int id) {
        Picture bean = pictureService.get(id);
        return bean;
    }

    @PutMapping("/pictures/{id}")
    public Object update(@PathVariable("id") int id, MultipartFile image, HttpServletRequest request) {
        Picture bean = pictureService.get(id);
        String name = request.getParameter("name");
        bean.setName(name);
        pictureService.update(bean);
        return null;
    }

    @PostMapping("pictures/adjustIndex")
    public Object adjust(@RequestParam("indexJsons") String indexJsons) {
        JSONArray a = JSONUtil.parseArray(indexJsons);
        for (Object object : a) {
            JSONObject o = (JSONObject) object;
            int id = o.getInt("id");
            int index = id;
            if (o.containsKey("index"))
                index = o.getInt("index");
            pictureService.adjust(id, index);
        }

        return "success";

    }

    @GetMapping("/pictures/recover/{id}")
    public Object recover(@PathVariable("id") int id) {
        Picture bean = pictureService.get(id);
        bean.setStatus(PictureService.status_normal);
        bean.setIndex(pictureService.getMaxIndex(PictureService.status_normal) + 1);
        pictureService.update(bean);
        return null;
    }

    public static void sop(Object obj) {
        System.out.println(obj);
    }
}
