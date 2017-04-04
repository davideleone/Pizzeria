package app.gestionale;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Funzioni {

    public static boolean isTimestampCorretto(String str) {
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        try {
            format.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        return (pattern.matcher(str).matches());
    }

    public static boolean isBoolean(String str) {
        return (str.toLowerCase().equals("false") || str.toLowerCase().equals("true"));
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return (str.indexOf(".") > -1);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}