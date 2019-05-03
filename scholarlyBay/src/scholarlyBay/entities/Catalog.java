package scholarlyBay.entities;

import scholarlyBay.models.*;


import java.util.*;
import java.util.stream.Collectors;

public class Catalog {
    public ArrayList<CatalogItem> catalog = new ArrayList<>();
    public ArrayList<CatalogItem> requestedItems = new ArrayList<>();
    private static int idCounter = 0;
    public static ArrayList<CatalogItem> salesHistory = new ArrayList<>();
    public double vendorSales = 0.0;
    public ArrayList<CatalogItem> vendorSalesHistory = new ArrayList<>();

    // Sample data for catalog
    public Catalog() {
        catalog.add(new CatalogItem(idCounter++, 0, -1, "Algebra Book", "1111111", 10.00, new Coupon(0, 0)));
        catalog.add(new CatalogItem(idCounter++, 0, -1, "Reading Book", "1111112", 20.00, new Coupon(123, 20)));
        catalog.add(new CatalogItem(idCounter++, 0, -1, "Algebra II Book", "1111113", 10.00, new Coupon(142, 50)));
        salesHistory.add(new CatalogItem(idCounter++, 0, -1, "My Old Algebra Book", "1111111", 11.00, new Coupon(0, 0)));
    }

    /**
     * Promots user to enter info about item to request
     * @param - scanner
     */
    public void requestItem(Scanner in) {
        System.out.println("Enter name of item to request: ");
        String name = in.nextLine();
        requestedItems.add(new CatalogItem(idCounter++, -1, -1, name, "0", 0.00, new Coupon(0, 0), 0));
    }

    /**
     * shows all items that have been requested
     * @param - scanner
     */
    public void showRequestedItems(Scanner in) {
        if (requestedItems.size() == 0) {
            System.out.println("No requested items");
            return;
        }
        for (int i = 0; i < requestedItems.size(); i++) {
            System.out.println("[" + i + "] " + requestedItems.get(i).itemName);
        }
        System.out.println("Enter [item #]: List This Item, [q]: quit");
        String input = in.nextLine();
        if (input.equals("q")) {
            System.out.println("Quitting");
            return;
        }
        try {
            int itemNumber = Integer.parseInt(input);
            CatalogItem selectedCatalogItem = requestedItems.get(itemNumber);
            provideRequestedItem(in, selectedCatalogItem, selectedCatalogItem.sellerID);
            input = "";
        } catch (NumberFormatException n) {
            System.out.println("Invalid Input");
        } catch (IndexOutOfBoundsException i) {
            System.out.println("CatalogItem out of range: " + i.getMessage());
        }
    }

    /**
     * shows all items that have been requested
     * @param - scanner
     * @param - item to be provided
     * @param - seller id.
     */
    public void provideRequestedItem(Scanner in, CatalogItem item, int sellerID) {
        System.out.println("Enter a price: ");
        int price = in.nextInt();
        item.price = price;
        item.sellerID = sellerID;
        catalog.add(item);
        requestedItems.remove(item);
    }

/*
/Adds and item to the catalog as provided by the user, linking the item added to the account that added item
*/
    public void add(Scanner in, Account account) {
        System.out.println("What is the name of the item being added?");
        String name = in.nextLine();
        System.out.println("What is the ISBN number of the item?");
        String isbn = in.nextLine();
        if(!salesHistory.isEmpty()) {
            ArrayList<CatalogItem> matchingItems = new ArrayList<>();
            for (CatalogItem item : salesHistory) {
                if (item.serialISBN.equals(isbn)) {
                    matchingItems.add(item);
                }
            }
            System.out.println("We found [" + matchingItems.size() + "] previous sales of this item");
            if (matchingItems.size() > 0) {
                for (CatalogItem item : matchingItems) {
                    System.out.println(item.itemName + ": $" + item.price);
                }
            }
        }
        else {
            System.out.println("No previous sales found for this item");
        }


        System.out.println("What is the price of the item? (don't put a $)");
        String price = in.nextLine();
        System.out.println("Would you like to add a coupon code? (0 if no, enter the coupon code otherwise)");
        int couponCode = 0;
        double percentOff = 0;
        try {
            couponCode = Integer.parseInt(in.nextLine());
            if (couponCode < 0) {
                couponCode = 0;
            }
            if (couponCode == 0) {
                percentOff = 0;
            } else {
                System.out.println("What is the percent that the coupon takes off the price of the item. (Give as a number less than 100 but greater than 0");
                percentOff = Double.parseDouble(in.nextLine());
                while (percentOff < 0 || percentOff > 100) {
                    System.out.println("Inputted perecent is not valid retry. (Give as a number less than 100 but greater than 0");
                    percentOff = Double.parseDouble(in.nextLine());
                }
            }
        } catch (Exception e) {
            couponCode = 0;
            percentOff = 0;
        }

        System.out.println("Would you like to add a fee of sale? (0 if no, enter the fee value otherwise)");
        double fee = 0;

        try {
            fee = Double.parseDouble(in.nextLine());
            if (fee < 0) {
                fee = 0;
            }

        } catch (Exception e) {
            fee = 0;

        }

        System.out.println("What type of item is this? (book, pdf, cd, etc)");
        String itemCat = in.nextLine();

        CatalogItem newCatalogItem = new CatalogItem(idCounter++, account.accountID, -1, name, itemCat, isbn, Double.parseDouble(price), new Coupon(couponCode, percentOff),fee);
        newCatalogItem.trackingNumber = System.currentTimeMillis();
        System.out.println("Tracking Details Added: " + newCatalogItem.trackingNumber + " [" + newCatalogItem.itemName + "]");
        catalog.add(newCatalogItem);
        System.out.println("CatalogItem added!");
    }
/*
/Deletes the item provided by the account given that the item exists and the item belongs to the account trying to delete it
*/
    public void delete(Account account) {
        System.out.println("Please give the name of the item to delete ");
        Scanner in = new Scanner(System.in);

        System.out.println("Please give the itemID of the item to be deleted or press c to cancel.");
        String itemID = in.nextLine();
        if (itemID.equalsIgnoreCase("c")) {
            return;
        }

        catalog.removeIf(catalogItem -> catalogItem.itemID == Integer.parseInt(itemID) && catalogItem.sellerID == account.accountID);

        System.out.println("CatalogItem deleted successfully.");
    }
/*
/Allows an account to edit a specified item they are tryng to sell
*/
    public void edit(Scanner in, Account account) {
        System.out.println("What is the name of the item you want to edit?");
        String itemName = in.nextLine();
        if (catalog.stream().noneMatch(i -> i.itemName.equals(itemName) && i.sellerID == account.accountID)) {
            System.out.println("That item does not exist in the catalog, try adding it.");
        } else {
            CatalogItem editCatalogItem = catalog.stream().filter(i -> i.itemName.equals(itemName) && i.sellerID == account.accountID).findFirst().get();
            System.out.println("What property would you like to edit?");
            System.out.println("Price : p");
            System.out.println("Name: n");
            System.out.println("Coupon Code: c");
            System.out.println("Fee: f");
            System.out.println("Type of Item: i");
            if (editCatalogItem.buyerID == -1) {
                System.out.println("Tracking: t");
            }
            String choice = in.nextLine();
            if (choice.equalsIgnoreCase("p")) {
                System.out.println("What is the new price?");
                String newPrice = in.nextLine();
                editExistingItem(editCatalogItem, "price", newPrice);
            }
            if (choice.equalsIgnoreCase("n")) {
                System.out.println("What is the new name?");
                String newName = in.nextLine();
                editExistingItem(editCatalogItem, "itemname", newName);
            }
            if (choice.equalsIgnoreCase("c")) {
                System.out.println("What is the coupon code?");
                String couponCode = in.nextLine();
                editExistingItem(editCatalogItem, "coupon", couponCode);
            }
            if (choice.equalsIgnoreCase("i"))
            {
                System.out.println("What is the new item type?");
                String itemType = in.nextLine();
                editExistingItem(editCatalogItem, "itemCategory", itemType);
            }
            if (choice.equalsIgnoreCase("t")) {
                editCatalogItem.trackingNumber = System.currentTimeMillis();
                System.out.println("Tracking Details Added: " + editCatalogItem.trackingNumber + " [" + editCatalogItem.itemName + "]");
            }
            if (choice.equalsIgnoreCase("f")) {
                System.out.println("What is the new fee?");
                String newFee = in.nextLine();
                editExistingItem(editCatalogItem, "fee", newFee);
            }
        }
    }

  /*
  /Where the account provides the item and property to edit, then sends it off to the editCatalogItem method
  */
    private void editExistingItem(CatalogItem catalogItem, String property, String value) {
        if (catalog.contains(catalogItem)) {
            for (CatalogItem i : catalog) {
                if (i.equals(catalogItem)) {
                    if (property.equalsIgnoreCase("itemname")) {
                        i.itemName = value;
                    } else if (property.equalsIgnoreCase("price")) {
                        try {
                            double doubleVal = Double.parseDouble(value);
                            i.price = doubleVal;
                        } catch (Exception e) {
                            System.out.println("Issue with value provided for price.");
                        }
                    }
                    else if(property.equalsIgnoreCase("itemCategory"))
                    {
                        i.itemCategory = value;
                    }
                    else if(property.equalsIgnoreCase("fee")){
                        try {
                            double doubleVal = Double.parseDouble(value);
                            i.fee = doubleVal;
                        } catch (Exception e) {
                            System.out.println("Issue with value provided for the fee.");
                        }
                    }
                    else if (property.equalsIgnoreCase("coupon")) {
                        int couponCode = 0;
                        double percentOff = 0;
                        try {
                            couponCode = Integer.parseInt(value);

                            if (couponCode < 0) {
                                couponCode = 0;
                                percentOff = 0;
                            }
                            if (couponCode > 0) {
                                Scanner in = new Scanner(System.in);
                                System.out.println("What is the percent that the coupon takes off the price of the item. (Give as a number less than 100 but greater than 0");
                                percentOff = Double.parseDouble(in.nextLine());
                            }
                        } catch (Exception e) {
                            couponCode = 0;
                        }
                        i.coupon = new Coupon(couponCode, percentOff);
                    }

                }
            }

        } else {
            System.out.println("The catalogItem to be edited does not exist in the catalog.");
        }
    }
/*
/Allows users to view the all items in the catalog that have yet to be sold
/From here it allows the users to interact with items in the catalog, like adding them to the cart
*/
    public void view(Account account) {
        if (catalog.size() == 0) {
            System.out.println("[No Items To Be Displayed]");
            return;
        }
        Scanner in = new Scanner(System.in);
        if(!account.favorites.favorites.isEmpty()){
            System.out.println("Would you like to view the catalog [c] or your favorites [f]?");
            String input = in.nextLine();
            if(input.equalsIgnoreCase("f")){
                input = "";
                while (!input.equals("q")) {
                    // Clear console
                    System.out.println("CatalogItem Catalog:");
                    for (int i = 0; i < account.favorites.favorites.size(); i++) {
                        Favorite tempFav = account.favorites.favorites.get(i);
                        int tempId = tempFav.catalogItemID;
                        CatalogItem temp = catalog.stream().filter(x -> x.itemID == tempId).findFirst().get();
                        if (temp.buyerID == -1) {
                            System.out.println("[" + i + "] $" + temp.price + " " + temp.itemName + ", " + "Coupon Code: " + temp.coupon.code + " , " + "Fee: " + temp.fee);
                        }
                    }
                    System.out.println("Enter [item #]: Show CatalogItem, [q]: Quit: ");
                    input = in.nextLine();
                    if (input.equals("q")) {
                        System.out.println("Quitting");
                        break;
                    }
                    try {
                        int itemNumber = Integer.parseInt(input);
                        CatalogItem selectedCatalogItem = catalog.get(itemNumber);
                        viewItem(selectedCatalogItem, account);
                        input = "";
                    } catch (NumberFormatException n) {
                        switch (input.toLowerCase()) {
                            case "a":
                                add(in, account);
                                break;
                            default:
                                System.out.println("Invalid Input");
                                break;
                        }
                    } catch (IndexOutOfBoundsException i) {
                        System.out.println("CatalogItem out of range: " + i.getMessage());
                    }
                }
            }
            return;
        }

        String input = "";
        while (!input.equals("q")) {
            // Clear console
            System.out.println("CatalogItem Catalog:");
            for (int i = 0; i < catalog.size(); i++) {
                CatalogItem temp = catalog.get(i);
                // Since we can purchase from a vendor, display all items, sold or not.
                String availableUsed = "no";
                if (temp.buyerID == -1) {
                    availableUsed = "yes";
                }
                System.out.println("[" + i + "] $" + temp.price + " " + temp.itemName + ", " + "Coupon Code: " + temp.coupon.code + ", " + "Available Used? " + availableUsed + " , " + "Fee: " + temp.fee);
            }
            System.out.println("Enter [item #]: Show CatalogItem, [p]: Filter by price, [t]: Filter by type, [n]: Filter by name, [q]: Quit: ");
            input = in.nextLine();
            if (input.equals("q")) {
                System.out.println("Quitting");
                break;
            }
            try {
                int itemNumber = Integer.parseInt(input);
                CatalogItem selectedCatalogItem = catalog.get(itemNumber);
                viewItem(selectedCatalogItem, account);
                input = "";
            } catch (NumberFormatException n) {
                switch (input.toLowerCase()) {
                    case "a":
                        add(in, account);
                        break;
                    case "p":
                        filterByItemPrice(in);
                        break;
                    case "t":
                        filterByItemType(in);
                        break;
                    case "n":
                        searchByItemName(in);
                        break;
                    default:
                        System.out.println("Invalid Input");
                        break;
                }
            } catch (IndexOutOfBoundsException i) {
                System.out.println("CatalogItem out of range: " + i.getMessage());
            }
        }

    }
/*
/Allows the user to view a specific item in the catalog, showing more detail than what would have been shown in the catalog view
*/
    private void viewItem(CatalogItem displayCatalogItem, Account currentAccount) {
        Scanner in = new Scanner(System.in);
        String input = "";
        while (!input.equals("q")) {
            System.out.println("\n###\n###\n" + displayCatalogItem.itemName + "\n");
            System.out.println("$" + displayCatalogItem.price);
            System.out.println("Seller: " + displayCatalogItem.sellerID);
            System.out.println("Coupon Code: " + displayCatalogItem.coupon.code);
            if (displayCatalogItem.ratings != null) {
                System.out.println("Rating: " + displayCatalogItem.rating);
            }
            else {
                System.out.println("This item does not yet have a rating");
            }
            if (displayCatalogItem.comments != null) {
                System.out.println("Comments: ");
                for (String i : displayCatalogItem.comments) {
                    System.out.println(i);
                }
            } else {
                System.out.println("There are no comments on this item");
            }
            while (!input.equals("s") && !input.equals("c") && !input.equals("r") && !input.equals("b") && !input.equals("q") && !input.equals("e")) {
                if (displayCatalogItem.buyerID == 0) {
                    System.out.println("Item is spoken for, [q]: Quit");
                } else{
                    System.out.println("Enter [b]: Buy, [m]: Contact Seller, [r]: Rate, [c]: Add a comment, [s]: Save, [q]: Quit: ");
                }
                input = in.nextLine();
                if (input.equals("b")) {
                    currentAccount.cart.add(displayCatalogItem);
                }
            }
            switch (input) {
                case "b":
                    //Buy
                    if (displayCatalogItem.buyerID != 0) {
                        try {
                            System.out.println("Purchase new from vendor? (y/n) Price: " + displayCatalogItem.price*1.4);
                            String response = in.next();
                            if (response.toLowerCase().equals("y")) {
                                // case 1: vendor purchase. Does not remove item from catalog.
                                currentAccount.chargeAccount(displayCatalogItem.price*1.4);
                                vendorSales += displayCatalogItem.price * 1.4;
                                System.out.println("Account [" + currentAccount.accountID + "] " + "Successfully charged: $" + displayCatalogItem.price*1.4);
                                vendorSalesHistory.add(displayCatalogItem);
                            }
                            else {
                                // case 2: standard purchase
                                currentAccount.chargeAccount(displayCatalogItem.price);
                                displayCatalogItem.buyerID = currentAccount.accountID;
                                System.out.println("Account [" + currentAccount.accountID + "] " + "Successfully charged: $" + displayCatalogItem.price);
                                salesHistory.add(displayCatalogItem);
                            }
                        } catch (Exception e) {
                            System.out.println("Insufficient Funds");
                        } finally {
                            input = "q";
                        }
                    }
                    input = "q";
                    break;

                case "s":
                    currentAccount.favorites.add(displayCatalogItem.itemID, currentAccount.accountID);
                    System.out.println("The item has been saved");
                    input = "q";
                    break;

                case "r":
                    // rating an item
                   rateItem(in, displayCatalogItem, currentAccount);
                    input = "q";
                    break;

                case "c":
                    // add a comment
                    addComment(in, displayCatalogItem, currentAccount);
                    input = "q";
                    break;


                case "m":
                    Account.sendUserMessage(in,currentAccount);
                    System.out.println("Your message has been sent to the Seller");
                input = "q";
                break;

                case "e":
                    //Edit
                    if (displayCatalogItem.sellerID != currentAccount.accountID) {
                        input = " ";
                    } else {
                        edit(in, currentAccount);
                        input = "q";
                    }
                    break;
            }
        }
    }

    /*
    /Allows a user to rate an item in the catalog from 1 to 5 stars
    */
    public void rateItem(Scanner in, CatalogItem item, Account account) {
        int input = 99;
        while (input != 0) {
            System.out.println("Submit your rating for this item on a scale of 1 to 5, or enter 0 to cancel.");
            // note that ratings is a list of accounts that have rated an item, not the actual rating.
            input = in.nextInt();
            // check that user has never rated this item before
            if (null != item.ratings && item.ratings.contains(account.accountID)) {
                System.out.println("You have already rated this item ");
                input = 0;
                break;
            // we now need to know the item's current rating
            }
            else {
                Double rating = (input + 0.0); // cheesing it since java is so picky about types
                if ((rating <= 5) && (rating >= 1)) {
                    // special case for submission of first rating
                    if (null == item.ratings) {
                        item.rating = rating;
                        item.ratings = new ArrayList<Integer>();
                        item.ratings.add(account.accountID);
                        break;
                    } else {
                        // general case - adding another rating

                        double oldRating = item.rating;
                        ArrayList<Integer> ratings = item.ratings;
                        item.rating = ((oldRating * ratings.size() + rating) / (ratings.size() + 1));
                        item.ratings.add(account.accountID);
                        break;
                    }
                } else if (rating == 0) {
                    System.out.println("Returning to item view...");
                    break;
                } else {
                    System.out.println("Please enter a valid rating.");
                }
            }
        }
    }

/*
/Allows users to add comments to items
*/
    public void addComment(Scanner in, CatalogItem item, Account account) {
        if (item.comments == null) {
            item.comments = new ArrayList<>();
        }
        // either way, get the new comment and add it to the list.
        System.out.println("Please enter your comment about this item");
        String comment = in.nextLine();
        item.comments.add(account.firstName + ": " + comment);
    }

    public void searchByItemName(Scanner in){
        System.out.println("Please enter the name of the book");
        String name = in.nextLine();
        if(catalog.stream().noneMatch(item -> item.itemName.equals(name))){
            System.out.println("No item found with name: " + name);
        }
        else{
            System.out.println("Found item: ");
            CatalogItem foundItem = catalog.stream().filter(item -> item.itemName.equals(name)).findFirst().get();
            System.out.println(String.format("ID: %d   %s   $%f   %s   %s", foundItem.itemID, foundItem.itemName, foundItem.price, foundItem.itemCategory, foundItem.serialISBN));
        }
    }

    public void filterByItemPrice(Scanner in){
        System.out.println("Enter the max price you are willing to spend");
        int maxPrice = Integer.parseInt(in);
        if(catalog.stream().noneMatch(item -> item.price <= maxPrice)){
            System.out.println("No item found with price lower than: " + maxPrice);
        }
        else{
            System.out.println("Found item: ");
            CatalogItem foundItem = catalog.stream().filter(item -> item.price.equals(maxPrice)).findFirst().get();
            System.out.println(String.format("ID: %d   %s   $%f   %s   %s", foundItem.itemID, foundItem.itemName, foundItem.price, foundItem.itemCategory, foundItem.serialISBN));
        }
    }

    public void filterByItemType(Scanner in){
        System.out.println("Enter the type of item you want to purchase");
        String type = in.nextLine();
        if(catalog.stream().noneMatch(item -> item.itemCategory.equals(type))){
            System.out.println("No item found with that type: " + type);
        }
        else{
            System.out.println("Found item: ");
            CatalogItem foundItem = catalog.stream().filter(item -> item.itemCategory.equals(type)).findFirst().get();
            System.out.println(String.format("ID: %d   %s   $%f   %s   %s", foundItem.itemID, foundItem.itemName, foundItem.price, foundItem.itemCategory, foundItem.serialISBN));
        }
    }

    public void displayPopularItems(){
        System.out.println("Top 5 ISBNs on ScholarlyBay:");
        Map<String, List<CatalogItem>> popular = catalog.stream().collect(Collectors.groupingBy(CatalogItem::getSerialISBN));
        ArrayList<String> popularISBNs = (ArrayList<String>) popular.entrySet().stream().sorted(Comparator.comparingInt(x -> x.getValue().size())).map(x -> x.getKey()).collect(Collectors.toList());
        popularISBNs.stream().limit(5).forEach(System.out::println);
    }

/*
/Returns the items in the catalog database
*/
    ArrayList<CatalogItem> getDataset(){
        return catalog;
    }

}