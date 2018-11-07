package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    //static local Map(cache) based on Guava using LRU strategy
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).
            maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).
            build(new CacheLoader<>() {
                //If key not shot, call load() to return a value.
                @Override
                public String load(String s) throws Exception {
                    return null;
                }
            });

    public static void setKey(String key, String value){
        localCache.put(key, value);
    }
    public static String getKey(String key){
        String value = null;
        try {
            return localCache.get(key);
        } catch (ExecutionException e) {
            logger.error("localCache get error: ", e);
        }
        return null;
    }
}
