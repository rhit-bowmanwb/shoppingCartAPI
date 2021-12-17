import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class shoppingCartTest {

	private Database db;

	@Test
	void testAddAndRemoveItemToCart() {
		Cart testCart = new Cart(1, db);
		Item socks = new Item(1, "socks", "these socks are awesome", "", 2.99);
		testCart.addItem(socks, 2);
		assertTrue(testCart.getMapItemsToQuantity().containsKey(socks));

		Item book = new Item(2, "Bridge to Terebithia", "This is a book", "", 9.99);
		testCart.addItem(book, 1);
		assertTrue(testCart.getMapItemsToQuantity().containsKey(book));
		testCart.addItem(book, 1); // should just increase quantity in the cart
		assertEquals(2, testCart.getMapItemsToQuantity().get(book));

		assertTrue(testCart.removeItem(socks));
		assertFalse(testCart.getMapItemsToQuantity().containsKey(socks));

		Item soda = new Item(3, "Pepsi", "Pepsi from Pepsi Co.", "", 1.50);
		assertFalse(testCart.removeItem(soda));
	}

	@Test
	void testComputeCostNoDiscounts() {
		double actualCost = 0;
		Cart testCart = new Cart(1, db);
		assertEquals(actualCost, testCart.getCost(), 0);
		Item socks = new Item(1, "socks", "these socks are awesome", "", 2.99);
		testCart.addItem(socks, 2);
		actualCost += 2.99 * 2;
		assertEquals(actualCost + actualCost * 0.07, testCart.computeCost(), 0.01);

		testCart.updateQuantity(socks, 2);
		actualCost += 2.99 * 2;
		assertEquals(actualCost + actualCost * 0.07, testCart.computeCost(), 0.01);

		Item soda = new Item(3, "Pepsi", "Pepsi from Pepsi Co.", "", 1.50);
		testCart.addItem(soda, 10);
		actualCost += 1.50 * 10;
		assertEquals(actualCost + actualCost * 0.07, testCart.computeCost(), 0.01);
		testCart.removeItem(socks);
		actualCost -= 2.99 * 4;
		assertEquals(actualCost + actualCost * 0.07, testCart.computeCost(), 0.01);
	}

	@Test
	void testUpdateQuantityOfItem() {
		Cart testCart = new Cart(1, db);
		Item socks = new Item(1, "socks", "these socks are awesome", "", 2.99);
		testCart.addItem(socks, 2);
		testCart.updateQuantity(socks, -1);
		assertEquals(1, testCart.getMapItemsToQuantity().get(socks));
		
		testCart.updateQuantity(socks, -5); // should remove socks from cart
		assertTrue(testCart.getMapItemsToQuantity().isEmpty());
	}
	
	@Test
	void testApplyDiscount() {
		Cart testCart = new Cart(1, db);
		Item socks = new Item(1, "socks", "these socks are awesome", "", 2.99);
		Map<Item, Integer> discountItems = new HashMap<Item, Integer>();
		discountItems.put(socks, 1);
		PercentageDiscount discount = new PercentageDiscount(0.1, "SOCKS", discountItems);
		testCart.applyDiscount(discount);
		assertFalse(testCart.getDiscounts().contains(discount));
		testCart.addItem(socks, 2);
		testCart.applyDiscount(discount);
		assertTrue(testCart.getDiscounts().contains(discount));
		
		double cost = 2 * 2.99 + 2 * 2.99 * 0.07;
		double costAfterDiscount = cost - cost * 0.1;
		assertEquals(costAfterDiscount, testCart.computeCost(), 0.01);
		
		PercentageDiscount discount2 = new PercentageDiscount(0.1, "SOCKS2", discountItems);
		testCart.applyDiscount(discount2);
		double costAfterDiscount2 = cost - cost * 0.2;
		assertEquals(costAfterDiscount2, testCart.computeCost(), 0.01);
	}
	
	@Test
	void testUpdateItem() {
		Item socks = new Item(1, "socks", "these socks are awesome", "", 2.99);
		socks.update("description", "these socks are cool");
		assertEquals("these socks are cool", socks.getDescription());
		socks.update("stock", "5");
		assertEquals(5, socks.getNumInStock());
		
		Cart testCart = new Cart(1, db); 
		testCart.addItem(socks, 3);
		assertEquals(2.99 * 3 + 2.99 * 3 * 0.07, testCart.computeCost(), 0.01);
		socks.update("price", "2.00");
		assertEquals(6.00 + 6.00 * 0.07, testCart.computeCost(), 0.01);
		
	}
	
	@Test
	void testUpdateDiscount() {
		Cart testCart = new Cart(1, db);
		Item socks = new Item(1, "socks", "these socks are awesome", "", 2.99);
		testCart.addItem(socks, 1);
		Map<Item, Integer> discountItems = new HashMap<Item, Integer>();
		discountItems.put(socks, 1);
		PercentageDiscount discount = new PercentageDiscount(0.1, "SOCKS", discountItems);
		testCart.applyDiscount(discount);
		discount.updateAmount(0.2);
		assertEquals(0.2, testCart.getDiscountByCode(discount.getCode()).getAmount(), 0);
		
		Item soda = new Item(3, "Pepsi", "Pepsi from Pepsi Co.", "", 1.50);
		discount.updateCriteria(soda, 1);
		testCart.applyDiscount(discount);
		assertFalse(testCart.getDiscounts().contains(discount));
	}
	

}
