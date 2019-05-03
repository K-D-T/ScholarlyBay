package scholarlyBay.models;

import scholarlyBay.scholarlyBay;

import java.util.ArrayList;
import java.util.List;

public class CatalogItem
{
	public CatalogItem(int itemID, int sellerID, int buyerID, String itemName, String itemCategory, String serialISBN, double price, Coupon coupon, double fee){
		this.itemID = itemID;
		this.sellerID = sellerID;
		this.buyerID = buyerID;
		this.itemName = itemName;
		this.itemCategory = itemCategory;
		this.serialISBN = serialISBN;
		this.price = price;
		this.coupon = coupon;
		this.rating = 0; // should be overwritten on first rating
		this.ratings = null;
		this.comments = null;
		this.fee = fee;
	}

	public String getSerialISBN() { return serialISBN; }

	public int getItemID(){
		return itemID;
	}
	
	public int itemID;

	public int sellerID;

	public int buyerID;
	
	public Coupon coupon;

	public String itemName;

	public String itemCategory;

	public String serialISBN;

	public double price;

	public double rating;

	public ArrayList<Integer> ratings;

	public ArrayList<String> comments;
	
	public long trackingNumber;

	public double fee;
}