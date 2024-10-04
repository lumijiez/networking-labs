package org.lumijiez.models;

import org.lumijiez.serializer.LumiSerializable;
import org.lumijiez.serializer.LumiSerializeField;

import java.util.Date;
import java.util.List;

@LumiSerializable
public class FilteredProducts {
    @LumiSerializeField
    public List<Product> products;

    @LumiSerializeField
    public Date date;

    @LumiSerializeField
    public float price;
}
