package config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StatCallback,AsyncCallback.DataCallback {

   ZooKeeper zk;
   MyCof cof;
   CountDownLatch latch = new CountDownLatch(1);

   public MyCof getCof(){return cof; }

   public void setMyCof(MyCof cof){this.cof = cof;}

   public ZooKeeper getZk(){return zk;}

   public void setZk(ZooKeeper zk){this.zk = zk;}

   public  void aWait(){
       zk.exists("",this,this,"cbd");

       try {
           latch.await();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if(bytes != null){
            String s1 = new String(bytes);
            cof.setConfig(s1);
            latch.countDown();
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if(stat != null){
            zk.getData("",this,this,"abc");
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                zk.getData("",this,this,"abc");
                break;
            case NodeDeleted:
               cof.setConfig("");
               latch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("",this,this,"abc");
                break;
            case NodeChildrenChanged:
                break;
        }
    }
}
