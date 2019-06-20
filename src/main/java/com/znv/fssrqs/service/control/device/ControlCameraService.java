package com.znv.fssrqs.service.control.device;

import com.alibaba.fastjson.JSONObject;
import com.znv.fssrqs.dao.mysql.ControlCameraMapper;
import com.znv.fssrqs.entity.mysql.TCfgDevice;
import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.service.control.device.dto.CameraControlDTO;
import com.znv.fssrqs.service.control.device.dto.CameraUnDeployDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ControlCameraService {

    @Autowired
    private ControlCameraMapper controlCameraMapper;

    @Transactional
    public Object deployControl(CameraControlDTO cameraControlDto){
        List<String> cameraIdList = cameraControlDto.getCameraIds();
        String libId = cameraControlDto.getLibId();
        String startTime = cameraControlDto.getControlStartTime();
        String endTime = cameraControlDto.getControlEndTime();
        String title = cameraControlDto.getTitle();
        String id = cameraControlDto.getId();
        int libCountLimit = cameraControlDto.getLibCountLimit();
        int cameraCountLimit = 5;

        String cameraIds = StringUtils.join(cameraIdList, ",");
        List<Map<String, Object>> retData =controlCameraMapper.up_fss_deploy_camera(id, title, cameraIds, libId, startTime, endTime, libCountLimit, cameraCountLimit);

        List<String> successList = new ArrayList<String>();
        List<Object> failList = new ArrayList<Object>();
        retData.forEach( r->{
            String ret = r.get("ret").toString();
            String cId = r.get("camera_id").toString();

            TCfgDevice device = controlCameraMapper.listDeviceById(cId);
            String cameraName = device.getDeviceName();

            if (!cameraIdList.contains(cId)) {
                successList.add(cId);
            }
            if ("1".equals(ret) || "2".equals(ret)) {
                return;
            }
            Map<String,Object> errorObj = new HashMap<String,Object>();
            if ("0".equals(ret)) {
                StringBuffer sb = new StringBuffer("");
                sb.append(String.format("布控点位:%s,ID:%s,错误码:%s,错误信息:%s", cameraName, cId, "50000", "布控任务已存在")).append(System.getProperty("line.separator"));
                errorObj.put("Code","50000");
                errorObj.put("Message",sb);
                errorObj.put("Id",cId);
            }
            if ("3".equals(ret)) {
                StringBuffer sb = new StringBuffer("");
                sb.append(String.format("布控点位:%s,ID:%s,错误码:%s,错误信息:%s", cameraName, cId, "50000", "单个摄像头下布控任务数超过上限")).append(System.getProperty("line.separator"));
                errorObj.put("Code","50000");
                errorObj.put("Message",sb);
                errorObj.put("Id",cId);
            }
            if ("4".equals(ret)) {
                StringBuffer sb = new StringBuffer("");
                sb.append(String.format("布控点位:%s,ID:%s,错误码:%s,错误信息:%s", cameraName, cId, "50000", "布控大库的摄像头数目大于" + cameraCountLimit
                        + "个")).append(System.getProperty("line.separator"));
                errorObj.put("Code","50000");
                errorObj.put("Message",sb);
                errorObj.put("Id",cId);
            }

            failList.add(errorObj);
        });

        /*if(!cameraIdList.isEmpty()){
            ProducerBase producer = PhoenixClient.getProducer();
            cameraIdList.forEach(tid -> {
                JSONObject notifyMsg = new JSONObject();
                String tablename = ConfigManager.getString("fss.mysql.table.cameralib.name");
                notifyMsg.put("table_name", tablename);
                notifyMsg.put("msg_type", ConfigManager.getString(VConstants.NOTIFY_TOPIC_MSGTYPE));
                notifyMsg.put("reference_id", libId);
                notifyMsg.put("primary_id", tid);
                boolean ret = producer.sendData(notifyMsg);
                if(ret) {
                }else {
                }
            });
        }*/

        Map<String,Object> reData = new HashMap<>();
        reData.put("FailNum",failList.size());
        reData.put("TotalNum",cameraIdList.size());
        reData.put("SuccessNum",successList.size());
        reData.put("List",failList);

        return reData;
    }

    @Transactional
    public Object editDeployControl(CameraControlDTO cameraControlDto){
        String cameraId = cameraControlDto.getCameraId();
        String libId = cameraControlDto.getLibId();
        String startTime = cameraControlDto.getControlStartTime();
        String endTime = cameraControlDto.getControlEndTime();
        String title = cameraControlDto.getTitle();
        String id = cameraControlDto.getId();
        int libCountLimit = cameraControlDto.getLibCountLimit();
        int cameraCountLimit = 5;

        List<Map<String, Object>> retData =controlCameraMapper.up_fss_deploy_camera(id, title, cameraId, libId, startTime, endTime, libCountLimit, cameraCountLimit);

        if(retData!=null && retData.size()>0){
            Map<String,Object> r = retData.get(0);

            String ret = r.get("ret").toString();
            String cId = r.get("camera_id").toString();

            TCfgDevice device = controlCameraMapper.listDeviceById(cId);
            String cameraName = device.getDeviceName();

            if ("0".equals(ret)) {
                StringBuffer sb = new StringBuffer("");
                sb.append(String.format("布控点位:%s,ID:%s,错误码:%s,错误信息:%s", cameraName, cId, "500", "布控任务已存在")).append(System.getProperty("line.separator"));
                throw new BusinessException(ErrorCodeEnum.UNDIFINITION.getCode(),sb.toString());
            }
            if ("3".equals(ret)) {
                StringBuffer sb = new StringBuffer("");
                sb.append(String.format("布控点位:%s,ID:%s,错误码:%s,错误信息:%s", cameraName, cId, "500", "单个摄像头下布控任务数超过上限")).append(System.getProperty("line.separator"));
                throw new BusinessException(ErrorCodeEnum.UNDIFINITION.getCode(),sb.toString());
            }
            if ("4".equals(ret)) {
                StringBuffer sb = new StringBuffer("");
                sb.append(String.format("布控点位:%s,ID:%s,错误码:%s,错误信息:%s", cameraName, cId, "500", "布控大库的摄像头数目大于" + cameraCountLimit
                        + "个")).append(System.getProperty("line.separator"));
                throw new BusinessException(ErrorCodeEnum.UNDIFINITION.getCode(),sb.toString());
            }

        }
       /* ProducerBase producer = PhoenixClient.getProducer();
        JSONObject notifyMsg = new JSONObject();
        String tablename = ConfigManager.getString("fss.mysql.table.cameralib.name");
        notifyMsg.put("table_name", tablename);
        notifyMsg.put("msg_type", ConfigManager.getString(VConstants.NOTIFY_TOPIC_MSGTYPE));
        notifyMsg.put("reference_id", libId);
        notifyMsg.put("primary_id", cameraId);
        boolean ret = producer.sendData(notifyMsg);
        if(ret) {
        }else {
        }*/



        Map<String,Object> reData = new HashMap<>();
        reData.put("Id",cameraId);
        return reData;
    }

    @Transactional
    public void unDeployControl( CameraUnDeployDTO cameraUnDeployDTO){
        int i = controlCameraMapper.up_fss_undeploy_camera(cameraUnDeployDTO.getCameraIds());
        if(i==0){
            throw new BusinessException(ErrorCodeEnum.UNDIFINITION);
        }

        List<String> cameraIds = cameraUnDeployDTO.getCameraIds();
        String libId = cameraUnDeployDTO.getLibId();

        /*JSONObject notifyMsg = new JSONObject();
        String tablename = ConfigManager.getString("fss.mysql.table.cameralib.name");
        notifyMsg.put("table_name", tablename);
        notifyMsg.put("msg_type", ConfigManager.getString(VConstants.NOTIFY_TOPIC_MSGTYPE));
        notifyMsg.put("reference_id", libId);

        ProducerBase producer = PhoenixClient.getProducer();
        cameraIds.forEach(cameraid->{
            notifyMsg.put("primary_id", cameraid);
            boolean ret = producer.sendData(notifyMsg);
            if(ret) {
            }else {
            }
        });*/
    }
}
