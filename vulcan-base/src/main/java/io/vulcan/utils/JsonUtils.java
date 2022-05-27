package io.vulcan.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import java.util.List;

public final class JsonUtils {

    private JsonUtils() {}

    public static <T> String encode(T src) {
        return JSON.toJSONString(src);
    }

    public static <T> byte[] encodeToBytes(T src) {
        return JSON.toJSONBytes(src);
    }

    public static <T> T decode(String src, Class<T> clazz) {
        return JSON.parseObject(src, clazz);
    }

    public static <T> T decode(String src, TypeReference<T> type) {
        return JSON.parseObject(src, type);
    }

    public static <T> List<T> decodeToList(String src, Class<T> clazz) {
        return JSON.parseArray(src, clazz);
    }

    public static <T> T decode(byte[] src, Class<T> clazz) {
        return JSON.parseObject(src, clazz);
    }

    public static <T> T decode(byte[] src, TypeReference<T> type) {
        return type.parseObject(src);
    }

    public static <T> List<T> decodeToList(byte[] src, Class<T> clazz) {
        return JSON.parseArray(src, clazz);
    }
}
