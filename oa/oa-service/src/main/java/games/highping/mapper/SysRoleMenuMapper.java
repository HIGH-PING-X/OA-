package games.highping.mapper;

import games.highping.bean.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author HIGH-
* @description 针对表【sys_role_menu(角色菜单)】的数据库操作Mapper
* @createDate 2023-12-08 16:44:18
* @Entity games.highping.bean.SysRoleMenu
*/

@Mapper
@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

}




