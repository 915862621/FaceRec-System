package top.jonion.laboratory.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.jonion.laboratory.pojo.Face;

@Mapper
public interface FaceMapper extends BaseMapper<Face> {
    public Face selectFaceByAccount(String account);

    public Face findFaceById(String id);

}
