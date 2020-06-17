package lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatchCallback implements Watcher,AsyncCallback.StatCallback,AsyncCallback.StringCallback,AsyncCallback.ChildrenCallback{
    ZooKeeper zk;
    String threadName;
    CountDownLatch cc = new CountDownLatch(1);
    String pathName;

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void tryLock(){
        zk.create("/lock",threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL,this,"abc");
        try {
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock(){

        try {
            zk.delete(pathName,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {

    }

    @Override
    public void process(WatchedEvent watchedEvent) {

        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/",false,this,"abc");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }
    //每个线程启动后创建锁，然后get锁目录的所有孩子，不注册watch在锁目录
    @Override
    public void processResult(int i, String s, Object o, String s1) {
        if(s1 != null){
            pathName = s1;
            zk.getChildren("",false,this,"abc");

        }
    }

    //获得目录的所有有序节点，然后排序，然后取自己在有序list中的index
    @Override
    public void processResult(int i, String s, Object o, List<String> list) {
        Collections.sort(list);
        int a = list.indexOf(pathName.substring(1));

        if(a == 0){
            try {
                zk.setData("/lock",threadName.getBytes(),-1);
                cc.countDown();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else {
            zk.exists(""+list.get(a-1),this,this,"abc");
        }
    }
}
