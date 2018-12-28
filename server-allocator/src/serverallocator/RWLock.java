package serverallocator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class RWLock {
    Lock l;
    Condition canRead;
    Condition canWrite;
    int wantWrite;
    int readers, writers;

    RWLock() {
        l = new ReentrantLock();
        canRead = l.newCondition();
        canWrite = l.newCondition();
        wantWrite = 0;
        readers = 0;
        writers = 0;
        
    }
    
    void readLock() {
        l.lock();
        try {
            while (writers > 0 || wantWrite > 0)
                canRead.await();
            readers++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            l.unlock();
        }
    }

    void readUnlock() {
        l.lock();
        try {
            readers--;
            if (readers == 0)
                canWrite.signalAll();
        } finally {
            l.unlock();
        }
    }

    void writeLock() {
        l.lock();
        try {
            wantWrite++;
            while (readers + writers > 0)
                canWrite.await();
            wantWrite--;
            writers++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            l.unlock();
        }
    }

    void writeUnlock() {
        l.lock();
        try {
            writers--;
            if (wantWrite > 0)
                canWrite.signalAll();
            else
                canRead.signalAll();
        } finally {
            l.unlock();
        }
    }
}
