package frameProcessing;

/**
 * Created by Mogi on 17.11.2015.
 */
public class PositionData {



    private int[] upLeftCorner = new int[2];

    private int[] upRightCorner = new int[2];

    private int[] downLeftCorner = new int[2];

    private int[] downRightCorner = new int[2];

    public PositionData (int xl, int xr, int yu, int yd) {
        this.upLeftCorner[0] = xl;
        this.upLeftCorner[1] = yu;
        this.upRightCorner[0] = xr;
        this.upRightCorner[1] = yu;
        this.downLeftCorner[0] = xl;
        this.downLeftCorner[1] = yd;
        this.downRightCorner[0] = xr;
        this.downRightCorner[1] = yd;
    }

    public int[] getUpLeftCorner() {
        return upLeftCorner;
    }

    public void setUpLeftCorner(int[] upLeftCorner) {
        this.upLeftCorner = upLeftCorner;
    }

    public int[] getUpRightCorner() {
        return upRightCorner;
    }

    public void setUpRightCorner(int[] upRightCorner) {
        this.upRightCorner = upRightCorner;
    }

    public int[] getDownLeftCorner() {
        return downLeftCorner;
    }

    public void setDownLeftCorner(int[] downLeftCorner) {
        this.downLeftCorner = downLeftCorner;
    }

    public int[] getDownRightCorner() {
        return downRightCorner;
    }

    public void setDownRightCorner(int[] downRightCorner) {
        this.downRightCorner = downRightCorner;
    }
}
