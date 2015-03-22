package com.aayush.bae;

import java.util.Map;

/**
 * Created by aayushranaut on 3/22/15.
 * com.aayush.bae
 */
public class Utils {
    public static String substituteString(String template, Map<String, String> substitutions) {
        String result = AppController.getInstance().getResources().getString(R.string.main_url) + template;
        for (Map.Entry<String, String> substitution : substitutions.entrySet()) {
            String pattern = "{" + substitution.getKey() + "}";
            result = result.replace(pattern, substitution.getValue());
        }
        return result;
    }
}
