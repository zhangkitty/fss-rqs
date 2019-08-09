package com.znv.fssrqs.controller.face.compare.n.n;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.CompareTaskDao;
import com.znv.fssrqs.elasticsearch.ElasticSearchClient;
import com.znv.fssrqs.entity.mysql.CompareTaskEntity;
import com.znv.fssrqs.service.compareservice.QueryResultService;
import com.znv.fssrqs.util.Result;
import com.znv.fssrqs.util.Template;
import com.znv.fssrqs.vo.ResponseVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午2:10
 */

@RestController
@Slf4j
public class QueryResultController {


    @Autowired
    private QueryResultService queryResultService;

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private CompareTaskDao compareTaskDao;

    /**
     * n:m的分析结果查询
     * @param queryResultParams
     * @return
     */

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/pairsearch.ds",method = RequestMethod.POST)
    public ResponseVo queryResult(@RequestBody QueryResultParams queryResultParams){

        return  ResponseVo.success(queryResultService.queryResultService(queryResultParams));

    }

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/pairexport.ds",method = RequestMethod.POST)
    public void export(@RequestBody QueryResultParams queryResultParams,HttpServletResponse response){
        JSONObject jsonObject = queryResultService.queryResultService(queryResultParams);
CompareTaskEntity compareTaskEntity = compareTaskDao.findAllCompareTask()
        .stream().filter(value->value.getTaskId().equals(queryResultParams.getTaskId())).collect(Collectors.toList()).get(0);
        exportExcel(response,jsonObject,compareTaskEntity.getLib1Name(),compareTaskEntity.getLib2Name());
    }

    @RequestMapping(value = "/site/FSSAPP/pc/nvsm/pairdel.ds/{taskId}",method = RequestMethod.GET)
    public ResponseVo pairDel(@PathVariable String taskId) {
        // 任务编辑&&没有异常&&重新开始，需要删除历史比对数据
        StringBuffer sb = new StringBuffer();
        sb.append("http://")
                .append(elasticSearchClient.getHost())
                .append(":")
                .append(elasticSearchClient.getPort())
                .append("/")
                .append("n2m_face_result_n_project_v1.20")
                .append("/")
                .append("n2m_face_result")
                .append("/")
                .append("_delete_by_query");
        Map<String, String> map = new HashMap<>();
        map.put("taskId", taskId);
        String content = "{\"query\":{\"bool\":{\"filter\":{\"term\":{\"task_id\":\"${taskId}\"}}}}}";
        Result<JSONObject, String>  result = elasticSearchClient.postRequest(sb.toString(), (JSONObject) JSONObject.parseObject(Template.renderString(content, map)));
        return ResponseVo.success(result.value().get("deleted"));
    }

    private void exportExcel(HttpServletResponse response,JSONObject obj, String libName1, String libName2) {
        String filename = "";
        try {
            filename = URLEncoder.encode("n比N数据导出.xls", "utf-8");
        } catch (UnsupportedEncodingException e1) {
        }
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + "");
        // 多两行写入列名和导出条数
        // int rows = Integer.parseInt(obj.getString("total")) + 2;
        int rows = obj.getJSONArray("list").size() + 2;
        int cols = 18;
        Workbook book = null;
        Sheet sheet = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            book = new SXSSFWorkbook(128); // 缓存128在内存。
            sheet = book.createSheet("n比N数据导出");
            CellStyle cellStyle = book.createCellStyle();
            cellStyle.setWrapText(true);
            writeExcel(book, sheet, rows, cols, obj, libName1, libName2);
            bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            book.write(bufferedOutputStream);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    private void writeExcel(Workbook book, Sheet sheet, int row, int col, JSONObject obj, String libName1,
                            String libName2) throws IOException {
        Row sheetRow0 = sheet.createRow(0);
        Cell cell00 = sheetRow0.createCell(0);
        cell00.setCellValue("总条数");
        Cell cell01 = sheetRow0.createCell(1);
        cell01.setCellValue(obj.getString("total"));
        Cell cell02 = sheetRow0.createCell(2);
        cell02.setCellValue("导出条数");
        Cell cell03 = sheetRow0.createCell(3);
        cell03.setCellValue(obj.getJSONArray("list").size());
        // 列名
        Row sheetRow = sheet.createRow(1);
        for (int j = 0; j < col; j++) {
            Cell cell = sheetRow.createCell(j);
            switch (j) {
                case 0:
                    cell.setCellValue("分析任务id");
                    break;
                case 1:
                    cell.setCellValue("标识id");
                    break;
                case 2:
                    cell.setCellValue("比对相似度");
                    break;
                case 3:
                    cell.setCellValue("名单库1名称（库id）");
                    break;
                case 4:
                    cell.setCellValue("名单库2名称（库id）");
                    break;
                case 5:
                    cell.setCellValue("人员1id");
                    break;
                case 6:
                    cell.setCellValue("人员2id");
                    break;
                case 7:
                    cell.setCellValue("人员1图片");
                    break;
                case 8:
                    cell.setCellValue("人员2图片");
                    break;
                case 9:
                    cell.setCellValue("人员1姓名");
                    break;
                case 10:
                    cell.setCellValue("人员2姓名");
                    break;
                case 11:
                    cell.setCellValue("人员1身份证ID");
                    break;
                case 12:
                    cell.setCellValue("人员2身份证ID");
                    break;
                case 13:
                    cell.setCellValue("比对时间");
                    break;
                case 14:
                    cell.setCellValue("备注");
                    break;
                case 15:
                    cell.setCellValue("编辑人id");
                    break;
                case 16:
                    cell.setCellValue("编辑人姓名");
                    break;
                case 17:
                    cell.setCellValue("最后编辑时间");
                    break;
            }
        }
        // 内容
        JSONArray hits = obj.getJSONArray("list");
        for (int i = 0; i + 2 < row; i++) {
            sheetRow = sheet.createRow(i + 2);
            for (int j = 0; j < col; j++) {
                Cell cell = sheetRow.createCell(j);
                switch (j) {
                    case 0:
                        String taskId = ((JSONObject) (hits.get(i))).getString("task_id");
                        cell.setCellValue(taskId);
                        break;
                    case 1:
                        String id = ((JSONObject) (hits.get(i))).getString("id");
                        cell.setCellValue(id);
                        break;
                    case 2:
                        double similarity;
                        similarity = ((JSONObject) (hits.get(i))).getFloatValue("compare_sim");
                        similarity = similarity * 100;
                        DecimalFormat decimalFormat = new DecimalFormat(".00");
                        String sim = decimalFormat.format(similarity);
                        sim = sim + "%";
                        cell.setCellValue(sim);
                        break;
                    case 3:
                        String lib1Id = ((JSONObject) (hits.get(i))).getString("lib_id1");
                        String nameId1 = libName1 + "(" + lib1Id + ")";
                        cell.setCellValue(nameId1);
                        break;
                    case 4:
                        String lib2Id = ((JSONObject) (hits.get(i))).getString("lib_id2");
                        String nameId2 = libName2 + "(" + lib2Id + ")";
                        cell.setCellValue(nameId2);
                        break;
                    case 5:
                        String personId1 = ((JSONObject) (hits.get(i))).getString("person_id1");
                        cell.setCellValue(personId1);
                        break;
                    case 6:
                        String personId2 = ((JSONObject) (hits.get(i))).getString("person_id2");
                        cell.setCellValue(personId2);
                        break;
                    case 7:
                        String img1 = ((JSONObject) (hits.get(i))).getString("person1_img_url");
                        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                        String formulaa = "=HYPERLINK(\"" + img1 + "\",\"查看图片\")";
                        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                        cell.setCellFormula(formulaa);
                        break;
                    case 8:
                        String img2 = ((JSONObject) (hits.get(i))).getString("person2_img_url");
                        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                        String formulab = "=HYPERLINK(\"" + img2 + "\",\"查看图片\")";
                        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                        cell.setCellFormula(formulab);
                        break;
                    case 9:
                        String personName1 = ((JSONObject) (hits.get(i))).getString("person_name1");
                        cell.setCellValue(personName1);
                        break;
                    case 10:
                        String personName2 = ((JSONObject) (hits.get(i))).getString("person_name2");
                        cell.setCellValue(personName2);
                        break;
                    case 11:
                        String card1 = ((JSONObject) (hits.get(i))).getString("card_id1");
                        cell.setCellValue(card1);
                        break;
                    case 12:
                        String card2 = ((JSONObject) (hits.get(i))).getString("card_id2");
                        cell.setCellValue(card2);
                        break;
                    case 13:
                        String cT = ((JSONObject) (hits.get(i))).getString("compare_time");
                        if (cT == null || "".equals(cT)) {
                            cell.setCellValue("");
                        } else {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+'08:00");
                                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date compareTime = sdf.parse(cT);
                                String cpTime = f.format(compareTime);
                                cell.setCellValue(cpTime);
                            } catch (ParseException e) {
                                log.error("", e);
                            }
                        }
                        break;
                    case 14:
                        String remarkInfo = ((JSONObject) (hits.get(i))).getString("remark");
                        cell.setCellValue(remarkInfo);
                        break;
                    case 15:
                        String editId = ((JSONObject) (hits.get(i))).getString("editor_id");
                        cell.setCellValue(editId);
                        break;
                    case 16:
                        String editName = ((JSONObject) (hits.get(i))).getString("editor_name");
                        cell.setCellValue(editName);
                        break;
                    case 17:
                        String leT = ((JSONObject) (hits.get(i))).getString("last_editor_time");
                        if (leT == null || "".equals(leT)) {
                            cell.setCellValue("");
                        } else {
                            try {
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+'08:00");
                                SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date editTime = sdf2.parse(leT);
                                String edittime = f2.format(editTime);
                                cell.setCellValue(edittime);
                            } catch (ParseException e) {
                                log.error("", e);
                            }
                        }
                        break;
                }
            }
        }
    }
}
