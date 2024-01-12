package games.highping.service;

import games.highping.bean.OaProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author HIGH-
* @description 针对表【oa_process_type(审批类型)】的数据库操作Service
* @createDate 2023-12-12 14:57:49
*/
public interface OaProcessTypeService extends IService<OaProcessType> {
    List<OaProcessType> findProcessType();
}
