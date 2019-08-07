package com.znv.fssrqs.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    public Set<String> getSet(String key) {
        final Set<String> members = redisTemplate.opsForSet().members(key);
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

    public long addList(Object key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    public long addList(Object key, List<Object> values) {
        return redisTemplate.opsForList().leftPush(key, values);
    }

    public Object popLeft(Object key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public List<Object> getAllList(String key) {
        return redisTemplate.opsForList().range(key, 0, getSetSize(key));
    }

    public long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public <K, V> void putMap(String key, Map<K, V> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public <V> List<V> getValues4Map(Object key) {
        return redisTemplate.opsForHash().values(key);
    }

    public <K> Set<K> getKeysFromMap(Object key) {
        return redisTemplate.opsForHash().keys(key);
    }

    public Object getValueFromMap(Object key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public boolean addZSet(Object key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public Set<?> keys(Object pattern) {
        return redisTemplate.keys(pattern);
    }

    public boolean expire(Object key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit).booleanValue();
    }

    public boolean expireAt(Object key, Date date) {
        return redisTemplate.expireAt(key, date).booleanValue();
    }

    public void rename(Object oldKey, Object newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    public Object ping() {
        return redisTemplate.execute(new RedisCallback<String>() {

            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                final String ping = connection.ping();
                return ping;
            }
        });
    }

    public void seleteDb(int index) {
        assert index >= 0 && index <= 15;
        redisTemplate.execute(new RedisCallback() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.select(index);
                return "ok";
            }
        });
    }

    public void config(String param, String value) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setConfig(param, value);
                return "ok";
            }
        });
    }

    /**
     * 将key中存储的数字增加1
     *
     * @param key
     * @return
     */
    public long increment(Object key) {
        return redisTemplate.opsForValue().increment(key).longValue();
    }

    public long increment(Object key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta).longValue();
    }

    public double increment(Object key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta).doubleValue();
    }

    public long decrease(Object key) {
        return redisTemplate.opsForValue().decrement(key).longValue();
    }

    public long decrease(Object key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta).longValue();
    }

    /**
     * key存在并且为一个字符串,追加key原来值的末尾
     *
     * @param key
     * @param value
     */
    public int append(Object key, String value) {
        return redisTemplate.opsForValue().append(key, value).intValue();
    }

    /**
     * 移动指定key到另一个索引
     *
     * @param key
     * @param index
     * @return
     */
    public boolean move(Object key, int index) {
        return redisTemplate.move(key, index).booleanValue();
    }

    /**
     * 获取key存储的值的类型
     *
     * @param key
     * @return
     */
    public DataType typeOf(Object key) {
        return redisTemplate.type(key);
    }

    /**
     * 移除key的过期时间,key将持久保持
     *
     * @param key
     * @return
     */
    public boolean persist(Object key) {
        return redisTemplate.persist(key).booleanValue();
    }

    /**
     * 如果key不存在，则替换成功
     *
     * @param oldKey
     * @param newKey
     */
    public boolean renamenx(Object oldKey, Object newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey).booleanValue();
    }

    /**
     * 检查key是否存在
     *
     * @param key
     * @return
     */
    public boolean isExists(Object key) {
        return redisTemplate.hasKey(key).booleanValue();
    }

    /**
     * 若key不存在,设置key的值为value
     *
     * @param key   不能为空
     * @param value 不能为空
     * @return true:success,false:failure
     */
    public boolean setNx(Object key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value).booleanValue();
    }

    /**
     * 删除指定数据库索引数据
     *
     * @param index 数据库索引
     */
    public void flushDb(int index) {
        stringRedisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.select(index);
                connection.flushDb();
                return "ok";
            }
        });
    }

    /**
     * 获取当前数据库索引
     */
    public int getCurrentDb() {
        return ((LettuceConnectionFactory) redisTemplate.getConnectionFactory()).getDatabase();
    }

    /**
     * 删除当前数据库的所有key
     */
    public void flushDb() {
        stringRedisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    /**
     * 删除所有数据库的key
     */
    public void flushAll() {
        stringRedisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushAll();
                return "ok";
            }
        });
    }

    /**
     * 序列化给定的key,并返回被序列化的值
     *
     * @param key
     * @return
     */
    public byte[] dump(Object key) {
        return redisTemplate.dump(key);
    }

    public void beginTransaction() {
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
    }

    public void commit() {
        redisTemplate.exec();
    }

    public void closeTransaction() {
        redisTemplate.setEnableTransactionSupport(false);
    }

    public void discard() {
        redisTemplate.discard();
    }

    public void watch(Object key) {
        redisTemplate.watch(key);
    }

    public Map<Object, Double> scan4Zset(Object key) {
        final Cursor<ZSetOperations.TypedTuple<Object>> scan = redisTemplate.opsForZSet().scan(key, ScanOptions.NONE);
        Map<Object, Double> map = new HashMap<>();
        while (scan.hasNext()) {
            final ZSetOperations.TypedTuple<Object> item = scan.next();
            map.put(item.getValue(), item.getScore());
        }
        return map;
    }
}
