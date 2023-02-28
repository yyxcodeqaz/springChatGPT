package com.chatgpt.springgpt.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * 获取ChatGPT信息的接口
 *
 * @author leaf
 */
public interface ChatInfo {
    /**
     * 初始化Chat
     *
     * @return web驱动
     */
    WebDriver initChat();

    /**
     * 向chat发送数据
     *
     * @param webDriver web驱动
     * @param textInfo  要发送的数据
     */
    void sendKeys(WebDriver webDriver, String textInfo);

    /**
     * 重新获取数据
     *
     * @param webDriver web驱动
     */
    void regenerate(WebDriver webDriver);

    /**
     * 获取chat回复的数据的集合
     *
     * @param webDriver web驱动
     * @return chat回复数据的List
     */
    List<WebElement> chatReplyList(WebDriver webDriver);

    /**
     * 获取chat最后一次回复的信息数据
     *
     * @param webDriver web驱动
     * @return chat回复的数据
     */
    String chatLastReply(WebDriver webDriver);
}
