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
	
	@Option(
		name = "--single",
		required = false,
		usage = "get all single postings of all accounts and generate the more detailed postings report (which also contains the balances)")
	private boolean singlePostings = false;
	
	@Option(
		name = "--balances",
		required = false,
		usage = "get the balances of all accounts")
	private boolean balances = false;
	
	@Option(
		name = "--low",
		required = false,
		usage = "check if the balance of an account is below a minimum account")
	private boolean lowBalance = false;
	
	@Option(name = "--low-account",
		required = false,
		metaVar = "<ACCOUNT>",
		usage = "account(s) for --low (multiple times allowed)")
	private List<String> lowAccounts = null;
	
	@Option(name = "--low-minimum",
		required = false,
		metaVar = "<WHOLE NUMBER>",
		usage = "minimum amount for --low")
	private Double lowMinimum = null;
	
	@Option(
		name = "--days",
		required = false,
		metaVar = "<DAYS>",
		usage = "go back this amount of days for fetching the postings, e.g. 7 (the default) - this option is only effective in single postings mode!")
	private int daysToFetchInSingleMode = 7;
	
	@Option(name = "--no-positive", required = false,
		usage = "do not get single postings which contain a positive amount of money")
	private boolean noPositive = false;
	
	@Option(name = "--no-negative", required = false,
		usage = "do not get single postings which contain a negative amount of money")
	private boolean noNegative = false;
	
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
			List<Account> accounts = fetcher.fetchAccountsWithPostings(daysToFetchInSingleMode, noPositive, noNegative);
			// generate report and print it to stdout
			System.out.println(Reporter.generatePostingsReport(accounts));
		}
		
		if (balances) {
			// fetch data from Hibscus server
			List<Account> accounts = fetcher.fetchAccountsWithBalances();
			// generate report and print it to stdout
			System.out.println(Reporter.generateBalancesReport(accounts));
		}
		
		if (lowBalance) {
			// fetch data from Hibscus server
			List<Account> accounts = fetcher.fetchAccountsWithBalances();
			// generate report and print it to stdout
			System.out.println(Reporter.generateLowReport(accounts, lowAccounts, lowMinimum));
		}
	}
	
	public static void die(String message) {
		if (message != null) {
			System.err.println(message);
		}
		System.exit(1);
	}
	
}
