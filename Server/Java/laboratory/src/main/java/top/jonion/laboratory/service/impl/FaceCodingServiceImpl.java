package top.jonion.laboratory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.jonion.laboratory.mapper.FaceMapper;
import top.jonion.laboratory.mapper.PersonFaceMapper;
import top.jonion.laboratory.pojo.Face;
import top.jonion.laboratory.pojo.PersonFace;
import top.jonion.laboratory.utils.SnowflakeIdWorker;

import java.sql.Timestamp;

@Service
public class FaceCodingServiceImpl {

    @Autowired
    private FaceMapper faceMapper;
    @Autowired
    private PersonFaceMapper personFaceMapper;


    public Face selectFaceByAccount(String account) {
        Face face = faceMapper.selectFaceByAccount(account);
        return face;
    }


    public PersonFace selectPersonFaceById(String userId){
        PersonFace personFace = personFaceMapper.findPersonFaceByUserId(userId);
        return personFace;
    }

    public void updateFaceCoding(Face face, PersonFace personFace){
        face.setId(personFace.getSys_face_id());
        Timestamp updateTime = new Timestamp(System.currentTimeMillis());
        face.setUpdatetime(updateTime);
        faceMapper.updateById(face);
    }

    /**
     * 插入人脸数据
     * @param face 人脸数据参数
     * @param id 用户的id
     */
    @Transactional
    public void insertFaceCoding(Face face,String id){
        PersonFace personFace =new PersonFace();
        personFace.setId(SnowflakeIdWorker.getUUID());

        face.setId(SnowflakeIdWorker.getUUID());
        personFace.setSys_person_id(id);
        personFace.setSys_face_id(face.getId());

        Timestamp addtime = new Timestamp(System.currentTimeMillis());
        face.setAddtime(addtime);

        personFaceMapper.insert(personFace);
        faceMapper.insert(face);
    }
}