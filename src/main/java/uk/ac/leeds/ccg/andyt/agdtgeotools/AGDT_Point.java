/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.agdtgeotools;

import java.io.Serializable;

/**
 *
 * @author geoagdt
 */
public class AGDT_Point implements Serializable {

    private int x;
    private int y;

    public AGDT_Point(int x, int y) {
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

    public double getDistance(AGDT_Point p) {
        double result;
        double xdiff = (double) (getX() - p.getX());
        double ydiff = (double) (getY() - p.getY());
        result = Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof AGDT_Point) {
            AGDT_Point o;
            o = (AGDT_Point) obj;
            if (this.hashCode() == o.hashCode()) {
                return this.x == o.x && this.y == o.y;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }
}
