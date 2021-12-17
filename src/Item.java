
public class Item {

	private String name, description, pictureURL;
	private double price;
	private int itemID, numInStock;

	public Item(int itemID, String name, String description, String pictureURL, double price) {
		this.itemID = itemID;
		this.name = name;
		this.description = description;
		this.pictureURL = pictureURL;
		this.price = price;
	}
	
	public boolean update(String field, String value) {
		switch (field) {
		case "name":
			this.name = value;
			return true;
		case "description":
			this.description = value;
			return true;
		case "picture":
			this.pictureURL = value;
			return true;
		case "price":
			double price;
			try {
				price = Double.parseDouble(value);
			} catch (NumberFormatException e){
				return false;
			}
			if (price < 0) {
				return false;
			}
			this.price = price;
			return true;
		case "stock":
			int stock;
			try {
				stock = Integer.parseInt(value);
			} catch (NumberFormatException e){
				return false;
			}
			if (stock < 0) {
				return false;
			}
			this.numInStock = stock;
			return true;
		default:
			return false;
		}	
	}
	
	public String getPictureURL() {
		return this.pictureURL;
	}

	public String getName() {
		return this.name;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public int getItemID() {
		return this.itemID;
	}
	
	public int getNumInStock() {
		return this.numInStock;
	}
	
	public void setNumInStock(int numInStock) {
		this.numInStock = numInStock;
	}

	public String getDescription() {
		return this.description;
	}

}
