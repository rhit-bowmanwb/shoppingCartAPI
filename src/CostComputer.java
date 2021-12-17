import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CostComputer {
	
	private Tax tax;

	public double computeCost(Cart cart) {
		double cost = 0;
		Set<Item> items = cart.getMapItemsToQuantity().keySet();
		Iterator<Item> it = items.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			int quantity = cart.getMapItemsToQuantity().get(item);
			cost += item.getPrice() * quantity;
		}
		Iterator<Discount> discIt = cart.getDiscounts().iterator();
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
		cart.setCost(cost);
		return cart.getCost() + (cart.getCost() * estimatedTax);
	}
	
}
