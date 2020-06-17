package lock;



import config.Zkutils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLock {

    ZooKeeper zk;

    @Before
    public  void conn(){
        zk = Zkutils.getZk();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lock(){

        for(int i= 0; i<10;i++){
            new Thread(()->{
                WatchCallback watchCallBack = new WatchCallback();
                watchCallBack.setZk(zk);
                String threadName = Thread.currentThread().getName();
                watchCallBack.setThreadName(threadName);
                watchCallBack.tryLock();
                System.out.println("分支切换.....");
                //干活
                //释放锁
                watchCallBack.unLock();
            }).start();
        }
        while (true){}
    }
}
