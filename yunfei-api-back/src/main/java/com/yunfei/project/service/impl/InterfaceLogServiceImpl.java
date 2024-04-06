package com.yunfei.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunfei.yunfeiapicommon.model.entity.InterfaceLog;
import com.yunfei.project.service.InterfaceLogService;
import com.yunfei.project.mapper.InterfaceLogMapper;
import org.springframework.stereotype.Service;

/**
* @author houyunfei
* @description 针对表【interface_log(接口调用记录表)】的数据库操作Service实现
* @createDate 2024-04-05 19:50:39
*/
@Service
public class InterfaceLogServiceImpl extends ServiceImpl<InterfaceLogMapper, InterfaceLog>
    implements InterfaceLogService{

}




