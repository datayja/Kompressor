package eu.gemstonewebdesign.java.kompressor;

import java.io.Serializable;

public class Node implements Comparable<Node>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final byte data;
	private final char cdata;
	private final long weight;
	private final boolean leaf; // leaves are leaf by default
	
	private final Node left;
	private final Node right;
	private Node parent = null;
	
	/**
	 * New leaf Node.
	 * 
	 * @param data
	 * @param weight
	 */
	public Node(byte data, long weight)
	{
		this.data = data;
		this.cdata = (char) data;
		this.weight = weight;
		this.leaf = true;
		this.left = null;
		this.right = null;
	}
	
	/**
	 * New internal Node.
	 * 
	 * @param left
	 * @param right
	 */
	public Node(Node left, Node right)
	{
		this.left = left;
		this.right = right;
		this.data = -1;
		this.cdata = '0';
		this.leaf = false;
		this.weight = this.left.getWeight() + this.right.getWeight();
		this.left.setParent(this);
		this.right.setParent(this);
	}
	
	@Override
	public int compareTo(Node o)
	{
		long foreign_weight = o.getWeight();
		if (this.weight < foreign_weight)
			return -1;
		else if (foreign_weight < this.weight)
			return 1;
		else
			return 0;
	}
	
	public Byte getData()
	{
		return data;
	}
	
	public Node getLeft()
	{
		return this.left;
	}
	
	public Node getParent()
	{
		return this.parent;
	}
	
	public Node getRight()
	{
		return this.right;
	}
	
	public long getWeight()
	{
		return weight;
	}
	
	public boolean isLeaf()
	{
		return this.leaf;
	}
	
	public boolean isRoot()
	{
		return this.parent == null;
	}
	
	private void setParent(Node parent)
	{
		this.parent = parent;
	}
}
