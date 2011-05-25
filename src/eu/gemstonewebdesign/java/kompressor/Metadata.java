package eu.gemstonewebdesign.java.kompressor;

import java.io.Serializable;
import java.util.Map;

final public class Metadata implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final Node tree;
	
	private final long kompressed_bit_count;
	
	private final long dekompressed_byte_count;
	
	public Metadata(Node tree, long kompressed_bit_count, long dekompressed_byte_count)
	{
		this.tree = tree;
		this.kompressed_bit_count = kompressed_bit_count;
		this.dekompressed_byte_count = dekompressed_byte_count;
	}
	
	public long kompressedBitCount()
	{
		return this.kompressed_bit_count;
	}
	
	public long dekompressedByteCount()
	{
		return this.dekompressed_byte_count;
	}
	
	public Node tree()
	{
		return this.tree;
	}
}
