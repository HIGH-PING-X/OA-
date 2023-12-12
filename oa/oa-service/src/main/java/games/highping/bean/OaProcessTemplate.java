package games.highping.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 审批模板
 * @TableName oa_process_template
 */
@TableName(value ="oa_process_template")
@Data
public class OaProcessTemplate implements Serializable {
    /**
     * 角色id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 图标路径
     */
    private String iconUrl;

    /**
     *
     */
    private String processTypeId;

    /**
     * 表单属性
     */
    private String formProps;

    /**
     * 表单选项
     */
    private String formOptions;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 流程定义上传路径
     */
    private String processDefinitionPath;

    /**
     * 流程定义模型id
     */
    private String processModelId;

    /**
     * 描述
     */
    private String description;

    /**
     *
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标记（0:不可用 1:可用）
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String processTypeName;

}