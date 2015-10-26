package com.wse.serialize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wse.model.DocumentObject;
import com.wse.model.LexiconObject;
import com.wse.model.MetaObject;

public class KryoSerializer {
	
	private Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
	
	public void serialize(MetaObject metaObject) {
	    try (Output output = new Output(new FileOutputStream("meta.ser"))) {
	    	Kryo kryo=new Kryo();
	        kryo.writeClassAndObject(output, metaObject);
	    } catch (FileNotFoundException ex) {
	    	logger.error(ex.getMessage(), ex);
	    	ex.printStackTrace();
	    }	        
	}
	
	public void serializeLexiconMap(Map<String,LexiconObject> lexiconObjectMap) {
	    try (Output output = new Output(new FileOutputStream("lexi.ser"))) {
	    	Kryo kryo=new Kryo();
	        kryo.writeClassAndObject(output, lexiconObjectMap);
	    } catch (FileNotFoundException ex) {
	    	logger.error(ex.getMessage(), ex);
	    	ex.printStackTrace();
	    }	        
	}
	
	public Map<String,LexiconObject> deserializeLexiconMap() {
		Map<String,LexiconObject> retrievedObject=null;
        try (Input input = new Input( new FileInputStream("lexi.ser"))){
            Kryo kryo=new Kryo();
            retrievedObject=(Map<String,LexiconObject>) kryo.readClassAndObject(input);
        } catch (FileNotFoundException ex) {
        	logger.error(ex.getMessage(), ex);
        	ex.printStackTrace();
        }
        logger.debug("Retrieved from map: " + retrievedObject.keySet().size());
        return retrievedObject;
	}
	
	public void serializeDocumentMap(Map<Long,DocumentObject> documentObjectMap) {
	    try (Output output = new Output(new FileOutputStream("doc.ser"))) {
	    	Kryo kryo=new Kryo();
	        kryo.writeClassAndObject(output, documentObjectMap);
	    } catch (FileNotFoundException ex) {
	    	logger.error(ex.getMessage(), ex);
	    	ex.printStackTrace();
	    }	        
	}
	
	public Map<Long,DocumentObject> deserializeDocumentMap() {
		Map<Long,DocumentObject> retrievedObject=null;
        try (Input input = new Input( new FileInputStream("doc.ser"))){
            Kryo kryo=new Kryo();
            retrievedObject=(Map<Long,DocumentObject>) kryo.readClassAndObject(input);
        } catch (FileNotFoundException ex) {
        	logger.error(ex.getMessage(), ex);
        	ex.printStackTrace();
        }
        logger.debug("Retrieved from map: " + retrievedObject.keySet().size());
        return retrievedObject;
	}
	
	public MetaObject deserialize() {
		MetaObject retrievedObject=null;
        try (Input input = new Input( new FileInputStream("meta.ser"))){
            Kryo kryo=new Kryo();
            retrievedObject=(MetaObject)kryo.readClassAndObject(input);
        } catch (FileNotFoundException ex) {
        	logger.error(ex.getMessage(), ex);
        	ex.printStackTrace();
        }
        logger.debug("Retrieved from file: " + retrievedObject.getTotalDocuments());
        return retrievedObject;
	}
	
	public static void main(String[] args) {
		//MetaObject obj = new MetaObject(1000,10.23);
		KryoSerializer ser = new KryoSerializer();
		//ser.serialize(obj);
		ser.deserializeLexiconMap();
	}
}
