
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Cart {

	private int cartID;
	private double cost = 0;
	private Database db;
	private Tax tax;
	private HashMap<Item, Integer> mapItemsToQuantity = new HashMap<Item, Integer>();
	private Set<Discount> discounts = new HashSet<Discount>();

	public Cart(int cartID, Database db) {
		this.cartID = cartID;
		this.db = db;
	}

	boolean addItem(Item item, int quantity) {
		int numInStock = 10; // hardcoded for testing. This should come from the db with
								// this.db.getNumInStock(item);
		if (this.checkIfContainsItemWithQuantity(item, 1)) {
			int numInCart = this.mapItemsToQuantity.get(item);
			if (numInCart + quantity <= 0) {
				this.removeItem(item);
				this.computeCost();
				return true;
			}
			if (quantity <= numInStock - numInCart) {
				this.mapItemsToQuantity.replace(item, numInCart + quantity);
				this.computeCost();
				return true;
			} else {
				// not enough in stock
				return false;
			}
		} else {
			if (quantity <= numInStock && quantity > 0) {
				this.mapItemsToQuantity.put(item, quantity);
				this.computeCost();
				return true;
			} else {
				// not enough in stock
				return false;
			}
		}
	}

	boolean updateQuantity(Item item, int changeQuantity) {
		return this.addItem(item, changeQuantity);
	}

	boolean removeItem(Item item) {
		if (this.checkIfContainsItemWithQuantity(item, 1)) {
			this.mapItemsToQuantity.remove(item);
			this.computeCost();
		} else {
			return false;
		}
		return true;
	}

	boolean checkIfContainsItemWithQuantity(Item item, int quantity) {
		if (this.mapItemsToQuantity.containsKey(item) && this.mapItemsToQuantity.get(item) >= quantity) {
			return true;
		} else {
			return false;
		}
	}

	boolean applyDiscount(Discount discount) {
		if (discount.checkIfAppliesToCart(this)) {
			this.discounts.add(discount);
			this.computeCost();
			return true;
		} else {
			if (this.discounts.contains(discount)) {
				this.discounts.remove(discount);
			}
			return false;
		}

	}
	
	boolean removeDiscount(Discount discount) {
		if (this.discounts.contains(discount)) {
			this.discounts.remove(discount);
			return true;
		} else {
			return false;
		}
	}

	double computeCost() {
		double cost = 0;
		Set<Item> items = this.mapItemsToQuantity.keySet();
		Iterator<Item> it = items.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			int quantity = this.mapItemsToQuantity.get(item);
			cost += item.getPrice() * quantity;
		}
		Iterator<Discount> discIt = this.discounts.iterator();
		Set<Discount> appliedDiscounts = new HashSet<Discount>();
		double costBeforeDiscounts = cost;
		while (discIt.hasNext()) {
			Discount discount = discIt.next();
			cost = discount.apply(appliedDiscounts, costBeforeDiscounts);
			appliedDiscounts.add(discount);
		}
		if (cost < 0) {
			cost = 0;
		}
		double estimatedTax = 0.07; // hardcoded for testing. Replace with this.tax.estimateTax();
		this.cost = cost;
		return this.cost + (this.cost * estimatedTax);
	}

	public int getCartID() {
		return this.cartID;
	}

	public double getCost() {
		return this.cost;
	}

	public HashMap<Item, Integer> getMapItemsToQuantity() {
		return this.mapItemsToQuantity;
	}

	public Set<Discount> getDiscounts() {
		return this.discounts;
	}
	
	public Discount getDiscountByCode(String code) {
		Iterator<Discount> it = this.discounts.iterator();
		while (it.hasNext()) {
			Discount discount = it.next();
			if (discount.getCode().equals(code)) {
				return discount;
			}
		}
		return null;
	}

}
