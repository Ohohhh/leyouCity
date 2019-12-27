package com.leyou.cart.interceptor;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器  登录过后转发到微服务之前 进行拦截 获取用户信息保存到本地变量 共享整个线程周期
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;
    // public static UserInfo userInfo;  存在线程安全问题 （多人共用一个钱包）
    // 定义一个线程本地局部域 存放userInfo 存在与整个线程周期  （每个人用自己的钱包）
    private static final ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 查询token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        // 根据token查询userInfo
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        if (userInfo == null){
            return false;
        }
        // 存放入线程本地域
        threadLocal.set(userInfo);
        return true;
    }
    // 对外提供了静态方法获取userInfo
    public static UserInfo getUserInfo() {
        return threadLocal.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 结束后手动清除 threadLocal 因为使用的是tomcat线程池  不会清除线程 只会回收线程
        threadLocal.remove();
    }
}
