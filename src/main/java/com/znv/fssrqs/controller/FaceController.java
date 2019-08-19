package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.DownloadFileByUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dongzelong on  2019/8/19 13:56.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
@Slf4j
public class FaceController {
    /**
     * 图片base64编码获取
     *
     * @param body
     * @param request
     * @return
     */
    @PostMapping("/base64/image")
    public String getImage(String body, HttpServletRequest request) {
        JSONObject jsonObject = JSON.parseObject(body);
        try {
            int x = !jsonObject.containsKey("X") ? -1 : jsonObject.getIntValue("X");
            int y = !jsonObject.containsKey("Y") ? -1 : jsonObject.getIntValue("Y");
            int width = !jsonObject.containsKey("Width") ? -1 : jsonObject.getIntValue("Width");
            int height = !jsonObject.containsKey("Height") ? -1 : jsonObject.getIntValue("Height");
            int srcWidth = !jsonObject.containsKey("srcWidth") ? -1 : jsonObject.getIntValue("srcWidth");
            int srcHeight = !jsonObject.containsKey("srcHeight") ? -1 : jsonObject.getIntValue("srcHeight");
            String url = jsonObject.getString("Url");
            String data = "";
            if (x == -1) {
                data = DownloadFileByUrl.getBase64ImgByUrl(url);
            } else {
                data = DownloadFileByUrl.getBase64ImgByUrl(url, x, y, width, height, srcWidth, srcHeight);
            }
            return data;
        } catch (Exception e) {
            log.error("get image failed:", e);
            throw ZnvException.error(CommonConstant.StatusCode.INTERNAL_ERROR, "GetBase64ImageFailed", e.getMessage());
        }
    }

    /**
     * 时空推演检索
     */
    @PostMapping("/time/space/deduction/retrieve")
    public String getTimeSpace(HttpServletRequest request) {
//        WriteLogUtils.writeLog(FaceController.ONE_VS_N_SEARCH, "serach face", user.getUsername(), request);
//        JSONObject jsonObj = sendRequest(QUERY_SUPER_SEARCH_FACE, request);
//        return packJsonData(jsonObj);\
        return null;
    }
}
