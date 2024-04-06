package com.yunfei.yunfeiapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 签名工具
 */
public class SignUtils {
    /**
     * 生成签名
     *
     * @param body      请求体
     * @param secretKey 秘钥
     * @return 签名
     */
    public static String genSign(String body, String secretKey) {
        return DigestUtil.sha256Hex(body + "." + secretKey);
    }
}
