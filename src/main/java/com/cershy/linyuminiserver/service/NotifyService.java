package com.cershy.linyuminiserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cershy.linyuminiserver.entity.Notify;

public interface NotifyService extends IService<Notify> {
    Notify getLatestNotify();
}
