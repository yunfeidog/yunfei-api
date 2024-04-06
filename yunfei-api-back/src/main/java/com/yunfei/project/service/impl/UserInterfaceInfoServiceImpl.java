package com.yunfei.project.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunfei.project.common.ErrorCode;
import com.yunfei.project.exception.BusinessException;
import com.yunfei.project.mapper.UserInterfaceInfoMapper;
import com.yunfei.project.service.InterfaceInfoService;
import com.yunfei.project.service.UserInterfaceInfoService;
import com.yunfei.project.service.UserService;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;
import com.yunfei.yunfeiapicommon.model.entity.User;
import com.yunfei.yunfeiapicommon.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户接口信息服务实现类
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
    }

    @Transactional
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        queryWrapper.eq(UserInterfaceInfo::getUserId, userId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        if (ObjUtil.isEmpty(userInterfaceInfo)) {
            // 不存在需要添加一条
            // 校验userId和interfaceInfoId是否存在
            InterfaceInfo checkInterfaceInfo = interfaceInfoService.getById(interfaceInfoId);
            User user = userService.getById(userId);
            if (ObjUtil.isEmpty(checkInterfaceInfo) || ObjUtil.isEmpty(user)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口信息或用户不存在");
            }
            userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setTotalInvokes(0);
            userInterfaceInfo.setStatus(0);
            this.save(userInterfaceInfo);
        }
        UserInterfaceInfo userInterfaceInfo1 = new UserInterfaceInfo();
        userInterfaceInfo1.setId(userInterfaceInfo.getId());
        userInterfaceInfo1.setTotalInvokes(userInterfaceInfo.getTotalInvokes() + 1);
        boolean flag = updateById(userInterfaceInfo1);
        if (!flag) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return true;
    }
}




