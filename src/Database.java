
import java.util.Set;

public interface Database {

	void connect();
	
	int getNumInStock(Item item);
	
	boolean checkIfDiscountIsValid(String code);
	
	void saveDiscount(Discount discount);
	
	Discount getDiscount(String code);
	
	void saveCart(Cart cart);
	
	Cart getCart(int cartID);
	
	void saveItem(Item item);
	
	Item getItem(int itemID);
	
	Set<Cart> getCartsContainingItem(int itemID);
	
	Set<Cart> getCartsWithDiscount(String code);
	
}
