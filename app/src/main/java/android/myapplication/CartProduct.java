package android.myapplication;

public class CartProduct {
    private String name;
    private String image;
    private String price;
    private String quantity;
    private String description;

    public CartProduct() {
    }

    public CartProduct(String name, String description, String image, String price, String quantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

}
