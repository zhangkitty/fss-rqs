package com.znv.fssrqs.param.face.compare.n.n;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 上午8:59
 */

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class NToNCompareTaskParam {

    private String TaskId;

    private String TaskName;

    private String CreateUser;

    private Long RemainningTime;

    private String ErrorMessage;

    private Integer Status;

    private Float Process;

    private Integer Lib1;

    private String Lib1Name;

    private Integer Lib2;

    private String Lib2Name;

    private Float Sim;

}
