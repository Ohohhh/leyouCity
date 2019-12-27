package com.leyou.client;

import com.leyou.item.API.SpeApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpeClient extends SpeApi {
}
