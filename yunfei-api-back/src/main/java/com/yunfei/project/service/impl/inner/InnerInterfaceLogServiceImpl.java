package com.yunfei.project.service.impl.inner;

import com.yunfei.yunfeiapicommon.model.entity.InterfaceLog;
import com.yunfei.yunfeiapicommon.service.InnerInterfaceLogService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 接口日志服务实现类
 */
@DubboService
public class InnerInterfaceLogServiceImpl implements InnerInterfaceLogService {


    @Override
    public boolean save(InterfaceLog interfaceLog) {
        return this.save(interfaceLog);
    }
}
