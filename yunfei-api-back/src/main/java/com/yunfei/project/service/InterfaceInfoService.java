package com.yunfei.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;

import java.util.List;

/**
 * 接口信息服务
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 获取调用次数最多的接口信息  topNum
     * @param topNum
     * @return
     */
    List<InterfaceInfo> listTopInvokeInterfaceInfo(int topNum);
}
