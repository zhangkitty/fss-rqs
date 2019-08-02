package com.znv.fssrqs.controller.face.compare.n.n;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.CompareTaskDao;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.param.face.compare.n.n.NToNCompareTaskParam;
import com.znv.fssrqs.service.compareservice.CompareService;
import com.znv.fssrqs.util.MD5Util;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:34
 */

@RestController
public class CompareController {

    @Autowired
    private CompareService compareService;

    @Autowired
    private CompareTaskDao compareTaskDao;


    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/taskLibCheck.ds",method = RequestMethod.POST)
    public ResponseVo check(@RequestBody String response){
        JSONObject jsonObject = JSONObject.parseObject(response);
        HashMap hashMap = compareService.check(jsonObject);
        if(hashMap.size()>0){
            return ResponseVo.getInstance(200000,"入参有错误",hashMap);
        }else {
            return ResponseVo.success(hashMap);
        }
    }

    @RequestMapping(value="/site/FSSAPP/pc/nvsm/tasksave.ds",method = RequestMethod.POST)
    public ResponseVo save(@RequestBody NToNCompareTaskParam nToNCompareTaskParam){
        Long num = compareTaskDao.findAllCompareTask().stream().filter(value->{
            if(value.getTaskId().equals(MD5Util.encode(nToNCompareTaskParam.toString()))){
                return true;
            }else {
                return false;
            }
        }).count();
        if(num>0){
            return ResponseVo.error("任务已经存在");
        }
        Integer result = compareService.save(nToNCompareTaskParam);

        if(result>0)
            return  ResponseVo.success(result);
        return ResponseVo.error("失败");
    }

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/queryTask.ds",method = RequestMethod.POST)
    public ResponseVo queryTask(@RequestBody QueryTaskParams queryTaskParams){
        //PageHelper.startPage(1, 1);
        List<CompareTaskEntity> list=compareTaskDao.findAllCompareTask();

        return  null;
    }
}
