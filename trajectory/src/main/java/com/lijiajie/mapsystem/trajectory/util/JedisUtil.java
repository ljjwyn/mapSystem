package com.lijiajie.mapsystem.trajectory.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author roman.zhang @Date: 2019/8/27 11:26 @Version:V1.0 @Description:JedisUtil
 */
@Component
@Slf4j
public class JedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    @Autowired
    private JedisPool jedisPool;

    private Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 设值 @param key @param value @return
     */
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.set(key, value);
        } catch (Exception e) {
            logger.error("set key:{} value:{} error", key, value, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 设值 @param key @param value @param expireTime 过期时间, 单位: s @return
     */
    public String set(String key, String value, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.setex(key, expireTime, value);
        } catch (Exception e) {
            logger.error("set key:{} value:{} expireTime:{} error", key, value, expireTime, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 取值 @param key @return
     */
    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("get key:{} error", key, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 设置 list
     * @param <T>
     * @param key
     * @param list
     */
    public <T> void setList(String key , List<T> list){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key.getBytes(),ObjectTranscoder.serialize(list));
        } catch (Exception e) {
            logger.error("Set key error : "+e);
        }finally {
            close(jedis);
        }
    }
    /**
     * 获取list
     * @param <T>
     * @param key
     * @return list
     */
    public <T> List<T> getList(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if(jedis == null || !jedis.exists(key.getBytes())){
                return null;
            }
            byte[] in = jedis.get(key.getBytes());
            List<T> list = (List<T>) ObjectTranscoder.deserialize(in);
            return list;
        } catch (Exception e) {
            logger.error("get key:{} error", key, e);
            return null;
        } finally {
            close(jedis);
        }
    }
    /**
     * 设置 map
     * @param <T>
     * @param key
     * @param map
     */
    public <T> void setMap(String key , Map<String,T> map){
        Jedis jedisMap = null;
        try {
            jedisMap = getJedis();
            jedisMap.set(key.getBytes(),ObjectTranscoder.serialize(map));
        } catch (Exception e) {
            logger.error("Set map key error : "+e);
        }finally {
            close(jedisMap);
        }
    }
    /**
     * 获取list
     * @param <T>
     * @param key
     * @return list
     */
    public <T> Map<String,T> getMap(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if(jedis == null || !jedis.exists(key.getBytes())){

                return null;
            }
            byte[] in = jedis.get(key.getBytes());
            Map<String,T> map = (Map<String, T>) ObjectTranscoder.deserialize(in);
            return map;
        } catch (Exception e) {
            logger.error("get key:{} error", key, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 删除key @param key @return
     */
    public Long del(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(key.getBytes());
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 把一个或多个元素添加到指定集合
     *
     * @param key
     * @param members
     */
    public void sadd(String key, String members){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.sadd(key, members);
        }catch (Exception e) {
            logger.error("Set error : "+e);
        }finally {
            close(jedis);
        }
    }

    /**
     * 判断元素是否是集合成员
     *
     * @param key
     * @param member
     * @return
     */
    public boolean sismember(String key, String member){
        Jedis jedis = null;
        boolean result;
        try {
            jedis = getJedis();
            result = jedis.sismember(key, member);
            return result;
        }catch (Exception e) {
            logger.error("Search Set error : "+e);
            return false;
        }finally {
            close(jedis);
        }
    }


    /**
     * 返回集合所有成员
     *
     * @param key
     * @return
     */
    public Set<String> smembers(String key){
        Jedis jedis = null;
        Set<String> result;
        try {
            jedis = getJedis();
            result = jedis.smembers(key);
            return result;
        }catch (Exception e) {
            logger.error("Get Set error : "+e);
            return null;
        }finally {
            close(jedis);
        }
    }


    /**
     * 移除一个或多个元素，不存在的  元素会被忽略
     *
     * @param key
     * @param members
     */
    public void srem(String key, String... members){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.srem(key, members);
        }catch (Exception e) {
            logger.error("Delete Set error : "+e);
        }finally {
            close(jedis);
        }
    }

    /**
     * 判断key是否存在 @param key @return
     */
    public Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.exists(key.getBytes());
        } catch (Exception e) {
            log.error("exists key:{} error", key, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 设值key过期时间 @param key @param expireTime 过期时间, 单位: s @return
     */
    public Long expire(String key, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire(key.getBytes(), expireTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 获取剩余时间 @param key @return
     */
    public Long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.ttl(key);
        } catch (Exception e) {
            log.error("ttl key:{} error", key, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    private void close(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }
}

