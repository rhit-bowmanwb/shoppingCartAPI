import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Github access token: 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 
 * 
 * @author bowmanwb
 *
 */

public class Main {
	
	private static Database db;
	private CostComputer costComputer = new CostComputer();
	
	public static void main(String[] args) {
		db.connect();
	}
	
	Cart handleAddItemRequest(int cartID, int itemID, int quantity) {
		Cart cart = db.getCart(cartID);
		Item item = db.getItem(itemID);
		boolean didAdd = cart.addItem(item, quantity);
		if (didAdd) {
			db.saveCart(cart);
			System.out.println("Item " + item.getName() + " was added to cart with quantity " + quantity + ".");
		} else {
			System.out.println("Item " + item.getName() + " could not be added to cart with quantity " + quantity + 
					" because there is not enough of this item in stock.");
		}
		return cart;
	}
	
	Cart handleRemoveItemRequest(int cartID, int itemID) {
		Cart cart = db.getCart(cartID);
		Item item = db.getItem(itemID);
		boolean didRemove = cart.removeItem(item);
		if (didRemove) {
			db.saveCart(cart);
			System.out.println("Item " + item.getName() + " was removed from cart.");
		} else {
			System.out.println("Item " + item.getName() + " was not removed from cart because there were none already "
					+ "in the cart.");
		}
		return cart;
	}
	
	Cart handleViewCartRequest(int cartID) {
		Cart cart = db.getCart(cartID);
		return cart;
	}
	
	Cart handleUpdateCartQuantityRequest(int cartID, int itemID, int changeQuantity) {
		Cart cart = db.getCart(cartID);
		Item item = db.getItem(itemID);
		boolean didUpdate = cart.updateQuantity(item, changeQuantity);
		if (didUpdate) {
			db.saveCart(cart);
			System.out.println("Quantity of item " + item.getName() + " in cart was updated.");
		} else {
			System.out.println("Quantity of item " + item.getName() + " in cart could not be updated because item was not in cart.");
		}
		return cart;
	}
	
	Cart handleApplyDiscountRequest(String code, int cartID) {
		Discount discount = db.getDiscount(code);
		Cart cart = db.getCart(cartID);
		if (db.checkIfDiscountIsValid(code)) {
			boolean didApply = cart.applyDiscount(discount);
			if (didApply) {
				db.saveCart(cart);
				System.out.println("Discount with code " + discount.getCode() + " was applied to cart.");
			} else {
				System.out.println("Discount with code " + discount.getCode() + " does not apply to cart.");
			}
		} else {
			System.out.println("Discount with code " + discount.getCode() + " is not valid.");
		}
		return cart;
	}
	
	Item handleUpdateItemRequest(int itemID, String field, String value) {
		Item item = db.getItem(itemID);
		boolean didUpdate = item.update(field, value);
		if (didUpdate) {
			db.saveItem(item);
			Set<Cart> cartsContainingItem = db.getCartsContainingItem(itemID);
			Iterator<Cart> it = cartsContainingItem.iterator();
			while(it.hasNext()) {
				Cart cart = it.next();
				if (item.getNumInStock() <= 0) {
					cart.removeItem(item);
				}
				costComputer.computeCost(cart);
				db.saveCart(cart);
			}
			System.out.println("Item " + item.getName() + " was updated.");
		} else {
			System.out.println("Item " + item.getName() + " could not be updated.");
		}
		return item;
	}
	
	Map<Item, Integer> handleUpdateDiscountAmount(String code, double amount) {
		Discount discount = db.getDiscount(code);
		boolean didUpdate = discount.updateAmount(amount);
		if (didUpdate) {
			db.saveDiscount(discount);
			Set<Cart> cartsWithDiscount = db.getCartsWithDiscount(code);
			Iterator<Cart> it = cartsWithDiscount.iterator();
			while(it.hasNext()) {
				Cart cart = it.next();
				costComputer.computeCost(cart);
				db.saveCart(cart);
			}
			System.out.println("Discount with code " + discount.getCode() + " updated successfully.");
		} else {
			System.out.println("Discount with code " + discount.getCode() + " was not updated.");
		}
		return discount.getMapItemsToQuantity();
	}
	
	Map<Item, Integer> handleUpdateDiscountCriteria(String code, int itemID, int quantity) {
		Discount discount = db.getDiscount(code);
		Item item = db.getItem(itemID);
		boolean didUpdate = discount.updateCriteria(item, quantity);
		if (didUpdate) {
			db.saveDiscount(discount);
			Set<Cart> cartsWithDiscount = db.getCartsWithDiscount(code);
			Iterator<Cart> it = cartsWithDiscount.iterator();
			while(it.hasNext()) {
				Cart cart = it.next();
				if (!discount.checkIfAppliesToCart(cart)) {
					boolean didRemove = cart.removeDiscount(discount);
					if (didRemove) {
						costComputer.computeCost(cart);	
						db.saveCart(cart);
					}
				}
			}
			System.out.println("Discount with code " + discount.getCode() + " was updated successfully");
		} else {
			System.out.println("Discount with code " + discount.getCode() + " was not updated.");
		}
		return discount.getMapItemsToQuantity();
	}
	
}
