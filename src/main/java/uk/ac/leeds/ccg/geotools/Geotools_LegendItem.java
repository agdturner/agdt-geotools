/*
 * Copyright (C) 2014 geoagdt.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.geotools;

import java.awt.Color;

/**
 *
 * @author geoagdt
 */
public class Geotools_LegendItem {

    private String label;
    private Color color;

    public Geotools_LegendItem() {
    }

    public Geotools_LegendItem(String l, Color c) {
        label = l;
        color = c;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param aLabel the label to set
     */
    public void setLabel(String aLabel) {
        label = aLabel;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param aColor the color to set
     */
    public void setColor(Color aColor) {
        color = aColor;
    }

}
