package scholarlyBay;

public class Utility {
    public static class Tuple<T, T1> {
        public final T one;
        public final T1 two;

        public Tuple(T one, T1 two){
            this.one = one;
            this.two = two;
        }
    }

    /*
        catalog.csv formatting:
        itemID, sellerID, buyerID, itemName, serialISBN, price
     */
    /*public static void parseCatalog(ArrayList<CatalogItem> catalog)
    {
        try(BufferedReader catalogReader = new BufferedReader(new FileReader("storage/catalog.csv"))) {
            String line;
            while((line = catalogReader.readLine()) != null){
                String[] values = line.split(",");
                catalog.add(new CatalogItem(
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]),
                        values[3],
                        values[4],
                        Double.parseDouble(values[5])
                ));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /*
        accounts.csv formatting:
        accountID, name, email, password, isAdmin
     */
    /*public static void parseAccounts(ArrayList<Account> accounts)
    {
        try(BufferedReader catalogReader = new BufferedReader(new FileReader("storage/accounts.csv"))) {
            String line;
            while((line = catalogReader.readLine()) != null){
                String[] values = line.split(",");
                accounts.add(new Account(
                        Integer.parseInt(values[0]),
                        values[1],
                        values[2],
                        values[3],
                        Boolean.parseBoolean(values[5])
                ));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    	/*
            cart_items.csv formatting:
            accountID, itemID
         */
}
