package games.highping.service;

import games.highping.bean.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HIGH-
* @description 针对表【sys_user(用户表)】的数据库操作Service
* @createDate 2023-12-07 16:28:23
*/
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    SysUser getByUsername(String username);
}
