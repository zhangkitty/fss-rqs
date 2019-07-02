package com.znv.fssrqs.websocket;


import com.znv.fssrqs.constant.MBUSConsts;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.RateLimiter;
import com.znv.fssrqs.util.SubscribManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Subscriber;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketMessage.Type;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketAsSession extends ConcurrentHashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    //订阅session
    private WebSocketSession session;
    //订阅者
    private Subscriber<? super WebSocketMessage> subscriber;

    private RateLimiter rateLimiterRealcollection;

    private RateLimiter rateLimiterBoardRealcollection;

    private String hostIp;

    private int hostPort;

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    private void initRateLimiter() {
        if (WebSocketSessionContext.getInstance().getMbusFlushSize() > 0) {
            rateLimiterRealcollection = RateLimiter.create(
                    WebSocketSessionContext.getInstance().getMbusFlushSize());
            rateLimiterBoardRealcollection = RateLimiter.create(
                    WebSocketSessionContext.getInstance().getMbusFlushSize());
        }
    }

    public boolean tryAcquireLimiterRealcollection() {
        if (WebSocketSessionContext.getInstance().getMbusFlushSize() > 0) {
            return rateLimiterRealcollection.tryAcquire();
        } else {
            return true;
        }
    }

    public boolean tryAcquireLimiterBoardRealcollection() {
        if (WebSocketSessionContext.getInstance().getMbusFlushSize() > 0) {
            return rateLimiterBoardRealcollection.tryAcquire();
        } else {
            return true;
        }
    }

    public WebSocketAsSession(WebSocketSession session) {
        //保存session
        this.session = session;
        //获取地址
        String addr = session.getHandshakeInfo().getHeaders().getHost().toString();
        //获取请求url
        URI uri = session.getHandshakeInfo().getUri();
        String[] hosts = addr.split(":");
        this.setHostIp(hosts[0]);
        this.setHostPort(DataConvertUtils.strToInt(hosts[1]));
        //获取url参数
        String strUri = uri.getQuery();
        if (!StringUtils.isEmpty(strUri)) {
            String[] paramStr = strUri.split("&");
            //存放起来
            this.put(MBUSConsts.SubscribeParams.FACE_PAGE_NAME, paramStr[0]);
            this.put(MBUSConsts.SubscribeParams.FACE_EVENT_TYPE, paramStr[1]);
        }
        initRateLimiter();

        this.receive();
    }

    /**
     * 保存订阅者
     *
     * @param subscriber
     */
    public void setSubscriber(Subscriber<? super WebSocketMessage> subscriber) {
        this.subscriber = subscriber;
    }

    /**
     * 接收消息
     */
    private void receive() {
        session.receive().doOnTerminate(() -> {
            try {
                subscriber.onComplete();
                session.close(CloseStatus.GOING_AWAY);
            } catch (Exception e) {
                log.warn("close session failed", e);
            }
            log.info("remove session={},from addr={}", this.getSession().getId(), this.getHostIp());
            //关闭移除sesson
            WebSocketSessionContext.getInstance().remove(this.session.getId());
        }).doOnNext(message -> {//接收消息处理
            subscripMessage(message, this);
        }).doOnError((e) -> {//出现错误
            if (e instanceof IOException) {
                log.error("io error", e);
            }
        }).subscribe();
    }

    /**
     * 处理消息
     *
     * @param msg
     * @param session
     */
    private void subscripMessage(WebSocketMessage msg, WebSocketAsSession session) {
        if (msg.getType() == Type.PONG) {// 如果是client回复的PONG

            return;
        }
        //接收到的数据
        String text = msg.getPayloadAsText();
        if (StringUtils.isEmpty(text)) {
            return;
        }
        //订阅类型
        String[] splitText = text.split("&");
        String scribeValue = null;
        if (splitText.length > 1) {
            //订阅值
            scribeValue = splitText[1];
        } else if (splitText.length == 1 && splitText[0].equals("cancel")) {
            WebSocketSessionContext.getInstance().remove(this.session.getId());
            return;
        }
        SubscribManager.getInstance().subscrib(Integer.parseInt(splitText[0]), scribeValue, session);
    }

    public WebSocketSession getSession() {
        return session;
    }

    public Subscriber<? super WebSocketMessage> getSubscriber() {
        return subscriber;
    }

    public void send(String text) {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        //将消息发给订阅者
        subscriber.onNext(session.textMessage(text));
    }

    public void send(WebSocketMessage msg) {
        subscriber.onNext(msg);
    }
}
