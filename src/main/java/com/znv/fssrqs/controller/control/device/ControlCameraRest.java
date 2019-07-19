package com.znv.fssrqs.controller.control.device;

import com.znv.fssrqs.enums.ErrorCodeEnum;
import com.znv.fssrqs.exception.BusinessException;
import com.znv.fssrqs.service.control.device.ControlCameraService;
import com.znv.fssrqs.service.control.device.dto.CameraControlDTO;
import com.znv.fssrqs.service.control.device.dto.CameraUnDeployDTO;
import com.znv.fssrqs.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(produces = { "application/json;charset=UTF-8" })
public class ControlCameraRest {

    @Autowired
    private ControlCameraService controlCameraService;

    /**
     * 新增布控
     * @param cameraControlDto
     * @return
     */
    @PostMapping("/CameraControl")
    public ResponseVo addControl(@Valid @RequestBody CameraControlDTO cameraControlDto){
        return ResponseVo.success("布控成功",controlCameraService.deployControl(cameraControlDto));
    }

    /**
     * 修改布控
     * @param cameraControlDto
     * @return
     */
    @PutMapping("/CameraControl")
    public ResponseVo editControl(@Valid @RequestBody CameraControlDTO cameraControlDto){
        if(cameraControlDto.getId()==null || cameraControlDto.getId().trim()==""){
            throw new BusinessException(ErrorCodeEnum.PARAM_ILLEGAL);
        }
        if(cameraControlDto.getCameraId()==null||cameraControlDto.getCameraId()==""){
            throw new BusinessException(ErrorCodeEnum.PARAM_ILLEGAL);
        }
        return ResponseVo.success("更新成功",controlCameraService.editDeployControl(cameraControlDto));
    }

    /**
     * 删除布控
     * @param cameraUnDeployDTO
     * @return
     */
    @DeleteMapping("/CameraControl")
    public ResponseVo deleteControl(@RequestBody CameraUnDeployDTO cameraUnDeployDTO){
        controlCameraService.unDeployControl(cameraUnDeployDTO);
        return ResponseVo.success(null);
    }
}
