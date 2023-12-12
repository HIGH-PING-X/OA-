package games.highping.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.OaProcess;
import games.highping.service.OaProcessService;
import games.highping.mapper.OaProcessMapper;
import games.highping.utils.vo.ProcessQueryVo;
import org.springframework.stereotype.Service;

/**
* @author HIGH-
* @description 针对表【oa_process(审批类型)】的数据库操作Service实现
* @createDate 2023-12-12 15:40:02
*/
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, OaProcess> implements OaProcessService{

    @Override
    public Object selectPage(Page<OaProcess> pageParam, ProcessQueryVo processQueryVo) {
        Page<OaProcess> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
    }
}




