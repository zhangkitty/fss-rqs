package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dongzelong on  2019/8/5 9:32.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
@Slf4j
public class RedisTemplateService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    public <T> boolean set(String key, T value) {
        try {
            String val = beanToString(value);
            if (val == null || val.length() <= 0) {
                return false;
            }
            stringRedisTemplate.opsForValue().set(key, val);
            return true;
        } catch (Exception e) {
            log.error("set key failed,key=" + key, e);
            return false;
        }
    }

    /**
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            return stringToBean(value, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除指定key
     *
     * @param key
     */
    public boolean delete(String key) {
        return stringRedisTemplate.delete(key).booleanValue();
    }

    /**
     * javap进行字节码解析
     *
     * @param value
     * @param <T>
     * @return
     */
    private <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }

        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String value, Class<T> clazz) {
        if (value == null || value.length() <= 0 || clazz == null) {
            return null;
        }

        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(value);
        } else if (clazz == String.class) {
            return (T) value;
        }
        return JSON.toJavaObject(JSON.parseObject(value), clazz);
    }

    /**
     * 针对数据结构为string
     *
     * @param keys
     * @return
     */
    public List<String> multiGet(Set<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 使用Pipeline(管道),组合命令,批量操作redis
     *
     * @param keys
     * @return 批量获取value
     */
    public List<String> executePipelined(Set<String> keys) {
        return redisTemplate.executePipelined(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                List<String> list = new ArrayList<>();
                for (String key : keys) {
                    StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                    String value = stringRedisConnection.get(key);
                    list.add(value);
                }
                return list;
            }
        });
    }

//    /**
//     * 批量获取hash数据结构value,自定义序列化
//     *
//     * @return
//     */
//    public List<Object> executePipelined() {
//        return redisTemplate.executePipelined(
//                new RedisCallback<String>() {
//                    // 自定义序列化
//                    RedisSerializer keyS = redisTemplate.getKeySerializer();
//
//                    @Override
//                    public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
//                        redisConnection.hGet(keyS.serialize(""), keyS.serialize(""));
//                        return null;
//                    }
//                }, redisTemplate.getValueSerializer());
//    }

    public Set<String> getSet(String key) {
        final Set<String> members = redisTemplate.opsForSet().members(key);
        //System.out.println("通过members(K key)方法获取变量中的元素值:" + members);
        return members;
    }

    public Object getObjectByIndex(String key, int index) {
        return redisTemplate.opsForList().index(key, index);
    }

    public long addSet(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }
}
