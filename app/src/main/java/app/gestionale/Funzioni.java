package app.gestionale;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static String formattaData(String ts) {
        return (ts.isEmpty() ? "" : ts.split("-")[2] + "-" + ts.split("-")[1] + "-" + ts.split("-")[0]);
    }

    public static String formattaData(Timestamp ts) {
        String dataStr = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(ts.getTime()));
        return dataStr;
    }
}