package com.znv.fssrqs.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.FnmsConsts;
import com.znv.fssrqs.constant.Status;
import com.znv.fssrqs.dao.mysql.CompareTaskDao;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.timer.CompareTaskLoader;
import com.znv.fssrqs.util.ConfigManager;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:27
 */

@Slf4j
@Component
public class NtoNCompare {

    @Autowired
    private CompareTaskDao compareTaskDao;

    @Scheduled(fixedRate = 3000)
    public void query() {
        if (CompareTaskLoader.getInstance().getExecQueue().size()==0) {
            if (CompareTaskLoader.getInstance().getWaitQueue().size() != 0) {
                CompareTaskEntity o = (CompareTaskEntity) CompareTaskLoader.getInstance().getWaitQueue().poll();
                CompareTaskLoader.getInstance().getExecQueue().add(o);
                CompareTaskLoader.getInstance().startSpark(o);
                o.setStatus(Status.STARTING.getCode());
                CompareTaskLoader.getInstance().notifyObserver(o);
            }
        }
        else {
            CompareTaskEntity task = (CompareTaskEntity)CompareTaskLoader.getInstance().getExecQueue().element();
//            int num1 = libCount(task.getLib1());
//            int num2 = libCount(task.getLib2());
//            if (num1 == 0 || num2 == 0) {
//                if (task.getProcess() == 1) {
//                    sendSpark(task, FnmsConsts.StatisticsModeIds.STOP_ACTION);
//                    task.setState(Status.FINISHING.getCode());
//                    task.setErrorMessage(task.getTaskId());
//                    CompareTaskServer.getInstance().notifyObserver(task);
//                } else {
//                    sendSpark(task, FnmsConsts.StatisticsModeIds.STOP_ACTION);
//                    task.setState(Status.PAUSING.getCode());
//                    task.setErrorMessage(task.getTaskId());
//                    CompareTaskServer.getInstance().notifyObserver(task);
//                }
//            }
            String retData = sendSpark(task, FnmsConsts.StatisticsModeIds.START_ACTION);

            log.error("retData:"+retData);
            JSONObject params = JSONObject.parseObject(retData);
            if (!StringUtils.isEmpty(retData) && params.getString("action").equals("201")) {
                String result = params.getString("result");
                log.error("result:"+result);
                float process = Float.parseFloat(result);
                if (task.getStatus() == Status.STARTING.getCode()) {
                    task.setStatus(Status.STARTED.getCode());
                }
                if (task.getProcess() > process) {
                    task.setProcess(task.getProcess());
                } else {
                    task.setProcess(process);
                }
                CompareTaskLoader.getInstance().notifyObserver(task);
                if (process == 1) {
                    task.setStatus(Status.FINISHING.getCode());
                    CompareTaskLoader.getInstance().notifyObserver(task);
                }
            } else {
                if (task.getStatus() == Status.PAUSING.getCode()) {
                    task.setStatus(Status.PAUSED.getCode());
                    CompareTaskLoader.getInstance().notifyObserver(task);
                    CompareTaskLoader.getInstance().removeObserver();
                    online = "";
                }
                if (task.getStatus() == Status.FINISHING.getCode()) {
                    task.setStatus(Status.FINISHED.getCode());
                    CompareTaskLoader.getInstance().notifyObserver(task);
                    CompareTaskLoader.getInstance().removeObserver();
                    online = "";
                }
                if (task.getStatus() == Status.STARTED.getCode()) {
                    online = "";
                }
            }
        }

    }

    private String online = "";

    public String sendSpark(CompareTaskEntity task, String action) {
        JSONObject sendObj = new JSONObject();
        String taskId = task.getTaskId();
        sendObj.put("task_id", taskId);
        sendObj.put("send_time", DataConvertUtils.dateToStr(new Date()));
        sendObj.put("action", action);
        String dct = "10.45.152.230";//ConfigManager.getString("zookeeper.quorum");
        String[] ips = dct.split(",");
        String data = null;
        if (StringUtils.isEmpty(online)) {
            for (String ip : ips) {
                String url = String.format("http://%s:%s/?%s", ip, "11111", URLEncoder.encode(sendObj.toString()));
                log.error("1:"+url);
                try {
                    data = HttpUtils.sendGet(url);
                    online = ip;
                    break;
                } catch (ConnectException ex) {
                    log.error("connection occur exception:" + ex.getMessage());
                } catch (Exception e) {
                    log.warn("", e);
                }
            }
        } else {
            try {
                String url = String.format("http://%s:%s/?%s", online, "11111", URLEncoder.encode(sendObj.toString()));
                log.error("2:"+url);
                ;
                data = HttpUtils.sendGet(url);
            } catch (Exception e) {
                log.warn("", e);
            }
        }
        return data;
    }
}
