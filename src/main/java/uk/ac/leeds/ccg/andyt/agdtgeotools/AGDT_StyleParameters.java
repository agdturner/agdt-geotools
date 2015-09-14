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
package uk.ac.leeds.ccg.andyt.agdtgeotools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.geotools.styling.Style;

/**
 *
 * @author geoagdt
 */
public class AGDT_StyleParameters {

    private HashMap<String, List<Style>> styles;
    private String classificationFunctionName;
    private int nClasses;
    private String paletteName;
    private boolean addWhiteForZero;
    private Style backgroundStyle;
    private String backgroundStyleTitle;
    private boolean drawBoundaries;
    private boolean doForeground;
    private ArrayList<Style> foregroundStyle0;
    private String foregroundStyleTitle0;
    private Style foregroundStyle1;
    private String foregroundStyleTitle1;
    private ArrayList<ArrayList<AGDT_LegendItem>> legendItems;

    public void setMaxForTheLastLegendItem(
            double max,
            int styleIndex) {
        int maxInt = (int) max;
        ArrayList<AGDT_LegendItem> legendItems;
        legendItems = getLegendItems(styleIndex);
        AGDT_LegendItem legendItem;
        legendItem = legendItems.get(legendItems.size() - 1);
        String currentLabel = legendItem.getLabel();
        String[] splitCurrentLabel = currentLabel.split("-");
        String newLabel;
        if (maxInt == max) {
            newLabel = splitCurrentLabel[0] + "-" + maxInt;
        } else {
            newLabel = splitCurrentLabel[0] + "-" + max;
        }
        legendItem.setLabel(newLabel);
    }

    /**
     * @param key
     * @param index
     * @return the style at styleIndex
     */
    public Style getStyle(
            String key,
            int index) {
        Style result;
        List<Style> styles0 = getStyles(key);
        if (styles0 == null) {
            styles0 = new ArrayList<Style>();
        }
        try {
            result = styles0.get(index);
        } catch (IndexOutOfBoundsException e) {
            int i = styles0.size();
            while (i <= index) {
                //styles0.set(index, null); // Fails
                styles0.add(i, null);
                i++;
            }
            return null;
        }
        return result;
    }

    public void setStylesNull() {
        if (styles != null) {
            Iterator<String> ite;
            ite = styles.keySet().iterator();
            while (ite.hasNext()) {
                String key = ite.next();
                List<Style> tStyles;
                tStyles = styles.get(key);
                if (!tStyles.isEmpty()) {
                    int size = tStyles.size();
//                for (int i = 0; i < size; i ++) {
//                    setStyle(null,i);
//                }
                    tStyles.clear();
                    for (int i = 0; i < size; i++) {
                        tStyles.add(null);
                    }
//                Iterator<Style> ite = styles.iterator();
//                while (ite.hasNext()) {
//                    Style style = ite.next();
//                    style = null;
//                }
                }
            }
        }
    }

    /**
     * @param key
     * @param style
     * @param index
     * @return the style
     */
    public Style setStyle(String key, Style style, int index) {
        Style result;
        result = getStyle(key, index); // This ensures that styles is initialised to the right length.
        getStyles(key).set(index, style);
        return result;
    }

//    public HashMap<String, List<Style>> getStyles() {
//        if (styles == null) {
//            styles = new HashMap<String, List<Style>>();
//        }
//        return styles;
//    }
    public List<Style> getStyles(String key) {
        List<Style> result;
        if (styles == null) {
            styles = new HashMap<String, List<Style>>();
            result = new ArrayList<Style>();
            styles.put(key, result);
        } else {
            result = styles.get(key);
            if (result == null) {
                result = new ArrayList<Style>();
                styles.put(key, result);
            }
        }
        return result;
    }

    /**
     * @return the classificationFunctionName
     */
    public String getClassificationFunctionName() {
        return classificationFunctionName;
    }

    /**
     * @param classificationFunctionName the classificationFunctionName to set
     */
    public void setClassificationFunctionName(String classificationFunctionName) {
        this.classificationFunctionName = classificationFunctionName;
    }

    /**
     * @return the nClasses
     */
    public int getnClasses() {
        return nClasses;
    }

    /**
     * @param nClasses the nClasses to set
     */
    public void setnClasses(int nClasses) {
        this.nClasses = nClasses;
    }

    /**
     * @return the paletteName
     */
    public String getPaletteName() {
        return paletteName;
    }

    /**
     * @param paletteName the paletteName to set
     */
    public void setPaletteName(String paletteName) {
        this.paletteName = paletteName;
    }

    /**
     * @return the addWhiteForZero
     */
    public boolean isAddWhiteForZero() {
        return addWhiteForZero;
    }

    /**
     * @param addWhiteForZero the addWhiteForZero to set
     */
    public void setAddWhiteForZero(boolean addWhiteForZero) {
        this.addWhiteForZero = addWhiteForZero;
    }

    /**
     * @return the backgroundStyle
     */
    public Style getBackgroundStyle() {
        return backgroundStyle;
    }

    /**
     * @param backgroundStyle the backgroundStyle to set
     */
    public void setBackgroundStyle(Style backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

    /**
     * @return the backgroundStyleTitle
     */
    public String getBackgroundStyleTitle() {
        return backgroundStyleTitle;
    }

    /**
     * @param backgroundStyleTitle the backgroundStyleTitle to set
     */
    public void setBackgroundStyleTitle(String backgroundStyleTitle) {
        this.backgroundStyleTitle = backgroundStyleTitle;
    }

    public boolean isDoForeground() {
        return doForeground;
    }

    public void setDoForeground(boolean doForeground) {
        this.doForeground = doForeground;
    }

//    /**
//     * @return the foregroundStyle0
//     */
//    public Style getForegroundStyle0() {
//        return foregroundStyle0;
//    }
    /**
     * @return the foregroundStyle0
     */
    public ArrayList<Style> getForegroundStyle0() {
        return foregroundStyle0;
    }

//    /**
//     * @param foregroundStyle0 the foregroundStyle0 to set
//     */
//    public void setForegroundStyle0(Style foregroundStyle0) {
//        this.foregroundStyle0 = foregroundStyle0;
//    }
    /**
     * @param foregroundStyle0 the foregroundStyle0 to set
     */
    public void setForegroundStyle0(ArrayList<Style> foregroundStyle0) {
        this.foregroundStyle0 = foregroundStyle0;
    }

    /**
     * @return the foregroundStyleTitle0
     */
    public String getForegroundStyleTitle0() {
        return foregroundStyleTitle0;
    }

    /**
     * @param foregroundStyleTitle0 the foregroundStyleTitle0 to set
     */
    public void setForegroundStyleTitle0(String foregroundStyleTitle0) {
        this.foregroundStyleTitle0 = foregroundStyleTitle0;
    }

    /**
     * @return the foregroundStyle1
     */
    public Style getForegroundStyle1() {
        return foregroundStyle1;
    }

    /**
     * @param foregroundStyle1 the foregroundStyle1 to set
     */
    public void setForegroundStyle1(Style foregroundStyle1) {
        this.foregroundStyle1 = foregroundStyle1;
    }

    /**
     * @return the foregroundStyleTitle0
     */
    public String getForegroundStyleTitle1() {
        return foregroundStyleTitle1;
    }

    /**
     * @param foregroundStyleTitle1 the foregroundStyleTitle1 to set
     */
    public void setForegroundStyleTitle1(String foregroundStyleTitle1) {
        this.foregroundStyleTitle1 = foregroundStyleTitle1;
    }

    /**
     * @param index
     * @return a specific list of legendItems
     */
    public ArrayList<AGDT_LegendItem> getLegendItems(int index) {
        ArrayList<AGDT_LegendItem> result;
        ArrayList<ArrayList<AGDT_LegendItem>> legendItems0;
        legendItems0 = getLegendItems();
        try {
            result = legendItems0.get(index);
        } catch (IndexOutOfBoundsException e) {
            result = null;
            int i = legendItems0.size();
            while (i <= index) {
                ArrayList<AGDT_LegendItem> newLegendItem;
                newLegendItem = new ArrayList<AGDT_LegendItem>();
                legendItems0.add(i, result);
                i++;
                result = newLegendItem;
            }
        }
        return result;
    }

    /**
     * @param legendItems the legendItems to set
     * @param index
     * @return
     */
    public ArrayList<AGDT_LegendItem> setLegendItems(
            ArrayList<AGDT_LegendItem> legendItems, int index) {
        ArrayList<AGDT_LegendItem> result;
        result = getLegendItems(index); // This ensures that legendItems is initialised to the right length.
        getLegendItems().set(index, legendItems);
        return result;
    }

    /**
     * @return the legendItems
     */
    public ArrayList<ArrayList<AGDT_LegendItem>> getLegendItems() {
        if (legendItems == null) {
            legendItems = new ArrayList<ArrayList<AGDT_LegendItem>>();
        }
        return legendItems;
    }

    /**
     * @param legendItems the legendItems to set
     */
    public void setLegendItems(ArrayList<ArrayList<AGDT_LegendItem>> legendItems) {
        this.legendItems = legendItems;
    }

    public AGDT_StyleParameters() {
//        this.classificationFunctionName = "";
//        this.nClasses = 0;
//        this.paletteName = "";
//        this.addWhiteForZero = false;
//        this.backgroundStyle = null;
//        this.backgroundStyleTitle = "";
    }

    public AGDT_StyleParameters(
            String classificationFunctionName,
            int nClasses,
            String paletteName,
            boolean addWhiteForZero,
            Style backgroundStyle,
            String backgroundStyleTitle,
            boolean drawBoundaries) {
        this.classificationFunctionName = classificationFunctionName;
        this.nClasses = nClasses;
        this.paletteName = paletteName;
        this.addWhiteForZero = addWhiteForZero;
        this.backgroundStyle = backgroundStyle;
        this.backgroundStyleTitle = backgroundStyleTitle;
        this.drawBoundaries = drawBoundaries;
    }

    /**
     * Initialise styleParameters. No deep copying.
     *
     * @param styleParameters
     */
    public AGDT_StyleParameters(AGDT_StyleParameters styleParameters) {
        this.addWhiteForZero = styleParameters.addWhiteForZero;
        this.backgroundStyle = styleParameters.backgroundStyle;
        this.backgroundStyleTitle = styleParameters.backgroundStyleTitle;
        this.classificationFunctionName = styleParameters.classificationFunctionName;
        this.foregroundStyle0 = styleParameters.foregroundStyle0;
        this.foregroundStyleTitle0 = styleParameters.foregroundStyleTitle0;
        this.legendItems = styleParameters.legendItems;
        this.nClasses = styleParameters.nClasses;
        this.paletteName = styleParameters.paletteName;
        //this.style = styleParameters.style;
        this.styles = styleParameters.styles;
        this.drawBoundaries = styleParameters.drawBoundaries;
    }

    /**
     * @return the drawBoundaries
     */
    public boolean isDrawBoundaries() {
        return drawBoundaries;
    }

    /**
     * @param drawBoundaries the drawBoundaries to set
     */
    public void setDrawBoundaries(boolean drawBoundaries) {
        this.drawBoundaries = drawBoundaries;
    }

}
