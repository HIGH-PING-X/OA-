package games.highping.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.OaProcessTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HIGH-
* @description 针对表【oa_process_template(审批模板)】的数据库操作Service
* @createDate 2023-12-12 14:57:38
*/
public interface OaProcessTemplateService extends IService<OaProcessTemplate> {

    IPage<OaProcessTemplate> selectPage(Page<OaProcessTemplate> pageParam);

    void publish(Long id);
}
