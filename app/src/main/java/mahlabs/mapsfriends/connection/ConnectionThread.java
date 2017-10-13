package mahlabs.mapsfriends.connection;

import android.util.Log;

import java.util.LinkedList;

/**
 * Created by 13120dde on 2017-10-12.
 */

public class ConnectionThread {
    private static final String LOG="IN_cThread";
    private Buffer<Runnable> buffer = new Buffer<>();
    private Worker worker;


    public void start(){
        if(worker==null){
            worker = new Worker();
            worker.start();
        }
    }

    public void stop(){
        if(worker!=null){
            worker.interrupt();
            worker=null;
        }
    }

    public void execute(Runnable r){
        buffer.put(r);
    }

    private class Worker extends Thread{
        public void run(){
            while(worker!=null){
                Runnable r;
                try {
                    r = buffer.get();
                    r.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(LOG,e.getMessage());

                }
            }
        }
    }
    private class Buffer<T>{
        private LinkedList<T> buffer = new LinkedList<T>();

        public synchronized void put(T object){
            buffer.addLast(object);
            notifyAll();
        }

        public synchronized T get() throws InterruptedException {
            while (buffer.isEmpty()){
                wait();
            }
            return buffer.removeFirst();
        }
    }
}
