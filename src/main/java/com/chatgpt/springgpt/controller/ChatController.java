package com.chatgpt.springgpt.controller;

import com.chatgpt.springgpt.service.ChatInfo;
import org.openqa.selenium.WebDriver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * ChatGPT控制类
 *
 * @author asd28
 */
@RequestMapping("/api/v1/chat")
@RestController
public class ChatController {
    @Resource
    ChatInfo chatInfo;
    WebDriver webDriver;

    /**
     * 初始化Chat
     * @return 初始化成功后的连接信息
     */
    @GetMapping
    public String initChat() {
        webDriver = chatInfo.initChat();
        return "已连接" + webDriver.getTitle() + "\t" + webDriver.getCurrentUrl();
    }

    /**
     * 向Chat发送获取到的text接口信息并返回Chat回答的数据
     * @param text 向Chat提问的问题
     * @return Chat回答后的数据
     */
    @RequestMapping("send")
    public String chatLastReply(@RequestParam("text") String text) {
        chatInfo.sendKeys(webDriver, text);
        return chatInfo.chatLastReply(webDriver);
    }
}
