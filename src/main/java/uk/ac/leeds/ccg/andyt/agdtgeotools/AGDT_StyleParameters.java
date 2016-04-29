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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.geotools.styling.Style;

/**
 *
 * @author geoagdt
 */
public class AGDT_StyleParameters extends AGDT_StyleParametersAbstract {

    private HashMap<String, List<Style>> styles;
    private String classificationFunctionName;
    private int nClasses;
    private int nClasses2;
    private String paletteName;
    private String paletteName2;
    private boolean addWhiteForZero;
    private Style backgroundStyle;
    private String backgroundStyleTitle;
    private boolean drawBoundaries;
    private boolean doForeground;
    private ArrayList<Style> foregroundStyles;
    private ArrayList<String> foregroundStyleNames;
    private Style foregroundStyle1;
    private String foregroundStyleTitle1;
    private ArrayList<ArrayList<AGDT_LegendItem>> legendItems;

    public void setMaxForTheLastLegendItem(
            double max,
            int styleIndex) {
        int maxInt = (int) max;
        BigDecimal maxBD;
        maxBD = BigDecimal.valueOf(max);
        ArrayList<AGDT_LegendItem> legendItems;
        legendItems = getLegendItems(styleIndex);
        if (legendItems != null) {
            AGDT_LegendItem legendItem;
            legendItem = legendItems.get(legendItems.size() - 1);
            String currentLabel = legendItem.getLabel();
            String[] splitCurrentLabel = currentLabel.split("-");
            String newLabel;
            BigDecimal currentMaxBD;
            currentMaxBD = new BigDecimal(splitCurrentLabel[0]);
            double currentMax;
            currentMax = currentMaxBD.doubleValue();
            int currentMaxInt;
            currentMaxInt = currentMaxBD.intValue();
            if (maxBD.compareTo(currentMaxBD) == 1) {
                if (maxInt == max) {
                    newLabel = splitCurrentLabel[0] + "-" + maxInt;
                } else {
                    newLabel = splitCurrentLabel[0] + "-" + max;
                }
            } else {
                if (currentMaxInt == currentMax) {
                    newLabel = splitCurrentLabel[0] + "-" + currentMaxInt;
                } else {
                    newLabel = splitCurrentLabel[0] + "-" + currentMax;
                }
            }
            // If the range is a single value simply use that.
            String[] splitNewLabel = newLabel.split("-");
            if (splitNewLabel[0].equalsIgnoreCase(splitNewLabel[1])) {
                newLabel = splitNewLabel[0];
            }
            legendItem.setLabel(newLabel);
        }
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
     * @param n the nClasses to set
     */
    public void setnClasses(int n) {
        this.nClasses = n;
    }

    /**
     * @return the nClasses2
     */
    public int getnClasses2() {
        return nClasses2;
    }

    /**
     * @param n the nClasses to set
     */
    public void setnClasses2(int n) {
        this.nClasses2 = n;
    }

    /**
     * @return the paletteName
     */
    public String getPaletteName() {
        return paletteName;
    }

    /**
     * @param s the paletteName to set
     */
    public void setPaletteName(String s) {
        this.paletteName = s;
    }

    /**
     * @return the paletteName
     */
    public String getPaletteName2() {
        return paletteName2;
    }

    /**
     * @param s the paletteName to set
     */
    public void setPaletteName2(String s) {
        this.paletteName2 = s;
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
//     * @return the foregroundStyles
//     */
//    public Style getForegroundStyle0() {
//        return foregroundStyles;
//    }
    /**
     * @return the foregroundStyles
     */
    public ArrayList<Style> getForegroundStyle0() {
        return foregroundStyles;
    }

//    /**
//     * @param foregroundStyles the foregroundStyles to set
//     */
//    public void setForegroundStyles(Style foregroundStyles) {
//        this.foregroundStyles = foregroundStyles;
//    }
    /**
     * @param foregroundStyle0 the foregroundStyles to set
     */
    public void setForegroundStyles(ArrayList<Style> foregroundStyle0) {
        this.foregroundStyles = foregroundStyle0;
    }

    public ArrayList<String> getForegroundStyleNames() {
        if (foregroundStyleNames == null) {
            foregroundStyleNames = new ArrayList<String>();
        }
        return foregroundStyleNames;
    }

    public void setForegroundStyleNames(ArrayList<String> foregroundStyleNames) {
        this.foregroundStyleNames = foregroundStyleNames;
    }

    /**
     * @return the foregroundStyleTitle0
     */
    public String getForegroundStyleName(int i) {
        return getForegroundStyleNames().get(i);
    }

    /**
     * @param foregroundStyleTitle0 the foregroundStyleTitle0 to set
     */
    public void setForegroundStyleName(int i, String foregroundStyleName) {
        ArrayList<String> fgsn = getForegroundStyleNames();
        while (fgsn.size() <= i) {
            fgsn.add(null);
        }
        fgsn.set(i, foregroundStyleName);
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
        this.foregroundStyles = styleParameters.foregroundStyles;
        this.foregroundStyleNames = styleParameters.foregroundStyleNames;
        this.legendItems = styleParameters.legendItems;
        this.nClasses = styleParameters.nClasses;
        this.nClasses2 = styleParameters.nClasses2;
        this.paletteName = styleParameters.paletteName;
        this.paletteName2 = styleParameters.paletteName2;
        //this.style = styleParameters.style;
        this.styles = styleParameters.styles;
        this.drawBoundaries = styleParameters.drawBoundaries;
    }

    

}
