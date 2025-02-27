package com.cershy.linyuminiserver.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.cershy.linyuminiserver.constant.MessageSource;
import com.cershy.linyuminiserver.constant.MessageType;
import com.cershy.linyuminiserver.constant.TextContentType;
import com.cershy.linyuminiserver.dto.UserDto;
import com.cershy.linyuminiserver.vo.message.SendMessageVo;
import com.cershy.linyuminiserver.vo.message.TextMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class AiChatService {

    @Resource
    @Lazy
    UserService userService;

    @Resource
    @Lazy
    MessageService messageService;

    @Resource
    DoubaoAiService doubaoAiService;

    @Resource
    DeepSeekAiService deepSeekAiService;

    @Resource
    RagFlowAiService ragFlowAiService;

    @Async("taskExecutor")
    public void sendBotReply(String userId, String targetId, UserDto botUser, String content) {
        UserDto user = userService.getUserById(userId);
        // 创建消息
        // at内容
        TextMessageContent atUser = new TextMessageContent();
        atUser.setType(TextContentType.At);
        JSONConfig config = new JSONConfig().setIgnoreNullValue(true);
        atUser.setContent(JSONUtil.toJsonStr(user, config));
        // 文本消息内容
        String ask = "请稍后尝试~";
        switch (botUser.getId()) {
            case "doubao":
                ask = doubaoAiService.ask(userId, content);
                break;
            case "deepseek":
                ask = deepSeekAiService.ask(userId, content);
                break;
            case "ragflow":
                ask = ragFlowAiService.ask(userId, content);
                break;
        }
        TextMessageContent msgText = new TextMessageContent();
        msgText.setType(TextContentType.Text);
        msgText.setContent(ask);
        // 合并消息内容
        JSONArray msgContent = new JSONArray();
        msgContent.add(atUser);
        msgContent.add(msgText);
        // 发送消息
        SendMessageVo sendMessageVo = new SendMessageVo();
        sendMessageVo.setTargetId(targetId);
        sendMessageVo.setSource(MessageSource.Group);
        sendMessageVo.setMsgContent(msgContent.toJSONString(0));
        sendMessageVo.setUserIp("机器人");
        sendMessageVo.setType(MessageType.Text);
        messageService.sendMessageToGroup(botUser.getId(), sendMessageVo);
    }
}
