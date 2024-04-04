package com.yunfei.project.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 用户接口信息 Mapper
 *
 *  
 *   
 */
public interface UserInterfaceInfoMapper extends BaseMapper<com.yunfei.yunfeiapicommon.model.entity.UserInterfaceInfo> {

    List<com.yunfei.yunfeiapicommon.model.entity.UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




