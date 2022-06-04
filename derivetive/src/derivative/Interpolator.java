package derivative;

import java.util.ArrayList;

public class Interpolator {

    public double interpolate(ArrayList<Point> points, double x) {
        double res = 0;
        for (Point p : points) {
            double numerator = 1;
            double denumerator = 1;
            for (Point point : points) {
                if (p != point) {
                    numerator *= x - point.getX();
                    denumerator *= p.getX() - point.getX();
                }
            }
            res += p.getY() * numerator / denumerator;
        }
        return res;
    }
}
