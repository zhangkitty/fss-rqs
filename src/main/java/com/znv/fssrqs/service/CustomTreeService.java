package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.mysql.CustomTreeMapper;
import com.znv.fssrqs.entity.mysql.CustomTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dongzelong on  2019/8/1 11:40.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Service
public class CustomTreeService {
    @Autowired
    private CustomTreeMapper customTreeMapper;

    public CustomTree getTree(String treeId) {
        return customTreeMapper.selectByPrimaryKey(treeId);
    }
}
