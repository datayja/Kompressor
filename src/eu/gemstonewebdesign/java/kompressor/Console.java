package eu.gemstonewebdesign.java.kompressor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída hlavního ovládacího rozhraní programu.
 * 
 * <p>Pokud se jako jediný parametr uvede "-i", pak je program interaktivní,
 * po spuštění umožňí zadávání příkazů. V hranatých závorkách vždy ukazuje
 * pořadové číslo příkazu, případně i název souboru, kterého se informační
 * zpráva týká. Jinak platí přehled následujících příkazů pro příkazy zadané
 * jako argumenty. Program je interaktivní i v případě, že se neuvede žádný
 * argument ("-i"). </p>
 * 
 * <p>Pokud je na příkazové řádce text typu "[##]cmd: ", pak program
 * očekává zadání příkazů. Řádky typu "[##]sys: text…" pak oznamují
 * zprávy programu, řádky typu "[##]err: text…" pak chybové zprávy.</p>
 * 
 * <p>Upozornění: pokud by některý z příkazů měl přepsat již existující
 * soubor, přepíše ho!</p>
 * 
 * <p>Možné příkazy jsou:</p>
 * <dl>
 * <dt>kompress filename1 [filename2…]</dt>
 * <dd>Komprimovat zadané soubory</dd>
 * <dt>kompress -f filelist1 [filelist2…]</dt>
 * <dd>Komprimovat soubory, které jsou vyjmenované na řádcích zadaných souborů</dd>
 * <dt>dekompress filename1 [filename2…]</dt>
 * <dd>Dekomprimovat zadané soubory</dd>
 * <dt>dekompress -f filelist1 [filelist2…]</dt>
 * <dd>Dekomprimovat soubory, které jsou vyjmenované na řádcích zadaných souborů</dd>
 * <dt>exit</dt>
 * <dd>Ukončit program</dd>
 * </dl>
 * 
 * <p>Program chápe i příkazy bez stylizovaných názvů, compress a decompress.</p>
 * 
 * <p>Po kompresi vzniknou dva nové soubory, 'filename.hkd' a 'filename.hkm', při dekompresi
 * se zadává jen 'filename'.</p>
 */
public class Console
{
	/**
	 * Zkontrolovat zadané názvy souborů pro vícenásobné výskyty.
	 * 
	 * Zajišťuje, aby neprobíhala akce vícekrát na jednom a tom samém souboru.
	 * 
	 * @param filenames Pole názvů ke kontrole
	 * @return Pole zkontrolovaných názvů
	 */
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
	 * Hlavní program.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
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
	}
	
	/**
	 * Spustit neinteraktivní konzoli. 
	 * @param args
	 */
	private static void runConsole(String[] args)
	{
		long cmd_no = 1;
		try
		{
			ConsoleCmd cmd = ConsoleCmd.parseArguments(args, cmd_no);
			
			String command = cmd.getCmd();
			if (command.equals("kompress_single"))
			{
				if (cmd.countFileNames() < 1)
				{
					System.out.println("[" + cmd_no + "]err: No file specified");
				}
				else
				{
					String filename = cmd.fileName();
					Kompressor kompressor = new Kompressor();
					kompressor.setAction(KompressorAction.KOMPRESS, filename);
					kompressor.setTarget(filename);
					kompressor.setMode(KompressorMode.FILE);
					kompressor.assignCmdNo(cmd_no);
					System.out.println("[" + cmd_no + "]sys: Kompressing...");
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
				// počká se na dokončení všech úkolů před zadáním dalších
				for (Kompressor kompressor : kompressors)
				{
					kompressor.join();
				}
			}
			else if (command.equals("dekompress_single"))
			{
				if (cmd.countFileNames() < 1)
				{
					System.out
							.println("[" + cmd_no + "]err: No file specified");
				}
				else
				{
					String filename = cmd.fileName();
					Kompressor kompressor = new Kompressor();
					kompressor.setAction(KompressorAction.DEKOMPRESS, filename);
					kompressor.setTarget(filename);
					kompressor.setMode(KompressorMode.FILE);
					kompressor.assignCmdNo(cmd_no);
					System.out.println("[" + cmd_no + "]sys: Dekompressing...");
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
		catch (InterruptedException e)
		{
			System.out.println("[" + cmd_no + "]err: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Spustit interaktivní konzoli. 
	 */
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
