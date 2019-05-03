package scholarlyBay.entities;

import scholarlyBay.models.Account;
import scholarlyBay.models.CartItem;
import scholarlyBay.models.CatalogItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cart {
    public static List<CartItem> cart = new ArrayList<>();


/*
/Allows users to view all items that they have added to their cart along.
/Also allows users to interact with their cart such as modifying it and buying the items in the cart.
*/
    public void view(Account account, Scanner in, Catalog catalog, Accounts accounts){

    	
    	List<CatalogItem> accountCart = account.cart;
    	if(cart.isEmpty()) {
    		for(int i = 0; i < accountCart.size(); i++) {
    			cart.add(new CartItem(accountCart.get(i).sellerID, accountCart.get(i).itemID, accountCart.get(i).itemName, 1, accountCart.get(i).price));
    		}
    	}
        

        
        while(true) {
        	cart.forEach(info -> System.out.println(String.format("ID: %d   %s   $%d   %f", info.itemID, info.name, info.quantity, info.price)));

            System.out.println("Please choose an option:");
            System.out.println("(Change) CatalogItem Quantity");
            System.out.println("(Remove CatalogItem) from Cart");
            System.out.println("(Buy) items in Cart");
            System.out.println("Return to the (Main Menu)");
        	String answer = in.nextLine();
        	if(answer.equalsIgnoreCase("change")){
        		changeQuantity(account, in);       		
        	}
        	else if(answer.equalsIgnoreCase("remove item")){
        		remove(account, in, catalog);       		
        	}
        	else if(answer.equalsIgnoreCase("buy")) {
        		buyCart(account, in, catalog, accounts);
        	}        
        	else if(answer.equalsIgnoreCase("main menu")){
        		break;
        	} 
        }
    }

    private void buyCart(Account account, Scanner in, Catalog catalog, Accounts accounts) {
    	if(cart.isEmpty()) {
    		System.out.println("There is nothing in the cart to buy.");
    		return;
    	}
    	ArrayList<CartItem> canCombine = new ArrayList<>();
		List<CatalogItem> catItem = new ArrayList<>();
    	double totalCost = 0;
    	for(CartItem cartItem : cart) {
    		for(int i = 0; i <cartItem.quantity; i++) {
    			totalCost += cartItem.price - cartItem.price * ((catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get().coupon.percentOff)/100) + (catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get().fee) ;

    		}
			catItem.add(catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get());
    	}

		for(int i = 0; i<catItem.size(); i++){
			for(int j = i; j<catItem.size(); j++){
				if(catItem.get(i).sellerID == catItem.get(j).sellerID){
					if(!canCombine.contains(cart.get(j))){
						canCombine.add(cart.get(j));
					}

				}
			}

		}
    	System.out.println("The total cost for your cart is: " + totalCost);
    	System.out.println("Would you like to do combined shipping for the following items? Enter the number(s) you wish to combine or [c] to continue. (Remember this is not guranteed and may or may not change the total)");
    	int shippingGroup = 1;
    	List<Integer> numInShippingGroup = new ArrayList<Integer>();
    	int numInShippingGroupVal = 0;
    	for(int i = 0; i<canCombine.size(); i++){
    		if(i == 0){
    			System.out.println("[" + shippingGroup + "]" + canCombine.get(0).name);
    			canCombine.get(0).shippingGroup = shippingGroup;
    			numInShippingGroupVal++;
			}
			else if(canCombine.get(i).accountID == canCombine.get(i-1).accountID){
				System.out.println("[" + shippingGroup + "]" + canCombine.get(i).name);
				canCombine.get(i).shippingGroup = shippingGroup;
				numInShippingGroupVal++;
			}
			else{
				numInShippingGroup.add(numInShippingGroupVal);
				numInShippingGroupVal = 0;
				shippingGroup++;
				System.out.print("\n");
				System.out.println("[" + shippingGroup + "]" + canCombine.get(i).name);
				canCombine.get(i).shippingGroup = shippingGroup;
			}


		}
		ArrayList<Integer> combined = new ArrayList<>(); //prevents message spam
		String combining = in.nextLine();
    	while(!combining.equalsIgnoreCase("c")){
			ArrayList<CartItem> toCombine = new ArrayList<>();
			if(Integer.parseInt(combining) <= shippingGroup && Integer.parseInt(combining)>0){
				int i = numInShippingGroup.get(Integer.parseInt(combining)-1);
				int j = 0;
				while(canCombine.get(j).shippingGroup!=Integer.parseInt(combining)){
					j++;
				}
				while(i>0){
					toCombine.add(canCombine.get(j));
					j++;
					i--;
				}
				int sellerId = canCombine.get(j-1).accountID;
				Account sellerAccount = accounts.getDataset().stream().filter(acc -> acc.accountID == sellerId).findFirst().get();
				String message = "Account: " + account.accountID + " has requested the following items to be combined in their shipping, contact them again to confirm or refuse.\n";
				for(CartItem cartItem : toCombine){
					message += "Item Name: " + cartItem.name + "Item ID: " + cartItem.itemID + "\n";
				}
				account.addMessageToInbox(message, sellerAccount);
			}
			System.out.println("Would you like additional groups from above to be combined. Type a number above or [c] to cancel");
			combining = in.nextLine();
		}

    	System.out.println("Please insert the amount to pay, or (c) to cancel: ");
    	String amountPaid = in.nextLine();
    	if(amountPaid.equalsIgnoreCase("c")) {
    		return;
    	}
    	else {
    		double remainingCost = totalCost - Double.parseDouble(amountPaid);
    		while(remainingCost > 0) {
    			System.out.println("The remaining cost for your cart is: " + remainingCost);
    			System.out.println("Please insert the remaining amount to pay, or (c) to cancel: ");
    			amountPaid = in.nextLine();
    			if(amountPaid.equalsIgnoreCase("c")) {
    				return;
    			}
    			else {
    				remainingCost = remainingCost - Double.parseDouble(amountPaid);
    			}
    		}
    		System.out.println("Payment accepted, thank you for your business!");
    		for(CartItem cartItem : cart) {      			
    			catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get().buyerID = account.accountID;
    		    int sellerID = catalog.getDataset().stream().filter(item -> item.itemID == cartItem.itemID).findFirst().get().sellerID;
                Account sellerAccount = accounts.getDataset().stream().filter(acc -> acc.accountID == sellerID).findFirst().get();
    		    sellerAccount.itemSold("Item ID: " + cartItem.itemID + ", Item Name: " +  cartItem.name + " has been sold at the price of " + (cartItem.price - cartItem.price * ((catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get().coupon.percentOff)/100) + (catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get().fee) ));
    		    Catalog.salesHistory.add(catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get());
    		    account.favorites.favorites.removeIf(x -> x.catalogItemID == cartItem.itemID);
    		    sellerAccount.itemsSold.add(catalog.catalog.stream().filter(catalogItem -> catalogItem.itemID == cartItem.itemID).findFirst().get());
    		}
    		cart.removeAll(cart);
    		account.cart.removeAll(account.cart);

    	}
    	
    	
	
    }
/*
/Adds the given item to the cart that is associated with the given account
*/
	public void add(int accountID, CatalogItem item, int quantity){
        cart.add(new CartItem(accountID, item.itemID, item.itemName, quantity, item.price));
    }
/*
/Removes the given item from the cart
*/
    private void remove(Account account, Scanner in, Catalog catalog){
        while(true){
            System.out.println("Please enter the ID of the item you want to remove from your cart");
            int itemID = Integer.parseInt(in.next());

            if(cart.removeIf(cartItem -> cartItem.itemID == itemID)){
            	account.cart.removeIf(cartItem -> cartItem.itemID == itemID);
                break;
            }
        }

       
    }
/*
/Changes the quantity of an item in the cart
*/
    private void changeQuantity(Account account, Scanner in){
        System.out.println("What item ID would you like to change?");
        String answer = in.next();
        int itemIDCopy = -1;
        try {
            int itemID = Integer.parseInt(answer);
            itemIDCopy = itemID;

            if(cart.stream().noneMatch(cartItem -> cartItem.itemID == itemID)){
                throw new Exception();
            }
        }
        catch(Exception e){
            System.out.println("Something went wrong");
            return;
        }

        System.out.println("What would you like to change quantity to?");
        int quantity;
        answer = in.next();
        try {
            quantity = Integer.parseInt(answer);
            int itemIDCopy2 = itemIDCopy;

            cart.stream()
                    .filter(cartItem -> cartItem.itemID == itemIDCopy2)
                    .findFirst()
                    .get()
                    .quantity = quantity;
        }
        catch(Exception e) {
            System.out.println("Something went wrong");
            return;
        }
    }

    
}