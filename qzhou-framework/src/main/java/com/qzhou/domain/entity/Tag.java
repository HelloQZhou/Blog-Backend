package com.qzhou.domain.entity;import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

;
/**
 * 标签(Tag)表实体类
 *
 * @author Qzhou
 * @since 2023-04-28 15:14:03
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("qz_tag")
public class Tag  {
    @TableId
    private Long id;

    //标签名
    private String name;
    
    private Long createBy;

    public Tag(String name, Long createBy, Date createTime, Long updateBy, Date updateTime, String remark) {
        this.name = name;
        this.createBy = createBy;
        this.createTime = createTime;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
        this.remark = remark;
    }

    private Date createTime;
    
    private Long updateBy;
    
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
    //备注
    private String remark;

    public Tag(Long id,String name, Long updateBy, Date updateTime, String remark) {
        this.id=id;
        this.name = name;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
        this.remark = remark;
    }
}

