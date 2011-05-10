package eu.gemstonewebdesign.java.kompressor;

public class Node implements Comparable<Node>
{
	private byte data;
	private long weight;
	private boolean leaf = true; // leaves are leaf by default
	
	private Node left = null;
	private Node right = null;
	private Node parent = null;
	
	/**
	 * New leaf Node. 
	 * @param data
	 * @param weight
	 */
	public Node(byte data, long weight)
	{
		this.data = data;
		this.weight = weight;
	}
	
	/**
	 * New internal Node. 
	 * @param left
	 * @param right
	 */
	public Node(Node left, Node right)
	{
		this.left = left;
		this.right = right;
		this.leaf = false;
		this.weight = this.left.getWeight() + this.right.getWeight();
		this.left.setParent(this);
		this.right.setParent(this);
	}
	
	public Byte getData()
	{
		return data;
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
		return (this.parent == null);
	}
	
	public Node getLeft()
	{
		return this.left;
	}
	
	public Node getRight()
	{
		return this.right;
	}
	
	public Node getParent()
	{
		return this.parent;
	}
	
	private void setParent(Node parent)
	{
		this.parent = parent;
	}
	
	@Override
	public int compareTo(Node o)
	{
		long foreign_weight = o.getWeight();
		if ( this.weight < foreign_weight )
		{
			return -1;
		}
		else if ( foreign_weight < this.weight )
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
