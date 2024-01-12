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
    private OaProcessTemplateService processTemplateService;

    //查询所有审批分类和每个分类所有审批模板
    @Override
    public List<OaProcessType> findProcessType() {
        //1 查询所有审批分类，返回list集合
        List<OaProcessType> processTypeList = baseMapper.selectList(null);

        //2 遍历返回所有审批分类list集合
        for (OaProcessType processType:processTypeList) {
            //3 得到每个审批分类，根据审批分类id查询对应审批模板
            //审批分类id
            Long typeId = processType.getId();
            //根据审批分类id查询对应审批模板
            LambdaQueryWrapper<OaProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OaProcessTemplate::getProcessTypeId,typeId);
            List<OaProcessTemplate> processTemplateList = processTemplateService.list(wrapper);

            //4 根据审批分类id查询对应审批模板数据（List）封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }

}




