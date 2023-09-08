package whu.edu.cn.entity.coverage;

public class Subset {

    private String axisName;
    private boolean interval;
    /**
     * number or text
     */
    private Object lowPoint;
    private Object highPoint;
    private Object point;
    private boolean isNumber;

    public boolean isNumber() {
        return isNumber;
    }

    public void setNumber(boolean number) {
        isNumber = number;
    }

    public String getAxisName() {
        return axisName;
    }

    public void setAxisName(String axisName) {
        this.axisName = axisName;
    }

    public boolean isInterval() {
        return interval;
    }

    public void setInterval(boolean interval) {
        this.interval = interval;
    }

    public Object getLowPoint() {
        return lowPoint;
    }

    public void setLowPoint(Object lowPoint) {
        this.lowPoint = lowPoint;
    }

    public Object getHighPoint() {
        return highPoint;
    }

    public void setHighPoint(Object highPoint) {
        this.highPoint = highPoint;
    }

    public Object getPoint() {
        return point;
    }

    public void setPoint(Object point) {
        this.point = point;
    }
}
