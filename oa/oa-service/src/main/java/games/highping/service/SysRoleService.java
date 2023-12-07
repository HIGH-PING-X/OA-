package games.highping.service;

import games.highping.bean.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import games.highping.utils.vo.AssginRoleVo;

import java.util.Map;

/**
* @author HIGH-
* @description 针对表【sys_role(角色)】的数据库操作Service
* @createDate 2023-12-06 15:00:52
*/
public interface SysRoleService extends IService<SysRole> {

    Map<String, Object> findRoleDataByUserId(Long userId);

    void doAssign(AssginRoleVo assginRoleVo);
}
