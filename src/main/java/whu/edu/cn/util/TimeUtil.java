package whu.edu.cn.util;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TimeUtil {

    public enum DateTimeType {
        DATE_TIME,
        INTERVAL
    }

    public static class DateTimeResult {
        public DateTimeType type;
        public String value;
        public String startTime;
        public String endTime;
    }

    public enum ISOTimeType {
        DATE,
        TIME,
        DATE_TIME
    }

    public static class ISODateTime {
        public ISOTimeType type;
        public String value;
    }

    /**
     * Parses a date and time string in ISO 8601 format.
     * The method takes a string as input and tries to parse it as an ISO 8601 date and time string.
     * It first tries to parse the string as an OffsetDateTime using the DateTimeFormatter.ISO_DATE_TIME formatter.
     * If that fails, it tries to parse the string as a LocalDate using the "yyyy-MM-dd" formatter.
     * If that fails, it tries to parse the string as a LocalTime using the "HH:mm:ss" formatter.
     * If that fails, it tries to parse the string as a LocalDateTime using the "yyyy-MM-dd HH:mm:ss" formatter.
     * If all of these attempts fail, the method returns null.
     * The method returns an ISODateTime object that contains the parsed value and type (date, time, or date-time).
     *
     * @param timeInput the date and time string to parse
     * @return an ISODateTime object that contains the parsed value and type, or null if the string is not a valid date, time, or date-time
     */
    public ISODateTime parseDateTimeISO8610(String timeInput) {
        ISODateTime isoDateTime = new ISODateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(timeInput, DateTimeFormatter.ISO_DATE_TIME);
            System.out.println("Parsed as ISO 8601 date-time");
            isoDateTime.type = ISOTimeType.DATE_TIME;
            isoDateTime.value = convertTime(timeInput);
            return isoDateTime;
        } catch (DateTimeParseException eISO) {
            try {
                LocalDate date = LocalDate.parse(timeInput, dateFormatter);
                System.out.println("Parsed as date");
                isoDateTime.type = ISOTimeType.DATE;
                isoDateTime.value = timeInput;
                return isoDateTime;
            } catch (DateTimeParseException eDate) {
                try {
                    LocalTime time = LocalTime.parse(timeInput, timeFormatter);
                    System.out.println("Parsed as time");
                    isoDateTime.type = ISOTimeType.TIME;
                    isoDateTime.value = timeInput;
                    return isoDateTime;
                } catch (DateTimeParseException eTime) {
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(timeInput, dateTimeFormatter);
                        System.out.println("Parsed as date-time");
                        isoDateTime.type = ISOTimeType.DATE_TIME;
                        isoDateTime.value = timeInput;
                        return isoDateTime;
                    } catch (DateTimeParseException eDateTime) {
                        System.out.println("Not a valid date, time, or date-time");
                        return null;
                    }
                }
            }
        }
    }

    /**
     * Parses a date and time string using a regular expression.
     * The method takes a string as input and tries to parse it as a date and time string using a regular expression.
     * The method uses a regular expression to match the input string against several possible patterns for date and time strings.
     * If the input string matches one of the patterns, the method sets the type and value of the DateTimeResult object accordingly.
     * If the input string does not match any of the patterns, the method returns a DateTimeResult object with null type and value.
     * The method returns a DateTimeResult object that contains the parsed value and type (date, time, or interval).
     *
     * @param input the date and time string to parse
     * @return a DateTimeResult object that contains the parsed value and type, or null if the string is not a valid date, time, or interval
     */
    public DateTimeResult parseDateTime(String input) {
        String dateTimePattern = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z)";
        String pattern = dateTimePattern + "|(" + dateTimePattern + "\\/" + dateTimePattern + ")" +
                "|(\\.\\.\\/" + dateTimePattern + ")" +
                "|(" + dateTimePattern + "\\/\\.\\.)";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        DateTimeResult result = new DateTimeResult();
        int a = matcher.groupCount();
        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                result.type = DateTimeType.DATE_TIME;
                result.value = matcher.group(1);
            } else if (matcher.group(2) != null) {
                result.type = DateTimeType.INTERVAL;
                result.startTime = matcher.group(3);
                result.endTime = matcher.group(4);
            } else if (matcher.group(5) != null) {
                result.type = DateTimeType.INTERVAL;
                result.endTime = matcher.group(6);
            } else if (matcher.group(7) != null) {
                result.type = DateTimeType.INTERVAL;
                result.startTime = matcher.group(8);
            }
        }
        return result;
    }

    /**
     * Converts a date and time string in ISO 8601 format to "yyyy-MM-dd HH:mm:ss" format.
     * The method takes a string as input and tries to parse it as an ISO 8601 date and time string using a SimpleDateFormat object.
     * If the input string is in the correct format, the method converts it to a  "yyyy-MM-dd HH:mm:ss" format.
     * The method returns the converted string, or null if the input string is not in the correct format.
     *
     * @param inputTime the date and time string to convert
     * @return the converted date and time string, or null if the input string is not in the correct format
     */
    public String convertTime(String inputTime) {
        if (inputTime == null) return null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = inputFormat.parse(inputTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a date and time string in format "yyyy-MM-dd HH:mm:ss" to a format "yyyy-MM-dd'T'HH:mm:ss'Z'".
     *
     * @param inputTime the date and time string to convert
     * @return the converted date and time string, or null if the input string is not in the correct format
     */
    public String convertTime2Standard(String inputTime) {
        if (inputTime == null) return null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = inputFormat.parse(inputTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a date and time string in "yyyy-MM-dd'T'HH:mm:ss'Z'" format to a Timestamp object.
     *
     * @param dateString the date and time string to convert
     * @return the Timestamp object, or null if the input string is not in the correct format
     */
    public Timestamp convertStringToTimestamp(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date parsedDate = dateFormat.parse(dateString);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] intersectTimeRanges(String[] timeRange1, String[] timeRange2) {
        LocalDateTime startTime1 = parseDateTimeStr(timeRange1[0]);
        LocalDateTime endTime1 = parseDateTimeStr(timeRange1[1]);

        LocalDateTime startTime2 = parseDateTimeStr(timeRange2[0]);
        LocalDateTime endTime2 = parseDateTimeStr(timeRange2[1]);

        LocalDateTime intersectionStart = startTime1.isAfter(startTime2) ? startTime1 : startTime2;
        LocalDateTime intersectionEnd = endTime1.isBefore(endTime2) ? endTime1 : endTime2;

        if (intersectionStart.isBefore(intersectionEnd) || intersectionStart.isEqual(intersectionEnd)) {
            // 有相交部分或者一个时间范围被完全包含在另一个范围中
            return new String[]{formatDateTimeStr(intersectionStart), formatDateTimeStr(intersectionEnd)};
        } else {
            // 没有相交部分
            return null;
        }
    }
    private static LocalDateTime parseDateTimeStr(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    private static String formatDateTimeStr(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static void main(String[] args) {
        // 示例时间范围数组
        String[] timeRange1 = {"1978-01-01 07:00:00", "1980-01-01 07:00:00"};
        String[] timeRange2 = {"1978-01-02 07:00:00", "1978-01-03 07:00:00"};

        // 调用函数获取相交部分的时间范围
        String[] intersection = intersectTimeRanges(timeRange1, timeRange2);

        // 打印结果
        if (intersection != null) {
            System.out.println("Time ranges intersect or one is completely contained within the other:");
            System.out.println("Intersection Start: " + intersection[0]);
            System.out.println("Intersection End: " + intersection[1]);
        } else {
            System.out.println("Time ranges do not intersect or one is not completely contained within the other.");
        }
    }

}
