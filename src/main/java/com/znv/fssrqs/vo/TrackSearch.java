package com.znv.fssrqs.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by dongzelong on  2019/7/31 13:15.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Data
public class TrackSearch {
    private String StartTime;
    private String EndTime;
    private List<String> CameraIDs;
    private Integer PageSize;
    private Integer CurrentPage;
    private Integer OrderType;
    private Integer TotalRows;
    private Integer TotalPage;
    private Integer QueryType;
    private List<String> OfficeIDs;
}
