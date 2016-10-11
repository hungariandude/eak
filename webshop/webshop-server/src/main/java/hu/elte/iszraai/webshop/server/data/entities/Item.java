package hu.elte.iszraai.webshop.server.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Item implements Serializable, Comparable<Item> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    private int               id;

    @Column(nullable = false, length = 100)
    private String            name;

    @Column(nullable = false)
    private int               price;

    public Item() {
    }

    public Item(final int id, final String name, final int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + id + ", name=" + name + ", price=" + price + "]";
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Item other = (Item) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final Item o) {
        int nameCompareResult = name.compareTo(o.name);
        if (nameCompareResult == 0) {
            return Integer.compare(price, o.price);
        } else {
            return nameCompareResult;
        }
    }

}
