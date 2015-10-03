package com.wse.parse;

import java.rmi.server.UID;

public class IDGenerator 
{
	public static String getDocumentID() {
		return new UID().toString();
	}
}
