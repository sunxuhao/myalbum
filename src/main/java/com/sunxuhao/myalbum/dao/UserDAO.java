package com.sunxuhao.myalbum.dao;

import com.sunxuhao.myalbum.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findByName(String name);

    User findByNameAndPassword(String name, String password);
}
