package top.jonion.laboratory.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 传输到python服务器
 * 网络传输工具类
 */

public class NetworkUtil {


    /**
     * 生成post请求的JSON请求参数
     * 请求示例:
     * {
     * "id":1,
     * "name":"黄伟杰"
     * }
     *
     * @return httpEntity
     */
    private static HttpEntity<Map<String, String>> generatePostJson(Map<String, String> jsonMap) {

        //如果需要其它的请求头信息、都可以在这里追加
        HttpHeaders httpHeaders = new HttpHeaders();

        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");

        httpHeaders.setContentType(type);

        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(jsonMap, httpHeaders);

        return httpEntity;
    }

    /**
     * 通知python图片保存成功
     *
     * @param uri      服务器地址
     * @param imageurl 图片保存路径
     * @return Body
     */
    public static String sendPostSuccess(String uri, String imageurl, String username) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> jsonMap = new HashMap<>(6);
        jsonMap.put("msg", "图片上传成功");
        jsonMap.put("username", username);
        jsonMap.put("url", imageurl);
        jsonMap.put("flag", "true");

        ResponseEntity<String> apiResponse = restTemplate.postForEntity
                (
                        uri,
                        NetworkUtil.generatePostJson(jsonMap),
                        String.class
                );
        return apiResponse.getBody();
    }

    public static String pyIsExistFace(String uri, String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> jsonMap = new HashMap<>(6);
        jsonMap.put("url", imageUrl);
        jsonMap.put("flag", "isExistFace");

        ResponseEntity<String> apiResponse = restTemplate.postForEntity(
                uri,
                NetworkUtil.generatePostJson(jsonMap),
                String.class
        );
        return apiResponse.getBody();
    }


    public static String pyIsWink(String uri, String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> jsonMap = new HashMap<>(6);
        jsonMap.put("url", imageUrl);
        jsonMap.put("flag", "isWink");

        ResponseEntity<String> apiResponse = restTemplate.postForEntity(
                uri,
                NetworkUtil.generatePostJson(jsonMap),
                String.class
        );
        return apiResponse.getBody();
    }


    public static String pyIsOpenMouth(String uri, String imageUrl) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> jsonMap = new HashMap<>(6);
        jsonMap.put("url", imageUrl);
        jsonMap.put("flag", "isOpenMouth");

        ResponseEntity<String> apiResponse = restTemplate.postForEntity(
                uri,
                NetworkUtil.generatePostJson(jsonMap),
                String.class
        );
        return apiResponse.getBody();
    }

    public static String pyRec(String uri, String imageUrl, String faceCoding) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> jsonMap = new HashMap<>(6);
        jsonMap.put("url", imageUrl);
        jsonMap.put("face", faceCoding);
        jsonMap.put("flag", "Rec");

        ResponseEntity<String> apiResponse = restTemplate.postForEntity(
                uri,
                NetworkUtil.generatePostJson(jsonMap),
                String.class
        );
        return apiResponse.getBody();
    }
}
