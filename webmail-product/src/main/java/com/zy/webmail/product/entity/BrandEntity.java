package com.zy.webmail.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.zy.common.vaild.AddGroup;
import com.zy.common.vaild.UpdateGroup;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author zhuoyu
 * @email 787958123@qq.com
 * @date 2020-04-18 11:53:40
 */
@Data
@Accessors(chain = true)
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改时id不能为空",groups = {UpdateGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class,UpdateGroup.class})  //标记校验注解，必须要有至少一个非空字符
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty(message = "logo地址不能为空",groups = AddGroup.class)
	@URL(message = "logo地址必须为合法的url",groups = {AddGroup.class, UpdateGroup.class})   //限定url格式
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message = "检索不能为空",groups = AddGroup.class)
	@Pattern(regexp="^[a-zA-Z]$",message = "检索首字母必须要为字母",groups = {AddGroup.class,UpdateGroup.class})  //自定义校验规则，比如正则表达式
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空",groups = AddGroup.class)
	@Min(value=0,message = "排序必须大于0",groups = {AddGroup.class,UpdateGroup.class})  //限定最小值
	private Integer sort;

}
