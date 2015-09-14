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
}