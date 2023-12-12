package games.highping.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.OaProcess;
import com.baomidou.mybatisplus.extension.service.IService;
import games.highping.utils.vo.ProcessQueryVo;

/**
* @author HIGH-
* @description 针对表【oa_process(审批类型)】的数据库操作Service
* @createDate 2023-12-12 15:40:02
*/
public interface OaProcessService extends IService<OaProcess> {

    Object selectPage(Page<OaProcess> pageParam, ProcessQueryVo processQueryVo);
}
