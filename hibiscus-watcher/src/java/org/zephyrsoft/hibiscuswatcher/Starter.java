package org.zephyrsoft.hibiscuswatcher;

import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.zephyrsoft.hibiscuswatcher.model.Account;

/**
 * Start the application.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Starter {
	
	@Option(name = "--url", required = true, metaVar = "<URL>", usage = "URL of the XML-RPC services of Hibiscus")
	private String url = null;
	
	@Option(name = "--username", required = false, metaVar = "<LOGIN>",
		usage = "username to access the XML-RPC services at the given URL, defaults to 'admin'")
	private String username = "admin";
	
	@Option(name = "--password", required = true, metaVar = "<PASSWORD>",
		usage = "password to access the XML-RPC services at the given URL")
	private String password = null;
	
	public static void main(String[] args) {
		new Starter(args);
	}
	
	private Starter(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			die(1, "");
		}
		
		// fetch data from Hibscus server
		Fetcher fetcher = new Fetcher(url, username, password);
		List<Account> accounts = fetcher.fetchAccountsWithBalances();
		
		// generate report and print it to stdout
		System.out.println(Reporter.generateBalancesReport(accounts));
	}
	
	public static void die(int statusCode, String message) {
		System.err.println(message);
		System.exit(statusCode);
	}
	
}
