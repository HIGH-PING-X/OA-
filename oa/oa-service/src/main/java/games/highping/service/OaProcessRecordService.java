package games.highping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import games.highping.bean.OaProcessRecord;

/**
* @author HIGH-
* @description 针对表【oa_process_record(审批记录)】的数据库操作Service
* @createDate 2024-01-04 16:53:11
*/
public interface OaProcessRecordService extends IService<OaProcessRecord> {
    void record(Long processId, Integer status, String description);
}
