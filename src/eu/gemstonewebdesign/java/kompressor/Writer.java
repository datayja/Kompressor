package eu.gemstonewebdesign.java.kompressor;

import java.io.IOException;
import java.io.OutputStream;

public class Writer
{
	private OutputStream output;
	
	private byte current_byte = 0;
	
	private byte current_count = 0;
	
	private long bit_count = 0;
	
	public Writer(OutputStream output)
	{
		this.output = output;
	}
	
	public void write(Boolean data) throws IOException
	{
		if (data)
		{
			this.current_byte = (byte) ((this.current_byte << 1) | 1);
		}
		else
		{
			this.current_byte = (byte) (this.current_byte << 1);
		}
		this.current_count++;
		this.bit_count++;
		
		if (this.current_count == 8)
		{
			this.output.write(this.current_byte);
			
			this.reset();
		}
	}
	
	public void flush() throws IOException
	{
		if (this.current_count != 0)
		{
			this.current_byte = (byte) (this.current_byte << (8 - this.current_count));
			this.output.write(this.current_byte);
			this.current_byte = 0;
			this.reset();
		}
	}
	
	private void reset()
	{
		this.current_byte = 0;
		this.current_count = 0;
	}
	
	public long bitCount()
	{
		return this.bit_count;
	}
}
