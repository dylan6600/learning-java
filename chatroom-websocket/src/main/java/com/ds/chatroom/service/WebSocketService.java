package com.ds.chatroom.service;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.ds.chatroom.domain.InMessage;
import com.ds.chatroom.domain.OutMessage;
import com.ds.chatroom.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@Slf4j
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendTopicMessage(String dest, InMessage message) throws InterruptedException {

        for (int i = 0; i < 20; i++) {
            Thread.sleep(500L);
            template.convertAndSend(dest, new OutMessage(message.getContent() + i));
        }
    }


    /**
     * 获取系统信息，推送给客户端
     */
    public void sendServerInfo() {
        int processors = Runtime.getRuntime().availableProcessors();
        Long freeMem = Runtime.getRuntime().freeMemory();
        Long maxMem = Runtime.getRuntime().maxMemory();
        String message = String.format("服务器可用处理器:%s; 虚拟机空闲内容大小: %s; 最大内存大小: %s", processors, freeMem, maxMem);
        template.convertAndSend("/topic/server_info", new OutMessage(message));
    }

    /**
     * 获取股票信息
     *
     * @param stockCode
     */
    public void sendStock(String stockCode) {
        String url = "https://hq.sinajs.cn/etag.php?_=" + System.currentTimeMillis() + "&list=" + stockCode;
        String result1 = HttpRequest.get(url)
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
                .header("referer", "https://finance.sina.com.cn/realstock/company/" + stockCode + "/nc.shtml")
                .timeout(20000)
                .execute().body();
        String[] re = result1.split(",");
        String ret = String.format("股票代码:%s,股票名称:%s,最新价格:%s", stockCode, re[0].split("\"")[1], re[3]);
        log.info("send msg:" + ret);
        if (!StringUtils.isEmpty(ret)) {
            template.convertAndSend("/topic/stock", new OutMessage(ret));
        }
    }

    /**
     * 发送在线用户
     *
     * @param onlineUser
     */
    public void sendOnlineUser(Map<String, User> onlineUser) {
        String msg = "";
        for (Map.Entry<String, User> entry : onlineUser.entrySet()) {
            msg = msg.concat(entry.getValue().getUsername() + " || ");
        }
        log.info("/topic/onlineuser  " + msg);
        template.convertAndSend("/topic/onlineuser", new OutMessage(msg));
    }

    /**
     * 用于多人聊天
     *
     * @param message
     */
    public void sendTopicChat(InMessage message) {
        String msg = message.getFrom() + "发送：" + message.getContent();
        template.convertAndSend("/topic/chat", new OutMessage(msg));
    }

}
