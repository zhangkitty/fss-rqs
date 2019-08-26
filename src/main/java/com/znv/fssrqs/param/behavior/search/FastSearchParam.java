package com.znv.fssrqs.param.behavior.search;

import lombok.Data;

import java.util.List;

/**
 * Created by dongzelong on  2019/8/22 15:34.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Data
public class FastSearchParam {
    private String StartTime;
    private String EndTime;
    private List<String> OfficeIDs;
    private List<String> CameraIDs;
    private List<String> Features;
    private boolean IsLopq;
    private String SortField;
    private String SortOrder;
    private int CoarseCodeNum = 3;
    private int CurrentPage;
    private int PageSize;
    private float SimThreshold;
    private String FilterType;
}
