package com.znv.fssrqs.config;

//import com.znv.fssrqs.service.MessageReceiver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by dongzelong on  2019/8/5 13:28.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Configuration
public class RedisConfig {
    /**
     * 重写Redis序列化方式，使用Json方式:
     * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的。
     * RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer。
     * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer。
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        //GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        // 设置值（value）的序列化采用FastJsonRedisSerializer。
        //redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

//    @Bean
//    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
//                                            MessageListenerAdapter listenerAdapter) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        //订阅了一个叫chat的通道
//        container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
//        container.addMessageListener(listenerAdapter, new PatternTopic("chat1"));
//        return container;
//    }

//    /**
//     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
//     *
//     * @param receiver
//     * @return
//     */
//    @Bean
//    MessageListenerAdapter listenerAdapter(MessageReceiver receiver) {
//        //给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“receiveMessage”
//        //不填defaultListenerMethod默认调用handleMessage
//        return new MessageListenerAdapter(receiver, "receiverMessage");
//    }
}
