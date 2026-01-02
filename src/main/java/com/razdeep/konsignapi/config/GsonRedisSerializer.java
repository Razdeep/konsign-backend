package com.razdeep.konsignapi.config;

import com.google.gson.Gson;
import com.razdeep.konsignapi.model.CustomPageImpl;
import java.io.UnsupportedEncodingException;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class GsonRedisSerializer implements RedisSerializer {
    Gson gson = null;

    public GsonRedisSerializer() {
        this.gson = new Gson();
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return gson.toJson(o).getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        try {
            return gson.fromJson(new String(bytes, "UTF-8"), CustomPageImpl.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
