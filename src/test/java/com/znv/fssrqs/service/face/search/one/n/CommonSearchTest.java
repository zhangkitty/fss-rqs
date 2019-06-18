package com.znv.fssrqs.service.face.search.one.n;

import com.znv.fssrqs.param.face.search.one.n.GeneralSearchParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonSearchTest {

    @Autowired
    private CommonSearch commonSearch;

    @Test
    public void commonSearch() throws IOException {
        GeneralSearchParam generalSearchParam = new GeneralSearchParam();

        generalSearchParam.setBeginTime("2010-01-01 23:59:59");
        generalSearchParam.setEndTime("2020-01-01 23:59:59");
        generalSearchParam.setPageNum(1);
        generalSearchParam.setPageSize(10);


        commonSearch.commonSearch(generalSearchParam);
    }
}