package com.znv.fssrqs.service.reid;

import com.znv.fssrqs.dao.mysql.ReidTaskDao;
import com.znv.fssrqs.entity.mysql.ReidTaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date new Date() 下午3:03
 */

@Service
public class ReidTaskService {

    @Autowired
    private ReidTaskDao reidTaskDao;

    public Integer save(ReidTaskEntity reidTaskEntity){




        reidTaskDao.sava(reidTaskEntity);
        return reidTaskEntity.getTaskId();
    }
}
