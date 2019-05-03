package scholarlyBay;

import scholarlyBay.entities.Accounts;
import scholarlyBay.entities.Cart;
import scholarlyBay.entities.Catalog;
import scholarlyBay.entities.Favorites;
import scholarlyBay.models.Account;

import java.util.Scanner;
/*
/Main class where the application is run, using the main method and getMainMenuOption method./
*/
public class scholarlyBay {
	private static Accounts accounts = new Accounts();
	private static Cart cart = new Cart();
	private static Catalog catalog = new Catalog();
	private static Favorites favorites = new Favorites();

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		Account account = new Account();

		System.out.println("Welcome to ScholarlyBay. Do you have an account? Y or N");
		String hasAccount = in.next();

		if (hasAccount.equalsIgnoreCase("n")) {
			account = Account.createAccount();
			accounts.add(account);
		}
		else {
			account = accounts.signIn(in);
		}

		System.out.println("\n" + account.accountID + "\n");

		boolean cont = true;

		while(cont) {
			cont = getMainMenuOption(account);
		}
	}
/*
/Used to show the main menu options to the user as they browse the application
/almost every method call in the application returns here eventually.
*/
	private static boolean getMainMenuOption(Account account) {
		Scanner in  = new Scanner(System.in);
		if(account.isAdmin){
		    System.out.println("Please choose and option to do from the list below");
		    System.out.println("[1] (Delete CatalogItem) from Catalog");
		    System.out.println("[2] (Edit CatalogItem) in Catalog");
		    System.out.println("[3] (Delete) Account");
		    System.out.println("[4] Send message");
		    System.out.println("[5] Log out");
            System.out.println("[6] More Options");
		    System.out.println("Quit: Q");
		    String option = in.nextLine();
    		int op = -1;
    		try {
    			op = Integer.parseInt(option);
    		} catch (Exception e) {

    		}
    		if(option.equalsIgnoreCase("delete item") || op == 1){
    		    catalog.delete(account);
    		}
    		if(option.equalsIgnoreCase("edit item") || op == 2){
    		    catalog.edit(in, account);
    		}
    		if(option.equalsIgnoreCase("delete account") || op == 3){
    		    accounts.delete(account);
    		}
    		if(option.equalsIgnoreCase("send message") || op == 4){
    		    account.sendUserMessage(in, account);
    		}
    		if(option.equalsIgnoreCase("log out") || op == 5){
    		    main(null);
    			return false;
    		}
    		if (option.equalsIgnoreCase("q") || option.equalsIgnoreCase("quit")) {
    			in.close();
    			return false;
    		}
        }else{
    		System.out.println("Please choose and option to do from the list below");
    		System.out.println("[0] View Catalog");
    		System.out.println("[1] (Add CatalogItem) to Catalog");
    		System.out.println("[2] (Delete CatalogItem) from Catalog");
    		System.out.println("[3] (Edit CatalogItem) in Catalog");
    		System.out.println("[4] View Purchase History");
    		System.out.println("[5] (Delete) Account");
    		System.out.println("[6] View Cart");
    		System.out.println("[7] Send message");
    		System.out.println("[8] View messages");
    		System.out.println("[9] View Items Sold");
    		System.out.println("[10] Request Item");
    		System.out.println("[11] View Requested Item");
    		System.out.println("[12] Log out");
            System.out.println("[13] View Ratings and Comments");
            System.out.println("[14] Transfer to Account");
            System.out.println("[15] View Vendor Sales");
    		System.out.println("Quit: Q");

    		String option = in.nextLine();
    		int op = -1;
    		try {
    			op = Integer.parseInt(option);
    		} catch (Exception e) {

    		}
    		if (option.equalsIgnoreCase("view catalog") || op == 0) {
    			catalog.view(account);
    		}
    		if (option.equalsIgnoreCase("add item") || op == 1) {
    			catalog.add(in, account);
    		}
    		if(option.equalsIgnoreCase("delete item") || op == 2) {
    			catalog.delete(account);
    		}
    		if (option.equalsIgnoreCase("edit item") || op == 3) {
    			catalog.edit(in, account);
    		}
    		if (option.equalsIgnoreCase("view purchase history") || op == 4) {
    			accounts.viewPurchaseHistory(catalog, account.accountID, in);
    		}
    		if(option.equalsIgnoreCase("view sale history") || op == 5){
    			accounts.viewSaleHistory(catalog, account.accountID);
    		}
    		if (option.equalsIgnoreCase("delete account") || op == 5) {
    			accounts.delete(account);
    		}
    		if(option.equalsIgnoreCase("view cart") || op == 6) {
    			cart.view(account, in, catalog, accounts);
    		}
    		if(option.equalsIgnoreCase("send message") || op == 7){
    			account.sendUserMessage(in, account);
    		}
    		if(option.equalsIgnoreCase("view messages" )|| op == 8){
    			account.viewMessages();

    		}
    		if(option.equalsIgnoreCase("view items sold") || op == 9){
    			account.viewItemsSold();
    		}
    		if(option.equalsIgnoreCase("request item") || op == 10){
    			catalog.requestItem(in);
    		}
    		if(option.equalsIgnoreCase("requested items") || op == 11){
    			catalog.showRequestedItems(in);
    		}
    		if (option.equalsIgnoreCase("log out") || op == 12) {
    			main(null);
    			return false;
    		}
            if(option.equalsIgnoreCase("view ratings and comments") ||  op == 13) {
                accounts.viewRatingsAndComments();
            }
            if(option.equalsIgnoreCase("transfer to account")) {
                // credit account with a user-specified amount of money
                System.out.println("How much?");
                double transferAmount = in.nextInt() + 0.0;
                account.creditAmount(transferAmount);
            }
            else if (option.equalsIgnoreCase("view vendor sales")) {
                // view vendor sales and items sold by them
                System.out.println("Total Vendor Sales: $" + catalog.vendorSales);
                System.out.println("Items Sold:");
                for(int i = 0; i < catalog.vendorSalesHistory.size(); i++) {
                    CatalogItem temp = catalog.salesHistory.get(i);
                    System.out.println("id: " + temp.itemID + ", title: " + temp.itemName + ", ISBN: " + temp.serialISBN + ", price: " + temp.price*1.4);
                }
            }
    		if (option.equalsIgnoreCase("q")) {
    			in.close();
    			return false;
    		}
		}

		return true;

	}
}