import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Shop {

	/*
	 * This is the Shop Class.
	 * The Shop Class allows users to view inventory, add items to cart, update cart, and checkout.
	 * This class is mainly ran under the shopping() method and will return back to the original class with the updated inventory.
	 * First it goes into the viewInventory() method to let users options which items to add to cart or to have them actually
	 * view the cart, checkout, or terminate program.
	 * 
	 */
	public static ArrayList<Item> shopping(Scanner sc, ArrayList<Item> inventory, ArrayList<Item> cart, ArrayList<Item> Orginalinventory) {
		int inventorySize = inventory.size();
		int selection = viewInventory(sc, inventory, cart);

		if (selection <= inventorySize) {
			int userQty = getQty(selection, sc, inventory);
			if (userQty != 0) {
				boolean QTYok = checkQty(selection, userQty, inventory, cart);
				if (QTYok) {
					System.out.println(	userQty + " " +
							inventory.get(selection - 1).getName() + "s for " +
							Item.formatPrice((inventory.get(selection - 1).getPrice() * userQty)) + "...");
					String addToCart = Validator.getChoice(sc, "Do you want to add " + inventory.get(selection - 1).getName() + " to cart? (y/n): ", "yn");
					
					if (addToCart.equalsIgnoreCase("y")) {
						cart = addToCart(sc, inventory, cart, selection, userQty);
						System.out.println(inventory.get(selection - 1).getName() + "s added to shopping cart.\n");
					}
					shopping(sc, inventory, cart, Orginalinventory);
				}
				else {
					System.out.println("Error!  Please choose inventory quantity of " + 
							inventory.get(selection-1).getQty() +
							" or less");
				}
			}
			else {
				shopping(sc, inventory, cart, Orginalinventory);
			}
		}
		else if (selection == inventorySize + 1) {
			viewCart(cart);
			System.out.println();

			String decide = Validator.getChoice(sc, "Would you like to clear cart? (y/n)", "yn");
			
			if (decide.equalsIgnoreCase("y")) {
				cart.clear();
				for (int i = 0 ; i < inventory.size(); i++) {
					inventory.get(i).setQty(Orginalinventory.get(i).getQty());
				}
			}
			shopping(sc, inventory, cart, Orginalinventory);
		} 
		else if (selection == inventorySize + 2) {
			Checkout.checkout(sc, cart);
		}
		else if (selection == inventorySize + 3) {
			System.out.println("Thank you for shopping with us! Have a great day!");
			System.exit(0);
		}
		return inventory;
	}

	
	//Method to view what's currently in the cart.
	public static void viewCart(ArrayList<Item> Cart) {
		DecimalFormat df = new DecimalFormat("0.00");
		System.out.println("\n                   Your Shopping Cart                    ");
		System.out.println("ID  Item        Category      Description       Qty     Price    total");
		System.out.println("==  ====        ========      ===========       ===     =====    =====");
		
		if (Cart.size() == 0) {
			System.out.println();
			System.out.println("Cart is empty.");
		}
		for(int i=0; i < Cart.size(); i++ ) {
			System.out.printf("%-4s%-14s", (i + 1) , Cart.get(i));
			
			System.out.println( "  " + Double.valueOf(df.format(Cart.get(i).getPrice() * Cart.get(i).getQty())) +"");
		}
	}
	
	//Method to return back what item (by the index number) he wants to add to cart.
	public static int getItem(Scanner sc, ArrayList<Item> inventory) {
		int userInput = Validator.getInt(sc, "Please select an item by ID number: ", 1, inventory.size() + 3);
		return userInput;
	}

	//Method to return back the quantity of the selected item.
	public static int getQty(int selection, Scanner sc, ArrayList<Item> inventory) {
		int userInput = 0;
		if (inventory.get(selection-1).getQty() != 0) {
			userInput = Validator.getInt(sc, "How many " + inventory.get(selection - 1).getName() + "s do you want? ", 0,
					inventory.get(selection - 1).getQty());
		}
		else {
			System.out.println("There are no more " + inventory.get(selection-1).getName() + "s.");
		}
		return userInput;
	}
	
	//Method to view inventory and allow users option to select what item to add to cart.
	public static int viewInventory(Scanner sc, ArrayList<Item> inventory, ArrayList<Item> cart) {
		System.out.println("\n                 Inventory Shelf                ");
		System.out.println("ID  Item        Category      Description       Qty     Price");
		System.out.println("==  ====        ========      ===========       ===     =====");
		for (int i = 0; i < inventory.size(); i++) {
			System.out.printf("%-4s%-14s", (i + 1) , inventory.get(i));
			System.out.println();
		}
		System.out.println((inventory.size() + 1) + ". View Cart");
		System.out.println((inventory.size() + 2) + ". Checkout");
		System.out.println((inventory.size() + 3) + ". Quit\n");

		int selection = getItem(sc, inventory);
		
		return selection;
	}
	
	//Method to add item to cart and update inventory to reflect that that item has been moved from inventory
	//to cart.
	public static ArrayList<Item> addToCart(Scanner sc, ArrayList<Item> inventory, ArrayList<Item> cart, int selection, int userQty){
		
		inventory = removeFromInventory(inventory, userQty, selection);
			
		Item tempItem = new Item(inventory.get(selection-1).getId(),
								inventory.get(selection-1).getName(), inventory.get(selection-1).getCat(),
								inventory.get(selection-1).getDesc(), 
								userQty,
								inventory.get(selection-1).getPrice());
		
		boolean found = false;
		for (int i = 0; i < cart.size(); i++) {
			if (cart.get(i).getId() == tempItem.getId()) {
				int updateQty = cart.get(i).getQty() + tempItem.getQty();
				cart.get(i).setQty(updateQty);
				found = true;
			}
		}
		if (!found) {
			cart.add(tempItem);
		}
	
		return cart;
	}

	//Method to check that the quantity of the item the user wants, is less than or greater than what is in inventory.
	public static boolean checkQty(int selection, int userQty, ArrayList<Item> inventory, ArrayList<Item> cart) {
		boolean qtyOk = true;
		
		for (int i = 0; i < cart.size(); i++) {
			if (cart.get(i).getId() == selection) {
				if (cart.get(i).getQty() + userQty > inventory.get(selection-1).getQty() ) {
					qtyOk = false;
					}
				else {
					qtyOk = true;
				}
			}
		}
		return qtyOk;
	}
	
	//Method to update quantity to reflect that that item has been added to cart or have been purchased.
	public static ArrayList<Item> removeFromInventory(ArrayList<Item> inventory, int userQty, int selection){
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).getId() == selection) {
				int updatedQty = inventory.get(i).getQty() - userQty;
					inventory.get(i).setQty(updatedQty);
			}
		}
		return inventory;
	}
}
