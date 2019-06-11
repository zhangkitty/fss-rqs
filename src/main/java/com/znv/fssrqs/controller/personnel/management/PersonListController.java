package com.znv.fssrqs.controller.personnel.management;

import com.znv.fssrqs.param.personnel.management.PersonListSearchParams;
import com.znv.fssrqs.service.personnel.management.PersonListService;
import com.znv.fssrqs.vo.Response;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author zhangcaochao
 * @Description TODO
 * @Date 2019.06.10 下午3:05
 */

@RestController
@RequestMapping(produces = { "application/json;charset=UTF-8" })
public class PersonListController {

    @Autowired
    private PersonListService personListService;

    @GetMapping(value="/VIID/Persons")
    public Response getPersonList(@Validated PersonListSearchParams personListSearchParams) throws IOException {
        System.out.println(personListSearchParams.toString());
        JSONObject jsonObject = personListService.getPersonList();
        return Response.success(jsonObject);
    }
}
