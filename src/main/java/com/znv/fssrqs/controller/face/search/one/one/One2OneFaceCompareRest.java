package com.znv.fssrqs.controller.face.search.one.one;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.param.face.search.one.one.One2OneFaceCompareRestParams;
import com.znv.fssrqs.service.face.search.one.one.One2OneFaceCompareService;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午9:07
 */
@RestController
public class One2OneFaceCompareRest {

    @Autowired
    private One2OneFaceCompareService one2OneFaceCompareService;


    @PostMapping("/compare/img_character_value")
    public ResponseVo getFaceAttr(@RequestBody String content) {
        JSONObject json = JSON.parseObject(content);
        String imageData = json.getString("ImageData");
        JSONObject result = one2OneFaceCompareService.getFaceAttr(imageData);
        if (result != null) {
            return ResponseVo.success(result);
        }
        return ResponseVo.error("该图片不是人脸");
    }


    @PostMapping("/compare/img_compara_value")
    public ResponseVo getComparaValue(@RequestBody One2OneFaceCompareRestParams one2OneFaceCompareRestParams) {

        JSONObject result = one2OneFaceCompareService.getCompareValue(one2OneFaceCompareRestParams.getImageOne(), one2OneFaceCompareRestParams.getImageTwo());

        return ResponseVo.success(result);
    }
}
