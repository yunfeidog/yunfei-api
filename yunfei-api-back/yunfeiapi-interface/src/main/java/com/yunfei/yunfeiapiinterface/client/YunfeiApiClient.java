package com.yunfei.yunfeiapiinterface.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yunfei.yunfeiapiclientsdk.model.User;
import com.yunfei.yunfeiapiinterface.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

public class YunfeiApiClient {
    public static void main(String[] args) {
        String accessKey = "yunfei";
        String secretKey = "abcdefgh";
        YunfeiApiClient client = new YunfeiApiClient(accessKey, secretKey);
        client.getNameByGet("yunfei");
        client.getNameByPost("yunfei");
        User user = new User();
        user.setUsername("yunfei");
        client.getUsernameByPost(user);
    }

    public String getNameByGet(String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        String res = HttpUtil.get("http://localhost:10002/api/name/get", map);
        System.out.println(res);
        return res;

    }

    public String getNameByPost(String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        String res = HttpUtil.post("http://localhost:10002/api/name/post", map);
        System.out.println(res);
        return res;
    }

    String accessKey;
    String secretKey;

    public YunfeiApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("accessKey", accessKey);
        //一定不能直接传递 secretKey
//        headerMap.put("secretKey", secretKey);
        headerMap.put("nonce", RandomUtil.randomNumbers(4));
        headerMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        headerMap.put("sign", SignUtils.genSign(body, secretKey));
        headerMap.put("body", body);

        return headerMap;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        String res = HttpRequest.post("http://localhost:10002/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json).execute()
                .body();
        System.out.println(res);
        return res;
    }
}
