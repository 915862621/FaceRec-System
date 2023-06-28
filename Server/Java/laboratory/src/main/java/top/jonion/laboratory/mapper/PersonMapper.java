package top.jonion.laboratory.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.jonion.laboratory.pojo.Person;


@Mapper
public interface PersonMapper extends BaseMapper<Person> {
    Person findPersonByAccount(String account);

}