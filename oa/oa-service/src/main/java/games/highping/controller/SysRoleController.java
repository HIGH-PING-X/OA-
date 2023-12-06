package games.highping.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.SysRole;
import games.highping.utils.result.Result;
import games.highping.service.SysRoleService;
import games.highping.utils.vo.SysRoleQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "分页查询角色")
    @GetMapping("{page}/{limit}")
    public Result pageQuery(@PathVariable Integer page, @PathVariable Integer limit, SysRoleQueryVo sysRoleQueryVo) {
        Page<SysRole> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        sysRoleService.page(pageParam, wrapper);
        IPage<SysRole> iPage = sysRoleService.page(pageParam, wrapper);
        return Result.ok(iPage);
    }

    @ApiOperation(value = "添加角色")
    @GetMapping("/save")
    public Result save(SysRole sysRole) {
        boolean success = sysRoleService.save(sysRole);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

}
