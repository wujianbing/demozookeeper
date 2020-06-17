package config;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class Zkutils {

    private static ZooKeeper zk;
    private static String address = "zookeeper服务地址";
    private static DefaultWatch watch = new DefaultWatch();
    private static CountDownLatch init = new CountDownLatch(1);

    public static ZooKeeper getZk(){

        try {
            zk = new ZooKeeper(address,1000,watch);
            watch.setCc(init);
            init.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }


}
