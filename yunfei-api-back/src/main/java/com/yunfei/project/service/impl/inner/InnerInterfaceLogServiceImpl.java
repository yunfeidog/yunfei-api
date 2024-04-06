package com.yunfei.project.service.impl.inner;

import com.yunfei.project.mapper.InterfaceLogMapper;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceLog;
import com.yunfei.yunfeiapicommon.service.InnerInterfaceLogService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 接口日志服务实现类
 */
@DubboService
public class InnerInterfaceLogServiceImpl implements InnerInterfaceLogService {

    @Resource
    private InterfaceLogMapper interfaceLogMapper;


    @Override
    public boolean saveInterfaceLog(InterfaceLog interfaceLog) {
        return interfaceLogMapper.insert(interfaceLog) > 0;
    }
}
