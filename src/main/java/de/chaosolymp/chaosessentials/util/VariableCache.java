package de.chaosolymp.chaosessentials.util;

import de.chaosolymp.chaosessentials.config.VariableConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VariableCache {

    private static VariableCache instance;
    private final HashMap<String, String> varMap = new HashMap<>();

    private VariableCache() {
        Set<String> keySet = VariableConfig.get().getKeys(true);
        for (String k : keySet) {
            varMap.put(k, VariableConfig.get().getString(k));
        }

    }

    public static VariableCache getInstance() {
        if (instance == null) {
            instance = new VariableCache();
        }
        return instance;
    }

    public String getKeyFromValue(String value) {
        value = value.replaceAll("%", "");

        if (!varMap.containsValue(value))
            return null;
        for (Map.Entry<String, String> v : varMap.entrySet()) {
            if (v.getValue().equals(value)) {
                return v.getKey();
            }
        }
        return null;
    }

    public String getValue(String key) {
        return varMap.get(key);
    }

    public HashMap<String, String> getVarMap() {
        return varMap;
    }
}
