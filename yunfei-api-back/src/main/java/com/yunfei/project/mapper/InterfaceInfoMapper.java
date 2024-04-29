package com.yunfei.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接口信息 Mapper
 */
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {

    /**
     * 获取调用次数最多的接口信息  topNum
     *
     * @param topNum
     * @return
     */
    List<InterfaceInfo> listTopInvokeInterfaceInfo(@Param("topNum") int topNum);
}



