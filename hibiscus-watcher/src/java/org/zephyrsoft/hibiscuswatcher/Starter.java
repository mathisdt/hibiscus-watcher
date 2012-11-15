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
	
	@Option(name = "--single", required = false,
		usage = "get all single postings for all accounts and don't generate the balance report")
	private boolean singlePostings = false;
	
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
			die(null);
		}
		
		Fetcher fetcher = new Fetcher(url, username, password);
		
		if (singlePostings) {
			// fetch data from Hibscus server
			List<Account> accounts = fetcher.fetchAccountsWithPostings();
			// generate report and print it to stdout
			System.out.println(Reporter.generatePostingsReport(accounts));
		} else {
			// fetch data from Hibscus server
			List<Account> accounts = fetcher.fetchAccountsWithBalances();
			// generate report and print it to stdout
			System.out.println(Reporter.generateBalancesReport(accounts));
		}
	}
	
	public static void die(String message) {
		if (message != null) {
			System.err.println(message);
		}
		System.exit(1);
	}
	
}
