package org.lumijiez.models;

import org.lumijiez.serializer.LumiSerializable;
import org.lumijiez.serializer.LumiSerializeField;

@LumiSerializable
public class Product {

    @LumiSerializeField
    public String id;

    @LumiSerializeField
    public String name;

    @LumiSerializeField
    public String imageUrl;

    @LumiSerializeField
    public String specifications;

    @LumiSerializeField
    public String oldPrice;

    @LumiSerializeField
    public String newPrice;

    @LumiSerializeField
    public String discount;

    @LumiSerializeField
    public String productLink;

    public Product() {}

    public Product(String id, String name, String imageUrl, String specifications, String oldPrice, String newPrice, String discount, String productLink) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.specifications = specifications;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.discount = discount;
        this.productLink = productLink;
    }
}
