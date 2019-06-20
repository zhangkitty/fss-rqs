package com.znv.fssrqs.dao.mysql;


import com.znv.fssrqs.entity.mysql.PersonLib;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dongzelong on  2019/6/4 14:11.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public interface PersonLibMapper {
    int deleteByPrimaryKey(Integer libId);

    int insert(PersonLib record);

    int insertSelective(PersonLib record);

    PersonLib selectByPrimaryKey(Integer libId);

    int updateByPrimaryKeySelective(PersonLib record);

    int updateByPrimaryKey(PersonLib record);

    List<PersonLib> queryAll();

    List<PersonLib> queryLibByLibType(PersonLib record);

    HashMap<String, Object> deleteByLibId(Integer libId);

    HashMap<String, Object> save(PersonLib personLib);
}
