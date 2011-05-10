package eu.gemstonewebdesign.java.kompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

public class Kompressor extends Thread
{
	private static final int read_byte_buffer_size = 8;
	
	// Huffman kompressed data
	private static final String extension_data = ".hkd";
	
	// Huffman kompressed tree
	private static final String extension_tree = ".hkt";
	
	private KompressorMode mode = KompressorMode.FILE;
	
	private KompressorAction action = KompressorAction.KOMPRESS;
	
	private String status = "Idle";
	
	private String filename;

	private String target;

	private long cmd_no;
	
	public void run()
	{
		if (this.mode == KompressorMode.FILE)
		{
			if (this.action == KompressorAction.KOMPRESS)
			{
				this.kompressFile(this.filename, this.target);
			}
			else if (this.action == KompressorAction.DEKOMPRESS)
			{
				this.dekompressFile(this.filename, this.target);
			}
		}
	}
	
	public Kompressor() {}
	
	public synchronized String getStatus()
	{
		return this.status;
	}
	
	public synchronized void setStatus(String status)
	{
		this.status = status;
	}
	
	public void setMode(KompressorMode mode)
	{
		synchronized(this)
		{
			this.mode = mode;
		}
	}
	
	public void setAction(KompressorAction action, String filename)
	{
		synchronized(this)
		{
			this.action = action;
			this.filename = filename;
		}
	}
	
	public void setTarget(String target)
	{
		synchronized (this)
		{
			this.target = target;
		}
	}
	
	private void kompressFile(String file, String to)
	{
		File file_to_kompress = new File(file);
		FileInputStream file_to_kompress_reader = null;
		
		File kompressed_file = new File(file + Kompressor.extension_data);
		FileOutputStream kompressed_file_writer = null;
		
		File kompressed_file_tree = new File(file + Kompressor.extension_tree);
		ObjectOutputStream kompressed_file_tree_writer = null;
		
		try
		{
			System.out.println("[" + this.cmd_no + "]sys: Checking files (" + file + ")");
			
			// test input file
			if (file_to_kompress.isFile()) 
			{
				if (file_to_kompress.canRead())
				{
					file_to_kompress_reader = new FileInputStream( file_to_kompress );
				}
				else
				{
					System.err.println("[" + this.cmd_no + "]err: Unable to read input file: " + file);
					return;
				}
			}
			else
			{
				System.err.println("[" + this.cmd_no + "]err: Invalid input file: " + file);
				return;
			}
			
			// test output file
			if ( ! kompressed_file.exists())
			{
				if ( ! kompressed_file.createNewFile())
				{
					System.err.println("[" + this.cmd_no + "]err: Could not create output file: " + to);
					return;
				}
			}
			else 
			{
				System.err.println("[" + this.cmd_no + "]sys: Overwriting an existing output file: " + to);
				
				// not fatal error - will overwrite
			}
			
			if (kompressed_file.isFile())
			{
				if (kompressed_file.canWrite())
				{
					kompressed_file_writer = new FileOutputStream( kompressed_file );
				}
				else 
				{
					System.err.println("[" + this.cmd_no + "]err: Unable to write to output file: " + file);
					return;
				}
			}
			else
			{
				System.err.println("[" + this.cmd_no + "]err: Invalid output file: " + to);
				return;
			}
			
			System.out.println("[" + this.cmd_no + "]sys: Analyzing input file (" + file + ")");
			
			// analyze input file now
			Map<Byte, Long> stats = new HashMap<Byte, Long>();
			
			byte[] in = new byte[ Kompressor.read_byte_buffer_size ];
			int bytes_read = 0;
			
			assert(file_to_kompress != null);
			
			while ((bytes_read = file_to_kompress_reader.read(in)) != -1)
			{
				for (int i = 0; i < bytes_read; i++)
				{
					// increment byte counter for the read byte
					Long current_counter = (Long) stats.get((Byte) in[i] );
					if (current_counter == null) 
					{
						current_counter = new Long(0);
					}
					current_counter++;
					stats.put((Byte) in[i], current_counter);
				}
			}
			
			PriorityQueue<Node> queue = new PriorityQueue<Node>();
			
			boolean passed_overflow = false;
			for (byte byte_id = Byte.MIN_VALUE; byte_id <= Byte.MAX_VALUE; byte_id+=1)
			{
				if (passed_overflow) break;
				
				Long final_counter = (Long) stats.get((Byte) byte_id);
				if (final_counter != null)
				{
					// System.out.println("Adding node for byte " + byte_id + " with weight " + final_counter);
					
					Node node = new Node(byte_id, final_counter);
					
					queue.add(node);
				}
				// else - skip non-existent bytes 
				
				if (byte_id == Byte.MAX_VALUE)
				{
					// additional constraint, adding 1 to Byte.MAX_VALUE overflows to Byte.MIN_VALUE
					passed_overflow = true; // prevent infinite loop
				}
			}
			
			while ( queue.size() > 1 )
			{
				assert(queue.size() >= 2);
				
				Node node_1 = queue.poll();
				Node node_2 = queue.poll();
				
				// System.out.println("Polled for node 1: " + node_1.getData() + ", " + node_1.getWeight());
				// System.out.println("Polled for node 2: " + node_2.getData() + ", " + node_2.getWeight());
				
				Node parent = new Node(node_1, node_2);
				
				// replace the two nodes with the new parenting one
				queue.add(parent);
			}
			
			// tree is all build up, so now only retrieve the root:
			Node tree = queue.poll();
			
			// System.out.println("Polled for root: " + tree.getData() + ", " + tree.getWeight());
			
			assert(queue.size() == 0);
			queue = null; // explicitly free resources
			
			System.out.println("[" + this.cmd_no + "]sys: Kompressing input file (" + file + ")");
			
			file_to_kompress_reader.close();
			file_to_kompress_reader = new FileInputStream( file_to_kompress );
			
			Map<Byte, Boolean[]> table = this.computeTable(tree);
			
			// kompress the file now
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Input file " + file + " not found");
		}
		catch (IOException e)
		{
			System.err.println("I/O error in kompressing " + file + " to " + to + ": " + e.getMessage());
		}
		finally 
		{
			try
			{
				if (file_to_kompress_reader != null)
				{
					file_to_kompress_reader.close();
				}
				if (kompressed_file_writer != null)
				{
					kompressed_file_writer.close();
				}
			}
			catch (IOException e)
			{
				// not interesting
			}
		}
	}
	
	private Map<Byte, Boolean[]> computeTable(Node tree)
	{
		Map<Byte, Boolean[]> table = new HashMap<Byte, Boolean[]>();
		
		Stack<Boolean> path = new Stack<Boolean>();
		
		this.computeTableStep(tree, path, table);
		
		return table;
	}

	private void computeTableStep(Node node, Stack<Boolean> path, Map<Byte, Boolean[]> table)
	{
		if (node.isLeaf())
		{
			// leaf node - add path
			table.put(node.getData(), (Boolean[]) path.toArray());
		}
		else
		{
			// not a leaf node - proceed with children - both are not null!
			
			path.push(Boolean.FALSE); // left child
			this.computeTableStep(node.getLeft(), path, table);
			path.pop();
			
			path.push(Boolean.TRUE); // right child
			this.computeTableStep(node.getRight(), path, table);
			path.pop();
		}
	}
	
	private void dekompressFile(String file, String to)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void assignCmdNo(long cmd_no)
	{
		this.cmd_no = cmd_no;
	}
}
