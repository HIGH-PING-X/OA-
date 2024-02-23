package games.highping.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import games.highping.bean.OaProcess;
import games.highping.bean.OaProcessRecord;
import games.highping.bean.OaProcessTemplate;
import games.highping.bean.SysUser;
import games.highping.security.LoginUserInfoHelper;
import games.highping.service.OaProcessRecordService;
import games.highping.service.OaProcessService;
import games.highping.mapper.OaProcessMapper;
import games.highping.service.OaProcessTemplateService;
import games.highping.service.SysUserService;
import games.highping.utils.vo.ApprovalVo;
import games.highping.utils.vo.ProcessFormVo;
import games.highping.utils.vo.ProcessQueryVo;
import games.highping.utils.vo.ProcessVo;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.lang.Process;
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
    @Autowired
    private OaProcessService oaProcessService;
    @Autowired
    private HistoryService historyService;

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
            OaProcess process = baseMapper.selectById(processId);
            //根据Process对象获取ProcessVo对象
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId(task.getId());
            //放到最终list集合里面
            processVoList.add(processVo);
        }
        //封装返回page对象
        return new Page<ProcessVo>(pageParam.getCurrent(),pageParam.getSize(),query.count()).setRecords(processVoList);
    }

    @Override
    public Map<String, Object> show(Long id) {
        //根据流程ID获取流程信息
        OaProcess process = baseMapper.selectById(id);
        //根据流程ID获取流程记录信息
        LambdaQueryWrapper<OaProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OaProcessRecord::getProcessId,id);
        List<OaProcessRecord> processRecordList = oaProcessRecordService.list(wrapper);
        //根据模板ID查询模板信息
        OaProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());
        //判断当前用户是否可以进行审批
        boolean isApprove = false;
        for (Task task : this.getCurrentTaskList(process.getProcessInstanceId())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = (String) authentication.getPrincipal();
            if(task.getAssignee().equals(username))
                isApprove=true;
        }
        //将数据封装Map集合中
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove",isApprove);
        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        //获取任务ID，根据任务ID获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry:variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        //判断审批状态值
        if (approvalVo.getStatus() == 1) {
            Map<String, Object> variable = new HashMap<>();
            taskService.complete(taskId, variable);
        } else {
            this.endTask(taskId);
        }
        //记录审批相关过程信息
        String description = approvalVo.getStatus().intValue() == 1 ? "通过" : "驳回";
        oaProcessRecordService.record(approvalVo.getProcessId(),approvalVo.getStatus(),description);
        //查询下一个审批人
        OaProcess process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assignList = new ArrayList<>();
            for (Task task : taskList) {
                String assignee = task.getAssignee();
                SysUser sysUser = sysUserService.getByUsername(assignee);
                assignList.add(sysUser.getName());
            }
            process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");
            process.setStatus(1);
        } else {
            if (approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批通过");
                process.setStatus(2);
            } else {
                process.setDescription("审批驳回");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);
    }

    //结束流程
    private void endTask(String taskId) {
        //根据任务ID获取任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //获取流程定义模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        //获取结束流向节点
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if (CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        //当前流向节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
        //清理当前流动反向
        currentFlowNode.getOutgoingFlows().clear();
        //创建新流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        //当前节点指向新方向
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);
        //完成当前任务
        taskService.complete(taskId);
    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<OaProcess> pageParam) {
        //封装查询条件
        //调用方法条件分页查询，返回list集合
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(LoginUserInfoHelper.getUsername()).finished().orderByTaskCreateTime().desc();
        // 开始位置和每页显示记录数
        int begin = (int)((pageParam.getCurrent()-1)*pageParam.getSize());
        int size = (int)pageParam.getSize();
        List<HistoricTaskInstance> list = query.listPage(begin, size);
        long totalCount = query.count();

        //遍历返回list集合，封装List<ProcessVo>
        List<ProcessVo> processVoList = new ArrayList<>();
        for(HistoricTaskInstance item : list) {
            //流程实例id
            String processInstanceId = item.getProcessInstanceId();
            //根据流程实例id查询获取process信息
            LambdaQueryWrapper<OaProcess> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OaProcess::getProcessInstanceId,processInstanceId);
            OaProcess process = baseMapper.selectOne(wrapper);
            // process转换成processVo
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId("0");
            //放到list
            processVoList.add(processVo);
        }
        //IPage封装分页查询所有数据，返回
        IPage<ProcessVo> pageModel = new Page<ProcessVo>(pageParam.getCurrent(),pageParam.getSize(), totalCount);
        pageModel.setRecords(processVoList);
        return pageModel;
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        return baseMapper.selectPage(pageParam, processQueryVo);
    }
}




