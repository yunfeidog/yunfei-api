package com.cxk.yunfeiapitestsdk;

import com.yunfei.yunfeiapiclientsdk.client.YunfeiApiClient;
import com.yunfei.yunfeiapiclientsdk.exception.GlobalApiException;
import com.yunfei.yunfeiapiclientsdk.model.params.UserParams;
import com.yunfei.yunfeiapiclientsdk.model.response.UserResponse;
import com.yunfei.yunfeiapiclientsdk.model.response.WeiboHotSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class YunfeiApiTestsdkApplicationTests {

    @Resource
    YunfeiApiClient yunfeiApiClient;

    @Test
    void contextLoads() {
        UserParams user = new UserParams();
        user.setUsername("admin");
        try {
            // UserResponse usernameByPost = yunfeiApiClient.getUsernameByPost(user);
            // System.out.println("测试sdk调用结果：" + usernameByPost.getUsername());
            WeiboHotSearchResponse weiboHotSearch = yunfeiApiClient.getWeiboHotSearch();
            System.out.println("测试sdk调用结果：" + weiboHotSearch);
        } catch (GlobalApiException e) {
            throw new RuntimeException(e);
        }
    }
}
