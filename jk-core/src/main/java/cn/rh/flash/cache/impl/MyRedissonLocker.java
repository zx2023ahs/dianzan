package cn.rh.flash.cache.impl;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis 锁
 */
@Component
public class MyRedissonLocker {

    @Autowired
    private RedissonClient redissonClient;

    /**************************可重入锁： 一线程外层函数获得锁之后 在进入内层方法会自动获取该锁  可避免死锁**************************/

    /**
     * 拿不到lock就不罢休，不然线程就一直block
     * //没有超时时间,默认30s
     * @param lockKey
     * @return
     */
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        //lock.lock();
        return lock;
    }
    /**
     * 自己设置超时时间
     * @param lockKey 锁的key
     * @param timeout 秒 如果是-1，直到自己解锁，否则不会自动解锁
     * @return
     */
    public RLock lock(String lockKey, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 自己设置超时时间
     * @param lockKey 锁的key
     * @param unit 锁时间单位
     * @param timeout 超时时间
     */
    public RLock lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    /**************************公平锁 先来先服务**************************/
    /**
     *  尝试加锁，最多等待waitTime，上锁以后leaseTime自动解锁
     * @param fairLock   锁
     * @param unit      锁时间单位
     * @param waitTime  等到最大时间，强制获取锁
     * @param leaseTime 锁失效时间
     * @return 如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false
     */
    public boolean tryLock( RLock fairLock, TimeUnit unit, int waitTime, int leaseTime) {
        try {
            return fairLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 拿不到锁 就直接 滚蛋  不等待
    public boolean lock( RLock fairLock ) {
        try {
            //  尝试加锁，最多等待waitTime，上锁以后leaseTime自动解锁
            return fairLock.tryLock(0, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;

        }
    }


    public boolean fairLock( RLock fairLock ) {
        try {
            //  尝试加锁，最多等待waitTime，上锁以后leaseTime自动解锁
            return fairLock.tryLock(20, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;

        }
    }

    /**
     * 释放锁
     */
    public void unlock(RLock lock) {
        lock.unlock();
    }



}
