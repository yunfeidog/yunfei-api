package com.yunfei.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunfei.project.annotation.AuthCheck;
import com.yunfei.project.common.BaseResponse;
import com.yunfei.project.common.ErrorCode;
import com.yunfei.project.common.ResultUtils;
import com.yunfei.project.exception.BusinessException;
import com.yunfei.project.mapper.UserInterfaceInfoMapper;
import com.yunfei.project.model.vo.InterfaceInfoVO;
import com.yunfei.project.service.InterfaceInfoService;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;
import com.yunfei.yunfeiapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        final int topNum = 3;
        List<InterfaceInfo> list = interfaceInfoService.listTopInvokeInterfaceInfo(topNum);
        if (CollectionUtils.isEmpty(list)) {
            return ResultUtils.success(Collections.emptyList());
        }
        // 转为VO
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVOList);
    }
}
