package games.highping.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import games.highping.bean.OaProcess;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import games.highping.utils.vo.ProcessQueryVo;
import games.highping.utils.vo.ProcessVo;
import org.apache.ibatis.annotations.Param;

/**
* @author HIGH-
* @description 针对表【oa_process(审批类型)】的数据库操作Mapper
* @createDate 2023-12-12 15:40:02
* @Entity games.highping.bean.OaProcess
*/
public interface OaProcessMapper extends BaseMapper<OaProcess> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);

}




