package com.data.filtro.Util;

import com.data.filtro.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonConverter {

    public static String convertListToJsonProduct(List<Product> products) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(products);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertToJsonProduct(Product product) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(product);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Product> convertJsonToListProduct(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Product> products = objectMapper.readValue(json, new TypeReference<List<Product>>() {});
            return products;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Product convertJsonToProduct(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Product product = objectMapper.readValue(json, Product.class);
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

