package com.github.liyue2008.rpc.serialize.impl;

import com.github.liyue2008.rpc.serialize.Serializer;

public class HessianSerializer implements Serializer {

    @Override
    public int size(Object entry) {
        return 0;
    }

    @Override
    public void serialize(Object entry, byte[] bytes, int offset, int length) {

    }

    @Override
    public Object parse(byte[] bytes, int offset, int length) {
        return null;
    }

    @Override
    public byte type() {
        return 0;
    }

    @Override
    public Class getSerializeClass() {
        return null;
    }
}
