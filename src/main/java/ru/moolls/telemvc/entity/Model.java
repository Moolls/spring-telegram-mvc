package ru.moolls.telemvc.entity;

import lombok.Data;

import java.util.HashMap;

@Data
public class Model {
    private HashMap<String, String> modelMap;

    public void put(String key, String value) {
        modelMap.put(key, value);
    }
}
