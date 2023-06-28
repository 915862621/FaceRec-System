package top.jonion.laboratory.pojo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("t_sys_person_face")
public class PersonFace {
    @TableId
    private String id;
    private String sys_person_id;
    private String sys_face_id;
    
}