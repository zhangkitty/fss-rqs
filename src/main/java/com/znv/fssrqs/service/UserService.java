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

import java.util.HashMap;
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
        Map<String, Object> params = new HashMap<>();
        return mUserDao.getUser(params);
    }

    public List<HUserEntity> find() throws Exception{
        return hUserDao.findAll();
    }

    public Map<String, Object> upCfgUserlogin(Map<String, Object> params) throws BusinessException {

        List<Map<String, Object>> procedureRet = mUserDao.upCfgUserlogin(params);
        if (procedureRet.isEmpty()) {
            log.info("user {} login failed, ret list empty.", params.get("userName"));
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOGIN_ERROR);
        }

        Map<String, Object> map = procedureRet.iterator().next();
        if (map == null || !map.containsKey("ret")) {
            log.info("user {} login failed, ret map empty.", params.get("userName"));
            throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOGIN_ERROR);
        }

        int ret = Integer.valueOf(String.valueOf(map.get("ret")) );
        log.info("user {} login ret {}.", params.get("userName"), ret);
        switch (ret) {
            case 1:
                break;
            case -536861934: // 用户不存在
                throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOGIN_ERROR);
            case -536861613: // 用户被锁
                throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOCKED);
            default:
                throw new BusinessException(ErrorCodeEnum.UNAUTHED_LOGIN_ERROR);
        }

        // TODO: 写日志

        return map;
    }
}
