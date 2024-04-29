package com.yunfei.yunfeiapicommon.service;

import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;

/**
 * 内部接口信息服务
 */
public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

    /**
     * 该接口的总调用次数+1
     * @param interfaceId
     * @return
     */
    boolean invokeCount(Long interfaceId);
}
