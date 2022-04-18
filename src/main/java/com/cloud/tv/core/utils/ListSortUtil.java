package com.cloud.tv.core.utils;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ListSortUtil {

    public static void compareTo(List<Map<Object, Object>> list){
        Collections.sort(list, new Comparator<Map<Object, Object>>() {
            @Override
            public int compare(Map<Object, Object> o1, Map<Object, Object> o2) {
                String key1 = o1.get("policyCheckTotal").toString();
                String key2 = o2.get("policyCheckTotal").toString();
                return key2.compareTo(key1);
            }
        });

    }

    public static void sort(List<Map<String, Double>> list){
        Collections.sort(list, new Comparator<Map<String, Double>>() {
            @Override
            public int compare(Map<String, Double> o1, Map<String, Double> o2) {
                Integer key1 = o1.get("grade").intValue();
                Integer key2 = o2.get("grade").intValue();
                return key1.compareTo(key2);
            }
        });
    }
}
