package com.leyou.cart.service;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "leyou:cart:uid:";
    /**
     * 添加购物车方法
     * @param cart
     */
    public void addCart(Cart cart) {
        // 获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        // 获取购物车记录
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        // 判断当前购物记录是否在购物车记录里
        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();
        Boolean hasKey = operations.hasKey(key);
        if (hasKey){
            // 在，修改数量
            String cartJson = operations.get(key).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);
        }else {
            // 不在，新添加记录
            cart.setUserId(userInfo.getId());
            Sku sku = goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? " " : StringUtils.split(sku.getImages(),",")[0]);
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
        }
        // 保存到redis
        operations.put(key, JsonUtils.serialize(cart));
    }

    /**
     * 登录情况下查找购物车记录
     * @return
     */
    public List<Cart> queryCarts() {
        // 获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        // 没有购物车记录就返回空
        if (!redisTemplate.hasKey(KEY_PREFIX+userInfo.getId())){
            return null;
        }
        // 购物车map
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        // 拿到购物车map记录中的cart值
        List<Object> cartsJson = operations.values();
        if (CollectionUtils.isEmpty(cartsJson)){
            return null;
        }
        // 集合转化并返回
        return cartsJson.stream().map(cartJson ->{
            return JsonUtils.parse(cartJson.toString(),Cart.class);
        }).collect(Collectors.toList());
    }

    /**
     * 修改购物车数据
     * @param cart
     */
    public void update(Cart cart) {
        // 拿到用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Integer num = cart.getNum();
        // 拿到购物车记录
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(userInfo.getId().toString());
        String  cartJson = operations.get(cart.getSkuId()).toString();
        cart = JsonUtils.parse(cartJson,Cart.class);
        // 修改
        cart.setNum(num);
        // 存入redis
        operations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    /**
     * 删除
     * @param skuId
     */
    public void delete(String skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(userInfo.getId().toString());
        operations.delete(skuId);
    }
}
