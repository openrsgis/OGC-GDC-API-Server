package whu.edu.cn.entity.Ingest;

/**
 * @author czp
 * @version 1.0
 * @date 2020/5/16 17:13
 */
public class GcTileExtent {
    private Double leftBottomLong;
    private Double leftBottomLat;
    private Double rightUpperLong;
    private Double rightUpperLat;
    private Integer row;
    private Integer column;

    public GcTileExtent(){}
    public GcTileExtent(Double leftBottomLong,Double leftBottomLat,Double rightUpperLong,Double rightUpperLat,Integer row,Integer column){
        this.leftBottomLong=leftBottomLong;
        this.leftBottomLat=leftBottomLat;
        this.rightUpperLong=rightUpperLong;
        this.rightUpperLat=rightUpperLat;
        this.row=row;
        this.column=column;
    }
    public Double getLeftBottomLong() {
        return leftBottomLong;
    }

    public void setLeftBottomLong(Double leftBottomLong) {
        this.leftBottomLong = leftBottomLong;
    }

    public Double getLeftBottomLat() {
        return leftBottomLat;
    }

    public void setLeftBottomLat(Double leftBottomLat) {
        this.leftBottomLat = leftBottomLat;
    }

    public Double getRightUpperLong() {
        return rightUpperLong;
    }

    public void setRightUpperLong(Double rightUpperLong) {
        this.rightUpperLong = rightUpperLong;
    }

    public Double getRightUpperLat() {
        return rightUpperLat;
    }

    public void setRightUpperLat(Double rightUpperLat) {
        this.rightUpperLat = rightUpperLat;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }
}
