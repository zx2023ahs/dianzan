package cn.rh.flash.utils;

public class SqlFileNameUtils {

    /**
     * 小驼峰转下换线
     * @param input
     * @return
     */
    public static String convert(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                // Convert uppercase letter to lowercase and add underscore before it
                result.append("_").append(Character.toLowerCase(currentChar));
            } else {
                // Keep lowercase letters and digits unchanged
                result.append(currentChar);
            }
        }
        return result.toString();
    }
}
