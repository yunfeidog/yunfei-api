package com.yunfei.yunfeiapiinterface;

import com.yunfei.yunfeiapiclientsdk.client.YunfeiApiClient;
import com.yunfei.yunfeiapiclientsdk.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 测试类
 */
@SpringBootTest
class yunfeiApiInterfaceApplicationTests {

    @Resource
    private YunfeiApiClient yunfeiapiClient;

    @Test
    void contextLoads() {
    }
}
