package scholarlyBay.models;

import scholarlyBay.entities.*;


import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

/*
/Class represents a single account, and the methods a single account can do
*/
public class Account
{
	/**
	 * @Credit at StackOverflow Jason Buberel
	 */
	private final static String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,4}$";
	/**
	 * "House number" Street Address ,City, State, Area code
	 */
	private final static String shippingAddressRegex ="^[0-9]+[a-zA-z .0-9]+[a-zA-Z -]+[a-zA-Z ]+[0-9]+$";
	private final static String passwordRegex = "^[a-zA-Z0-9.~!@#$%^&*()_+=<>?]+$";
	/**
	 * limited inbox space that allows for messages.
	 */
	public String[] inbox;
	
	/**
	 * value used to determine number of messages
	 */
	private static int numberOfMessages = 0;
	/**
	 * When 50 messages are inbox without being deleted, this overrides the earliest message
	 */
	public int messageReset = 0;

	public ArrayList<Integer> ratings;

	public ArrayList<String> comments;

	public double rating;


	public Account(){
		accountID = -1;
	}

	private int idCounter = 0;

	public Account(String firstName, String lastName, String email, String school, String shippingAddress, String password, boolean isAdmin) {
		accountID = idCounter++;
		this.firstName  = firstName;
		this.lastName  = lastName;
		this.email  = email;
		this.school  = school;
		this.shippingAddress  = shippingAddress;
		this.password  = password;
		this.isAdmin = isAdmin;
		// user rating
		this.rating = 0.0;
		// IDs of users who have rated user
		this.ratings = new ArrayList<Integer>();
		// comments on this user
		this.comments = new ArrayList<String>();


	}
	public ArrayList<CatalogItem> cart = new ArrayList<CatalogItem>();
	public String firstName = "";
	public String lastName = "";
	public String email = "";
	public String school = "";
	public String shippingAddress = "";
	public String password = "";
	public String passwordCheck = "";
	public boolean isAdmin = false;
	public ArrayList<CatalogItem> purchaseHistory = new ArrayList<CatalogItem>();
	public ArrayList<CatalogItem> itemsSold = new ArrayList<CatalogItem>();
	public int accountID;
	private double balance;
	public final static String nameRegex = "[ a-zA-Z\'-]*";
	public Favorites favorites = new Favorites();

    /**
     * Account constructor
     */
	public Account(String firstName, String lastName, String email, String school, String shippingAddress, String password, boolean isAdmin, int accountID) {
		this.firstName  = firstName;
		this.lastName  = lastName;
		this.email  = email;
		this.school  = school;
		this.shippingAddress  = shippingAddress;
		this.password  = password;
		this.isAdmin = isAdmin;
		this.accountID = accountID;
		this.inbox = new String[50];

	}

    /**
     *  Deprecated, used by a few of Joe's methods
	*	charge account (amount)
	*	charges the account balance by the amount if possible else throws error
	 */
	public void chargeAccount(double amount) throws IllegalStateException {
		if (this.balance - amount < 0) {
			throw new IllegalStateException();
		} else {
			this.balance -= amount;
		}
	}
	

	public void creditAmount(double amount) {
	    if(amount > 0) {
            balance += amount;
        }
    }
	/**
	 * Send User message
	 * @param in - Scanner used for input
	 */
	public void sendUserMessage(Scanner in, Account senderAccount){
		System.out.println("Please enter the message to be sent to ");
		String message = in.nextLine();
		message = "" + senderAccount.email + " sent the message: " + message;
		System.out.println("Please enter the email address to send to");
		String emailAddress = in.nextLine();
		Account sendTo = Accounts.accounts.stream().filter(x -> x.email.equals(emailAddress)).findFirst().get();
		addMessageToInbox(message,sendTo);

	}

	public void addMessageToInbox(String message, Account sendTo){
		if(sendTo.inbox[49] != null){
			sendTo.inboxMaintenance(message);
			sendTo.inbox[messageReset] = message;
		}
		sendTo.inbox[numberOfMessages] = message;
		sendTo.numberOfMessages++;
		//Find user
		newMessage();
	}
	
	/**
	 * Private method used to elim
	 * @param message
	 * Value that holds message
	 */
	protected void inboxMaintenance(String message){
		if(inbox[49] != null){
			inbox[messageReset] = message; 
			messageReset++;
			if(messageReset == 50){
				messageReset = 0;
			}
		}
	}
	
	/**
	 * Method used to notify the user that an iterm has been sold
	 * @param message - Value that is sent to the user.
	 */
	public void itemSold(String message){
		if(inbox[49] != null){
			inboxMaintenance(message);
			inbox[messageReset] = message;
		}
		inbox[numberOfMessages] = message;
		numberOfMessages++;
		//Find user
		newMessage();
	}

	public void viewMessages(){
		int i = 1;
		for(String message : inbox){
			if(message == null){
				break;
			}
			System.out.println("Message number [" + i + "] : " + message + "\n\n");
			i++;
		}
		Scanner in = new Scanner(System.in);
		System.out.println("Press q to return to the main menu.");
		String input = " ";
		while(!input.equalsIgnoreCase("q")){
			input = in.nextLine();
		}
	}

	public void viewItemsSold(){
		int i = 0;
		for(CatalogItem itemSold : itemsSold){
			System.out.println("[" + i + "] $" + itemSold.price + " " + itemSold.itemName + ", " + "Coupon Code: " + itemSold.coupon.code);
			i++;
		}
		System.out.println("Press q when you wish to return to the main menu.");
		Scanner in = new Scanner(System.in);
		String input = " ";
		while(!input.equals("q")){
			input = in.nextLine();
		}
	}

	//
	/**
	 * Method used to find number of Messages at the last login
	 * @return Number of Messages in User inbx
	 */
	protected int getLastLogInMessageCount(){
		return numberOfMessages;
	}
	
	
	/**
	 * Method used to find number of Messages currently in inbox.
	 * @return Number of Messages currently in User inbox.
	 */
	protected int getCurrentMessageCount(){
		return numberOfMessages;
	}
	
	/**
	 * Method sent at login if user has a new Message.
	 */
	protected void newMessage(){
		if(getLastLogInMessageCount() != getCurrentMessageCount())
			System.out.println("Congrats. You have a new Message");
	}
	
	public static Account createAccount() {
		Scanner in = new Scanner(System.in);
	    Pattern name = Pattern.compile(nameRegex);
		Pattern email = Pattern.compile(emailRegex);
		Pattern pass = Pattern.compile(passwordRegex);
		Pattern shippingAdd = Pattern.compile(shippingAddressRegex);
		
		System.out.println("Welcome to ScholarBay");
		try {
			TimeUnit.SECONDS.sleep(3);//Credit @Pshemo @ StackOverflow
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("To create an Account we will need some information");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.print("If this is okay, we'll get you started on your way to ");
		System.out.print("selling and buying today!");
		System.out.println();
		System.out.print("So, the first thing we'll need is your first name:");
		
		
    	String tempFirstName= "";
		String tempLastName = "";
		String tempSchool = "" ;
		String tempEmail= "";
		String tempPassword = "";
		String tempPasswordCheck = "";
		String tempShippingAddress = "";
		
		Matcher nameMatcher;
		Matcher emailMatcher;
		Matcher passwordMatcher;
		Matcher shippingAddressMatcher;
		
		
		//While loop dedicated to checking first name value
		boolean nameVal = false;
		while(!nameVal){
			if(in.hasNext()){
				tempFirstName = in.next();
				nameMatcher = name.matcher(tempFirstName);
				nameVal = nameMatcher.matches();
			}
			if(!nameVal){
				System.out.println("The only accepted values are a-z,A-Z, ,-, and '");
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Please enter your first name:");
			}
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Great. Now lets get your last name:");
		
		//While loop dedicated to checking last name value
		nameVal = false;
		while(!nameVal){
			if(in.hasNext()){
				tempLastName = in.next();
				nameMatcher = name.matcher(tempLastName);
				nameVal = nameMatcher.matches();
			}
			if(!nameVal){
				System.out.println("The only accepted values are a-z,A-Z, ,-, and '");
				System.out.println("Please enter your last name:");
			}
		}
		
		boolean schoolVal = false;

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Thank you. To make your experience smoother, let's get your school:");
		
		//While loop designed to limit school value
		while(!schoolVal){
			in.nextLine();
			if(in.hasNext()){
				tempSchool = in.nextLine();
				nameMatcher = name.matcher(tempSchool);
				schoolVal = nameMatcher.matches();
			}
			if(!schoolVal){
				System.out.println("Please enter a valid school");
				System.out.println("Only letters, spaces, and dashes are valid characters");
			}	
		}
		
		
		
		boolean shippingVal = false;

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Thank you. Let's have a shipping address. Example: 1234 Seasame Street Ames Iowa 50222");
		
		//While loop designed to check shipping address
		while(!shippingVal){
			if(in.hasNext()){
				tempShippingAddress = in.nextLine();
				shippingAddressMatcher = shippingAdd.matcher(tempShippingAddress);
				shippingVal = shippingAddressMatcher.matches();
			}
			if(!shippingVal){
				System.out.println("You have given an invalid shipping Address.");
				System.out.println("Example: 1234 Sesame Street Ames Iowa 50010");
			}
		}
		
		
		
		boolean emailVal = false;

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Thank you. Please provide a valid email. Example: joe.stat@yak.com");
		
		//While loop designed to validate email format
		while(!emailVal){
			if(in.hasNext()){
				tempEmail = in.next();
				emailMatcher = email.matcher(tempEmail);
				emailVal = emailMatcher.matches();
			}
			if(!emailVal){
				System.out.println("Invalid response. Please provide an email with the form: \"joe.stat@yak.com\"");
			}
		}
		
		
		boolean passVal = false;
		boolean passCheckVal = false;

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Thank you. Please pick a password. Your password can be whatever you want");
		System.out.println("Available characters to use are all letters, numbers and these special characters: .~!@#$%^&*()_+=<>?");
		
		//While loop designed check both the password and passwordCheck value to ensure correct values.
		while((!passVal || !passCheckVal) || !(tempPassword.equals(tempPasswordCheck))){
			if(in.hasNext()){
				tempPassword = in.next();
				passwordMatcher = pass.matcher(tempPassword);
				passVal = passwordMatcher.matches();
				System.out.println("Please retype your password.");
				tempPasswordCheck = in.next();
				passwordMatcher = pass.matcher(tempPasswordCheck);
				passCheckVal = passwordMatcher.matches();
			}
			if(!passVal || !passCheckVal || !(tempPassword.equals(tempPasswordCheck))){
				System.out.println("You have provided invalid information. Please provide valid passwords.");
			}
			
		}
		
		System.out.println("Thank you. Your account has been created.");
		

		
		return new Account(tempFirstName,tempLastName,tempEmail,tempSchool,tempShippingAddress,tempPassword,false, Accounts.getIDCounter());



	}
	
	
}