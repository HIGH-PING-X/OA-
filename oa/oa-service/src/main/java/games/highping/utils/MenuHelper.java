package games.highping.utils;

import games.highping.bean.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> list) {

        List<SysMenu> trees = new ArrayList<>();
        for (SysMenu sysMenu : list) {
            if (sysMenu.getParentId().longValue() == 0) {
                trees.add(getChildren(sysMenu, list));
            }
        }
    return trees;
    }

    public static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> list) {
        sysMenu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu menu : list) {
            if (sysMenu.getId().longValue() == menu.getParentId().longValue()) {
                sysMenu.getChildren().add(getChildren(menu, list));
            }
        }
        return sysMenu;
    }

}
