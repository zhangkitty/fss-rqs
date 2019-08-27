package com.znv.fssrqs.util.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.EventDao;
import com.znv.fssrqs.util.SpringContextUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dongzelong on 2017/7/7 16:33.
 *
 * @author dongzelong
 * @version 1.0
 */
public class CellDesc {
    //索引号
    public final int idx;
    //标题
    private final String title;
    private final ICellGetter getter;
    public boolean isSpecial;

    CellDesc(String title, int idx, ICellGetter getter, boolean isSpecial) {
        this.title = title;
        this.idx = idx;
        this.getter = getter;
        this.isSpecial = isSpecial;
    }

    public void setValue(Row row, JSONObject jsonObject, Locale locale) {
        Cell cell = row.createCell(idx);
        cell.setCellValue(getter.get(jsonObject, locale));
    }

    public void setRowNo(Row row, ResultSet rs, Locale locale) {
        Cell cell = row.createCell(idx);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        CellStyle cellStyle = cell.getCellStyle();
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(String.valueOf(row.getRowNum()));
    }


    public void setWrapperValue(Row row, JSONObject jsonObject, Locale locale, JSONArray features, Map<String, Map<String, Object>> eventMap) {
        Cell cell = row.createCell(idx);
        if (getter instanceof StringCellGetter) {
            switch (((StringCellGetter) getter).getColumn()) {
                case "CameraName":
                    String camera_name = jsonObject.getString("CameraName");
                    if (camera_name == null) {
                        camera_name = "--";
                    }
                    cell.setCellValue(camera_name);
                    break;
                case "SmallPictureUrl":
                    String rt_image_data = jsonObject.getString("SmallPictureUrl");
                    String flag = jsonObject.getString("AlarmType");
                    if (!flag.equals("2")) {
                        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                        String formula2 = "=HYPERLINK(\"" + rt_image_data + "\",\"查看图片\")";
                        cell.setCellFormula(formula2);
                    } else {
                        cell.setCellValue("--");
                    }
                    break;
                case "BigPictureUrl":
                    String big_img = jsonObject.getString("BigPictureUrl");
                    String formula3 = "";
                    if (!("").equals(big_img) && big_img != null) {
                        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                        formula3 = "=HYPERLINK(\"" + big_img + "\",\"查看图片\")";
                        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                        cell.setCellFormula(formula3);
                    } else {
                        formula3 = "--";
                        cell.setCellValue(formula3);
                    }
                    break;
                case "Similarity/Score":
                    double similarity;
                    if (features.size() > 0) {
                        similarity = jsonObject.getFloatValue("Similarity");
                    } else {
                        similarity = jsonObject.getFloatValue("Score");
                    }
                    similarity = similarity * 100;
                    if (similarity == 200) {
                        cell.setCellValue("--");
                    } else {
                        DecimalFormat decimalFormat = new DecimalFormat(".00");
                        String sim = decimalFormat.format(similarity);
                        sim = sim + "%";
                        cell.setCellValue(sim);
                    }
                    break;
                case "PersonImg":
                    String person_img = jsonObject.getString("PersonImg");
                    cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                    String formula5 = "=HYPERLINK(\"" + person_img + "\",\"查看图片\")";
                    cell.setCellFormula(formula5);
                    break;
                case "AlarmType":
                    String alarm_type = jsonObject.getString("AlarmType");
                    if ("0".equals(alarm_type)) {
                        alarm_type = "正常数据";
                    } else if ("1".equals(alarm_type)) {
                        alarm_type = "小孩独自出门";
                    } else if ("2".equals(alarm_type)) {
                        alarm_type = "老人未出门";
                    } else if ("3".equals(alarm_type)) {
                        alarm_type = "实时比对告警";
                    } else {
                        alarm_type = "";
                    }
                    cell.setCellValue(alarm_type);
                    break;
                case "OpTime":
                    String op_time = jsonObject.getString("OpTime");
                    cell.setCellValue(op_time);
                    break;
                case "ControlEventID":
                    String lib_name = jsonObject.getString("ControlEventID");
                    if (!StringUtils.isEmpty(lib_name)) {
                        Map<String, Object> curEvent = eventMap.getOrDefault(lib_name, null);
                        if (Objects.nonNull(curEvent)) {
                            cell.setCellValue((String) curEvent.getOrDefault("LibName", ""));
                        } else {
                            cell.setCellValue("");
                        }
                    } else {
                        cell.setCellValue("");
                    }
                    break;
                case "ControlCommunityId":
                    String controlUnit = jsonObject.getString("ControlCommunityId");
                    if (controlUnit == null) {
                        controlUnit = "--";
                    }
                    cell.setCellValue(controlUnit);
                    break;
                case "ControlPoliceCategory":
                    String controlPoliceType = jsonObject.getString("ControlPoliceCategory");
                    String controlPoliceName = null;
                    if (controlPoliceType == null || "".equals(controlPoliceType)) {
                        controlPoliceName = "--";
                    } else {
                        controlPoliceName = AlarmXlsOutput.allPoliceTypes.getString(controlPoliceType);
                    }
                    cell.setCellValue(controlPoliceName);
                    break;
                case "ControlPersonName":
                    String controlName = jsonObject.getString("ControlPersonName");
                    if (controlName == null) {
                        controlName = "--";
                    }
                    cell.setCellValue(controlName);
                    break;
                case "ControlStartTime/ControlEndTime":
                    String startTime = jsonObject.getString("ControlStartTime");
                    String endTime = jsonObject.getString("ControlEndTime");
                    String controlTime = "--";
                    if (startTime != null && endTime != null) {
                        String st = LocalDateTime
                                .parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String et = LocalDateTime
                                .parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        controlTime = st + " - " + et;
                    }
                    cell.setCellValue(controlTime);
                    break;
                case "Comment":
                    String comment = jsonObject.getString("Comment");
                    if (comment == null) {
                        comment = "--";
                    }
                    cell.setCellValue(comment);
                    break;
                default:
                    cell.setCellValue(jsonObject.getString(((StringCellGetter) getter).getColumn()));
            }
        } else {
            cell.setCellValue(getter.get(jsonObject, locale));
        }
    }
}
