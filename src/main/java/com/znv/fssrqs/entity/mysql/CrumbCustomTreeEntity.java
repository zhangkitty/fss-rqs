package com.znv.fssrqs.entity.mysql;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午7:59
 */

@Data
public class CrumbCustomTreeEntity {

    private Integer id;

    private Integer parentId;

    private String crumb;

    private String nodeId;

    private String nodeName;

    private String nodeDesc;

    private Boolean isLeaf;

    private Boolean isDel;

    private Boolean isDefault;

}
