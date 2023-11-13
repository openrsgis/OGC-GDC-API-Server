package whu.edu.cn.entity.coverage;

import whu.edu.cn.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoverageSubset {
    private String properties;
    private String subset;
    private Double scaleFactor;
    private String scaleSize;
    private String scaleAxes;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getSubset() {
        return subset;
    }

    public void setSubset(String subset) {
        this.subset = subset;
    }

    public Double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(Double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public String getScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(String scaleSize) {
        this.scaleSize = scaleSize;
    }

    public String getScaleAxes() {
        return scaleAxes;
    }

    public void setScaleAxes(String scaleAxes) {
        this.scaleAxes = scaleAxes;
    }

    /**
     * B0,B1,B2 ---> [B0,B1,B2]
     *
     * @return [B0, B1, B2]
     */
    public String[] getPropertyList() {
        if (this.properties == null) return null;
        return this.properties.split(",");
    }

    /**
     * Lat(20,40),Lon(120,130) --> [120, 20, 130, 40]
     *
     * @return [120, 20, 130, 40]
     */
    public Double[] getSpatialSubset() {
        if (this.subset == null) return null;
//        String lonPattern = "Lon\\((\\d+):(\\d+)\\)";
//        String latPattern = "Lat\\((\\d+):(\\d+)\\)";
        String lonPattern = "Lon\\((.*?):(.*?)\\)";
        String latPattern = "Lat\\((.*?):(.*?)\\)";
        Pattern lonRegex = Pattern.compile(lonPattern);
        Matcher lonMatcher = lonRegex.matcher(this.subset);
        Pattern latRegex = Pattern.compile(latPattern);
        Matcher latMatcher = latRegex.matcher(this.subset);
        Double[] coordinates = new Double[4];
        if (lonMatcher.find()) {
            coordinates[0] = Double.parseDouble(lonMatcher.group(1));
            coordinates[2] = Double.parseDouble(lonMatcher.group(2));
        }
        if (latMatcher.find()) {
            coordinates[1] = Double.parseDouble(latMatcher.group(1));
            coordinates[3] = Double.parseDouble(latMatcher.group(2));
        }
        if (checkNullInArray(coordinates)) {
            return null;
        } else {
            return coordinates;
        }
    }

    public Boolean checkNullInArray(Double[] coordinates) {
        Boolean containsNull = false;
        for (Double coordinate : coordinates) {
            if (coordinate == null) {
                containsNull = true;
                break;
            }
        }
        return containsNull;
    }

    /**
     * time("2022-01-07") --> 2022-01-07
     *
     * @return 2022-01-07
     */
    public String getTemporalSubset() {
        if (this.subset == null) return null;
        String timePattern = "time\\(\"(.*?)\"\\)";
        Pattern timeRegex = Pattern.compile(timePattern);
        Matcher timeMatcher = timeRegex.matcher(this.subset);
        if (timeMatcher.find()) {
            return timeMatcher.group(1);
        } else {
            return null;
        }
    }

    /**
     * retrieve the datetime range from the subset.
     * time("2012-01-10") -> ["2012-01-10 00:00:00", "2012-01-10 23:59:59"]
     * time("2012-01-10":"2012-01-11") -> ["2012-01-10 00:00:00", "2012-01-11 23:59:59"]
     * time("2012-01-10 00:00:00") -> ["2012-01-10 00:00:00", "2012-01-10 00:00:00"]
     * time("2012-01-10 00:00:00":"2012-01-11 00:00:00") -> ["2012-01-10 00:00:00", "2012-01-11 00:00:00"]
     * time("2012-01-10T00:00:00Z":"2012-01-11T00:00:00Z") -> ["2012-01-10 00:00:00", "2012-01-11 00:00:00"]
     *
     * @return
     */
    public List<String> getTemporalSubsetDouble() {
        if (this.subset == null) return null;
        List<String> timeString = new ArrayList<>();
        TimeUtil timeUtil = new TimeUtil();
        String timePattern2 = "time\\(\"(.*?)\":\"(.*?)\"\\)";
        if (this.subset.contains(":*")) {
            timePattern2 = "time\\(\"(.*?)\":\\*\\)";
        } else if (this.subset.contains("*:")) {
            timePattern2 = "time\\(\\*:\"(.*?)\"\\)";
        }
        Pattern timeRegex2 = Pattern.compile(timePattern2);
        Matcher timeMatcher2 = timeRegex2.matcher(this.subset);
        if (timeMatcher2.find()) {
            TimeUtil.ISODateTime isoDateTimeStart;
            TimeUtil.ISODateTime isoDateTimeEnd;
            isoDateTimeStart = timeUtil.parseDateTimeISO8610(timeMatcher2.group(1));
            if (this.subset.contains(":*") || this.subset.contains("*:")) {
                isoDateTimeEnd = null;
            } else {
                isoDateTimeEnd = timeUtil.parseDateTimeISO8610(timeMatcher2.group(2));
            }
            if (isoDateTimeStart == null && isoDateTimeEnd == null) {
                return null;
            } else {
                if (isoDateTimeStart == null) {
                    timeString.add(0, null);
                } else if (isoDateTimeStart.type == TimeUtil.ISOTimeType.DATE) {
                    timeString.add(0, isoDateTimeStart.value + " 00:00:00");
                } else if (isoDateTimeStart.type == TimeUtil.ISOTimeType.DATE_TIME) {
                    timeString.add(0, isoDateTimeStart.value);
                } else {
                    timeString.add(0, null);
                }
                if (isoDateTimeEnd == null) {
                    timeString.add(1, null);
                } else if (isoDateTimeEnd.type == TimeUtil.ISOTimeType.DATE) {
                    timeString.add(1, isoDateTimeEnd.value + " 23:59:59");
                } else if (isoDateTimeEnd.type == TimeUtil.ISOTimeType.DATE_TIME) {
                    timeString.add(1, isoDateTimeEnd.value);
                } else {
                    timeString.add(1, null);
                }
                return timeString;
            }
        } else {
            String timePattern = "time\\(\"(.*?)\"\\)";
            Pattern timeRegex = Pattern.compile(timePattern);
            Matcher timeMatcher = timeRegex.matcher(this.subset);
            if (timeMatcher.find()) {
                TimeUtil.ISODateTime isoDateTime = timeUtil.parseDateTimeISO8610(timeMatcher.group(1));
                if (isoDateTime == null) {
                    return null;
                } else if (isoDateTime.type == TimeUtil.ISOTimeType.DATE) {
                    timeString.add(isoDateTime.value + " 00:00:00");
                    timeString.add(isoDateTime.value + " 23:59:59");
                    return timeString;
                } else if (isoDateTime.type == TimeUtil.ISOTimeType.DATE_TIME) {
                    timeString.add(isoDateTime.value);
                    timeString.add(isoDateTime.value);
                    return timeString;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public List<Subset> getAdditionalSubset() {
        List<Subset> subsetList = new ArrayList<>();
        if (this.subset == null) return null;
        String[] subsets = this.subset.split(",");
        for (String subsetCtr : subsets) {
            Subset subset = new Subset();
            String regex = "(.*?)\\((.*?)\\)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(subsetCtr);
            if (matcher.find()) {
                String axisName = matcher.group(1);
                if (axisName.equals("Lon") || axisName.equals("Lat") || axisName.equals("time")) continue;
                String content = matcher.group(2);
                boolean isInterval = content.contains(":");
                if (isInterval) {
                    String regex2 = "(.*?):(.*?)";
                    Pattern pattern2 = Pattern.compile(regex2);
                    Matcher matcher2 = pattern2.matcher(content);
                    if (matcher2.find()) {
                        subset.setNumber(!matcher2.group(1).contains("\"") && !matcher2.group(2).contains("\""));
                        Object lowPoint = ConvertPoint(matcher2.group(1));
                        Object highPoint = ConvertPoint(matcher2.group(2));
                        subset.setLowPoint(lowPoint);
                        subset.setHighPoint(highPoint);
                    }
                } else {
                    subset.setNumber(!content.contains("\""));
                    subset.setPoint(ConvertPoint(content));
                }
                subset.setAxisName(axisName);
                subset.setInterval(isInterval);
                subsetList.add(subset);
            }
        }
        return subsetList;
    }

    /**
     * 统一按照String返回
     *
     * @param point the point with string type
     * @return string without ""
     */
    public Object ConvertPoint(String point) {
        if (point.equals("*")) return null;
        if (!point.contains("\"")) {
            return point;
        } else {
            return point.replace("\"", "");
        }
    }


    /**
     * convert the scale-size to the integer list; For example: Lat(300),Lon(400)--->[400, 300] 经度在前面
     *
     * @return Integer [] (such as [300, 400])
     */
    public Integer[] getScaleSizeList() {
        if (this.scaleSize == null) return null;
        Integer[] lonLat = new Integer[2];
//        Pattern pattern = Pattern.compile("Lat\\((\\d+)\\),Lon\\((\\d+)\\)");
//        Matcher matcher = pattern.matcher(this.scaleSize);
//        if (matcher.find()) {
//            String latitude = matcher.group(1);
//            String longitude = matcher.group(2);
//            lonLat[0] = Integer.parseInt(longitude);
//            lonLat[1] = Integer.parseInt(latitude);
//        }
//        Pattern lonPattern = Pattern.compile("Lon\\((\\d+)\\)");
//        Pattern latPattern = Pattern.compile("Lat\\((\\d+)\\)");
//        Pattern lonPattern = Pattern.compile("Lon\\((.*?):(.*?)\\)");
//        Pattern latPattern = Pattern.compile("Lat\\((.*?):(.*?)\\)");
        Pattern lonPattern = Pattern.compile("Lon\\((.*?)\\)");
        Pattern latPattern = Pattern.compile("Lat\\((.*?)\\)");
        Matcher lonMatcher = lonPattern.matcher(this.scaleSize);
        Matcher latMatcher = latPattern.matcher(this.scaleSize);
        if (lonMatcher.find()) {
            lonLat[0] = Integer.parseInt(lonMatcher.group(1));
        }
        if (latMatcher.find()) {
            lonLat[1] = Integer.parseInt(latMatcher.group(1));
        }
        return lonLat;
    }

    /**
     * convert the scale axes to the integer list; For example: Lat(2),Lon(4)---> [4,2] 经度在前
     *
     * @return
     */
    public Double[] getScaleAxesList() {
        if (this.scaleAxes == null) return null;
        Double[] lonLat = new Double[2];
//        Pattern lonPattern = Pattern.compile("Lon\\((\\d+)\\)");
//        Pattern latPattern = Pattern.compile("Lat\\((\\d+)\\)");
//        Pattern lonPattern = Pattern.compile("Lon\\((.*?):(.*?)\\)");
//        Pattern latPattern = Pattern.compile("Lat\\((.*?):(.*?)\\)");
        Pattern lonPattern = Pattern.compile("Lon\\((.*?)\\)");
        Pattern latPattern = Pattern.compile("Lat\\((.*?)\\)");
        Matcher lonMatcher = lonPattern.matcher(this.scaleAxes);
        Matcher latMatcher = latPattern.matcher(this.scaleAxes);
        if (lonMatcher.find()) {
            lonLat[0] = Double.parseDouble(lonMatcher.group(1));
        }
        if (latMatcher.find()) {
            lonLat[1] = Double.parseDouble(latMatcher.group(1));
        }
        return lonLat;
    }

    public static void main(String[] args) {
        CoverageSubset coverageSubset = new CoverageSubset();
//        String content = "preasure(1000),date(\"2010-20-12\")";
//        coverageSubset.setSubset(content);
//        List<Subset> subsetList = coverageSubset.getAdditionalSubset();
//        System.out.println("s");
//        coverageSubset.setSubset("time(\"2022-01-07\")");
//        String time = coverageSubset.getTemporalSubset();
//        System.out.println(time);
//        String scaleAxes = "Lat(20)";
//        coverageSubset.setScaleAxes(scaleAxes);
//        Double [] s = coverageSubset.getScaleAxesList();
//        Double s1 = s[0];
//        Double s2 = s[1];
//        System.out.println("s");
//        coverageSubset.setSubset("time(\"2010-10-12T08:01:02Z\":\"2010-10-12T08:01:12Z\")");
////        coverageSubset.setSubset("time(\"2010-10-12T08:01:02Z\")");
//        coverageSubset.setSubset("time(\"2010-10-12T08:01:02Z\":*)");
//        coverageSubset.setSubset("time(\"2010-10-12 23:01:02\")");
//        List<String> time = coverageSubset.getTemporalSubsetDouble();
        coverageSubset.setSubset("time(\"2019-01-17T08:59:59Z\":\"2019-01-19T21:00:01Z\"),Lon(4.6819:4.8784),Lat(51.6876:51.8346)");
        Double[] extent = coverageSubset.getSpatialSubset();
        System.out.println(extent);
    }


}
