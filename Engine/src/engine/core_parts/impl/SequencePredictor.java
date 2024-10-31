package engine.core_parts.impl;//package engine.core_parts.impl;
//
//import java.util.*;
//
//public class SequencePredictor {
//
//    // Predicts the next count elements in the sequence based on the initial elements
//    public static List<String> predictNext(List<String> initial, int count) {
//        List<String> result = new ArrayList<>();
//        if (initial.size() < 2 || count <= 0) return result;
//
//        // Correct typos in initial input and add them to the result
//        List<String> correctedInitial = new ArrayList<>();
//        for (String str : initial) {
//            String corrected = findClosestMatch(normalize(str), getDaysOfWeek(), getMonthsOfYear(), getHolidays());
//            correctedInitial.add(corrected != null ? corrected : str);
//        }
//        result.addAll(correctedInitial); // Add the corrected initial elements
//
//        String first = correctedInitial.get(0);
//        String second = correctedInitial.get(1);
//
//        // Check for days of the week pattern
//        if (isDayOfWeek(first) && isDayOfWeek(second)) {
//            result.addAll(generateCyclicPattern(correctedInitial, count, getDaysOfWeek()));
//        }
//        // Check for months of the year pattern
//        else if (isMonth(first) && isMonth(second)) {
//            result.addAll(generateCyclicPattern(correctedInitial, count, getMonthsOfYear()));
//        }
//        // Check for holidays pattern
//        else if (isHoliday(first) && isHoliday(second)) {
//            result.addAll(generateCyclicPattern(correctedInitial, count, getHolidays()));
//        }
//        // Check for numeric pattern
//        else if (isNumericPattern(correctedInitial)) {
//            result.addAll(generateNumericPattern(correctedInitial, count));
//        }
//        // Check for text with numbers pattern
//        else if (isTextWithNumberPattern(correctedInitial)) {
//            result.addAll(generateTextWithNumberPattern(correctedInitial, count));
//        } else {
//            System.err.println("Unrecognized or inconsistent pattern. Please check for typos or try again with a clearer pattern.");
//        }
//        return result;
//    }
//
//    // Generates a cyclic pattern based on known sequence, wrapping around after completion
//    private static List<String> generateCyclicPattern(List<String> initial, int count, List<String> sequence) {
//        List<String> result = new ArrayList<>();
//        int startIndex = sequence.indexOf(initial.get(initial.size() - 1));
//        if (startIndex == -1) return result;
//
//        for (int i = 0; i < count; i++) {
//            result.add(sequence.get((startIndex + i + 1) % sequence.size()));
//        }
//        return result;
//    }
//
//    // Finds the closest match in the target lists using edit distance
//    private static String findClosestMatch(String input, List<String>... targetLists) {
//        int minDistance = Integer.MAX_VALUE;
//        String closestMatch = null;
//
//        for (List<String> targetList : targetLists) {
//            for (String target : targetList) {
//                int distance = calculateEditDistance(input, target);
//                int threshold = Math.max(1, target.length() / 3);  // Adaptive threshold based on word length
//                if (distance < minDistance && distance <= threshold) {
//                    minDistance = distance;
//                    closestMatch = target;
//                }
//            }
//        }
//        return closestMatch;
//    }
//
//    // Calculates edit distance between two strings (Levenshtein distance)
//    private static int calculateEditDistance(String str1, String str2) {
//        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
//
//        for (int i = 0; i <= str1.length(); i++) dp[i][0] = i;
//        for (int j = 0; j <= str2.length(); j++) dp[0][j] = j;
//
//        for (int i = 1; i <= str1.length(); i++) {
//            for (int j = 1; j <= str2.length(); j++) {
//                int cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
//                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
//            }
//        }
//        return dp[str1.length()][str2.length()];
//    }
//
//    // Normalizes input to ignore case, minor formatting inconsistencies, and common abbreviations
//    private static String normalize(String str) {
//        return str.trim().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
//    }
//
//    // Checks if a string is a day of the week (after normalization)
//    private static boolean isDayOfWeek(String str) {
//        return getDaysOfWeek().contains(str);
//    }
//
//    // Checks if a string is a month (after normalization)
//    private static boolean isMonth(String str) {
//        return getMonthsOfYear().contains(str);
//    }
//
//    // Checks if a string is a holiday (after normalization)
//    private static boolean isHoliday(String str) {
//        return getHolidays().contains(str);
//    }
//
//    // Helper to get list of days in week, normalized for consistency
//    private static List<String> getDaysOfWeek() {
//        return Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
//    }
//
//    // Helper to get list of months in the year, normalized for consistency
//    private static List<String> getMonthsOfYear() {
//        return Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");
//    }
//
//    // Helper to get list of holidays, normalized for consistency
//    private static List<String> getHolidays() {
//        return Arrays.asList("newyear", "christmas", "thanksgiving", "easter", "halloween", "independenceday");
//    }
//
//    // Check if initial list follows a numeric pattern
//    // Check if initial list follows a numeric pattern
//    private static boolean isNumericPattern(List<String> initial) {
//        try {
//            int first = Integer.parseInt(initial.get(0).trim());
//            int second = Integer.parseInt(initial.get(1).trim());
//            return (second - first) == (Integer.parseInt(initial.get(2).trim()) - second);
//        } catch (NumberFormatException | IndexOutOfBoundsException e) {
//            return false;
//        }
//    }
//
//    private static List<String> generateNumericPattern(List<String> initial, int count) {
//        List<String> result = new ArrayList<>();
//        int first = Integer.parseInt(initial.get(0).trim());
//        int second = Integer.parseInt(initial.get(1).trim());
//        int diff = second - first;
//
//        int nextValue = Integer.parseInt(initial.get(initial.size() - 1).trim()) + diff;
//        for (int i = 0; i < count; i++) {
//            result.add(String.valueOf(nextValue));
//            nextValue += diff;
//        }
//        return result;
//    }
//
//    // Check if initial list follows a text with number pattern
//    private static boolean isTextWithNumberPattern(List<String> initial) {
//        return initial.stream().allMatch(str -> str.matches(".*\\d+"));
//    }
//
//    // Generate a sequence for text with numbers (e.g., "ex 1", "ex 2")
//    private static List<String> generateTextWithNumberPattern(List<String> initial, int count) {
//        List<String> result = new ArrayList<>();
//        String prefix = initial.get(0).replaceAll("\\d", "").trim();
//        int lastNumber = Integer.parseInt(initial.get(initial.size() - 1).replaceAll("\\D+", ""));
//        int secondLastNumber = Integer.parseInt(initial.get(initial.size() - 2).replaceAll("\\D+", ""));
//        int diff = lastNumber - secondLastNumber;
//
//        int nextNumber = lastNumber + diff;
//        for (int i = 0; i < count; i++) {
//            result.add(prefix + " " + nextNumber);
//            nextNumber += diff;
//        }
//        return result;
//    }
//}
//


import java.util.*;

public class SequencePredictor {

    // Predicts the next count elements in the sequence based on the initial elements
    public static List<String> predictNext(List<String> initial, int count) {
        List<String> result = new ArrayList<>();
        if (initial.size() < 2 || count <= 0) return result;

        // Calculate how many additional elements are needed
        int additionalElements = count - initial.size();
        if (additionalElements <= 0) {
            return new ArrayList<>(initial.subList(0, count)); // Return initial values if count <= initial size
        }

        // Fill in missing (blank) values in the initial list
        List<String> filledInitial = fillInMissingValues(initial);

        // Correct typos in initial input and add them to the result
        List<String> correctedInitial = new ArrayList<>();
        for (String str : filledInitial) {
            String corrected = findClosestMatch(str, getDaysOfWeek(), getMonthsOfYear(), getHolidays());
            correctedInitial.add(corrected != null ? corrected : str);
        }
        result.addAll(correctedInitial); // Add the corrected initial elements

        // Get the last two elements from corrected initial to detect the pattern
        String last = correctedInitial.get(correctedInitial.size() - 1);
        String secondLast = correctedInitial.get(correctedInitial.size() - 2);

        // Check for days of the week pattern
        if (isDayOfWeek(secondLast) && isDayOfWeek(last)) {
            result.addAll(generateCyclicPattern(last, additionalElements, getDaysOfWeek()));
        }
        // Check for months of the year pattern
        else if (isMonth(secondLast) && isMonth(last)) {
            result.addAll(generateCyclicPattern(last, additionalElements, getMonthsOfYear()));
        }
        // Check for holidays pattern
        else if (isHoliday(secondLast) && isHoliday(last)) {
            result.addAll(generateCyclicPattern(last, additionalElements, getHolidays()));
        }
        // Check for numeric pattern
        else if (isNumericPattern(correctedInitial)) {
            result.addAll(generateNumericPattern(correctedInitial, additionalElements));
        }
        // Check for text with numbers pattern
        else if (isTextWithNumberPattern(correctedInitial)) {
            result.addAll(generateTextWithNumberPattern(correctedInitial, additionalElements));
        } else {
            System.err.println("Unrecognized or inconsistent pattern. Please check for typos or try again with a clearer pattern.");
        }
        return result;
    }

    // Fills in missing values based on detected patterns
    private static List<String> fillInMissingValues(List<String> initial) {
        List<String> filledInitial = new ArrayList<>(initial);

        if (isNumericPattern(filledInitial)) {
            int first = Integer.parseInt(filledInitial.get(0).trim());
            int second = Integer.parseInt(filledInitial.get(1).trim());
            int diff = second - first;
            for (int i = 2; i < filledInitial.size(); i++) {
                if (filledInitial.get(i).isEmpty()) {
                    int predictedValue = Integer.parseInt(filledInitial.get(i - 1).trim()) + diff;
                    filledInitial.set(i, String.valueOf(predictedValue));
                }
            }
        } else if (isTextWithNumberPattern(filledInitial)) {
            String prefix = filledInitial.get(0).replaceAll("\\d", "").trim();
            int lastNumber = Integer.parseInt(filledInitial.get(1).replaceAll("\\D+", ""));
            int secondLastNumber = Integer.parseInt(filledInitial.get(0).replaceAll("\\D+", ""));
            int diff = lastNumber - secondLastNumber;
            for (int i = 2; i < filledInitial.size(); i++) {
                if (filledInitial.get(i).isEmpty()) {
                    int predictedValue = Integer.parseInt(filledInitial.get(i - 1).replaceAll("\\D+", "")) + diff;
                    filledInitial.set(i, prefix + " " + predictedValue);
                }
            }
        } else {
            for (int i = 0; i < filledInitial.size(); i++) {
                if (filledInitial.get(i).isEmpty()) {
                    String prevValue = filledInitial.get(i - 1);
                    List<String> sequence = null;
                    if (isDayOfWeek(prevValue)) sequence = getDaysOfWeek();
                    else if (isMonth(prevValue)) sequence = getMonthsOfYear();
                    else if (isHoliday(prevValue)) sequence = getHolidays();
                    if (sequence != null) {
                        int index = (sequence.indexOf(prevValue.toLowerCase()) + 1) % sequence.size();
                        filledInitial.set(i, sequence.get(index));
                    }
                }
            }
        }
        return filledInitial;
    }

    // Helper to find closest match using edit distance
    private static String findClosestMatch(String input, List<String>... targetLists) {
        int minDistance = Integer.MAX_VALUE;
        String closestMatch = null;
        input = input.toLowerCase();
        for (List<String> targetList : targetLists) {
            for (String target : targetList) {
                int distance = calculateEditDistance(input, target.toLowerCase());
                int threshold = Math.max(1, target.length() / 3);
                if (distance < minDistance && distance <= threshold) {
                    minDistance = distance;
                    closestMatch = target;
                }
            }
        }
        return closestMatch;
    }

    // Calculates edit distance
    private static int calculateEditDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        for (int i = 0; i <= str1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= str2.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[str1.length()][str2.length()];
    }

    // Check if initial list follows a numeric pattern
    private static boolean isNumericPattern(List<String> initial) {
        try {
            int first = Integer.parseInt(initial.get(0).trim());
            int second = Integer.parseInt(initial.get(1).trim());
            return (second - first) == (Integer.parseInt(initial.get(2).trim()) - second);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    // Generates a numeric sequence
    private static List<String> generateNumericPattern(List<String> initial, int count) {
        List<String> result = new ArrayList<>();
        int first = Integer.parseInt(initial.get(0).trim());
        int second = Integer.parseInt(initial.get(1).trim());
        int diff = second - first;
        int nextValue = Integer.parseInt(initial.get(initial.size() - 1).trim()) + diff;
        for (int i = 0; i < count; i++) {
            result.add(String.valueOf(nextValue));
            nextValue += diff;
        }
        return result;
    }

    // Check if initial list follows a text with number pattern
    private static boolean isTextWithNumberPattern(List<String> initial) {
        return initial.stream().allMatch(str -> str.matches(".*\\d+"));
    }

    // Generates a text with numbers sequence
    private static List<String> generateTextWithNumberPattern(List<String> initial, int count) {
        List<String> result = new ArrayList<>();
        String prefix = initial.get(0).replaceAll("\\d", "").trim();
        int lastNumber = Integer.parseInt(initial.get(initial.size() - 1).replaceAll("\\D+", ""));
        int secondLastNumber = Integer.parseInt(initial.get(initial.size() - 2).replaceAll("\\D+", ""));
        int diff = lastNumber - secondLastNumber;
        int nextNumber = lastNumber + diff;
        for (int i = 0; i < count; i++) {
            result.add(prefix + " " + nextNumber);
            nextNumber += diff;
        }
        return result;
    }

    // Cyclic pattern generation for days, months, and holidays
    private static List<String> generateCyclicPattern(String startValue, int count, List<String> sequence) {
        List<String> result = new ArrayList<>();
        int startIndex = sequence.indexOf(startValue.toLowerCase());
        if (startIndex == -1) return result;
        for (int i = 0; i < count; i++) {
            result.add(sequence.get((startIndex + i + 1) % sequence.size()));
        }
        return result;
    }

    // Day, month, and holiday lists and checks
    private static List<String> getDaysOfWeek() {
        return Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
    }

    private static List<String> getMonthsOfYear() {
        return Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");
    }

    private static List<String> getHolidays() {
        return Arrays.asList("newyear", "christmas", "thanksgiving", "easter", "halloween", "independenceday");
    }

    private static boolean isDayOfWeek(String str) {
        return getDaysOfWeek().contains(str.toLowerCase());
    }

    private static boolean isMonth(String str) {
        return getMonthsOfYear().contains(str.toLowerCase());
    }

    private static boolean isHoliday(String str) {
        return getHolidays().contains(str.toLowerCase());
    }
}
