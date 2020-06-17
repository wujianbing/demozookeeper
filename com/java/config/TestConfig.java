package config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

    ZooKeeper zk;

    @Before
    public  void conn(){
       zk = Zkutils.getZk();
    }

    @After
    public void colse(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf(){
        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setZk(zk);
        MyCof cof = new MyCof();
        watchCallBack.setMyCof(cof);

        watchCallBack.aWait();

        while (true){
            if(cof.getConfig().equals("")){
                System.out.println("config diu le ........");
                watchCallBack.aWait();
            }else {
                System.out.println(cof.getConfig());
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
