package games.highping.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.OaProcess;
import games.highping.bean.OaProcessTemplate;
import games.highping.bean.SysUser;
import games.highping.security.LoginUserInfoHelper;
import games.highping.service.OaProcessRecordService;
import games.highping.service.OaProcessService;
import games.highping.mapper.OaProcessMapper;
import games.highping.service.OaProcessTemplateService;
import games.highping.service.SysUserService;
import games.highping.utils.vo.ProcessFormVo;
import games.highping.utils.vo.ProcessQueryVo;
import games.highping.utils.vo.ProcessVo;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
* @author HIGH-
* @description 针对表【oa_process(审批类型)】的数据库操作Service实现
* @createDate 2023-12-12 15:40:02
*/
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, OaProcess> implements OaProcessService{

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    @Lazy
    private OaProcessTemplateService oaProcessTemplateService;
    @Autowired
    private OaProcessRecordService oaProcessRecordService;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
        return pageModel;
    }

    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //根据id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        //根据审批模板id查询模板信息
        OaProcessTemplate oaProcessTemplate = oaProcessTemplateService.getById(processFormVo.getProcessTemplateId());
        //保存提交审批信息到业务表
        OaProcess oaProcess = new OaProcess();
        BeanUtils.copyProperties(processFormVo,oaProcess);
        oaProcess.setStatus(1);
        String workNo = System.currentTimeMillis() + "";
        oaProcess.setProcessCode(workNo);
        oaProcess.setUserId(LoginUserInfoHelper.getUserId());
        oaProcess.setFormValues(processFormVo.getFormValues());
        oaProcess.setTitle(sysUser.getName() + "发起" + oaProcessTemplate.getName() + "申请");
        baseMapper.insert(oaProcess);
        //启动流程实例
        String processDefinitionKey = oaProcessTemplate.getProcessDefinitionKey();
        String businessKey = String.valueOf(oaProcess.getId());
        String formValues = processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String,Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry:formData.entrySet()) {
            map.put(entry.getKey(),entry.getValue());
        }
//        与上面的for循环等效
//        Iterator<Map.Entry<String, Object>> iterator = formData.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Object> entry = iterator.next();
//            map.put(entry.getKey(), entry.getValue());
//        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("data",map);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        //查询下一个审批人
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
        List<String> nameList = new ArrayList<>();
        for (Task task : taskList) {
            String assigneeName = task.getAssignee();
            SysUser user = sysUserService.getByUsername(assigneeName);
            String name = user.getName();
            nameList.add(name);
            //推送消息
        }
        oaProcess.setProcessInstanceId(processInstance.getId());
        oaProcess.setDescription("等待" + StringUtils.join(nameList.toArray(), ",") + "审批");
        //业务和流程关联
        baseMapper.updateById(oaProcess);
        oaProcessRecordService.record(oaProcess.getId(),1,"提交申请");
    }

    //当前任务
    private List<Task> getCurrentTaskList(String id) {
        List<org.activiti.engine.task.Task> taskList = taskService.createTaskQuery().processInstanceId(id).list();
        return taskList;
    }

    @Override
    public Page<ProcessVo> findPending(Page<OaProcess> pageParam) {
        //封装查询条件，根据当前登录的用户名称
        TaskQuery query = taskService.createTaskQuery()
                                    .taskAssignee(LoginUserInfoHelper.getUsername())
                                    .orderByTaskCreateTime()
                                    .desc();
        //调用方法分页条件查询,返回list集合,代办任务集合
        int begin = (int)((pageParam.getCurrent() - 1)*pageParam.getSize());
        int size = (int)pageParam.getSize();
        List<Task> taskList = query.listPage(begin, size);
        //封装返回list集合数据到list<ProcessVo>里面
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task : taskList) {
            //从task获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            //根据流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            //从流程实例对象获取业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            //根据业务key获取Process对象
            long processId = Long.parseLong(businessKey);
            baseMapper.selectById(processId);
            //根据Process对象获取ProcessVo对象
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(processId,processVo);
            processVo.setTaskId(task.getId());
            //放到最终list集合里面
            processVoList.add(processVo);
        }
        //封装返回page对象
        Page<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(),pageParam.getSize(),query.count());
        page.setRecords(processVoList);
        return page;
    }

}




