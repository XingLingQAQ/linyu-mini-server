package com.cershy.linyuminiserver.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cershy.linyuminiserver.constant.BadgeType;
import com.cershy.linyuminiserver.constant.NotifyType;
import com.cershy.linyuminiserver.constant.UserType;
import com.cershy.linyuminiserver.dto.NotifyDto;
import com.cershy.linyuminiserver.dto.UserDto;
import com.cershy.linyuminiserver.entity.User;
import com.cershy.linyuminiserver.exception.LinyuException;
import com.cershy.linyuminiserver.mapper.UserMapper;
import com.cershy.linyuminiserver.service.ChatListService;
import com.cershy.linyuminiserver.service.UserService;
import com.cershy.linyuminiserver.service.WebSocketService;
import com.cershy.linyuminiserver.utils.CacheUtil;
import com.cershy.linyuminiserver.vo.user.CreateUserVo;
import com.cershy.linyuminiserver.vo.user.UpdateUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    WebSocketService webSocketService;

    @Resource
    CacheUtil cacheUtil;

    @Resource
    ChatListService chatListService;

    @Override
    public boolean isExist(String name, String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, name)
                .or().eq(User::getEmail, email);
        return count(queryWrapper) > 0;
    }

    @Override
    public User getUserByNameOrEmail(String name, String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, name)
                .or().eq(User::getEmail, email);
        return getOne(queryWrapper);
    }

    @Override
    public User getUserByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, name);
        return getOne(queryWrapper);
    }

    @Override
    public User createUser(CreateUserVo createUserVo) {
        User user = new User();
        user.setId(IdUtil.simpleUUID());
        user.setName(createUserVo.getName());
        user.setEmail(createUserVo.getEmail());
        save(user);
        return user;
    }

    @Override
    public UserDto getUserById(String userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public List<UserDto> listUser() {
        return userMapper.listUser();
    }

    @Override
    public List<String> onlineWeb() {
        return webSocketService.getOnlineUser();
    }

    @Override
    public Map<String, UserDto> listMapUser() {
        return userMapper.listMapUser();
    }

    @Override
    public void online(String userId) {
        NotifyDto notifyDto = new NotifyDto();
        notifyDto.setTime(new Date());
        notifyDto.setType(NotifyType.Web_Online);
        notifyDto.setContent(JSONUtil.toJsonStr(getUserById(userId)));
        webSocketService.sendNotifyToGroup(notifyDto);
    }

    @Override
    public void offline(String userId) {
        NotifyDto notifyDto = new NotifyDto();
        notifyDto.setTime(new Date());
        notifyDto.setType(NotifyType.Web_Offline);
        notifyDto.setContent(JSONUtil.toJsonStr(getUserById(userId)));
        //离线更新，已读列表（防止用户直接关闭浏览器等情况）
        chatListService.read(userId, cacheUtil.getUserReadCache(userId));
        webSocketService.sendNotifyToGroup(notifyDto);
    }

    @Override
    public void deleteExpiredUsers(LocalDate expirationDate) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(User::getLoginTime, expirationDate);
        if (remove(queryWrapper)) {
            log.info("---清理过期用户成功---");
        }
    }

    @Override
    public void updateUserBadge(String id) {
        User user = getById(id);
        if (user == null) return;
        List<String> badges = user.getBadge();
        if (badges == null) {
            badges = new ArrayList<>();
        }
        boolean isUpdate = false;
        // 是否是第一个用户
        if (count() == 1) {
            if (!badges.contains(BadgeType.Crown)) {
                badges.add(BadgeType.Crown);
                isUpdate = true;
            }
        }
        // 根据用户创建时间发放徽章
        long diffInDays = DateUtil.between(user.getCreateTime(), new Date(), DateUnit.DAY);
        if (diffInDays >= 0 && diffInDays <= 7) {
            if (!badges.contains(BadgeType.Clover)) {
                badges.add(BadgeType.Clover);
                isUpdate = true;
            }
        } else if (diffInDays > 7) {
            if (badges.contains(BadgeType.Clover)) {
                badges.remove(BadgeType.Clover);
                isUpdate = true;
            }
            if (!badges.contains(BadgeType.Diamond)) {
                badges.add(BadgeType.Diamond);
                isUpdate = true;
            }
        }
        if (isUpdate) {
            user.setBadge(badges);
            updateById(user);
        }
    }

    @Override
    public void initBotUser() {
        //豆包机器人
        User doubao = getById("doubao");
        if (doubao == null) {
            User robot = new User();
            robot.setId("doubao");
            robot.setName("豆包");
            robot.setEmail(IdUtil.simpleUUID() + "@robot.com");
            robot.setType(UserType.Bot);
            save(robot);
        }
        //deepseek机器人
        User deepseek = getById("deepseek");
        if (deepseek == null) {
            User robot = new User();
            robot.setId("deepseek");
            robot.setName("DeepSeek");
            robot.setEmail(IdUtil.simpleUUID() + "@robot.com");
            robot.setType(UserType.Bot);
            save(robot);
        }
        //ragflow知识库（林语小助手）
        User ragflow = getById("ragflow");
        if (ragflow == null) {
            User robot = new User();
            robot.setId("ragflow");
            robot.setName("林语小助手");
            robot.setEmail(IdUtil.simpleUUID() + "@robot.com");
            robot.setType(UserType.Bot);
            save(robot);
        }
    }

    @Override
    public boolean updateUser(String userid, UpdateUserVo updateUserVo) {
        User user = getUserByName(updateUserVo.getName());
        if (user != null) {
            if (!user.getId().equals(userid))
                throw new LinyuException("用户名已被使用~");
        } else {
            user = getById(userid);
        }
        user.setName(updateUserVo.getName());
        user.setAvatar(updateUserVo.getAvatar());
        return updateById(user);
    }
}
