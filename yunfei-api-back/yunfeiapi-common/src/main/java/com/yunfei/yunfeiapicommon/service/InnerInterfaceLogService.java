package com.yunfei.yunfeiapicommon.service;

import com.yunfei.yunfeiapicommon.model.entity.InterfaceLog;

/**
 * 接口调用日志
 */
public interface InnerInterfaceLogService {
    /**
     * 存储日志
     */
    boolean saveInterfaceLog(InterfaceLog interfaceLog);
}
