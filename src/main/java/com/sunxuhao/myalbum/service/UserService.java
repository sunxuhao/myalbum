package com.sunxuhao.myalbum.service;

import com.sunxuhao.myalbum.dao.UserDAO;
import com.sunxuhao.myalbum.pojo.User;
import com.sunxuhao.myalbum.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "user")
public class UserService {
    @Autowired
    UserDAO userDAO;

    public boolean isExist(String name) {
        UserService userService = SpringContextUtil.getBean(UserService.class);
        User user = userService.getByName(name);
        return user != null;
    }

    @Cacheable(key = "'user-'+#p0")
    public User getByName(String name) {
        User user = userDAO.findByName(name);
        return user;
    }

    @CachePut(key = "'user-'+#p0.name")
    public void add(User user) {
        userDAO.save(user);
    }

    @Cacheable(key = "'user-'+#p0+'-'+#p1")
    public User get(String name, String password) {
        User user = userDAO.findByNameAndPassword(name, password);
        return user;
    }

}
