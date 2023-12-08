package games.highping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.SysMenu;
import games.highping.service.SysMenuService;
import games.highping.mapper.SysMenuMapper;
import games.highping.utils.MenuHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author HIGH-
* @description 针对表【sys_menu(菜单表)】的数据库操作Service实现
* @createDate 2023-12-08 16:44:05
*/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService{

    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> list = baseMapper.selectList(null);
        List<SysMenu> resultList = MenuHelper.buildTree(list);
        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, id);
        Long count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("该菜单下存在子菜单，无法删除");
        }
        baseMapper.deleteById(id);
    }
}




