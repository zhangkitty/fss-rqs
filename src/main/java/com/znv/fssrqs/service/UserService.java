package com.znv.fssrqs.service;

import com.znv.fssrqs.dao.hbase.HUserDao;
import com.znv.fssrqs.dao.mysql.MUserDao;
import com.znv.fssrqs.entity.hbase.HUserEntity;
import com.znv.fssrqs.entity.mysql.MUserEntity;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
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

    public Map<String, Object> upCfgUserLogin(Map<String, Object> params) throws BusinessException {
        List<Map<String, Object>> procedureRet = mUserDao.upCfgUserLogin(params);
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
        Map<String, Object> logParams = new HashMap<>();
        logParams.put("userId", map.get("user_id"));
        logParams.put("deviceId", params.get("serverId"));
        logParams.put("accessStyle", 1);
        logParams.put("accessTime", params.get("loginTime"));
        logParams.put("accessDescription", "Login System success username="+params.get("userName"));
        logParams.put("remoteHost", params.get("clientIp"));
        logParams.put("detailStyle", 1);
        logParams.put("loginClientType", 1);
        logParams.put("logState", 0); // 0 成功
        logParams.put("logParam", "");
        logParams.put("deviceName", "");
        mUserDao.upCfgSaveSystemLog(logParams);

        return map;
    }


    public void upCfgUserLogout(Map<String, Object> params) throws BusinessException {
        mUserDao.upCfgUserLogout(params);

        Map<String, Object> logParams = new HashMap<>();
        logParams.put("userId", params.get("userId"));
        logParams.put("deviceId", params.get("serverId"));
        logParams.put("accessStyle", 1);
        logParams.put("accessTime", params.get("logoutTime"));
        logParams.put("accessDescription", "Logout System success username="+params.get("userName"));
        logParams.put("remoteHost", params.get("clientIp"));
        logParams.put("detailStyle", 7);
        logParams.put("loginClientType", 1);
        logParams.put("logState", 0); // 0 成功
        logParams.put("logParam", "");
        logParams.put("deviceName", "");
        mUserDao.upCfgSaveSystemLog(logParams);
    }
}
