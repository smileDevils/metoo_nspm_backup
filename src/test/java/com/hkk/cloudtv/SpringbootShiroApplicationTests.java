package com.hkk.cloudtv;

import com.cloud.tv.core.service.IResService;
import com.cloud.tv.core.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;


@SpringBootTest
class SpringbootShiroApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IUserService userService;
    @Autowired
    private IResService resService;

    @Test
    void contextLoads() {

    }

    /**
     * 1，测试Redis缓存 Get、Set; 高并发存在问题
     * 双重检测（锁）
     */
    /*@Test
    public void redis(){
        // 字符串序列化器
        RedisSerializer keySerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(keySerializer);
        // 1. 查询缓存
        User user = (User) redisTemplate.opsForValue().get("user");
        if(user == null){
            // 2. 缓存为空查询数据库
            user = this.userService.findObjById(Long.parseLong("12"));
            // 3. 写入缓存
            redisTemplate.opsForValue().set("user", user);
        }
    }*/

//    @ApiOperation("高并发:缓存穿透")
//    @Test
//    public void concurrent() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                // 字符串序列化器
//                RedisSerializer keySerializer = new StringRedisSerializer();
//                redisTemplate.setKeySerializer(keySerializer);
//                // 1. 查询缓存
//                User user = (User) redisTemplate.opsForValue().get("user");
//                if (user == null) {
//                    System.out.println("查询数据库");
//                  //synchronized (this){
//                      // 2. 缓存为空查询数据库
//                      user = userService.findObjById(Long.parseLong("12"));
//                      // 3. 写入缓存
//                      redisTemplate.opsForValue().set("user", user);
//                 // }
//                }
//            }
//        };
//        // 多线程测试
//        ExecutorService executorService = Executors.newFixedThreadPool(25);
//        for (int i = 0; i < 10000; i++) {
//            executorService.submit(runnable);
//        }
//    }

//    @ApiOperation("高并发:缓存穿透")
//    @Test
//    public void penetrate() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                // 字符串序列化器
//                RedisSerializer keySerializer = new StringRedisSerializer();
//                redisTemplate.setKeySerializer(keySerializer);
//                // 1. 查询缓存
//                User user = (User) redisTemplate.opsForValue().get("user");
//                if (user == null) {
//                    System.out.println("查询数据库");
//                    synchronized (this){
//                        user = (User) redisTemplate.opsForValue().get("user");
//                        if(user == null){
//                            // 2. 缓存为空查询数据库
//                            user = userService.findObjById(Long.parseLong("12"));
//                            // 3. 写入缓存
//                            redisTemplate.opsForValue().set("user", user);
//                        }
//                    }
//                }
//            }
//        };
//        // 多线程测试
//        ExecutorService executorService = Executors.newFixedThreadPool(25);
//        for (int i = 0; i < 10000; i++) {
//            executorService.submit(runnable);
//        }
//    }


}
