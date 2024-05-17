package com.triadss.doctrack2.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EditTextStringProcessor {
    private final String DELIMITER = "|";
    private final String SPLITDELIMITER = "\\|";

    private final String KEYVALUEDELIMITER = ":";
    ArrayList<EditTextName> editTextNames;

    public EditTextStringProcessor(EditTextName... editTextNames)
    {
        this.editTextNames = Arrays.stream(editTextNames).collect(Collectors.toCollection(ArrayList::new));
    }

    public Map<String, String> getHashMap()
    {
        Map<String, String> map = editTextNames.stream()
                .collect(Collectors.toMap(editTextName -> editTextName.getName(),
                        editTextName -> editTextName.getEditText().getText().toString()));
        return map;
    }

    public void PopulateFromString(String stringValue)
    {
        List<String> separatedValue = StringToSeparatedValues(SPLITDELIMITER, stringValue);
        Map<String, String> map = SeparatedValuesToMap(KEYVALUEDELIMITER, separatedValue);
        for(EditTextName editTextName : editTextNames)
        {
            editTextName.getEditText().setText(map.get(editTextName.getName()));
        }
    }

    public String getString() {
        return getString(DELIMITER);
    }

    public String getString(String delimiter) {
        String collectedString = SeparatedValuesToString(DELIMITER,
                MapToSeparatedValues(KEYVALUEDELIMITER, getHashMap()));
        return collectedString;
    }

    public static List<String> StringToSeparatedValues(String delimiter, String separatedValues) {
        if(delimiter == "|")
            delimiter = "\\|";
        String[] splitList = separatedValues.split(delimiter);
        List<String> separatedList = Arrays.stream(separatedValues.split(delimiter))
                                            .collect(Collectors.toList());
        return separatedList;
    }

    public static Map<String, String> SeparatedValuesToMap(String delimiter, List<String> separatedValues) {
        Map<String, String> mapped = separatedValues.stream()
                .map(keyValueSeparated -> keyValueSeparated.split(delimiter))
                .collect(Collectors.toMap(keyValueArr -> keyValueArr[0],
                        keyValueArr -> keyValueArr.length > 1 ? keyValueArr[1] : ""));
        return mapped;
    }

    public static List<String> MapToSeparatedValues(String delimiter, Map<String, String> mapped)
    {
        List<String> mapEntries = mapped.entrySet().stream()
                .map(entry -> entry.getKey() + delimiter + entry.getValue())
                .collect(Collectors.toList());
        return mapEntries;
    }

    public static String SeparatedValuesToString(String delimiter, List<String> separatedValues)
    {
        String collectedString = String.join(delimiter, separatedValues);
        return collectedString;
    }
}
