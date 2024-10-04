package org.lumijiez.parsers;

import org.lumijiez.models.FilteredProducts;
import org.lumijiez.models.Product;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DarwinProductProcessor {

    public static FilteredProducts processProducts(List<Product> products, float minPrice, float maxPrice) {
        List<Product> filteredProducts = products.stream()
                .map(DarwinProductProcessor::convertPricesToEUR)
                .filter(product -> isInPriceRange(product, minPrice, maxPrice))
                .collect(Collectors.toList());

        float totalPrice = filteredProducts.stream()
                .map(DarwinProductProcessor::getPrice)
                .reduce(0f, Float::sum);

        FilteredProducts result = new FilteredProducts();
        result.products = filteredProducts;
        result.date = Date.from(Instant.now());
        result.price = totalPrice;

        return result;
    }

    private static Product convertPricesToEUR(Product product) {
        product.oldPrice = convertToEUR(product.oldPrice);
        product.newPrice = convertToEUR(product.newPrice);
        product.discount = convertToEUR(product.discount);
        return product;
    }

    private static String convertToEUR(String priceInMDL) {
        if (priceInMDL == null || priceInMDL.isEmpty()) {
            return null;
        }
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("ro-RO"));
            Number number = format.parse(priceInMDL.replaceAll("[^\\d]", ""));
            float price = number.floatValue();
            return String.format("%.2f EUR", price / 19);
        } catch (ParseException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    private static boolean isInPriceRange(Product product, float minPrice, float maxPrice) {
        float price = getPrice(product);
        return price >= minPrice && price <= maxPrice;
    }

    private static float getPrice(Product product) {
        if (product.newPrice != null) {
            return parsePrice(product.newPrice);
        }
        return 0f;
    }

    private static float parsePrice(String priceInEUR) {
        if (priceInEUR == null) {
            return 0f;
        }
        try {
            String numericPart = priceInEUR.replaceAll("[^\\d.]", "");
            return Float.parseFloat(numericPart);
        } catch (NumberFormatException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return 0f;
        }
    }
}