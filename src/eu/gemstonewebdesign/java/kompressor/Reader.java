package eu.gemstonewebdesign.java.kompressor;

import java.io.FileInputStream;
import java.io.IOException;

public class Reader
{
	private FileInputStream reader;
	private byte[] in;
	private int read_bytes = -2; // tato hodnota není vracena metodou read jako počet přečtených bytů
	private int current_byte = -1;
	private short current_bit = 0;
	private final byte leftmostbit;
	
	public Reader(FileInputStream file_to_dekompress_reader)
	{
		this.reader = file_to_dekompress_reader;
		this.in = new byte[Kompressor.read_byte_buffer_size];
		this.leftmostbit = (byte) (1 << 7);
	}

	public Boolean read() throws IOException
	{
		if ((this.read_bytes == 0) || (this.read_bytes == -1))
		{
			System.out.println("Reader: read zero bytes");
			return null;
		}
		else 
		{
			// pokud jsme na offsetu -1, načíst další dávku bytů 
			if (this.current_byte == -1)
			{
				this.read_bytes = this.reader.read(this.in);
				System.out.println("Reader: reading "+this.read_bytes+" bytes");
				this.current_byte++;
				return this.read();
			}
			// jinak čteme další bit z bajtu, který je na řadě
			else
			{
				// najdeme si poslední bit
				byte bit = (byte) (this.in[this.current_byte] & this.leftmostbit);
				System.out.println("Reader: byte "+this.current_byte+", bit "+this.current_bit+": "+Integer.toBinaryString(this.in[this.current_byte])); 
				
				// posuneme doprava
				this.in[this.current_byte] <<= 1;
				this.current_bit++;
				
				// kaskádový posun indexů
				if (this.current_bit == 8)
				{
					this.current_bit = 0;

					this.current_byte++;
					System.out.println("Reader: next byte: "+this.current_byte);
					if (
						(this.current_byte == (Kompressor.read_byte_buffer_size - 1)) 
						||
						(this.current_byte == this.read_bytes)
					) {
						this.current_byte = -1;
						System.out.println("Reader: reloading");
					}
				}
				
				if (bit == this.leftmostbit)
				{
					System.out.println('1');
					return Boolean.TRUE;
				}
				else
				{
					System.out.println('0');
					return Boolean.FALSE;
				}
			}
		}
	}
	
}
