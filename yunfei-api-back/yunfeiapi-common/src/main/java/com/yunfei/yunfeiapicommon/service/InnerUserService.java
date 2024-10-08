package com.yunfei.yunfeiapicommon.service;

import com.yunfei.yunfeiapicommon.model.entity.User;


/**
 * 内部用户服务
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户秘钥（accessKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

    /**
     * 扣除用户金币
     * @param userId
     * @param interfaceId
     * @return
     */
    boolean consumeCoins(Long userId, Long interfaceId);
}
