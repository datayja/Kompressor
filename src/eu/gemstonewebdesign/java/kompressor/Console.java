package eu.gemstonewebdesign.java.kompressor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Console
{
	private static String[] checkFilenamesForMultipleOccurences(
			String[] filenames)
	{
		List<String> checked_filenames = new ArrayList<String>();
		for (String filename : filenames)
		{
			if (!checked_filenames.contains(filename))
			{
				checked_filenames.add(filename);
			}
		}
		String[] names = new String[checked_filenames.size()];
		checked_filenames.toArray(names);
		return names;
	}
	
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		do
		{
			cmd_no++;
			
			// parse input
			System.out.print("[" + cmd_no + "]cmd: ");
			
			try
			{
				ConsoleCmd cmd = ConsoleCmd.parse(reader.readLine(), cmd_no);
				
				String command = cmd.getCmd();
				if (command.equals("exit"))
				{
					proceed = false;
				}
				else if (command.equals("kompress_single"))
				{
					if (cmd.countFileNames() < 1)
					{
						System.out.println("[" + cmd_no
								+ "]err: No file specified");
					}
					else
					{
						String filename = cmd.fileName();
						Kompressor kompressor = new Kompressor();
						kompressor.setAction(KompressorAction.KOMPRESS,
								filename);
						kompressor.setTarget(filename);
						kompressor.setMode(KompressorMode.FILE);
						kompressor.assignCmdNo(cmd_no);
						System.out.println("[" + cmd_no
								+ "]sys: Kompressing...");
						kompressor.start();
						kompressor.join();
					}
				}
				else if (command.equals("kompress_multi"))
				{
					String[] filenames = checkFilenamesForMultipleOccurences(cmd
							.fileNames());
					Kompressor[] kompressors = new Kompressor[filenames.length];
					int i = 0;
					System.out.println("[" + cmd_no
							+ "]sys: Kompressing multiple files...");
					for (String filename : filenames)
					{
						kompressors[i] = new Kompressor();
						kompressors[i].setAction(KompressorAction.KOMPRESS,
								filename);
						kompressors[i].setTarget(filename);
						kompressors[i].setMode(KompressorMode.FILE);
						kompressors[i].assignCmdNo(cmd_no);
						kompressors[i++].start();
					}
					// since all are started, now wait for them to join the main thread
					for (Kompressor kompressor : kompressors)
					{
						kompressor.join();
					}
				}
				else if (command.equals("dekompress_single"))
				{
					if (cmd.countFileNames() < 1)
					{
						System.out.println("[" + cmd_no
								+ "]err: No file specified");
					}
					else
					{
						String filename = cmd.fileName();
						Kompressor kompressor = new Kompressor();
						kompressor.setAction(KompressorAction.DEKOMPRESS,
								filename);
						kompressor.setTarget(filename);
						kompressor.setMode(KompressorMode.FILE);
						kompressor.assignCmdNo(cmd_no);
						System.out.println("[" + cmd_no
								+ "]sys: Dekompressing...");
						kompressor.start();
						kompressor.join();
					}
				}
				else if (command.equals("dekompress_multi"))
				{
					String[] filenames = checkFilenamesForMultipleOccurences(cmd
							.fileNames());
					Kompressor[] kompressors = new Kompressor[filenames.length];
					int i = 0;
					System.out.println("[" + cmd_no
							+ "]sys: Dekompressing multiple files...");
					for (String filename : filenames)
					{
						kompressors[i] = new Kompressor();
						kompressors[i].setAction(KompressorAction.DEKOMPRESS,
								filename);
						kompressors[i].setTarget(filename);
						kompressors[i].setMode(KompressorMode.FILE);
						kompressors[i].assignCmdNo(cmd_no);
						kompressors[i++].start();
					}
					// since all are started, now wait for them to join the main thread
					for (Kompressor kompressor : kompressors)
					{
						kompressor.join();
					}
				}
				else
				{
					System.out.println("[" + cmd_no + "]err: Unknown command");
				}
			}
			catch (IOException e)
			{
				System.out.println("[" + cmd_no + "]err: Read failed, retry");
			}
			catch (InterruptedException e)
			{
				System.out.println("[" + cmd_no + "]err: " + e.getMessage());
				e.printStackTrace();
			}
		} while (proceed);
		
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
		}
		
		System.out.println("Kompressor exited peacefully");
	}
}
