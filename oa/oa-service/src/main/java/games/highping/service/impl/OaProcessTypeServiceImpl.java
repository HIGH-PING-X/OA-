package games.highping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.OaProcessTemplate;
import games.highping.bean.OaProcessType;
import games.highping.service.OaProcessTemplateService;
import games.highping.service.OaProcessTypeService;
import games.highping.mapper.OaProcessTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author HIGH-
* @description 针对表【oa_process_type(审批类型)】的数据库操作Service实现
* @createDate 2023-12-12 14:57:49
*/
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, OaProcessType> implements OaProcessTypeService{


    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Override
    public Object findProcessType() {
        //查询所有审批分类，返回list集合
        List<OaProcessType> processTypeList = baseMapper.selectList(null);
        //遍历返回所有审批分类list集合
        for (OaProcessType oaProcessType : processTypeList) {
            //根据审批分类id查询审批模板
            LambdaQueryWrapper<OaProcessTemplate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OaProcessTemplate::getProcessTypeId,oaProcessType.getId());
            List<OaProcessTemplate> list = oaProcessTemplateService.list(queryWrapper);
            //将查询到的审批模板list集合放入审批分类对象中
            oaProcessType.setProcessTemplateList(list);
        }
        return processTypeList;
    }

}




