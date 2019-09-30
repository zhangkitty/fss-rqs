package com.znv.fssrqs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.constant.ExcelResource;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.DataConvertUtils;
import com.znv.fssrqs.util.SpringContextUtil;
import com.znv.fssrqs.util.file.AlarmXlsOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dongzelong on  2019/8/14 10:57.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Controller
@Slf4j
public class FileController {
    /**
     * 导出excel数据
     */
    @PostMapping(value = "/export/{resType}/excel")
    public void exportExcel(ModelMap model, @RequestHeader(value = "Host") String host, @PathVariable("resType") Integer resType, @RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        log.info("export start time:" + new Date().toString());
        if (!ExcelResource.contains(resType)) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "ExcelResourceNotExist");
        }
        Locale locale = request.getLocale();
        AlarmXlsOutput output = null;
        switch (resType) {
            case 1:
                final JSONObject jsonObject = JSON.parseObject(body);
                final int exportSize = jsonObject.getIntValue("Size");
                HistoryAlarmController historyAlarmController = SpringContextUtil.getCtx().getBean(HistoryAlarmController.class);
                String result = historyAlarmController.getHistoryAlarm(host, body);
                output = new AlarmXlsOutput(response, locale, result, body, exportSize);
                break;
            default:
                throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "ExcelResourceNotExist");
        }

        String filePath = "file" + File.separator + "export" + File.separator + getResName(resType, locale.toString()) + ".xlsx";
        try {
            Resource resource = new ClassPathResource(filePath);
            try (Workbook sheets = WorkbookFactory.create(resource.getInputStream())) {
                try (Workbook workbook = new SXSSFWorkbook((XSSFWorkbook) sheets, 1000, false)) {
                    output.render(getResName(resType, locale.toString()) + DataConvertUtils.dateToStr() + ".xlsx", model, workbook);
                }
            } catch (InvalidFormatException e) {
                log.error("", e);
            }
        } catch (IOException e) {
            log.error("Template file not found", e);
        }
        log.info("export finish time:" + new Date().toString());
    }


    private static String getResName(Integer resType, String locale) {
        return ExcelResource.valueOf(resType, locale);
    }
}
