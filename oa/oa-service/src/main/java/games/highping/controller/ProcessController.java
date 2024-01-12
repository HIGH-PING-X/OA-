package games.highping.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.OaProcess;
import games.highping.bean.OaProcessTemplate;
import games.highping.service.OaProcessService;
import games.highping.service.OaProcessTemplateService;
import games.highping.service.OaProcessTypeService;
import games.highping.utils.result.Result;
import games.highping.utils.vo.ProcessFormVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "审批流管理")
@RestController
@RequestMapping("/admin/process")
@CrossOrigin //跨域
public class ProcessController {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;
    @Autowired
    private OaProcessService oaProcessService;

    @GetMapping("/findProcessType")
    public Result findProcessType() {
        return Result.ok(oaProcessTypeService.findProcessType());
    }

    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId) {
        OaProcessTemplate oaProcessTemplate = oaProcessTemplateService.getById(processTemplateId);
        return Result.ok(oaProcessTemplate);
    }

    @ApiOperation("启动流程")
    @PostMapping("/startUp")
    public Result startUp(@RequestBody ProcessFormVo processFormVo) {
        oaProcessService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation("待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(@ApiParam(name = "page", value = "当前页码", required = true)
                              @PathVariable Long page,
                              @ApiParam(name = "limit", value = "每页记录数", required = true)
                              @PathVariable Long limit) {
        Page<OaProcess> pageParam = new Page<>(page, limit);
        return Result.ok(oaProcessService.findPending(pageParam));
    }

}
