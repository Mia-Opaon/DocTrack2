package com.triadss.doctrack2.utils;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckboxStringProcessor {
    private final String DELIMITER = "|";
    private final String SPLITDELIMITER = "\\|";
    ArrayList<CheckBox> checkboxes;
    CheckBox othersCheckbox;
    EditText othersText;
    public CheckboxStringProcessor(CheckBox othersCheckbox, EditText othersText, CheckBox... checkboxes)
    {
        this.checkboxes = Arrays.stream(checkboxes).collect(Collectors.toCollection(ArrayList::new));
        this.othersCheckbox = othersCheckbox;
        this.othersText = othersText;
        othersCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // CheckBox is checked
                    // Perform your logic here
                } else {
                    othersText.setText("");
                }
            }
        });
    }

    public void PopulateFromString(String stringValue)
    {
        List<String> values = Arrays.stream(stringValue.split(SPLITDELIMITER)).collect(Collectors.toList());
        for(CheckBox checkbox : checkboxes) {
            checkbox.setChecked(values.contains(checkbox.getText()));
        }
        String noneValue = values.stream()
                .filter(value -> !checkboxes.stream().anyMatch(checkBox -> checkBox.getText().equals(value)))
                .findFirst().orElse("");
        othersCheckbox.setChecked(!noneValue.isEmpty());
        othersText.setText(noneValue);
    }

    public String getString() {
        List<String> checkedNames = checkboxes.stream().filter(checkBox -> checkBox.isChecked())
                .map(checkBox -> checkBox.getText().toString())
                .collect(Collectors.toList());
        if (othersCheckbox.isChecked()) {
            checkedNames.add(othersText.getText().toString());
        }
        String collectedFromCheckboxes = String.join(DELIMITER, checkedNames);
        return collectedFromCheckboxes;
    }

    public static List<String> StringToSeparatedValues(String delimiter, String separatedValues) {
        if(delimiter == "|")
            delimiter = "\\|";
        String[] splitList = separatedValues.split(delimiter);
        List<String> separatedList = Arrays.stream(separatedValues.split(delimiter))
                .collect(Collectors.toList());
        return separatedList;
    }

    public static String SeparatedValuesToString(String delimiter, List<String> separatedValues)
    {
        String collectedString = String.join(delimiter, separatedValues);
        return collectedString;
    }
}
