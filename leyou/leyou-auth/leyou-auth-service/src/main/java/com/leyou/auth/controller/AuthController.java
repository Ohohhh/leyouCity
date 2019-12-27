package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("accredit")  // 用post请求安全，防止请求参数暴露在url上
    public ResponseEntity<Void> accredit(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = authService.accredit(username, password);
        if (StringUtils.isBlank(token)) {
            // 身份认证未通过 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 把返回的token设置到cookie中
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 校验用户信息
     * @param token
     * @param request
     * @param response
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN") String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            // 通过jwt工具类 公钥 解析出载荷 user
            UserInfo loginUser = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            if (loginUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // 刷新jwt中的过期时间  从新覆盖一个jwt
            token = JwtUtils.generateToken(loginUser,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
            // 把新token写入cookie
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
            return ResponseEntity.ok(loginUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
