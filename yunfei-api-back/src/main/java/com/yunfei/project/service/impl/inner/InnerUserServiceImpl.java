package com.yunfei.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunfei.project.common.ErrorCode;
import com.yunfei.project.exception.BusinessException;
import com.yunfei.project.mapper.UserMapper;
import com.yunfei.project.service.InterfaceInfoService;
import com.yunfei.project.service.UserService;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceInfo;
import com.yunfei.yunfeiapicommon.model.entity.User;
import com.yunfei.yunfeiapicommon.service.InnerUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部用户服务实现类
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean consumeCoins(Long userId, Long interfaceId) {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        Long consumeCoins = interfaceInfo.getConsumeCoins();
        User user = userMapper.selectById(userId);
        user.setRemainCoins(user.getRemainCoins() - consumeCoins);
        return userMapper.updateById(user) > 0;
    }
}
