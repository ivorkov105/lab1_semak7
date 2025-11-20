package CustomTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Point2D implements Comparable<Point2D> {

    private final double x;
    private final double y;

    private Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Point2D of(double x, double y) {
        return new Point2D(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double[] getCoords() {
        return new double[]{x, y};
    }

    public double getVector() {
        return Math.sqrt(x * x + y * y);
    }

    public static Point2D parse(String point) {
        if (point == null || point.trim().isEmpty()) {
            throw new IllegalArgumentException("Входная строка не может быть пустой.");
        }
        String[] parts = point.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Строка должна быть в формате 'x,y'. Получено: " + point);
        }
        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            return Point2D.of(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Координаты должны быть числами. Получено: " + point, e);
        }
    }

    @Override
    public int compareTo(Point2D other) {
        return Double.compare(this.getVector(), other.getVector());
    }

    @Override
    public String toString() {
        return "Point2D(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2D point2D = (Point2D) o;
        return Double.compare(point2D.x, x) == 0 && Double.compare(point2D.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}