package com.github.liyue2008.rpc.serialize.impl;

public class ClassArraySerializer extends ObjectSerializer{

    @Override
    public byte type() {
        return Types.TYPE_CLASS_ARRAY;
    }

    @Override
    public Class getSerializeClass() {
        return Class[].class;
    }
}
