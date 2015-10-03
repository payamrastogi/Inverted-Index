package com.wse.serialize;


import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

public class Serializer 
{
	public void serialize(Person person)
	{
		Kryo kryo = new Kryo();
		kryo.register(Person.class, new FieldSerializer(kryo, Person.class));
		kryo.register(String.class);
		kryo.register(Integer.class);
		ByteBuffer b = ByteBuffer.allocateDirect(1024);
		ByteBufferOutput buffer = new ByteBufferOutput(b);
		kryo.writeObject(buffer, person);
		//buffer.flush();
		//buffer.close();
		ByteBuffer b1 = ByteBuffer.allocateDirect(1024);
		ByteBufferInput bufferInput = new ByteBufferInput(b1);
		Person p = kryo.readObject(bufferInput, Person.class);
		System.out.println(p.name);
	}
	
	public static void main(String args[])
	{
		Person p = new Person();
		p.name = "payam";
		p.age = 10;
		Serializer s = new Serializer();
		s.serialize(p);
	}
}
