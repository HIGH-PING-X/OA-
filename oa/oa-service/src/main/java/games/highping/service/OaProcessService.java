package games.highping.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.OaProcess;
import com.baomidou.mybatisplus.extension.service.IService;
import games.highping.utils.vo.ApprovalVo;
import games.highping.utils.vo.ProcessFormVo;
import games.highping.utils.vo.ProcessQueryVo;
import games.highping.utils.vo.ProcessVo;

import java.util.Map;

/**
* @author HIGH-
* @description 针对表【oa_process(审批类型)】的数据库操作Service
* @createDate 2023-12-12 15:40:02
*/
public interface OaProcessService extends IService<OaProcess> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    void deployByZip(String deployPath);

    void startUp(ProcessFormVo processFormVo);

    Page<ProcessVo> findPending(Page<OaProcess> pageParam);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo);

    IPage<ProcessVo> findProcessed(Page<OaProcess> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
