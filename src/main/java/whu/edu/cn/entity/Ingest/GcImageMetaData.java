package whu.edu.cn.entity.Ingest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author czp
 * @version 1.0
 * @date 2020/6/5 23:12
 */
public class GcImageMetaData {
    //影像对应瓦片编号，成像时间，产品时间,波段
    //影像不同层级对应瓦片编号
//    private String[] grids;
    private HashMap<Integer,String[]> gridsHashMap;
    private Timestamp phenomenonTime;
    private Timestamp resultTime;
    private String measurement;
    private Double TopLeftLatitude;
    private Double TopLeftLongitude;
    private Double TopRightLatitude;
    private Double TopRightLongitude;
    private Double BottomRightLatitude;
    private Double BottomRightLongitude;
    private Double BottomLeftLatitude;
    private Double BottomLeftLongitude;
    private String cellType;
    private String sensor;
    private String productName;
    private String dataType;

    public GcImageMetaData (){}

    public GcImageMetaData (HashMap<Integer,String[]> gridsHashMap,Timestamp phenomenonTime,
                            Timestamp resultTime,String measurement,
                            Double TopLeftLatitude,Double TopLeftLongitude,
                             Double TopRightLatitude, Double TopRightLongitude,
                            Double BottomRightLatitude, Double BottomRightLongitude,
                            Double BottomLeftLatitude, Double BottomLeftLongitude,
                            String cellType,String sensor,String productName,String dataType){
        this.gridsHashMap=gridsHashMap;
        this.measurement=measurement;
        this.phenomenonTime=phenomenonTime;
        this.resultTime=resultTime;
        this.TopLeftLatitude=TopLeftLatitude;
        this.TopLeftLongitude=TopLeftLongitude;
       this.TopRightLatitude=TopRightLatitude;
       this.TopRightLongitude=TopRightLongitude;
       this.BottomRightLatitude=BottomRightLatitude;
       this.BottomRightLongitude=BottomRightLongitude;
       this.BottomLeftLatitude=BottomLeftLatitude;
       this.BottomLeftLongitude=BottomLeftLongitude;
       this.cellType=cellType;
       this.sensor=sensor;
       this.productName=productName;
       this.dataType=dataType;


    }

    public HashMap<Integer,String[]> getGridsHashMap() {
        return gridsHashMap;
    }

    public void setGridsHashMap(HashMap<Integer,String[]> gridsHashMap) {
        this.gridsHashMap = gridsHashMap;
    }

    public Timestamp getPhenomenonTime() {
        return phenomenonTime;
    }

    public void setPhenomenonTime(Timestamp phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public Timestamp getResultTime() {
        return resultTime;
    }

    public void setResultTime(Timestamp resultTime) {
        this.resultTime = resultTime;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public Double getTopLeftLatitude() {
        return TopLeftLatitude;
    }

    public void setTopLeftLatitude(Double topLeftLatitude) {
        TopLeftLatitude = topLeftLatitude;
    }

    public Double getTopLeftLongitude() {
        return TopLeftLongitude;
    }

    public void setTopLeftLongitude(Double topLeftLongitude) {
        TopLeftLongitude = topLeftLongitude;
    }

    public Double getTopRightLatitude() {
        return TopRightLatitude;
    }

    public void setTopRightLatitude(Double topRightLatitude) {
        TopRightLatitude = topRightLatitude;
    }

    public Double getTopRightLongitude() {
        return TopRightLongitude;
    }

    public void setTopRightLongitude(Double topRightLongitude) {
        TopRightLongitude = topRightLongitude;
    }

    public Double getBottomRightLatitude() {
        return BottomRightLatitude;
    }

    public void setBottomRightLatitude(Double bottomRightLatitude) {
        BottomRightLatitude = bottomRightLatitude;
    }

    public Double getBottomRightLongitude() {
        return BottomRightLongitude;
    }

    public void setBottomRightLongitude(Double bottomRightLongitude) {
        BottomRightLongitude = bottomRightLongitude;
    }

    public Double getBottomLeftLatitude() {
        return BottomLeftLatitude;
    }

    public void setBottomLeftLatitude(Double bottomLeftLatitude) {
        BottomLeftLatitude = bottomLeftLatitude;
    }

    public Double getBottomLeftLongitude() {
        return BottomLeftLongitude;
    }

    public void setBottomLeftLongitude(Double bottomLeftLongitude) {
        BottomLeftLongitude = bottomLeftLongitude;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public List<Double> getExtent(){
        List<Double> extent  = new ArrayList<>();
        extent.add(0, this.BottomLeftLongitude);
        extent.add(1, this.BottomLeftLatitude);
        extent.add(2, this.TopRightLongitude);
        extent.add(3, this.TopRightLatitude);
        return extent;
    }
}
