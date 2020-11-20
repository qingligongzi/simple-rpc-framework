package com.github.liyue2008.rpc.serialize.impl;

public class ObjectArraySerializer extends ObjectSerializer {

    @Override
    public byte type() {
        return Types.TYPE_OBJECT_ARRAY;
    }

    @Override
    public Class getSerializeClass() {
        return Object[].class;
    }
}
