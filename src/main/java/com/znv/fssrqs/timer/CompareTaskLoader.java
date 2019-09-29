package com.znv.fssrqs.timer;

import com.znv.fssrqs.config.SparkConfig;
import com.znv.fssrqs.constant.Status;
import com.znv.fssrqs.dao.mysql.CompareTaskDao;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.util.SpringContextUtil;
import com.znv.fssrqs.util.command.ssh.SSHCommandExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:20
 */


@Component
@Data
@Slf4j
public class CompareTaskLoader {
    private ExecutorService executor = Executors.newCachedThreadPool();
    private static class SingletonHolder {
        private static CompareTaskLoader instance = new CompareTaskLoader();
    }

    public static CompareTaskLoader getInstance() {
        return SingletonHolder.instance;
    }

    @Autowired
    private CompareTaskDao compareTaskDao;


    @Autowired
    private SparkConfig sparkConfig;


    @Autowired
    private ElasticSearchClient elasticSearchClient;

    private ConcurrentLinkedDeque<CompareTaskEntity> waitQueue = new ConcurrentLinkedDeque<CompareTaskEntity>();
    private ConcurrentLinkedDeque<CompareTaskEntity> execQueue = new ConcurrentLinkedDeque<CompareTaskEntity>();

    public void loadCompareTask() {

        List<CompareTaskEntity> list = SpringContextUtil.getCtx().getBean(CompareTaskDao.class).findAllCompareTask();

        list.stream().forEach(compareTaskEntity -> {
            Integer status = compareTaskEntity.getStatus();
            if (compareTaskEntity.getStatus() == Status.WAITING.getCode()) {
                waitQueue.add(compareTaskEntity);
            } else if (status == Status.STARTING.getCode() || status == Status.PAUSING.getCode() || status == Status.STARTED.getCode() || status == Status.FINISHING.getCode()) {
                execQueue.add(compareTaskEntity);
                if (status == Status.STARTING.getCode()) {
                    startSpark(compareTaskEntity);
                }
            }
        });
    }


    public void startSpark(CompareTaskEntity o) {
        String taskId = o.getTaskId();
        String lib1 = String.valueOf(o.getLib1());
        String lib2 = String.valueOf(o.getLib2());
        String sim = String.valueOf(o.getSim());

        String sparkStart = SpringContextUtil.getCtx().getBean(SparkConfig.class).getShellscript() + " ";//FssPropertyUtils.getInstance().getProperty("fss.spark.start");
        String userName = null;
        String password = null;
        String[] userPwdArr = null;
        String zookeepers = SpringContextUtil.getCtx().getBean(ElasticSearchClient.class).getHost(); //FssPropertyUtils.getInstance().getProperty("conf.spark.host");
        //String sshUserPwd = FssPropertyUtils.getInstance().getProperty("bigdata.ssh.username.password");
        //userPwdArr = sshUserPwd.split(",");
        userName = "root";//userPwdArr[0].split(":")[0];
        password = "@znv_2014"; //userPwdArr[0].split(":")[1];
        SSHCommandExecutor sshExecutor = new SSHCommandExecutor(zookeepers, userName, password);
        String startCommand = sparkStart + "start" + " " + taskId + " " + lib1 + " " + lib2 + " " + sim + " " + "11111";

        log.info("startSpark:" + startCommand);
        sshExecutor.execute(startCommand);
    }


    public void registerObserver(CompareTaskEntity o) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                o.setStatus(Status.WAITING.getCode());
                waitQueue.add(o);
                notifyObserver(o);

            }
        });
    }

    public void notifyObserver(CompareTaskEntity o) {

        SpringContextUtil.getCtx().getBean(CompareTaskDao.class).update(o);
    }


    public void removeObserver() {
        if (!execQueue.isEmpty()) {
            execQueue.remove();
        }
    }

    private String online = "";


}
