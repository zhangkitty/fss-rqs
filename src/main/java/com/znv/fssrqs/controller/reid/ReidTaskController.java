package com.znv.fssrqs.controller.reid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.znv.fssrqs.controller.reid.params.QueryReidTaskParma;
import com.znv.fssrqs.controller.reid.params.ReidTaskParam;
import com.znv.fssrqs.dao.mysql.ReidTaskDao;
import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import com.znv.fssrqs.service.reid.ReidTaskService;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:46
 */

@RestController
public class ReidTaskController {

    @Autowired
    private ReidTaskService reidTaskService;

    @Autowired
    private ReidTaskDao reidTaskDao;


    @RequestMapping(value = "/reid-task/save",method = RequestMethod.POST)
    public ResponseVo saveTask(@RequestBody  ReidTaskParam reidTaskParam){
        ReidTaskEntity reidTaskEntity = new ReidTaskEntity();
        BeanUtils.copyProperties(reidTaskParam, reidTaskEntity);
        Integer result = reidTaskService.save(reidTaskEntity);
        return ResponseVo.success(result);

    }

    @RequestMapping(value = "/reid-task/getAllTask",method = RequestMethod.POST)
    public ResponseVo getAll(@RequestBody String str){
        try {
            QueryReidTaskParma queryReidTaskParma = JSONObject.parseObject(str,QueryReidTaskParma.class);
            Page page = PageHelper.startPage(queryReidTaskParma.getPageNum(), queryReidTaskParma.getPageSize());
            List<ReidTaskEntity> list = reidTaskDao.getAll(queryReidTaskParma);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total",page.getTotal());
            jsonObject.put("list",list);
            return ResponseVo.success(jsonObject);
        }catch (Exception e){
            return  ResponseVo.error("入参错误");
        }


    }

    @RequestMapping(value = "/reid-task/update",method = RequestMethod.POST)
    public ResponseVo update(@RequestBody String str){
        try{
            ReidTaskEntity reidTaskEntity = JSONObject.parseObject(str,ReidTaskEntity.class);
            Integer result = reidTaskDao.update(reidTaskEntity);
            if(result>0){
                return ResponseVo.success(null);
            }else {
                return ResponseVo.error("更新失败");
            }
        }catch (Exception e){
            return  ResponseVo.error("入参错误");
        }
    }

    @RequestMapping(value = "/reid-task/delete",method = RequestMethod.POST)
    public ResponseVo delete(@RequestBody String str){
        JSONObject jsonObject = JSON.parseObject(str);
        List<Integer> list = new ArrayList<>();
        jsonObject.getJSONArray("ids").stream().forEach(v->list.add((Integer) v));
        Integer result =reidTaskDao.delete(list);
        if(result==list.size()){
            return ResponseVo.success(null);
        }else {
            return ResponseVo.error("删除失败");
        }
    }
}
