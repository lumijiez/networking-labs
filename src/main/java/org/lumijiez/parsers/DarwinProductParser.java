package org.lumijiez.parsers;

import org.lumijiez.models.Product;
import org.lumijiez.network.TCPClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DarwinProductParser {
    public static List<Product> parseProducts(String html) {
        List<Product> products = new ArrayList<>();
        String productPattern = "<div class=\"col-6 col-md-4 col-lg-3 night-mode\">.*?</div>\\s*</figure>\\s*</div>";
        Pattern pattern = Pattern.compile(productPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String productHtml = matcher.group();
            Product product = extractProductInfo(productHtml);
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    private static Product extractProductInfo(String productHtml) {
        String name = extractWithRegex(productHtml, "title=\"([^\"]+)\"\\s+class=\"ga-item\"");
        String imageUrl = extractWithRegex(productHtml, "src=\"([^\"]+)\"");
        String specifications = extractWithRegex(productHtml, "<span class=\"specification d-block\">([^<]+)</span>");
        String oldPrice = extractWithRegex(productHtml, "<span class=\"last-price\">([^<]+)</span>");
        String newPrice = extractWithRegex(productHtml, "<span class=\"price-new\"><b>([^<]+)</b>");
        String discount = extractWithRegex(productHtml, "<span class=\"difprice aclas\" title=\"([^\"]+)\">");
        String productLink = extractWithRegex(productHtml, "href=\"([^\"]+)\"\\s+title=\"[^\"]+\"\\s+class=\"ga-item\"");
        String id = extractId(productLink);
        if (name == null || newPrice == null) {
            return null;
        }

        return new Product(id, name, imageUrl, specifications, oldPrice, newPrice, discount, productLink);
    }

    private static String extractWithRegex(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String extractId(String productLink) {
        String body = TCPClient.getHttps(productLink);

        String regex = "ID produs:\\s*<strong[^>]*>\\s*(\\d{2}\\.\\d{8}\\.\\d{3})\\s*</strong>";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "0";
    }
}
