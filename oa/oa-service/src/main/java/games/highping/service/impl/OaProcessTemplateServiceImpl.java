package games.highping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.OaProcessTemplate;
import games.highping.bean.OaProcessType;
import games.highping.service.OaProcessService;
import games.highping.service.OaProcessTemplateService;
import games.highping.mapper.OaProcessTemplateMapper;
import games.highping.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author HIGH-
* @description 针对表【oa_process_template(审批模板)】的数据库操作Service实现
* @createDate 2023-12-12 14:57:38
*/
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, OaProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;
    @Autowired
    private OaProcessService oaProcessService;

    //分页查询审批模板，把审批类型对应名称查询
    @Override
    public IPage<OaProcessTemplate> selectPage(Page<OaProcessTemplate> pageParam) {
        //1 调用mapper的方法实现分页查询
        Page<OaProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        //2 第一步分页查询返回分页数据，从分页数据获取列表list集合
        List<OaProcessTemplate> processTemplateList = processTemplatePage.getRecords();
        //3 遍历list集合，得到每个对象的审批类型id
        for(OaProcessTemplate processTemplate : processTemplateList) {
            //得到每个对象的审批类型id
            String processTypeId = processTemplate.getProcessTypeId();
            //4 根据审批类型id，查询获取对应名称
            LambdaQueryWrapper<OaProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OaProcessType::getId,processTypeId);
            OaProcessType processType = oaProcessTypeService.getOne(wrapper);
            if(processType == null) {
                continue;
            }
            //5 完成最终封装processTypeName
            processTemplate.setProcessTypeName(processType.getName());
        }
        return processTemplatePage;
    }

    @Transactional
    @Override
    public void publish(Long id) {
        OaProcessTemplate processTemplate = this.getById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        if (!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())) {
            oaProcessService.deployByZip(processTemplate.getProcessDefinitionPath());
        }
    }

}
