package com.znv.fssrqs.controller.document;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.service.documentservice.DocService;
import com.znv.fssrqs.vo.ResponseVo;
import org.jamon.annotations.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.util.JAXBSource;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:38
 */

@RestController
public class DocOverViewController {

    @Autowired
    private DocService docService;

    @RequestMapping(value = "/doc-over-view",method = RequestMethod.GET)
    public ResponseVo docOverView(){
        JSONObject jsonObject = docService.getDocOverView();
        return ResponseVo.success(jsonObject);
    }
}
