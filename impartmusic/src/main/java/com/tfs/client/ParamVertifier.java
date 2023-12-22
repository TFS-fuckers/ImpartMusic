package com.tfs.client;

public class ParamVertifier {
    private ParamVertifier() {}

    /**
     * 是否为符合要求的名称
     * @param name 名称
     * @return 是否符合要求
     */
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
