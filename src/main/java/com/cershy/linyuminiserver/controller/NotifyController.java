package com.cershy.linyuminiserver.controller;

import com.cershy.linyuminiserver.annotation.UrlLimit;
import com.cershy.linyuminiserver.entity.Notify;
import com.cershy.linyuminiserver.service.NotifyService;
import com.cershy.linyuminiserver.utils.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/notify")
public class NotifyController {

    @Resource
    NotifyService notifyService;

    @UrlLimit
    @GetMapping("/get")
    public Object getLatestNotify() {
        Notify result = notifyService.getLatestNotify();
        return ResultUtil.Succeed(result);
    }

}
