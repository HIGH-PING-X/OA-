package games.highping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.OaProcessRecord;
import games.highping.bean.SysUser;
import games.highping.security.LoginUserInfoHelper;
import games.highping.service.OaProcessRecordService;
import games.highping.mapper.OaProcessRecordMapper;
import games.highping.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author HIGH-
* @description 针对表【oa_process_record(审批记录)】的数据库操作Service实现
* @createDate 2024-01-04 16:53:11
*/
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, OaProcessRecord> implements OaProcessRecordService{

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void record(Long processId, Integer status, String description) {
        OaProcessRecord oaProcessRecord = new OaProcessRecord();
        oaProcessRecord.setProcessId(processId);
        oaProcessRecord.setStatus(status);
        oaProcessRecord.setDescription(description);
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
        oaProcessRecord.setOperateUser(sysUser.getName());
        oaProcessRecord.setOperateUserId(userId);
        baseMapper.insert(oaProcessRecord);
    }
}




