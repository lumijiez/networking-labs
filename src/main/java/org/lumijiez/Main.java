package org.lumijiez;

import org.lumijiez.models.FilteredProducts;
import org.lumijiez.models.Product;
import org.lumijiez.network.TCPClient;
import org.lumijiez.parsers.DarwinParser;
import org.lumijiez.parsers.DarwinProductParser;
import org.lumijiez.parsers.DarwinProductProcessor;
import org.lumijiez.serializer.Lumi;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String host = "https://darwin.md/search?search=apple&page=1";

        String response = TCPClient.getHttps(host);
        String productList = DarwinParser.extractProductList(response);
        List<Product> products = DarwinProductParser.parseProducts(productList);

        FilteredProducts filtered = DarwinProductProcessor.processProducts(products, 0, 1000000);

        Lumi.jsonToFile(filtered, "filtered.json");
        Lumi.xmlToFile(filtered, "filtered.xml");

        System.out.println(TCPClient.checkJson(Lumi.toJson(filtered)));
        System.out.println(TCPClient.checkXml(Lumi.toXml(filtered)));

//      Serialization/Deserialization Test
        System.out.println(
                Lumi.toXml(
                        Lumi.fromJson(
                                Lumi.toJson(products.getFirst()), Product.class)));
    }
}