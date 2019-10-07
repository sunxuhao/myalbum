package com.sunxuhao.myalbum.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "picture")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer","post"})
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private int height;
    private int width;
    @Column(name = "index_")
    private int index;
    private long size;
    @Column(name = "createdate")
    private Date createDate;
    private String md5;
    private String name;
    private String status;

    @ManyToOne
    @JoinColumn(name = "pid")
    private Post post;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", height=" + height +
                ", width=" + width +
                ", index=" + index +
                ", size=" + size +
                ", createDate=" + createDate +
                ", md5='" + md5 + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", post=" + post +
                '}';
    }
}
