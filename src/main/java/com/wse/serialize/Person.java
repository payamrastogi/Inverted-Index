package com.wse.serialize;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.IntSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;

public class Person implements CustomSerialization
{
	public int age;
    public String name;

    public void readObjectData (Kryo kryo, ByteBuffer buffer) {
            StringSerializer.put(new ByteBufferInput(buffer), name);
            IntSerializer.put(buffer, age, true);
    }

    public void writeObjectData (Kryo kryo, ByteBuffer buffer) {
            name = StringSerializer.get(buffer);
            age = IntSerializer.get(buffer, true);
    }
}
