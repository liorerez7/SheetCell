package Controller.Utility;

import expression.api.EffectiveValue;

import java.text.DecimalFormat;

public class StringParser {

    public static String convertValueToLabelText(EffectiveValue effectiveValue) {
        Object objectValue;
        String value = "";

        if (effectiveValue != null) {
            objectValue = effectiveValue.getValue();

            if (objectValue instanceof Double) {
                Double doubleValue = (Double) objectValue;

                // Check if the value is NaN
                if (doubleValue.isNaN()) {
                    value = "NaN";
                }
                else{
                    if (doubleValue % 1 == 0) {
                        value = Integer.toString(doubleValue.intValue());  // Convert to int if no decimal part
                    }
                    else {
                        // Format to 2 decimal places if there are decimals
                        DecimalFormat df = new DecimalFormat("#.##");
                        value = df.format(doubleValue);
                    }
                }
            }
            else {
                if(objectValue instanceof Boolean) {
                    Boolean boolValue = (Boolean) objectValue;
                    value = boolValue ? "TRUE" : "FALSE";
                }
                else {
                    // Just use the object's string value
                    value = objectValue.toString();
                }
            }
        }
        return value;
    }
}
