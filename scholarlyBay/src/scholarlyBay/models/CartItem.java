package scholarlyBay.models;

import scholarlyBay.scholarlyBay;

public class CartItem {

    public CartItem(int accountID, int itemID, String name, int quantity, double price){
            this.accountID = accountID;
            this.itemID = itemID;
            this.quantity = quantity;
            this.name = name;
            this.price = price;
    }

    public int accountID;
    public int itemID;
    public int quantity;
	public String name;
	public double price;
    public int shippingGroup = 0;
    public int getItemID(){
        return itemID;
    }
}