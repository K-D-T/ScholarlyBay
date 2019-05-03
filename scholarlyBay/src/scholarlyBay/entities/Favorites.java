package scholarlyBay.entities;

import scholarlyBay.models.Favorite;

import java.util.ArrayList;

/*
/Class used to set items in a set of favorites for accounts to more easily view
*/
public class Favorites {
    public ArrayList<Favorite> favorites = new ArrayList<>();

/*
/Adds the specified item as a favorites
*/
    public boolean add(int catalogItemID, int accountID){
        favorites.add(new Favorite(catalogItemID, accountID));
        return true;
    }
}