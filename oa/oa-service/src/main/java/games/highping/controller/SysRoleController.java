package games.highping.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.SysRole;
import games.highping.service.SysUserService;
import games.highping.utils.result.Result;
import games.highping.service.SysRoleService;
import games.highping.utils.vo.AssginRoleVo;
import games.highping.utils.vo.SysRoleQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "根据id查询角色")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
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
    @PostMapping("/save")
    public Result save(@RequestBody SysRole sysRole) {
        boolean success = sysRoleService.save(sysRole);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "修改角色")
    @PutMapping("/update")
    public Result updateById(@RequestBody SysRole sysRole) {
        boolean success = sysRoleService.updateById(sysRole);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "根据id删除角色")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean success = sysRoleService.removeById(id);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "批量删除角色")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean success = sysRoleService.removeByIds(idList);
        if (success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> map = sysRoleService.findRoleDataByUserId(userId);
        return Result.ok(map);
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    @ApiOperation(value = "更新状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status){
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }

}
