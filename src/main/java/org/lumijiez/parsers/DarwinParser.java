package org.lumijiez.parsers;

public class DarwinParser {
    public static String extractProductList(String html) {
        String divStart = "<div class=\"item-products ga-list\" data-list-name=\"Căutare\">";
        String divEnd = "</div>";

        int startIndex = html.indexOf(divStart);
        if (startIndex == -1) {
            return "Product list div not found";
        }

        int endIndex = -1;
        int nestedLevel = 1;
        int searchStartIndex = startIndex + divStart.length();

        while (nestedLevel > 0 && searchStartIndex < html.length()) {
            int nextDivStart = html.indexOf("<div", searchStartIndex);
            int nextDivEnd = html.indexOf(divEnd, searchStartIndex);

            if (nextDivEnd == -1) {
                return "Malformed HTML: couldn't find closing div";
            }

            if (nextDivStart != -1 && nextDivStart < nextDivEnd) {
                nestedLevel++;
                searchStartIndex = nextDivStart + 1;
            } else {
                nestedLevel--;
                if (nestedLevel == 0) {
                    endIndex = nextDivEnd + divEnd.length();
                } else {
                    searchStartIndex = nextDivEnd + 1;
                }
            }
        }

        if (endIndex == -1) {
            return "Malformed HTML: couldn't find matching closing div";
        }

        return html.substring(startIndex, endIndex);
    }
}