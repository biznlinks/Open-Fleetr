/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amt.common.cache;

import com.tna.common.ObjectCache;
import java.sql.Timestamp;
import java.util.concurrent.locks.ReentrantLock;
import org.json.simple.JSONObject;

/**
 *
 * @author tareq
 */
public class CurrentStatusEntityCache {

    private static CurrentStatusEntityCache realTimeCache;
    private static ObjectCache<Long, JSONObject> cache;
    private static ReentrantLock lock;

    private CurrentStatusEntityCache() {
        cache = new ObjectCache();
        lock = new ReentrantLock();
    }

    public static CurrentStatusEntityCache getInstance() {
        if (realTimeCache == null) {
            synchronized(CurrentStatusEntityCache.class) {
                if (realTimeCache == null) {
                    realTimeCache = new CurrentStatusEntityCache();
                }
            }
        }
        return realTimeCache;

    }

    public static void cache(Long key, JSONObject value) {
        lock.lock();
        try {
            CurrentStatusEntityCache.getInstance().cache.cache(key, value);
        } finally {
            lock.unlock();
        }
    }

    public static JSONObject retreive(Long key) {
        JSONObject retreive;
        retreive = CurrentStatusEntityCache.getInstance().cache.retreive(key);
        return retreive;
    }

    public static void setTimeStamp(Timestamp time) {
        lock.lock();
        try {
            CurrentStatusEntityCache.getInstance().cache.setTimeStamp(time);
        } finally {
            lock.unlock();
        }
    }

    public static Timestamp getTimeStamp() {
        Timestamp timeStamp;
        timeStamp = CurrentStatusEntityCache.getInstance().cache.getTimeStamp();
        return timeStamp;
    }

}
