package de.chaosolymp.chaosessentials.chestshoplog;

import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;

public class ChestShopPurchase {
    private int quantity;
    private double price;
    private String item;
    private String owner;
    private String client;
    private boolean isSpecial;
    private String specialName;
    private TransactionType type;

    public ChestShopPurchase(int quantity, double price, String item, String owner, String client, boolean isSpecial, String specialName, TransactionType type) {
        this.quantity = quantity;
        this.price = price;
        this.item = item;
        this.owner = owner;
        this.client = client;
        this.isSpecial = isSpecial;
        this.specialName = specialName;
        this.type = type;
    }

    public ChestShopPurchase() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public String getSpecialName() {
        return specialName;
    }

    public void setSpecialName(String specialName) {
        this.specialName = specialName;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getTypeString() {
        return type.toString();
    }

    public boolean getBoolType() {
        return type.toString().equals("BUY");
    }
}
