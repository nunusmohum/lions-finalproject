package com.ll.ebookmarket.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Util {
    public static class str {
        public static boolean empty(String str) {
            if (str == null) return true;
            if (str.trim().length() == 0) return true;

            return false;
        }

        public static boolean eq(String str1, String str2) {
            if ( str1 == null && str2 == null ) return true;

            if ( str1 == null ) {
                str1 = "";
            }

            str1 = str1.trim();

            if ( str2 == null ) {
                str2 = "";
            }

            str2 = str2.trim();

            return str1.equals(str2);
        }
    }

    public static class url {
        public static String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }

    }

    public static class date {

        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class file {

        public static String getExt(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                    .orElse("");
        }
    }
}
