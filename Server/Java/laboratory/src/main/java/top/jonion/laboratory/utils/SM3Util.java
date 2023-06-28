package top.jonion.laboratory.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import java.security.MessageDigest;
import java.security.Security;

/**
 * SM3工具类
 */
public class SM3Util {
    public static String encode(String str) {
        Security.addProvider(new BouncyCastleProvider());

        // 生成一个消息摘要类，指定算法为SM3
        MessageDigest SM3 = null;
        try {
            SM3 = MessageDigest.getInstance("SM3");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 将传入的str字符串参数转化为字节数组
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        // 向消息摘要类传入字节串
        SM3.update(byteArray);
        // 计算并返回Hash值
        return Hex.toHexString(SM3.digest());
    }
}
