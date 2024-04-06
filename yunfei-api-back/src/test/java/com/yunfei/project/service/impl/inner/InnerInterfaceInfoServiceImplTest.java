package com.yunfei.project.service.impl.inner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class InnerInterfaceInfoServiceImplTest {

    @Resource
    InnerInterfaceInfoServiceImpl innerInterfaceInfoService;

    @Test
    void getInterfaceInfo() {
        // String url = "http://localhost:10003/api/name";
        // String method = "GET";
        // innerInterfaceInfoService.getInterfaceInfo(url, method);
    }
}
