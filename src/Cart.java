
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Cart {

	private int cartID;
	private double cost = 0;
	private Database db;
	private HashMap<Item, Integer> mapItemsToQuantity = new HashMap<Item, Integer>();
	private Set<Discount> discounts = new HashSet<Discount>();
	private CostComputer costComputer;

	public Cart(int cartID, Database db, CostComputer comp) {
		this.cartID = cartID;
		this.db = db;
		this.costComputer = comp;
	}

	boolean addItem(Item item, int quantity) {
		int numInStock = 10; // hardcoded for testing. This should come from the db with
								// this.db.getNumInStock(item);
		if (this.checkIfContainsItemWithQuantity(item, 1)) {
			int numInCart = this.mapItemsToQuantity.get(item);
			if (numInCart + quantity <= 0) {
				this.removeItem(item);
				this.costComputer.computeCost(this);
				return true;
			}
			if (quantity <= numInStock - numInCart) {
				this.mapItemsToQuantity.replace(item, numInCart + quantity);
				this.costComputer.computeCost(this);
				return true;
			} else {
				// not enough in stock
				return false;
			}
		} else {
			if (quantity <= numInStock && quantity > 0) {
				this.mapItemsToQuantity.put(item, quantity);
				this.costComputer.computeCost(this);
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
			this.costComputer.computeCost(this);
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
			this.costComputer.computeCost(this);
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

	public int getCartID() {
		return this.cartID;
	}

	public double getCost() {
		return this.cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
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
