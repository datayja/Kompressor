package eu.gemstonewebdesign.java.kompressor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleCmd
{
	private String cmd;
	private List<String> fileNames = new ArrayList<String>();
	
	public static ConsoleCmd parse( String cmdline, long cmd_no )
	{
		// hladový regexp - řádek rozdělí co nejdelší řada whitespace znaků
		String[] cmd_tokens = cmdline.split("\\s+");
		
		/*for (String s : cmd_tokens)
		{
			System.out.println(s);
		}*/
		
		String cmd_identifier = "unknown";
		ConsoleCmd cmd = null;
		
		if ( cmd_tokens.length >= 1 )
		{
			if ( cmd_tokens[0].equalsIgnoreCase("exit") )
			{
				cmd_identifier = "exit";
				cmd = new ConsoleCmd(cmd_identifier);
			}
			
			else if ( 
				cmd_tokens[0].equalsIgnoreCase("kompress") || 
				cmd_tokens[0].equalsIgnoreCase("compress") || 
				cmd_tokens[0].equalsIgnoreCase("decompress") || 
				cmd_tokens[0].equalsIgnoreCase("dekompress")
			) {
				// normalize the action name
				if ( cmd_tokens[0].equalsIgnoreCase("compress") )
				{
					cmd_tokens[0] = "kompress";
				}
				else if ( cmd_tokens[0].equalsIgnoreCase("decompress") )
				{
					cmd_tokens[0] = "dekompress";
				}
				
				if ( cmd_tokens.length == 2 )
				{
					cmd_identifier = cmd_tokens[0].toLowerCase() + "_single";
					cmd = new ConsoleCmd(cmd_identifier);
					cmd.fileNames.add(cmd_tokens[1]);
				}
				else if ( cmd_tokens.length >= 3 )
				{
					cmd_identifier = cmd_tokens[0].toLowerCase() + "_multi";
					cmd = new ConsoleCmd(cmd_identifier);
					if (cmd_tokens[1].equalsIgnoreCase("-f"))
					{
						// seznam souborů k (de)kompresi je v jiném souboru po řádcích
						for (int i = 2; i < cmd_tokens.length; i++)
						{
							try
							{
								File listfile = new File(cmd_tokens[i]);
								if (listfile.isFile() && listfile.canRead())
								{
									BufferedReader reader = new BufferedReader( new FileReader(listfile) );
									String filename = null;
									while ((filename = reader.readLine()) != null)
									{
										// ignorujeme prázdné řádky
										if (filename.length() > 0)
										{
											cmd.fileNames.add(filename);
										}
									}
									reader.close();
								}
								else
								{
									System.err.println("["+cmd_no+"]err: File '"+cmd_tokens[i]+"' is not readable or valid");
								}
							}
							catch (IOException e)
							{
								System.err.println("["+cmd_no+"]err: I/O error reading file list '"+cmd_tokens[i]+"': "+e.getMessage()+", skipping.");
							}
						}
					}
					else
					{
						for (int i = 1; i < cmd_tokens.length; i++)
						{
							cmd.fileNames.add(cmd_tokens[i]);
						}
					}
				}
			}
		}
		else
		{
			// default - unknown
			cmd = new ConsoleCmd(cmd_identifier);
		}
		
		return cmd;
	}
	
	private ConsoleCmd() {}
	
	private ConsoleCmd(String cmd)
	{
		this.cmd = cmd.toLowerCase();
	}
	
	public String getCmd()
	{
		return this.cmd;
	}

	public String fileName()
	{
		return this.fileNames.get(0);
	}
	
	public String[] fileNames()
	{
		String[] fns = new String[this.fileNames.size()];
		this.fileNames.toArray(fns);
		return fns;
	}
	
	public int countFileNames()
	{
		return this.fileNames.size();
	}
}
