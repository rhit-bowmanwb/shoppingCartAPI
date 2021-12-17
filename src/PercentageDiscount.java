
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PercentageDiscount extends Discount {

	public PercentageDiscount(double perc, String code, Map<Item, Integer> map) {
		super(perc, code, map);
	}
	
	public boolean updateCriteria(Item item, int quantity) {
		if (quantity <= 0) {
			this.mapItemsToQuantity.remove(item);
		}
		if (this.mapItemsToQuantity.containsKey(item)) {
			this.mapItemsToQuantity.replace(item, quantity);
		} else {
			this.mapItemsToQuantity.put(item, quantity);
		}
		return true;
	}
	
	public boolean updateAmount(double amount) {
		if (amount < 0 || amount > 1) {
			return false;
		}
		this.amount = amount;
		return true;
	}
	
	public double apply(Set<Discount> appliedDiscounts, double cost) {
		Set<Discount> discounts = appliedDiscounts;
		Iterator<Discount> it = discounts.iterator();
		double totalOff = this.amount;
		while(it.hasNext()) {
			Discount discount = it.next();
			if (discount instanceof PercentageDiscount) {
				totalOff += discount.amount;
			}
		}
		if (totalOff > 1) {
			return 0;
		}
		return cost - cost * totalOff;
	}
	
}
