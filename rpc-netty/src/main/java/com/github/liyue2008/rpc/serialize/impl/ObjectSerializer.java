package com.github.liyue2008.rpc.serialize.impl;

import com.github.liyue2008.rpc.serialize.Serializer;

import java.io.*;

public class ObjectSerializer implements Serializer {

    @Override
    public int size(Object entry) {
        return objectToByte(entry).length;
    }

    @Override
    public void serialize(Object entry, byte[] bytes, int offset, int length) {
        byte[] serializeBytes = objectToByte(entry);
        System.arraycopy(serializeBytes, 0, bytes, offset, serializeBytes.length);
    }

    @Override
    public Object parse(byte[] bytes, int offset, int length) {
        Object obj = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes, offset, length);
             ObjectInputStream ois = new ObjectInputStream(bis);) {

            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public byte type() {
        return Types.TYPE_OBJECT;
    }

    @Override
    public Class getSerializeClass() {
        return Object.class;
    }

    private byte[] objectToByte(Object entry) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(entry);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
