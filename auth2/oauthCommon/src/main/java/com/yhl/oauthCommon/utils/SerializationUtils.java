package com.yhl.oauthCommon.utils;

import org.springframework.core.ConfigurableObjectInputStream;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class SerializationUtils {

    // 序列化对象并转为base64字符串
    public static String serialize(Object state) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(state);
            oos.flush();
            return new BASE64Encoder().encode(bos.toByteArray());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // eat it
                }
            }
        }
    }

    // 将base64反序列化为对象
    public static <T> T deserialize(String base64) {
        ObjectInputStream oip = null;
        try {
            byte[] buffer = new BASE64Decoder().decodeBuffer(base64);
            oip = new ConfigurableObjectInputStream(new ByteArrayInputStream(buffer),
                    Thread.currentThread().getContextClassLoader());
            @SuppressWarnings("unchecked")
            T result = (T) oip.readObject();
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (oip != null) {
                try {
                    oip.close();
                } catch (IOException e) {
                    // eat it
                }
            }
        }
    }

}
