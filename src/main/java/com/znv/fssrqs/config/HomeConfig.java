package com.znv.fssrqs.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongzelong on  2019/9/4 11:34.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Configuration
@Data
public class HomeConfig {
    @Value("${conf.server.bussiness-and-access:8}")
    private Integer bussinessAndAccess;
    @Value("${conf.server.bussiness-and-access:4}")
    private Integer faceAIUnit;
    @Value("${conf.server.big-data:3}")
    private Integer bigData;
    @Value("${conf.server.machine.bussiness:1}")
    private Integer bussinessMachine;
    @Value("${conf.server.machine.face-ai-unit:1}")
    private Integer faceAIUnitMachine;
    @Value("${conf.server.machine.big_data_engine:1}")
    private Integer bigDataEngineMachine;
}
