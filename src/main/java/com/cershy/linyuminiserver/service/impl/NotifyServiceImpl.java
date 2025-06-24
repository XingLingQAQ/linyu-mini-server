package com.cershy.linyuminiserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cershy.linyuminiserver.entity.Notify;
import com.cershy.linyuminiserver.mapper.NotifyMapper;
import com.cershy.linyuminiserver.service.NotifyService;
import org.springframework.stereotype.Service;

@Service
public class NotifyServiceImpl extends ServiceImpl<NotifyMapper, Notify> implements NotifyService {

    @Override
    public Notify getLatestNotify() {
        LambdaQueryWrapper<Notify> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Notify::getCreateTime)
                .last("LIMIT 1");
        Notify latestNotify = getOne(queryWrapper);
        return latestNotify;
    }
}
