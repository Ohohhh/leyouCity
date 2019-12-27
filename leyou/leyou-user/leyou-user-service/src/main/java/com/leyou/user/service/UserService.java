package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    // key前缀 以区分不同类型
    private static final String KEY_PREFIX = "user:code";

    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return userMapper.selectCount(user) == 0;
    }

    /**
     * 发送验证码
     * @param phone
     */
    public void sendVerifyCode(String phone) {
        if (StringUtils.isEmpty(phone)){
            return;
        }
        // 生成随机验证码
        String code = NumberUtils.generateCode(6);
        // 发送消息到mq
        Map<String, String > msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","sms.verify.code",msg);
        // 保存验证码到redis 以验证
        redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
    }

    /**
     * 注册用户
     * @param user
     * @param code
     */
    public Boolean register(User user, String code) {
        // 拿到redis中的验证码
        String redisCode = redisTemplate.opsForValue().get(KEY_PREFIX+user.getPhone());
        // 1. 校验验证码
        if (!StringUtils.equals(code,redisCode)){
            return false;
        }
        // 2. 生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        // 3. 加盐加密
        String password = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(password);
        // 设置没指定的参数为null
        user.setId(null);
        user.setCreated(new Date());
        // 4. 添加用户
        boolean b = userMapper.insertSelective(user) == 1;
        if (b){
            // 如果注册成功 删除redis中的记录
            redisTemplate.delete(KEY_PREFIX+user.getPhone());
        }
        return b;
    }

    /**
     * 验证登录
     * @param username
     * @param password
     * @return
     */
    public User login(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        // 判断是否为空
        if (user == null){
            return null;
        }
        //校验密码
        if(!user.getPassword().equals(CodecUtils.md5Hex(password,user.getSalt()))){
            return null;
        }

        return user;
    }
}
