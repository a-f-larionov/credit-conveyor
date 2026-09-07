package ru.creditbank.common.library.helpers;

import java.util.Map;
import java.util.stream.Collectors;

public class LogHelper {

    private LogHelper() {
        // singleton utility class
    }

    public static String toStringDto(String className, Map<String, Object> fields) {
        var fieldsString = fields.entrySet().stream()
                .map(entry -> {
                    var key = entry.getKey();
                    var value = entry.getValue();
                    var formattedValue = (value instanceof String)
                            ? "\"" + value + "\""
                            : String.valueOf(value);
                    return key + "=" + formattedValue;
                })
                .collect(Collectors.joining(", "));
        return className + "[" + fieldsString + "]";
    }
}