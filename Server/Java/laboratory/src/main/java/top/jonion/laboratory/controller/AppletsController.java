package top.jonion.laboratory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.jonion.laboratory.config.ApplicationValues;
import top.jonion.laboratory.pojo.*;
import top.jonion.laboratory.service.impl.FaceCodingServiceImpl;
import top.jonion.laboratory.service.impl.PersonServiceImpl;
import top.jonion.laboratory.utils.FileManage;
import top.jonion.laboratory.utils.NetworkUtil;
import top.jonion.laboratory.vo.RespBean;


/**
 * 小程序控制器
 * 负责和小程序的交互
 */
@RestController
@RequestMapping("/AppletsController")
public class AppletsController {
    @Autowired
    private PersonServiceImpl personService;

    @Autowired
    private FaceCodingServiceImpl faceCodingServiceImpl;

    @Autowired
    private ApplicationValues appvalue;

    @Value("${uploadface}")
    private String UPLOAD_PATH;

    @Value("${upload}")
    private String UPLOAD;

    private static final Logger logger = LoggerFactory.getLogger(AppletsController.class);


    /**
     * 注册账号
     *
     * @param person
     * @return RespBean
     */
    @RequestMapping("register")
    @ResponseBody
    public RespBean registerAccount(Person person) {
        if (personService.findPersonByAccount(person.getAccount()) == null) {
            if (personService.insert(person) > 0) {
                Person regUser = personService.findPersonByAccount(person.getAccount());
                return RespBean.success("添加成功", regUser);
            } else {
                return RespBean.error("添加失败", false);
            }
        } else return RespBean.error("账号存在");
    }

    /**
     * 密码验证
     *
     * @param person
     * @return RespBean
     */
    @RequestMapping("/login")
    public RespBean login(Person person) {
        Person loginUser = personService.selectByOnePerson(person);
        if (loginUser != null) {
            loginUser.setPassword(""); // 密码不回传到前端
            return RespBean.success("密码正确", loginUser);
        } else {
            return RespBean.error("密码错误", false);
        }
    }

    /**
     * 添加或者修改人脸
     *
     * @param file
     * @param person
     * @return RespBean
     */
    @RequestMapping("/appUpdate")
    public RespBean appUpdate(MultipartFile file, Person person) {
        String personId = person.getId();
        Face face = new Face();
        String savedPicName = FileManage.savePictureReg(file, UPLOAD_PATH);
        if (!savedPicName.isEmpty()) {
            //保存图片的路径
            face.setUrl(savedPicName);
            //图片存储成功，通知python服务器解析图片
            String faceEncoding = NetworkUtil.sendPostSuccess(appvalue.getPythonUrl(), savedPicName, person.getAccount());
            //设置人脸编码
            face.setFacecoding(faceEncoding);
            //保存到人脸数据库
            PersonFace personFace = faceCodingServiceImpl.selectPersonFaceById(personId);
            //判断原来是否存在人脸数据
            if (personFace != null) {
                faceCodingServiceImpl.updateFaceCoding(face, personFace);
            } else {
                faceCodingServiceImpl.insertFaceCoding(face, personId);
            }
            return RespBean.success("添加成功");
        } else {
            return RespBean.error("添加失败");
        }
    }

    @RequestMapping("/Rec")
    public RespBean Rec(MultipartFile file, Person person) {
        Face recFace = faceCodingServiceImpl.selectFaceByAccount(person.getAccount());
        String faceCoding = recFace.getFacecoding();

        String savedPicName = FileManage.savePicture(file, UPLOAD_PATH);
        if (!savedPicName.isEmpty()) {
            String recResult = NetworkUtil.pyRec(appvalue.getPythonUrl(), savedPicName, faceCoding);
            if (recResult.equals("true")) {
                Person personRes = personService.findPersonByAccount(person.getAccount());
                return RespBean.success("验证通过", personRes);
            }
        }
        return RespBean.error("验证失败");
    }

    @RequestMapping("/isExistFace")
    public RespBean isExistFace(MultipartFile file) {
        String savedPicName = FileManage.savePicture(file, UPLOAD_PATH);
        if (!savedPicName.isEmpty()) {
            String isExistFaceResult = NetworkUtil.pyIsExistFace(appvalue.getPythonUrl(), savedPicName);
            if (isExistFaceResult.equals("true")) {
                return RespBean.success("检测到人脸");
            }
        }
        return RespBean.error("未检测到人脸");
    }

    @RequestMapping("/isWink")
    public RespBean isWink(MultipartFile file) {
        String savedPicName = FileManage.savePicture(file, UPLOAD_PATH);
        if (!savedPicName.isEmpty()) {
            String isWinkResult = NetworkUtil.pyIsWink(appvalue.getPythonUrl(), savedPicName);
            if (isWinkResult.equals("true")) {
                return RespBean.success("检测到眨眼");
            }
        }
        return RespBean.error("未检测到眨眼");
    }

    @RequestMapping("/isOpenMouth")
    public RespBean isOpenMouth(MultipartFile file) {
        String savedPicName = FileManage.savePicture(file, UPLOAD_PATH);
        if (!savedPicName.isEmpty()) {
            String isWinkResult = NetworkUtil.pyIsOpenMouth(appvalue.getPythonUrl(), savedPicName);
            if (isWinkResult.equals("true")) {
                return RespBean.success("检测到张嘴");
            }
        }
        return RespBean.error("未检测到张嘴");
    }

    /**
     * 更改用户头像
     *
     * @param file   图片
     * @param person 账号信息
     * @return
     */
    @RequestMapping("updataportrait")
    public RespBean updataPortrait(MultipartFile file, Person person) {
        String flagName = FileManage.savePortrait(file, UPLOAD + "portrait");
        logger.info("保存图片" + flagName);
        if (!flagName.isEmpty()) {
            person.setPortrait(flagName);
            if (personService.update(person) > 0) {
                return RespBean.success("更新成功", (Object) flagName);
            } else return RespBean.error("更新失败");

        }
        return RespBean.error("图片保存失败");
    }
}
