package ru.dargen.lwsm.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class Maps {

    public boolean equals(Map<Object, Object> first, Map<Object, Object> second) {
        for (Map.Entry<Object, Object> entry : first.entrySet()) {
            if (!entry.getValue().equals(second.get(entry.getKey()))) {
                return false;
            }
        }

        return true;
    }

}
