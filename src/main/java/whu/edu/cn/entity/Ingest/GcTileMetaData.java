package whu.edu.cn.entity.Ingest;

/**
 * @author czp
 * @version 1.0
 * @date 2020/5/27 16:57
 */
public class GcTileMetaData {
    private String tileDataId;
    private String gridCode;
    private GcTileExtent gcTileExtent;
    private String CRS;
    private String measurementName;
    private Integer column;
    private Integer row;
    private String trueDataWKT;
    private String cellType;

    public GcTileMetaData(){}
    public GcTileMetaData( String tileDataId, String gridCode, GcTileExtent gcTileExtent,
                           String CRS, String measurementName, Integer column, Integer row,
                           String trueDataWKT, String cellType){
        this.tileDataId=tileDataId;this.gridCode=gridCode;
        this.gcTileExtent=gcTileExtent;this.CRS=CRS;
        this.measurementName=measurementName;this.column=column;
        this.row=row;this.trueDataWKT=trueDataWKT;this.cellType=cellType;
    }
    public GcTileExtent getGcTileExtent() {
        return gcTileExtent;
    }

    public void setGcTileExtent(GcTileExtent gcTileExtent) {
        this.gcTileExtent = gcTileExtent;
    }

    public String getTileDataId() {
        return tileDataId;
    }

    public void setTileDataId(String tileDataId) {
        this.tileDataId = tileDataId;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getCRS() {
        return CRS;
    }

    public void setCRS(String CRS) {
        this.CRS = CRS;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }


    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public String getTrueDataWKT() {
        return trueDataWKT;
    }

    public void setTrueDataWKT(String trueDataWKT) {
        this.trueDataWKT = trueDataWKT;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }
}
