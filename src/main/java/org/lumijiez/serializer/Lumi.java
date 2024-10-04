package org.lumijiez.serializer;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lumi {
    public static String toJson(Object obj) throws IllegalAccessException {
        if (!obj.getClass().isAnnotationPresent(LumiSerializable.class)) {
            throw new IllegalArgumentException("Class is not marked as LumiSerializable");
        }

        StringBuilder json = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> serializedFields = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(LumiSerializeField.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(obj);
                serializedFields.add("\"" + fieldName + "\":" + valueToJson(value));
            }
        }

        json.append(String.join(",", serializedFields));
        json.append("}");
        return json.toString();
    }

    private static String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJsonString((String) value) + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof List) {
            return listToJson((List<?>) value);
        } else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return "\"" + dateFormat.format((Date) value) + "\"";
        } else {
            try {
                return toJson(value);
            } catch (IllegalAccessException e) {
                return "\"" + value + "\"";
            }
        }
    }

    private static String listToJson(List<?> list) {
        List<String> jsonItems = new ArrayList<>();
        for (Object item : list) {
            jsonItems.add(valueToJson(item));
        }
        return "[" + String.join(",", jsonItems) + "]";
    }

    private static String escapeJsonString(String input) {
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String toXml(Object obj) throws IllegalAccessException {
        if (!obj.getClass().isAnnotationPresent(LumiSerializable.class)) {
            throw new IllegalArgumentException("Class is not marked as LumiSerializable");
        }

        StringBuilder xml = new StringBuilder("<" + obj.getClass().getSimpleName() + ">");
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(LumiSerializeField.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(obj);
                xml.append(valueToXml(fieldName, value));
            }
        }

        xml.append("</").append(obj.getClass().getSimpleName()).append(">");
        return xml.toString();
    }

    private static String valueToXml(String fieldName, Object value) {
        StringBuilder xml = new StringBuilder("<" + fieldName + ">");

        if (value == null) {
            xml.append("null");
        } else if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            xml.append(escapeXmlString(value.toString()));
        } else if (value instanceof List) {
            xml.append(listToXml((List<?>) value));
        } else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            xml.append(escapeXmlString(dateFormat.format((Date) value)));
        } else if (value instanceof java.time.LocalDate) {
            xml.append(escapeXmlString(((java.time.LocalDate) value).toString()));
        } else if (value instanceof java.time.LocalDateTime) {
            xml.append(escapeXmlString(((java.time.LocalDateTime) value).toString()));
        } else if (value instanceof java.time.ZonedDateTime) {
            xml.append(escapeXmlString(((java.time.ZonedDateTime) value).toString()));
        } else {
            try {
                xml.append(toXml(value));
            } catch (IllegalAccessException e) {
                xml.append(escapeXmlString(value.toString()));
            }
        }

        xml.append("</").append(fieldName).append(">");
        return xml.toString();
    }

    private static String listToXml(List<?> list) {
        StringBuilder xml = new StringBuilder();
        for (Object item : list) {
            xml.append("<item>");
            xml.append(valueToXml("value", item));
            xml.append("</item>");
        }
        return xml.toString();
    }

    private static String escapeXmlString(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        if (!clazz.isAnnotationPresent(LumiSerializable.class)) {
            throw new IllegalArgumentException("Class is not marked as LumiSerializable");
        }

        T obj = clazz.getDeclaredConstructor().newInstance();
        Pattern pattern = Pattern.compile("\"(\\w+)\"\\s*:\\s*(.+?)(?=,\\s*\"|\\})");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = matcher.group(2).trim();

            Field field = getFieldByName(clazz, fieldName);
            if (field != null && field.isAnnotationPresent(LumiSerializeField.class)) {
                field.setAccessible(true);
                field.set(obj, parseValue(fieldValue, field.getType()));
            }
        }
        return obj;
    }

    public static <T> T fromXml(String xml, Class<T> clazz) throws Exception {
        if (!clazz.isAnnotationPresent(LumiSerializable.class)) {
            throw new IllegalArgumentException("Class is not marked as LumiSerializable");
        }

        T obj = clazz.getDeclaredConstructor().newInstance();
        Pattern pattern = Pattern.compile("<(\\w+)>(.*?)</\\1>");
        Matcher matcher = pattern.matcher(xml);

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = matcher.group(2);

            Field field = getFieldByName(clazz, fieldName);
            if (field != null && field.isAnnotationPresent(LumiSerializeField.class)) {
                field.setAccessible(true);
                field.set(obj, parseValue(fieldValue, field.getType()));
            }
        }

        return obj;
    }

    private static Field getFieldByName(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static Object parseValue(String value, Class<?> type) {
        if (type == String.class) {
            return value.replaceAll("^\"|\"$", "");
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }
        return value;
    }

    public static void xmlToFile(Object obj, String fileName) throws IllegalAccessException {
        XmlFileWriter.saveXmlToFile(obj, fileName);
    }

    public static void jsonToFile(Object obj, String fileName) throws IllegalAccessException {
        JsonFileWriter.saveJsonToFile(obj, fileName);
    }
}
