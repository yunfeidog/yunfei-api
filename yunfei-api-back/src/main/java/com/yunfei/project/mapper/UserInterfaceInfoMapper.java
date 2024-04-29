package com.yunfei.project.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunfei.yunfeiapicommon.model.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户接口信息 Mapper
 *
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(@Param("limit") int limit);
}




