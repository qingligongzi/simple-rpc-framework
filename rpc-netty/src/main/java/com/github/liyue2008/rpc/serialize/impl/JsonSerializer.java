package com.github.liyue2008.rpc.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.liyue2008.rpc.serialize.Serializer;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public abstract class JsonSerializer<T> implements Serializer<T> {

    @Override
    public int size(T entry) {
        return Integer.BYTES + JSON.toJSONString(entry).length();
    }

}
