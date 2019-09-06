package com.znv.fssrqs.util.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.constant.CommonConstant;
import com.znv.fssrqs.dao.mysql.EventDao;
import com.znv.fssrqs.dao.mysql.LibDao;
import com.znv.fssrqs.exception.ZnvException;
import com.znv.fssrqs.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import static com.znv.fssrqs.util.file.CellDescUtils.make;

/**
 * Created by dongzelong on  2019/8/14 11:21.
 *
 * @author dongzelong
 * @version 1.0
 * @Description 战果统计数据导出
 */
@Slf4j
public class AlarmXlsOutput extends AbstractXlsOutput {
    private static final CellDesc[] ALARM_CELLS = new CellDesc[]{
            make("姓名", 0, "PersonName", false),
            make("摄像头名称", 1, "CameraName", true),
            make("抓拍小图", 2, "SmallPictureUrl", true),
            make("抓拍大图", 3, "BigPictureUrl", true),
            make("相似度", 4, "Similarity/Score", true),
            make("名单库图片", 5, "PersonImg", true),
            make("告警类型", 6, "AlarmType", true),
            make("告警比对时间", 7, "OpTime", false),
            make("所属静态库", 8, "ControlEventID", true),
            make("抓拍时间", 9, "EnterTime", false),
            make("布控单位", 10, "ControlCommunityId", false),
            make("布控警种", 11, "ControlPoliceCategory", true),
            make("布控人姓名", 12, "ControlPersonName", true),
            make("布控时间", 13, "ControlStartTime/ControlEndTime", true),
            make("说明", 14, "Comment", true)
    };

    private static String policeTypesStr = "{\"0\":\"图侦\",\"1\":\"刑侦\",\"2\":\"治安\",\"3\":\"国保\",\"4\":\"户籍\",\"5\":\"网警\",\"6\":\"经侦\",\"7\":\"派出所\",\"8\":\"缉毒\",\"9\":\"反恐\",\"10\":\"技侦\",\"11\":\"缉私\"}";
    public static JSONObject allPoliceTypes = JSON.parseObject(policeTypesStr);
    private Locale locale;
    private String params;
    private String result;

    public AlarmXlsOutput(Object... params) {
        super((HttpServletResponse) params[0]);
        this.locale = (Locale) params[1];
        this.result = (String) params[2];
        this.params = (String) params[3];
    }

    /**
     * @param sheet
     * @param rowNum
     * @param jsonObject
     * @param cells
     * @throws SQLException
     */
    private void outputRow(Sheet sheet, int rowNum, JSONObject jsonObject, CellDesc[] cells, JSONArray features, Map<String, Map<String, Object>> eventMap) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }

        for (CellDesc cell : cells) {
            if (cell.isSpecial) {
                cell.setWrapperValue(row, jsonObject, locale, features, eventMap);
            } else {
                cell.setValue(row, jsonObject, locale);
            }
        }
    }

    /**
     * 重置单元格宽度
     *
     * @param sheet
     * @param cells
     */
    private void resize(Sheet sheet, CellDesc[] cells) {
        for (CellDesc cell : cells) {
            int index = cell.idx;
            sheet.setColumnWidth(index, (int) (sheet.getColumnWidth(index) * 1.3));
        }
    }

    private void buildAlarm(Workbook workbook) {
        final Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = workbook.getCellStyleAt(0);
        cellStyle.setWrapText(true);
        final int[] startRow = new int[]{2};
        JSONObject jsonObject = JSON.parseObject(result);
        if (CommonConstant.StatusCode.OK == jsonObject.getInteger("Code")) {
            JSONObject dataObject = jsonObject.getJSONObject("Data");
            Row firstRow = sheet.getRow(0);
            if (firstRow == null) {
                firstRow = sheet.createRow(0);
            }
            Cell firstCell = firstRow.createCell(0);
            firstCell.setCellValue("总条数");
            Cell secondCell = firstRow.createCell(1);
            secondCell.setCellValue(dataObject.getIntValue("Total"));
            Cell thirdCell = firstRow.createCell(2);
            thirdCell.setCellValue("导出条数");
            Cell fourthCell = firstRow.createCell(3);
            createHeaderRow(sheet);
            fourthCell.setCellValue(dataObject.getIntValue("Size"));
            JSONObject paramObject = JSON.parseObject(params);
            if (!paramObject.containsKey("Features")) {
                throw ZnvException.badRequest(CommonConstant.StatusCode.BAD_REQUEST, "FeaturesIsEmpty");
            }
            final JSONArray features = paramObject.getJSONArray("Features");
            JSONArray alarmDataList = dataObject.getJSONArray("List");
            final int size = alarmDataList.size();
            //EventDao eventDao = SpringContextUtil.getCtx().getBean(EventDao.class);
            final LibDao libDao = SpringContextUtil.getCtx().getBean(LibDao.class);
            Map<String, Map<String, Object>> eventMap = libDao.selectAllMap();
            for (int index = 0; index < size; index++) {
                outputRow(sheet, startRow[0], alarmDataList.getJSONObject(index), ALARM_CELLS, features, eventMap);
                ++startRow[0];
            }
        } else {
            throw ZnvException.error(jsonObject.getInteger("Code"), jsonObject.getString("Message"));
        }
        resize(sheet, ALARM_CELLS);
    }

    @Override
    protected void build(Workbook workbook, Map<String, Object> map) {
        this.buildAlarm(workbook);
    }

    private void createHeaderRow(Sheet sheet) {
        Row sheetRow = sheet.createRow(1);
        for (int j = 0; j < 15; j++) {
            Cell cell = sheetRow.createCell(j);
            switch (j) {
                case 0:
                    cell.setCellValue("姓名");
                    break;
                case 1:
                    cell.setCellValue("摄像头名称");
                    break;
                case 2:
                    cell.setCellValue("抓拍图片");
                    break;
                case 3:
                    cell.setCellValue("现场大图");
                    break;
                case 4:
                    cell.setCellValue("相似度");
                    break;
                case 5:
                    cell.setCellValue("名单库图片");
                    break;
                case 6:
                    cell.setCellValue("告警类型");
                    break;
                case 7:
                    cell.setCellValue("告警比对时间");
                    break;
                case 8:
                    cell.setCellValue("案事件类型");
                    break;
                case 9:
                    cell.setCellValue("抓拍时间");
                    break;
                case 10:
                    cell.setCellValue("布控单位");
                    break;
                case 11:
                    cell.setCellValue("布控警种");
                    break;
                case 12:
                    cell.setCellValue("布控人姓名");
                    break;
                case 13:
                    cell.setCellValue("布控时间");
                    break;
                case 14:
                    cell.setCellValue("说明");
                    break;
            }
        }
    }
}
