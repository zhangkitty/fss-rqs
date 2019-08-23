package com.znv.fssrqs.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Created by dongzelong on  2019/8/23 10:30.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DiskInfo {
    //分区所在IP地址
    private String IP;
    //分区名
    private String PartitionName;
    //总KB
    private long Total;
    //已使用KB
    private long Used;
    //可使用KB
    private long Available;
    //使用率
    private String Utilization;
}
