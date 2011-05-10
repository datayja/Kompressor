package eu.gemstonewebdesign.java.kompressor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// command line control loop
		
		boolean interactive = false;
		if (args.length == 0)
		{
			interactive = true;
		}
		else
		{
			for (String arg : args)
			{
				if (arg.equals("-i"))
				{
					interactive = true;
					break;
				}
			}
		}
		
		if (interactive)
		{
			runInteractiveConsole();
		}
		else
		{
			runConsole(args);
		}
		
		// TODO: implement archives by two separate files - the tree serialized and the
		// compressed data
	}
	
	private static void runConsole(String[] args)
	{
		// TODO Auto-generated method stub
		
	}
	
	private static void runInteractiveConsole()
	{
		boolean proceed = true;
		long cmd_no = 0;
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );
		do
		{
			cmd_no++;
			
			// parse input
			System.out.print("["+cmd_no+"]cmd: ");
			
			try
			{
				ConsoleCmd cmd = ConsoleCmd.parse(reader.readLine());
				
				String command = cmd.getCmd();
				if ( command.equals("exit") )
				{
					proceed = false;
				}
				else if ( command.equals("kompress_single") )
				{
					if (cmd.countFileNames() < 1)
					{
						System.out.println("["+cmd_no+"]err: No file specified");
					}
					else
					{
						String filename = cmd.fileName();
						
						Kompressor kompressor = new Kompressor();
						
						kompressor.setAction(KompressorAction.KOMPRESS, filename);
						
						kompressor.setTarget(filename);
						
						kompressor.setMode(KompressorMode.FILE);
						
						kompressor.assignCmdNo(cmd_no);
						
						System.out.println("["+cmd_no+"]sys: Kompressing...");
						
						kompressor.run();
						
						// TODO: check results periodically
					}
				}
				else if ( command.equals("kompress_multi_list") )
				{
					
				}
				else if ( command.equals("kompress_multi_inline") )
				{
					
				}
				else if ( command.equals("dekompress_single") )
				{
					
				}
				else if ( command.equals("dekompress_multi_list") )
				{
					
				}
				else if ( command.equals("dekompress_multi_inline") )
				{
					
				}
				else
				{
					System.out.println("["+cmd_no+"]err: Unknown command");
				}
			}
			catch (IOException e)
			{
				System.out.println("["+cmd_no+"]err: Read failed, retry");
			}
		} while (proceed);

		try
		{
			reader.close();
		}
		catch (IOException e) {}
		
		System.out.println("Kompressor exited peacefully");
	}
}
