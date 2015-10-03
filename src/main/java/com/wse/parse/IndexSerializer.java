package com.wse.parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class IndexSerializer {
	
	public static void indexSerialize(WordIndex wordIndex) {
	    try (Output output = new Output(new FileOutputStream("KryoTest.ser"))) {
	    	Kryo kryo=new Kryo();
	        kryo.writeClassAndObject(output, wordIndex);
	    } catch (FileNotFoundException ex) {
	    	//Logger.getLogger(IndexSerializer.class.getName()).log(Level.SEVERE, null, ex);
	    	ex.printStackTrace();
	    }	        
	}
	
	public static WordIndex indexDeserialize() {
		WordIndex retrievedObject=null;

        try (Input input = new Input( new FileInputStream("KryoTest.ser"))){
            Kryo kryo=new Kryo();
            retrievedObject=(WordIndex)kryo.readClassAndObject(input);
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(IndexSerializer.class.getName()).log(Level.SEVERE, null, ex);
        	ex.printStackTrace();
        }
        System.out.println("Retrieved from file: " + retrievedObject.toString());
        return retrievedObject;
	}
	
	public static void main(String[] args) {
		WordIndex indexObject = new WordIndex("Hello","xyz",2);
		IndexSerializer.indexSerialize(indexObject);
		WordIndex retrivedObject = IndexSerializer.indexDeserialize();
		System.out.println(retrivedObject);
	}
}
