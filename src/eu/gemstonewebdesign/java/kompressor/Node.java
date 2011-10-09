package eu.gemstonewebdesign.java.kompressor;

import java.io.Serializable;

/**
 * Implementace uzlu stromu v Huffmanově kódování. 
 */
public class Node implements Comparable<Node>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Znak přiřazený k uzlu.
	 */
	private final byte data;
	
	/**
	 * Váha uzlu.
	 */
	private final long weight;
	
	/**
	 * Příznak listu. 
	 */
	private final boolean leaf; 
	
	/**
	 * Levý syn.
	 */
	private final Node left;
	
	/**
	 * Pravý syn.
	 */
	private final Node right;
	
	/**
	 * Otec uzlu. 
	 */
	private Node parent = null;
	
	/**
	 * Uzel stromu, list. 
	 * 
	 * @param data		Znak uzlu
	 * @param weight 	Váha uzlu
	 */
	public Node(byte data, long weight)
	{
		this.data = data;
		this.weight = weight;
		this.leaf = true;
		this.left = null;
		this.right = null;
	}
	
	/**
	 * Uzel stromu, vnitřní. 
	 * 
	 * @param left 		Levý syn
	 * @param right 	Pravý syn
	 */
	public Node(Node left, Node right)
	{
		this.left = left;
		this.right = right;
		this.data = -1;
		this.leaf = false;
		this.weight = this.left.getWeight() + this.right.getWeight();
		this.left.setParent(this);
		this.right.setParent(this);
	}
	
	/**
	 * Porovnání s jiným uzlem pro PriorityQueue&lt;Node&gt;
	 */
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
	
	/**
	 * Přečíst znak uzlu. 
	 * @return Byte Znak uzlu
	 */
	public Byte getData()
	{
		return data;
	}
	
	/**
	 * Levý syn. 
	 * @return Node
	 */
	public Node getLeft()
	{
		return this.left;
	}
	
	/**
	 * Otec uzlu. 
	 * @return Node
	 */
	public Node getParent()
	{
		return this.parent;
	}
	
	/**
	 * Pravý syn. 
	 * @return Node
	 */
	public Node getRight()
	{
		return this.right;
	}
	
	/**
	 * Váha uzlu. 
	 * @return long
	 */
	public long getWeight()
	{
		return weight;
	}
	
	/**
	 * Zjištění příznaku listu. 
	 * @return boolean
	 */
	public boolean isLeaf()
	{
		return this.leaf;
	}
	
	/**
	 * Zjištění, jestli je uzel kořenem. 
	 * @return boolean
	 */
	public boolean isRoot()
	{
		return this.parent == null;
	}
	
	/**
	 * Nastavení rodiče uzlu. 
	 * @param parent Otec uzlu
	 */
	private void setParent(Node parent)
	{
		this.parent = parent;
	}
}
