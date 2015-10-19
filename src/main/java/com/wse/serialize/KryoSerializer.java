package com.wse.serialize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wse.model.MetaObject;

public class KryoSerializer {
	
	public void serialize(MetaObject metaObject) {
	    try (Output output = new Output(new FileOutputStream("meta.ser"))) {
	    	Kryo kryo=new Kryo();
	        kryo.writeClassAndObject(output, metaObject);
	    } catch (FileNotFoundException ex) {
	    	//Logger.getLogger(IndexSerializer.class.getName()).log(Level.SEVERE, null, ex);
	    	ex.printStackTrace();
	    }	        
	}
	
	public MetaObject deserialize() {
		MetaObject retrievedObject=null;

        try (Input input = new Input( new FileInputStream("meta.ser"))){
            Kryo kryo=new Kryo();
            retrievedObject=(MetaObject)kryo.readClassAndObject(input);
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(IndexSerializer.class.getName()).log(Level.SEVERE, null, ex);
        	ex.printStackTrace();
        }
        System.out.println("Retrieved from file: " + retrievedObject.toString());
        return retrievedObject;
	}
}
