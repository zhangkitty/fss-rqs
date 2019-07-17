package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.hbase.HUserDao;
import com.znv.fssrqs.dao.mysql.MUserDao;
import com.znv.fssrqs.entity.hbase.HUserEntity;
import com.znv.fssrqs.entity.mysql.MUserEntity;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    @Autowired
    private MUserDao mUserDao;

    @Autowired
    private HUserDao hUserDao;

    public List<MUserEntity> findAll() throws Exception{
        return mUserDao.findAll();
    }

    public List<HUserEntity> find() throws Exception{
        return hUserDao.findAll();
    }

    public Map<String, Object> upCfgUserlogin(Map<String, Object> params) throws BusinessException {
        List<Map<String, Object>> procedureRet = mUserDao.upCfgUserlogin(params);
        if (procedureRet.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOGIN_ERROR);
        }

        Map<String, Object> map = procedureRet.iterator().next();
        if (map == null || !map.containsKey("ret")) {
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOGIN_ERROR);
        }

        int ret = Integer.valueOf(String.valueOf(map.get("ret")) );
        switch (ret) {
            case 5: // 用户不存在
                throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOGIN_ERROR);
            case 6: // 用户超过最大登录数
                throw new BusinessException(ErrorCodeEnum.UNAUTHED_MAX_FAILED_TIMES);
            case 17: // 用户被锁
                throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOCKED);
        }

        // TODO: 写日志

        return map;
    }
}
