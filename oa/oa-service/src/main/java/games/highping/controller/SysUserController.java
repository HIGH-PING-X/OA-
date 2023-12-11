package games.highping.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.SysUser;
import games.highping.service.SysUserService;
import games.highping.utils.encryption.MD5;
import games.highping.utils.result.Result;
import games.highping.utils.vo.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author jerry
 * @since 2023-03-01
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户条件分页查询
     *
     * @param page
     * @param pageSize
     * @param sysUserQueryVo
     * @return
     */
    @ApiOperation("用户条件分页查询")
    @GetMapping("/{page}/{pageSize}")
    public Result page(@PathVariable int page, @PathVariable int pageSize, SysUserQueryVo sysUserQueryVo) {
        Page<SysUser> sysUserPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 获取条件
        String userName = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        // 判断条件值不为空
        if (!StringUtils.isEmpty(userName)){
            lambdaQueryWrapper.like(SysUser::getUsername,userName);
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            lambdaQueryWrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            lambdaQueryWrapper.le(SysUser::getCreateTime,createTimeEnd);
        }

        sysUserService.page(sysUserPage,lambdaQueryWrapper);

        return Result.ok(sysUserPage);
    }

    /**
     * 获取用户
     * @param id
     * @return
     */
    @ApiOperation("获取用户")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable long id){
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    /**
     * 更新用户
     * @param sysUser
     * @return
     */
    @ApiOperation("更新用户")
    @PutMapping("/update")
    public Result update(@RequestBody SysUser sysUser){
        boolean is_success = sysUserService.updateById(sysUser);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 保存用户
     * @param sysUser
     * @return
     */
    @ApiOperation("添加用户")
    @PostMapping("/save")
    public Result save(@RequestBody SysUser sysUser){
        // 密码加密
        String passwordMD5 = MD5.encrypt(sysUser.getPassword());
        sysUser.setPassword(passwordMD5);
        boolean is_success = sysUserService.save(sysUser);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable long id){
        boolean is_success = sysUserService.removeById(id);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "更新状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status){
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }

}
