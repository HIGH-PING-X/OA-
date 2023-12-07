package games.highping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.SysUser;
import games.highping.service.SysUserService;
import games.highping.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author HIGH-
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2023-12-07 16:28:23
*/
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        // 根据用户 userid 查询用户对象
        SysUser sysUser = baseMapper.selectById(id);

        // 设置修改状态
        if (status == 0 || status == 1) {
            sysUser.setStatus(status);
        } else {
            log.info("数值不合法");
        }

        // 调用方法进行修改
        baseMapper.updateById(sysUser);
    }
    
}




