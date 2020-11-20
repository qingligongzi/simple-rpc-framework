package com.github.liyue2008.rpc.serialize.impl;

public class IntegerSerializer extends ObjectSerializer {

    @Override
    public byte type() {
        return Types.TYPE_INTEGER;
    }

    @Override
    public Class getSerializeClass() {
        return Integer.class;
    }
}
