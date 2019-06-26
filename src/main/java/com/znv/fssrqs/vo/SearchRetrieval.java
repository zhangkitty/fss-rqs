package com.znv.fssrqs.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by dongzelong on  2019/6/26 11:07.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Data
public class SearchRetrieval {
    private List<String> OfficeIDs;
    private List<String> CameraIDs;
    private Integer MinimumShouldMatch;
    private String StartTime;
    private String EndTime;
    private List<Integer> EventIDs;
    private Integer CurrentPage;
    private Integer PageSize;
    private String SortField;
    private String SortOrder;
    //是否计算相似度
    private boolean IsCalcSim;
    //特征值列表
    private List<String> Features;
    private int SimilarityDegree;
}
