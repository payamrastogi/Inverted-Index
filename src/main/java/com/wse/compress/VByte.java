package com.wse.compress;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VByte 
{
	public void encode(long number, OutputStream out) throws IOException
	{
		if(number < 0)
			throw new IllegalArgumentException("only encode positive numbers in vbyte");
		while(number > 127)
		{
			out.write((int)(number & 127));
			number >>>= 7;
		}
		out.write((int)(number | 0x80));
	}
	
	public long decode(InputStream in) throws IOException
	{
		long out = 0;
		int shift = 0;
		long readByte = in.read();
		if(readByte ==-1)
			throw new EOFException();
		while((readByte & 0x80)==0)
		{
			if(shift >= 50)
				throw new IllegalArgumentException();
			
			out |= ((readByte &  127) << shift);
			readByte = in.read();
			if(readByte ==-1)
				throw new EOFException();
			shift =+ 7;
		}
		out |= ((readByte &  127) << shift);
		return out;
	}
}
