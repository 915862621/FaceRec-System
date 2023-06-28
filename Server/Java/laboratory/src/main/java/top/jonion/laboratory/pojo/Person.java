package top.jonion.laboratory.pojo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 人员实体类
 */
@Data
@TableName("t_sys_person")
public class Person {
    @TableId
    private String id;
    private String account;
    private String password;
    private String portrait;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp addtime;

}