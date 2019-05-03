package scholarlyBay.entities;

import scholarlyBay.models.Account;
import scholarlyBay.models.CatalogItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/*
/This class represents method calls on the database list of accounts, not individual account methods.
*/
public class Accounts {
    public static ArrayList<Account> accounts = new ArrayList<>();
    private static int idCounter = 0;
    private static String email = "";
    private static String password = "";
    //Adds a fresh account for testing
    public Accounts(){
        accounts.add(new Account("ad","min","admin@SB.com","NA",null,"coms362",true,idCounter++));
        accounts.add(new Account("R", "Last", "r@r.com", "School", null, "r", false, idCounter++));
        accounts.add(new Account("T", "Last", "t@t.com", "School", null, "t", false,idCounter++));
    }

    public void add(Account account){

    }


    /**
     * Adds an anonymous comment to the account of any user. Comments may only be
     * made by users who have previously purchased fromor sold to this seller.
     * @param commenteeID the user to be commented on.
     */
    public void commentOnUser(int commenteeID) {

        // look for account matching the given id;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).accountID == commenteeID) {
                Account commentee = accounts.get(i);
                // Instantiate comments list if this is first comment
                if (null == commentee.comments) {
                    commentee.comments = new ArrayList<>();
                }
                // prompt user for comment
                Scanner in = new Scanner(System.in);
                System.out.println("Please enter your comment, or 'x' to cancel");
                String comment = in.nextLine();
                if (comment.toLowerCase().equals("x")) {
                    System.out.println("Cancelling...");
                }
                else {
                    System.out.println("Adding comment");
                    commentee.comments.add(comment);
                }
            }
        }

    }

    public static int getIDCounter(){
        idCounter++;
        return idCounter;
    }
/*
/Deletes the given account from the database if it exists.
*/
    public void delete(Account account){
        System.out.println("Are you sure you want to do that? This action cannot be undone.");
        System.out.println("Please type [Y]es to confirm account deletion, or press any other key to cancel.");
        Scanner scanIn = new Scanner(System.in);
        String response = scanIn.next();
        if (response.toUpperCase() == ("Y")) {
            // success scenario - deletion of a previously valid account
            if (accounts.contains(account)) {
                accounts.remove(account);
            }
            System.out.println("Account not found");
        }
        System.out.println("Delection cancelled- glad you're staying with us!");
    }
/*
/Signs an account into the application to see all who are currently using the application
*/
    public Account signIn(Scanner in){
        System.out.println("Please provide your email.");
        email = in.next();
        System.out.println("Please provide your password.");
        password = in.next();

        while (accounts.stream().noneMatch(acc -> email.equals(acc.email) && acc.password.equals(password))) {
            System.out.println("Please provide your email.");
            email = in.next();
            System.out.println("Please provide your password.");
            password = in.next();
        }

        return accounts.stream().filter(acc -> acc.email.equals(email) && acc.password.equals(password)).findFirst().get();
    }
/*
/Allows an account to view all of the items they have purchases on this account
/Also allows users to report scams and return items here
*/
    public void viewPurchaseHistory(Catalog catalog, int currentAccountID, Scanner in){
        catalog.getDataset()
                .stream()
                .filter(x -> x.buyerID == currentAccountID)
                .forEach(catalogItem -> {
                    System.out.println(String.format("ID: %d   %s   $%f   %s", catalogItem.itemID, catalogItem.itemName, catalogItem.price, catalogItem.serialISBN));
                });
        System.out.println("Would you like to return any of these items? (y/n)");
        if(in.nextLine().equals("n"))
        {
            System.out.println("Would you like to report a scam? (y/n)");
            if(in.nextLine().equals("n"))
            {
                System.out.println("Would you like to rate or comment on a user you purchased from? (y/n)");

                if(in.nextLine().equals("n")) {
                    return;
                }
                else {
                    System.out.println("Which item was the transaction for? (enter item ID)");
                    int purchasedItem = Integer.parseInt(in.nextLine());
                    int sellerID = catalog.getDataset().stream().filter(x -> x.itemID == purchasedItem).findFirst().get().sellerID;
                    rateUser(sellerID, currentAccountID);
                    commentOnUser(sellerID);
                }
            }
            else
            {
                System.out.println("Which item would you like to report as a scam? (enter item ID)");
                int scamItem = Integer.parseInt(in.nextLine());
                reportScam(catalog.getDataset().stream().filter(x -> x.itemID == scamItem && x.buyerID==currentAccountID).findFirst().get());
            }
        }
        else
        {
            System.out.println("Which item would you like to return? (enter item ID)");
            int retItem = Integer.parseInt(in.nextLine());
            returnItem(catalog.getDataset().stream().filter(x -> x.itemID == retItem && x.buyerID==currentAccountID).findFirst().get());
        }
    }

    /**
     * Prompts one user (rater) to rate another (ratee).
     *
     * @param rateeID id of account to be rated
     * @param raterID id of account doing the rating
     */
    public void rateUser(int rateeID, int raterID) {
        Account ratee;
        Scanner in = new Scanner(System.in);
        for(int i = 0; i < accounts.size(); i++) {
            if(rateeID == accounts.get(i).accountID) {
                ratee = accounts.get(i);
                int input = 99;
                while (input != 0) {
                    System.out.println("Submit your rating for this user on a scale of 1 to 5, or enter 0 to cancel.");
                    // note that ratings is a list of accounts that have rated an item, not the actual rating.
                    input = in.nextInt();
                    // check that user has never rated this item before
                    if (null != ratee.ratings && ratee.ratings.contains(raterID)) {
                        System.out.println("You have already rated this seller");
                        input = 0;
                        break;
                        // we now need to know the user's current rating
                    }
                    else {
                        Double rating = (input + 0.0); // cheesing it since java is so picky about types
                        if ((rating <= 5) && (rating >= 1)) {
                            // special case for submission of first rating
                            if (ratee.ratings == null) {
                                ratee.rating = rating;
                                ratee.ratings = new ArrayList<Integer>();
                                ratee.ratings.add(raterID);
                                break;
                            } else {
                                // general case - adding another rating

                                double oldRating = ratee.rating;
                                ArrayList<Integer> ratings = ratee.ratings;
                                ratee.rating = ((oldRating * ratings.size() + rating) / (ratings.size() + 1));
                                ratee.ratings.add(raterID);
                                break;
                            }
                            // cancels rating process
                        } else if (rating == 0) {
                            System.out.println("Returning to transaction history...");
                            break;
                        } else {
                            System.out.println("Please enter a valid rating.");
                        }
                    }
                }
            }
        }
    }

/*
/Returns an item to the catalog
*/
    public void returnItem(CatalogItem itemReturn)
    {
        itemReturn.buyerID = -1;
        System.out.println(itemReturn.itemName + " has been returned");
        System.out.println("Your account has been refunded " + (itemReturn.price - itemReturn.price * (itemReturn.coupon.percentOff / 100)));
    }
/*
/Reports the given item as a scam for admins to view later
*/
    public void reportScam(CatalogItem itemScam)
    {
        System.out.println("Your purchase " + itemScam.itemName + " has been reported as a scam");

        //Make sure this works
        Account.addMessageToInbox("We have found possible scams.",account);
        returnItem(itemScam);
        itemScam.buyerID = 0; //so it doesn't show up in the catalog
    }

    /**
     * Display a list of all accounts, some basic info, and their ratings.
     * Invites current user to pick an account from the list and view comments along with the rating.
     */
    public void viewRatingsAndComments() {
        System.out.println("Here are all currently registered users");
        // display users
        for(int i = 0; i < accounts.size(); i++) {
            Account temp = accounts.get(i);
            System.out.println("id: " + temp.accountID + "   Name: " + temp.firstName + " " + temp.lastName + "   Rating: " + temp.rating);
        }
        System.out.println("Which user would you like to view? (enter ID) ");
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();
        // find desired account and display its rating and comments
        for (int i=0; i < accounts.size(); i++) {
            if (accounts.get(i).accountID == id) {
                Account user = accounts.get(i);
                System.out.println("Rating: " + user.rating);
                System.out.println("Comments" + user.comments.toString());
            }
        }
    }

/*
/Lets an account view all the items that they have listed on the catalog that have been bought.
*/
    public void viewSaleHistory(Catalog catalog, int accountID){
        Scanner in = new Scanner(System.in);
        catalog.getDataset()
                .stream()
                .filter(x -> x.sellerID == accountID && x.buyerID != -1) // buyerID == -1 -> unsold
                .forEach(catalogItem -> {
                    System.out.println(String.format("ID: %d   %s   $%f   %s", catalogItem.itemID, catalogItem.itemName, catalogItem.price, catalogItem.serialISBN));
                });
        System.out.println("Would you like to rate or comment on a user you sold an item to? (y/n) ");
        if (in.nextLine().equals("n")) {
            return;
        }
        else {
            System.out.println("Which sale would you like to review? (enter item ID) ");
            int soldItem = Integer.parseInt(in.nextLine());
            int buyerID = catalog.getDataset().stream().filter(x -> x.itemID == soldItem).findFirst().get().buyerID;
            rateUser(buyerID, accountID);
            System.out.println("Would you like to comment on this buyer? (y/n) ");
            if (in.nextLine().toLowerCase().equals("y")) {
                commentOnUser(buyerID);
            }
            else {
                return;
            }
        }

    }
/*
/simply returns the accounts in the database
*/
    public ArrayList<Account> getDataset(){
        return accounts;
    }
}