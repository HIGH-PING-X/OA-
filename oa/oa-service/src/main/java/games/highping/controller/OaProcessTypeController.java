package games.highping.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.OaProcessType;
import games.highping.service.OaProcessTypeService;
import games.highping.utils.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "审批类型管理")
@RestController
@RequestMapping("/admin/process/processType")
public class OaProcessTypeController {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    //@PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit) {
        Page<OaProcessType> pageParam = new Page<>(page,limit);
        IPage<OaProcessType> pageModel = oaProcessTypeService.page(pageParam);
        return Result.ok(pageModel);
    }

    //@PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        OaProcessType processType = oaProcessTypeService.getById(id);
        return Result.ok(processType);
    }

    //@PreAuthorize("hasAuthority('bnt.processType.add')")
    @ApiOperation(value = "新增")
    @PostMapping("/save")
    public Result save(@RequestBody OaProcessType processType) {
        oaProcessTypeService.save(processType);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processType.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody OaProcessType processType) {
        oaProcessTypeService.updateById(processType);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processType.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        oaProcessTypeService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "获取所有审批类型")
    @GetMapping("findAll")
    public Result findAll() {
        return Result.ok(oaProcessTypeService.list());
    }

}
