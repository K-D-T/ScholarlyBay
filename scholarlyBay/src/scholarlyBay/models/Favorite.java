package scholarlyBay.models;

public class Favorite {
    public int catalogItemID;
    private int accountID;

    public Favorite(int catalogItemID, int accountID){
        this.catalogItemID = catalogItemID;
        this.accountID = accountID;
    }
}