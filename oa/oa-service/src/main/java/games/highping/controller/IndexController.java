package games.highping.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import games.highping.bean.SysUser;
import games.highping.service.SysMenuService;
import games.highping.service.SysUserService;
import games.highping.utils.encryption.MD5;
import games.highping.utils.exception.NoobException;
import games.highping.utils.jwt.JwtConfig;
import games.highping.utils.result.Result;
import games.highping.utils.vo.LoginVo;
import games.highping.utils.vo.RouterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("token","admin-token");
//        return Result.ok(map);

        //获取输入的用户名密码
        String username = loginVo.getUsername();
        //根据用户名查询数据库
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        //用户信息是否存在
        if (sysUser == null) {
            throw new NoobException(201, "用户不存在");
        }
        //密码是否正确
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(loginVo.getPassword());
        if(!password_db.equals(password_input)) {
            throw new NoobException(201,"密码错误");
        }
        //判断用户是否被禁用
        if(sysUser.getStatus().intValue()==0) {
            throw new NoobException(201,"用户已经被禁用");
        }
        //生成token
        String token = JwtConfig.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return Result.ok(map);
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/info")
    public Result info(HttpServletRequest request) {
        //从请求头获取用户信息
        String token = request.getHeader("token");
        //从token字符串中获取用户id
        Long userId = JwtConfig.getUserId(token);
        //根据用户id查询数据库
        SysUser sysUser = sysUserService.getById(userId);
        //根据用户id获取用户菜单权限
        List<RouterVo> routerVoList = sysMenuService.findUserMenuListByUserId(userId);
        //根据用户id获取用户按钮权限
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("roles", "[admin]");
        map.put("name", sysUser.getName());
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("routers", routerVoList);
        map.put("buttons", permsList);
        return Result.ok(map);
    }

    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public Result logout() {
        return Result.ok();
    }

}
