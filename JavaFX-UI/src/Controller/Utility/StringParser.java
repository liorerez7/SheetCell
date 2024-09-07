package Controller.Utility;

import expression.api.EffectiveValue;

public class StringParser {

    public static String convertValueToLabelText(EffectiveValue effectiveValue)
    {
        Object objectValue;
        String value = "";

        if (effectiveValue != null) {
            objectValue = effectiveValue.getValue();

            if (objectValue instanceof Double) {
                Double doubleValue = (Double) objectValue;

                // Check if the value is NaN
                if (doubleValue.isNaN()) {
                    value = "NaN";
                } else {
                    // Convert the double to an int and then to a string
                    value = Integer.toString(doubleValue.intValue());
                }
            } else {
                // Just use the object's string value
                value = objectValue.toString();
            }
        }
        return value;
    }
}
