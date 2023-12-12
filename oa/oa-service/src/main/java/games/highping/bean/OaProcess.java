package games.highping.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 审批类型
 * @TableName oa_process
 */
@TableName(value ="oa_process")
@Data
public class OaProcess implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 审批code
     */
    private String processCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 审批模板id
     */
    private Long processTemplateId;

    /**
     * 审批类型id
     */
    private Long processTypeId;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 表单值
     */
    private String formValues;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 当前审批人
     */
    private String currentAuditor;

    /**
     * 状态（0：默认 1：审批中 2：审批通过 -1：驳回）
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
}