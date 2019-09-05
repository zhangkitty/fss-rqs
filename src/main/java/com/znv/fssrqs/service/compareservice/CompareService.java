package com.znv.fssrqs.service.compareservice;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.FnmsConsts;
import com.znv.fssrqs.constant.Status;
import com.znv.fssrqs.dao.mysql.CompareTaskDao;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.param.face.compare.n.n.NToNCompareTaskParam;
import com.znv.fssrqs.timer.CompareTaskLoader;
import com.znv.fssrqs.timer.NtoNCompare;
import com.znv.fssrqs.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:37
 */

@Service
public class CompareService {
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private CompareTaskDao compareTaskDao;

    @Autowired
    private CompareTaskLoader compareTaskLoader;

    public HashMap check(JSONObject jsonObject) {

        HashMap hashMap = new HashMap();

        Integer max = jsonObject.getIntValue("LimitCount");

        jsonObject.getJSONArray("LibID").forEach(value -> {
            if (getPersonCount((Integer) value) > max) {
                hashMap.put(value, "该库的人数超过" + max);
            }
        });

        return hashMap;
    }


    public Integer save(NToNCompareTaskParam nToNCompareTaskParam) {
        Integer result = compareTaskDao.save(nToNCompareTaskParam);
        CompareTaskEntity o = new CompareTaskEntity();
        o.setTaskId(nToNCompareTaskParam.getTaskId());
        o.setStatus(nToNCompareTaskParam.getStatus());
        o.setProcess(nToNCompareTaskParam.getProcess());
        o.setLib1(nToNCompareTaskParam.getLib1());
        o.setLib2(nToNCompareTaskParam.getLib2());
        o.setSim(nToNCompareTaskParam.getSim());
        if (result > 0) {
            CompareTaskLoader.getInstance().registerObserver(o);
        }
        return result;
    }

    public Integer update(NToNCompareTaskParam nToNCompareTaskParam) {
        CompareTaskEntity o = new CompareTaskEntity();
        o.setTaskId(nToNCompareTaskParam.getTaskId());
        o.setStatus(nToNCompareTaskParam.getStatus());
        o.setProcess(nToNCompareTaskParam.getProcess());
        o.setLib1(nToNCompareTaskParam.getLib1());
        o.setLib2(nToNCompareTaskParam.getLib2());
        o.setSim(nToNCompareTaskParam.getSim());
        Integer result = compareTaskDao.update(o);
        if (result > 0) {
            CompareTaskLoader.getInstance().registerObserver(o);
        }
        return result;
    }


    public Integer delete(String taskId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ConcurrentLinkedDeque<CompareTaskEntity> waitTask = CompareTaskLoader.getInstance().getWaitQueue();
                for (CompareTaskEntity wq : waitTask) {
                    if (wq.getTaskId().equals(taskId)) {
                        waitTask.remove(wq);
                    }
                }
            }
        });

        Integer result = compareTaskDao.delete(taskId);
        return result;
    }


    public Integer getPersonCount(Integer libID) {

        StringBuffer str = new StringBuffer();

        str.append("{\"size\":0,\"query\":{\"bool\":{\"must\":{\"term\":{\"lib_id\":").append(libID).append("}}}},\"from\":0}");

        JSONObject jsonObject = JSONObject.parseObject(str.toString());

        Result<JSONObject, String> result = elasticSearchClient.postRequest("http://10.45.152.230:9200/person_list_data_n_project_v1_2/person_list/_search?pretty", jsonObject);

        return (Integer) result.value().getJSONObject("hits").get("total");
    }


    /**
     * N：M 暂停
     */
    public void stop() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CompareTaskEntity task = CompareTaskLoader.getInstance().getExecQueue().element();
                task.setStatus(Status.PAUSING.getCode());
                CompareTaskLoader.getInstance().notifyObserver(task);
                NtoNCompare.getInstance().sendSpark(task, FnmsConsts.StatisticsModeIds.STOP_ACTION);
            }
        });
    }

    /**
     * 强制开始
     *
     * @param taskId
     */
    public void forceStart(String taskId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CompareTaskEntity execTask = CompareTaskLoader.getInstance().getExecQueue().element();
                NtoNCompare.getInstance().sendSpark(execTask, FnmsConsts.StatisticsModeIds.STOP_ACTION);
                execTask.setStatus(Status.PAUSING.getCode());
                CompareTaskLoader.getInstance().notifyObserver(execTask);
                CompareTaskEntity target = null;
                for (CompareTaskEntity q : CompareTaskLoader.getInstance().getWaitQueue()) {
                    if (q.getTaskId().equals(taskId)) {
                        target = q;
                        CompareTaskLoader.getInstance().getWaitQueue().remove(q);
                        break;
                    }
                }
                if (target != null) {
                    CompareTaskLoader.getInstance().getWaitQueue().addFirst(target);
                }
            }
        });
    }
}
