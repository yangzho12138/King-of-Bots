package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final static ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition(); // semaphore
    private Queue<Bot>  bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try{
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll(); // wake up blocked threads
        }finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot){
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);
    }

    @Override
    public void run() {
        while(true){
            lock.lock();
            if(bots.isEmpty()){ // block
                try {
                    condition.await(); // release lock
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else{
                Bot bot = bots.remove();
                lock.unlock(); // 取完对头后就和队列没有关系了，可以释放锁
                consume(bot); // time-consuming --> 在消费前释放锁
            }
        }
    }
}
