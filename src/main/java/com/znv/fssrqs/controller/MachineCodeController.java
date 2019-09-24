package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.util.FastJsonUtils;
import com.znv.fssrqs.util.HardWare;
import com.znv.fssrqs.util.MD5Util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dongzelong on  2019/8/15 15:13.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@RestController
public class MachineCodeController {
    @GetMapping("/generate/machine/code")
    public JSONObject code() {
        String str = "";
        try {
            String bs = HardWare.getHardInfo().getBaseBorderSerinumber();
            String cpu = HardWare.getHardInfo().getCpuSerinumber();
            String mac = HardWare.getHardInfo().getNetSerinumber();
            str = String.format("%s%s%s", bs, cpu, mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FastJsonUtils.JsonBuilder.ok().property("code", MD5Util.encode(str)).json();
    }

    @GetMapping("/generate/license")
    public JSONObject generateLicense() {
        return FastJsonUtils.JsonBuilder.ok().json();
    }
}
