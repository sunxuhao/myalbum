package com.sunxuhao.myalbum;

import com.sunxuhao.myalbum.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableCaching
//@ServletComponentScan
//@EnableElasticsearchRepositories(basePackages = "com.sunxuhao.myalbum.es")
//@EnableJpaRepositories(basePackages = {"com.sunxuhao.myalbum.dao", "com.sunxuhao.myalbum.pojo"})
public class MyalbumApplication {
    static {
        PortUtil.checkPort(6379, "Redis 服务端", true);
    }

    public static void main(String[] args) {
        SpringApplication.run(MyalbumApplication.class, args);
    }
}
