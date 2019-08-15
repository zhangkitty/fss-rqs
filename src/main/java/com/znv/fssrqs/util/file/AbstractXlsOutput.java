package com.znv.fssrqs.util.file;

import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.exception.ZnvException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by dongzelong on  2019/8/14 11:15.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public abstract class AbstractXlsOutput {
    //输出流头
    private static final String CONTENT_TYPE = "application/vnd.ms-excel;charset=UTF-8";

    private final HttpServletResponse response;

    public AbstractXlsOutput(HttpServletResponse response) {
        this.response = response;
    }

    public final void render(String fileName, Map<String, Object> map) {
        render(fileName, map, new SXSSFWorkbook(1000));
    }

    /**
     * 渲染数据
     *
     * @param fileName 文件名称
     * @param map      model
     * @param workbook 工作簿
     */
    public final void render(String fileName, Map<String, Object> map, Workbook workbook) {
        this.build(workbook, map);
        response.setContentType(CONTENT_TYPE);
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF8"));
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "FailedExportXLS");
        }
    }

    protected abstract void build(Workbook workbook, Map<String, Object> map);
}
