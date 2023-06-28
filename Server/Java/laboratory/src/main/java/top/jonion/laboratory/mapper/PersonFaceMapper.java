package top.jonion.laboratory.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.jonion.laboratory.pojo.PersonFace;

@Mapper
public interface PersonFaceMapper extends BaseMapper<PersonFace> {
    PersonFace findPersonFaceByUserId(String personId);

}