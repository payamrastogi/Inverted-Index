package com.wse.compress;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VByte 
{
	private final Logger logger = LoggerFactory.getLogger(VByte.class);
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
	
	public long decode(RandomAccessFile randomAccessFile, long filePointer)
	{
		long out = 0;
		try
		{
			int shift = 0;
			randomAccessFile.seek(filePointer);
			long readByte = randomAccessFile.read();
			if(readByte ==-1)
				throw new EOFException();
			while((readByte & 0x80)==0)
			{
				if(shift >= 50)
					throw new IllegalArgumentException();
			
				out |= ((readByte &  127) << shift);
				readByte = randomAccessFile.read();
				if(readByte ==-1)
					throw new EOFException();
				shift =+ 7;
			}
			out |= ((readByte &  127) << shift);
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
		return out;
	}
}
