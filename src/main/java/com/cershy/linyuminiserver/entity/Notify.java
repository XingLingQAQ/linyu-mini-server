package com.cershy.linyuminiserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "notify", autoResultMap = true)
public class Notify {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private String id;

    /**
     * 通知标题
     */
    @TableField("notify_title")
    private String notifyTitle;


    /**
     * 头像
     */
    @TableField("notify_content")
    private String notifyContent;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
