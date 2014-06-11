package com.adaptris.profiler;

import java.util.concurrent.locks.ReentrantLock;


public class Locker {
    
    private static Locker instance;
    
    private ReentrantLock lock;
    
    private Locker() {
        lock = new ReentrantLock(true);
    }
    
    public static Locker getInstance() {
        if(instance == null)
            instance = new Locker();
        return instance;
    }
    
    public ReentrantLock getLock() {
        return lock;
    }
}
