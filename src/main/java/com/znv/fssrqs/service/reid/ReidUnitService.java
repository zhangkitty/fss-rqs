package com.znv.fssrqs.service.reid;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.MDictDao;
import com.znv.fssrqs.dao.mysql.MReidDao;
import com.znv.fssrqs.entity.mysql.InfoDeviceTypeEntity;
import com.znv.fssrqs.entity.mysql.InfoManufactureEntity;
import com.znv.fssrqs.entity.mysql.ReidUnitEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReidUnitService {

    @Autowired
    private MDictDao dictMapper;

    @Autowired
    private MReidDao reidDao;

    /**
     * 查询人体分析单元的设备类型
     * @param mapParam
     * @return
     */
    public List<InfoDeviceTypeEntity> getInfoDeviceType(Map mapParam) {
        return dictMapper.getDeviceType(mapParam);
    }

    /**
     * 查询人体分析单元的厂商
     * @param mapParam
     * @return
     */
    public List<InfoManufactureEntity> getInfoManufacture(Map mapParam) {
        return dictMapper.getManufacture(mapParam);
    }

    /**
     * 查询人体分析单元（单个或列表）
     * @param mapParam
     * @return
     */
    public JSONObject getReidUnit(Map mapParam) {
        List<ReidUnitEntity> reidList = reidDao.getReidUnit(mapParam);
        Integer totalRows = reidDao.getReidUnitCount(mapParam);

        JSONObject dataObject = new JSONObject();
        dataObject.put("List", reidList);
        dataObject.put("TotalRows", totalRows);
        return dataObject;
    }

    /**
     * 新增人体分析单元
     * @param reidUnit
     * @return
     */
    public Integer addReidUnit(JSONObject reidUnit) {
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("DeviceID", null);
        mapParam.put("DeviceName", reidUnit.getString("DeviceName"));
        mapParam.put("PrecinctID", "010100000");
        mapParam.put("DeviceKind", 91);
        mapParam.put("DeviceType", reidUnit.getIntValue("DeviceType"));
        mapParam.put("ManufactureID", reidUnit.getIntValue("ManufactureID"));
        mapParam.put("UpServerID", null);
        mapParam.put("HttpPort", reidUnit.getIntValue("HttpPort"));
        mapParam.put("ServiceIP", reidUnit.getString("ServiceIP"));
        mapParam.put("Capacity", reidUnit.getIntValue("Capacity"));
        mapParam.put("Version", reidUnit.getString("Version"));
        mapParam.put("InterfaceDescribe", null);

        return reidDao.upReidAnalysisUnitSave(mapParam);
    }

    /**
     * 修改人体分析单元
     * @param reidUnit
     * @return
     */
    public Integer updateReidUnit(JSONObject reidUnit) {
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("DeviceID", reidUnit.getString("DeviceID"));
        mapParam.put("DeviceName", reidUnit.getString("DeviceName"));
        mapParam.put("PrecinctID", "010100000");
        mapParam.put("DeviceKind", 91);
        mapParam.put("DeviceType", reidUnit.getIntValue("DeviceType"));
        mapParam.put("ManufactureID", reidUnit.getIntValue("ManufactureID"));
        mapParam.put("HttpPort", reidUnit.getIntValue("HttpPort"));
        mapParam.put("ServiceIP", reidUnit.getString("ServiceIP"));
        mapParam.put("Capacity", reidUnit.getIntValue("Capacity"));
        mapParam.put("Version", reidUnit.getString("Version"));
        mapParam.put("InterfaceDescribe", null);

        return reidDao.upReidAnalysisUnitSave(mapParam);
    }


    /**
     * 删除人体分析单元
     * @param mapParam
     * @return
     */
    public Integer deleteReidUnit(Map mapParam) {
        return reidDao.upReidAnalysisUnitDelete(mapParam);
    }
}
