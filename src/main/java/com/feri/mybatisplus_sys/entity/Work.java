package com.feri.mybatisplus_sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Feri
 * @since 2019-03-21
 */
@TableName("work")
@Data
public class Work extends Model<Work> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
	private String name;
	private String address;
	private Integer money;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date createtime;

	public Work(){}
	public Work(String name, String address, Integer money, Date createtime) {
		this.name = name;
		this.address = address;
		this.money = money;
		this.createtime = createtime;
	}

	public Work(Integer id, String name, String address, Integer money, Date createtime) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.money = money;
		this.createtime = createtime;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
