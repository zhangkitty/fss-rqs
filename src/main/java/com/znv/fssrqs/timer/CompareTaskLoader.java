package com.znv.fssrqs.timer;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.FnmsConsts;
import com.znv.fssrqs.constant.Status;
import com.znv.fssrqs.dao.mysql.CompareTaskDao;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.util.*;
import com.znv.fssrqs.util.command.ssh.SSHCommandExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:20
 */


@Component
@Data
@Slf4j
public class CompareTaskLoader {

    private static class SingletonHolder {
        private static CompareTaskLoader instance = new CompareTaskLoader();
    }

    public static CompareTaskLoader getInstance() {
        return SingletonHolder.instance;
    }

    @Autowired
    private CompareTaskDao compareTaskDao;

    private  ConcurrentLinkedDeque waitQueue = new ConcurrentLinkedDeque();
    private  ConcurrentLinkedDeque execQueue = new ConcurrentLinkedDeque();

    public void loadCompareTask(){

        List<CompareTaskEntity> list = SpringContextUtil.getCtx().getBean(CompareTaskDao.class).findAllCompareTask();

        list.stream().forEach(compareTaskEntity -> {
            Integer status = compareTaskEntity.getStatus();
            if(compareTaskEntity.getStatus() == Status.WAITING.getCode()){
                waitQueue.add(compareTaskEntity);
            }else if(status == Status.STARTING.getCode()||status ==Status.PAUSING.getCode()||status == Status.STARTED.getCode() || status  == Status.FINISHING.getCode()) {
                execQueue.add(compareTaskEntity);
                if(status == Status.STARTING.getCode()){
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

        String sparkStart = "cd /home/fss/v1.32/fssDeployUtil/ &&  ./fss_service.sh execNNCompare ";//FssPropertyUtils.getInstance().getProperty("fss.spark.start");
        String userName = null;
        String password = null;
        String[] userPwdArr = null;
        String zookeepers = "lv230.dct-znv.com"; //FssPropertyUtils.getInstance().getProperty("conf.spark.host");
        //String sshUserPwd = FssPropertyUtils.getInstance().getProperty("bigdata.ssh.username.password");
        //userPwdArr = sshUserPwd.split(",");
        userName = "root";//userPwdArr[0].split(":")[0];
        password = "@znv_2014"; //userPwdArr[0].split(":")[1];
        SSHCommandExecutor sshExecutor = new SSHCommandExecutor(zookeepers, userName, password);
        String startCommand=sparkStart + "start" + " " + taskId + " " + lib1 + " " + lib2 + " " + sim + " " + "11111";

        log.info("startSpark:" + startCommand);
        sshExecutor.execute(startCommand);
    }


    public void registerObserver(CompareTaskEntity o) {
        ICAPVThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                waitQueue.add(o);
                o.setStatus(Status.WAITING.getCode());
                notifyObserver(o);

            }
        });
    }

    public void notifyObserver(CompareTaskEntity o){

        SpringContextUtil.getCtx().getBean(CompareTaskDao.class).update(o);
    }


    public void removeObserver() {
        if (!execQueue.isEmpty()) {
            execQueue.remove();
        }
    }

    private String online = "";



}
