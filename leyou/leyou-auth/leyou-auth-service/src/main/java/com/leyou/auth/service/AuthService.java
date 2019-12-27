package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String accredit(String username,String password) {
        // 查找user
        User loginUser = userClient.login(username, password);
        System.out.println(loginUser);
        // 判断user
        if (loginUser == null){
            return null;
        }
        try {
            // 根据user生成jwt
            // new 个载荷
            UserInfo userInfo = new UserInfo();
            userInfo.setId(loginUser.getId());
            userInfo.setUsername(loginUser.getUsername());
            return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
