package derivative;

public class Point implements Comparable<Point> {

    private double x;
    private double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int compareTo(Point o) {
        return Double.compare(this.x, o.x);
    }

    @Override
    public String toString() {
        return "[" + x + "; " + y + "]";
    }

}
