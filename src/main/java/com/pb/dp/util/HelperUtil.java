package com.pb.dp.util;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class HelperUtil {

    public String capitailizeWord(String str) {
        if(ObjectUtils.isNotEmpty(str)) {
            StringBuffer s = new StringBuffer();
            char ch = ' ';
            for (int i = 0; i < str.length(); i++) {
                if (ch == ' ' && str.charAt(i) != ' ')
                    s.append(Character.toUpperCase(str.charAt(i)));
                else
                    s.append(str.charAt(i));
                ch = str.charAt(i);
            }
            // Return the string with trimming
            return s.toString().trim();
        } else {
            return null;
        }
    }
}
