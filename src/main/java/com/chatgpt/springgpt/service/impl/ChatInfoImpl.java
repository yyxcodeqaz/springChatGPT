package com.chatgpt.springgpt.service.impl;

import com.chatgpt.springgpt.service.ChatInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatInfo实现
 *
 * @author leaf
 */
@Service
@Slf4j
public class ChatInfoImpl implements ChatInfo {

    @Value("${chat.replyCount}")
    private int replyCount;

    @Value("${chat.replyCountOld}")
    private int replyCountOld;

    @Override
    public WebDriver initChat() {
        //初始化
        EdgeOptions options = new EdgeOptions();
        options.setExperimentalOption("debuggerAddress", "localhost:9222");
        return new EdgeDriver(options);
    }

    @Override
    public void sendKeys(WebDriver webDriver, String textInfo) {
        //发送数据
        try {
            replyCountOld = replyCount;
            WebElement textBox = webDriver.findElement(By.cssSelector(".m-0"));
            textBox.click();
            Thread.sleep(250);
            String[] textLines = textInfo.split("\n");
            for (String textLine : textLines) {
                textBox.sendKeys(textLine);
            }
            Thread.sleep(250);
            textBox.sendKeys(Keys.SHIFT, Keys.ENTER);
            Thread.sleep(250);
            textBox.sendKeys(Keys.ENTER);
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void regenerate(WebDriver webDriver) {
        replyCount = replyCountOld;
        webDriver.findElement(By.cssSelector(".btn")).click();
    }

    @Override
    public List<WebElement> chatReplyList(WebDriver webDriver) {
        //获取reply的信息
        List<WebElement> infoEle = webDriver.findElements(By.cssSelector(".markdown > p , .markdown > ol li > p , .markdown > pre > div > div > code"));
        if (infoEle.size() > 0) {
            return infoEle;
        } else {
            return null;
        }
    }

    @Override
    public String chatLastReply(WebDriver webDriver) {
        //设置请求超时时间
        int timeout = 90;
        //初始化请求时间
        int timeNow = 0;
        StringBuilder strInfo = new StringBuilder();
        List<WebElement> elemList;
        //判断chatGPT是否正在回复
        while (webDriver.findElements(By.cssSelector(".result-streaming")).size() != 0) {
            //判断是否超时
            try {
                if (timeNow >= timeout) {
                    log.error("超过等待时长阈值");
                    timeNow=0;
                    elemList = chatReplyList(webDriver);
                    if (elemList.size() <= replyCount) {
                        log.error("未收到任何有效回复");
                        return strInfo.toString();
                    } else {
                        log.info("收到部分回复");
                    }
                }
                Thread.sleep(1000);
                timeNow = timeNow + 1;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //获取到chat的回复的数据
        elemList = chatReplyList(webDriver);
        //判断是否获取到数据
        if (elemList.size() <= replyCount) {
            strInfo.append("发生未知错误，未收到回复");
            replyCount = 0;
            return strInfo.toString();
        }
        //返回数据到接口
        for (int i = replyCount; i < elemList.size(); i++) {
            strInfo.append(elemList.get(i).getText());
            strInfo.append("\r\n");
        }
        //充值当前请求次数方便下次获取最后一次回复数据
        replyCount = elemList.size();
        return strInfo.toString();
    }
}
