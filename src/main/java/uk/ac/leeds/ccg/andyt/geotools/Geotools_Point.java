package uk.ac.leeds.ccg.andyt.geotools;

import java.io.Serializable;

/**
 * For storing a 2D point with int value of x and y.
 * @author geoagdt
 */
public class Geotools_Point implements Serializable {

    private int x;
    private int y;

    public Geotools_Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "x " + x + ", y " + y;
    }

    public double getDistance(Geotools_Point p) {
        double result;
        double xdiff = (double) (getX() - p.getX());
        double ydiff = (double) (getY() - p.getY());
        result = Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));
        return result;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Geotools_Point) {
            Geotools_Point o;
            o = (Geotools_Point) obj;
            if (this.hashCode() == o.hashCode()) {
                return this.x == o.x && this.y == o.y;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.y;
        return hash;
    }
    
}
