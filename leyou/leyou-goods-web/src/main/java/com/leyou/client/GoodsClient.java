package com.leyou.client;

import com.leyou.item.API.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("item-service")  //用于服务发现
public interface GoodsClient extends GoodsApi {
}
