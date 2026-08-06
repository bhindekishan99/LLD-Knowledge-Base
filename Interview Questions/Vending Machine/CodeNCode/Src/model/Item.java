package model;
import java.util.Objects;

public class Item{

    int price;
    String name;

    Item(int price, String name){
        this.price = price;
        this.name = name;

    }

    public String getName(){
        return name;
    }

    public int getPrice(){
        return price;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return name.equals(item.getName()) && price==item.getPrice();
    }

    @Override
    public int hashCode(){
        return Objects.hash(name,price);
    }

    @Override
    public String toString() {
        return "Item{" + "name=" + name + ", price=" + price + '}';
    }

}