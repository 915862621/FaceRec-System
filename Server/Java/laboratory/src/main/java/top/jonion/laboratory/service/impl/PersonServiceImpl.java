package top.jonion.laboratory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.jonion.laboratory.mapper.PersonMapper;
import top.jonion.laboratory.pojo.Person;
import top.jonion.laboratory.utils.SM3Util;
import top.jonion.laboratory.utils.SnowflakeIdWorker;
import java.sql.Timestamp;

@Service
public class PersonServiceImpl {
    @Value("${solt}")
    private String solt;

    @Autowired
    private PersonMapper personMapper;


    public Person findPersonByAccount(String account) {
        return personMapper.findPersonByAccount(account);
    }

    public int insert(Person person) {
        //添加雪花主键id
        person.setId(SnowflakeIdWorker.getUUID());
        String MDpassword = SM3Util.encode(person.getPassword() + solt);
        Timestamp addtime = new Timestamp(System.currentTimeMillis());
        person.setAddtime(addtime);
        person.setPassword(MDpassword);
        int n = personMapper.insert(person);
        return n;
    }

    public Person selectByOnePerson(Person person) {
        if (person.getPassword() != null) { //判断密码是不是为空,因为要完全匹配才可以找出来
            String MDpassword = SM3Util.encode(person.getPassword() + solt);
            person.setPassword(MDpassword);
        }
        return personMapper.selectOne(person);
    }


    public int update(Person person) {
        if (person.getPassword() != null) {//判断密码是不是为空,因为要完全匹配才可以找出来
            String MDpassword = SM3Util.encode(person.getPassword() + solt);
            person.setPassword(MDpassword);
        }
        return personMapper.updateById(person);
    }
}
