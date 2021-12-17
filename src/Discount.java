import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class Discount {

	protected double amount;
	protected String code;
	protected Map<Item, Integer> mapItemsToQuantity;

	public Discount(double amount, String code, Map<Item, Integer> map) {
		this.amount = amount;
		this.code = code;
		this.mapItemsToQuantity = map;
	}

	public double getAmount() {
		return this.amount;
	}

	public String getCode() {
		return this.code;
	}

	public Map<Item, Integer> getMapItemsToQuantity() {
		return this.mapItemsToQuantity;
	}

	abstract boolean updateCriteria(Item item, int quantity);

	abstract boolean updateAmount(double amount);
	
	abstract double apply(Set<Discount> appliedDiscounts, double cost);

	public boolean checkIfAppliesToCart(Cart cart) {
		Map<Item, Integer> cartMapItemsToQuantity = cart.getMapItemsToQuantity();
		Set<Item> necessaryItems = this.mapItemsToQuantity.keySet();
		Iterator<Item> it = necessaryItems.iterator();
		while (it.hasNext()) {
			Item discountItem = it.next();
			if (cartMapItemsToQuantity.containsKey(discountItem)) {
				if (cartMapItemsToQuantity.get(discountItem) >= this.mapItemsToQuantity.get(discountItem)) {
					continue;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

}
