package com.tfs.client;

public class ParamVertifier {
    private ParamVertifier() {}

    public static boolean isValidName(String name) {
        for(int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if(!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }
}
