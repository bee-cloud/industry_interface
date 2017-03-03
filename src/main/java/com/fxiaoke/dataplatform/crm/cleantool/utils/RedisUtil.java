package com.fxiaoke.dataplatform.crm.cleantool.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by weilei on 2016/11/10.
 */
public class RedisUtil {
    private static Jedis jedis;
    private static JedisPool pool;
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(false);
        pool = new JedisPool(config,"172.31.103.111",26379);
        jedis = pool.getResource();
    }

    public static void put(String key ,String value){
        jedis.set(key,value);
    }
    public static void main(String[] args){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(false);
        pool = new JedisPool(config,"172.31.103.111",26379);
        jedis = pool.getResource();
        jedis.set("weilei","13686548795");
        System.out.println(jedis.get("weilei"));
    }
}
