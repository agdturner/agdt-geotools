/*
 * Copyright 2020 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.geotools;

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
public class Geotools_StyleParameters {

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
    private ArrayList<ArrayList<Geotools_LegendItem>> legendItems;

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
    
    public void setMaxForTheLastLegendItem(            double max,            int styleIndex) {
        int maxInt = (int) max;
        BigDecimal maxBD = BigDecimal.valueOf(max);
        ArrayList<Geotools_LegendItem> legendItems = getLegendItems(styleIndex);
        if (legendItems != null) {
            Geotools_LegendItem legendItem;
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
    public Style getStyle(            String key,            int index) {
        Style r;
        List<Style> styles = getStyles(key);
        if (styles == null) {
            styles = new ArrayList<>();
        }
        try {
            r = styles.get(index);
        } catch (IndexOutOfBoundsException e) {
            int i = styles.size();
            while (i <= index) {
                //styles0.set(index, null); // Fails
                styles.add(i, null);
                i++;
            }
            return null;
        }
        return r;
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
    /**
     * Returns the style from {@link styles} associated with {@code key}. If such style aleady exists and 
     * @param key
     * @return 
     */
    public List<Style> getStyles(String key) {
        if (styles == null) {
            styles = new HashMap<>();
            return addStyle(key);
        } else {
            if (styles.containsKey(key)) {
                return styles.get(key);
            } else {
                return addStyle(key);
            }
        }
    }
    
    private List<Style> addStyle(String key) {
        List<Style> style = new ArrayList<>();
        styles.put(key, style);
        return style;
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
            foregroundStyleNames = new ArrayList<>();
        }
        return foregroundStyleNames;
    }

    public void setForegroundStyleNames(ArrayList<String> foregroundStyleNames) {
        this.foregroundStyleNames = foregroundStyleNames;
    }

    /**
     * @param i
     * @return the foregroundStyleTitle0
     */
    public String getForegroundStyleName(int i) {
        return getForegroundStyleNames().get(i);
    }

    /**
     * @param i
     * @param foregroundStyleName
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
    public ArrayList<Geotools_LegendItem> getLegendItems(int index) {
        ArrayList<Geotools_LegendItem> r;
        ArrayList<ArrayList<Geotools_LegendItem>> legendItems0;
        legendItems0 = getLegendItems();
        try {
            r = legendItems0.get(index);
        } catch (IndexOutOfBoundsException e) {
            r = null;
            int i = legendItems0.size();
            while (i <= index) {
                ArrayList<Geotools_LegendItem> li = new ArrayList<>();
                legendItems0.add(i, r);
                i++;
                r = li;
            }
        }
        return r;
    }

    /**
     * @param legendItems the legendItems to set
     * @param index
     * @return
     */
    public ArrayList<Geotools_LegendItem> setLegendItems(
            ArrayList<Geotools_LegendItem> legendItems, int index) {
        ArrayList<Geotools_LegendItem> result;
        result = getLegendItems(index); // This ensures that legendItems is initialised to the right length.
        getLegendItems().set(index, legendItems);
        return result;
    }

    /**
     * @return the legendItems
     */
    public ArrayList<ArrayList<Geotools_LegendItem>> getLegendItems() {
        if (legendItems == null) {
            legendItems = new ArrayList<>();
        }
        return legendItems;
    }

    /**
     * @param legendItems the legendItems to set
     */
    public void setLegendItems(ArrayList<ArrayList<Geotools_LegendItem>> legendItems) {
        this.legendItems = legendItems;
    }

    public Geotools_StyleParameters() {
//        this.classificationFunctionName = "";
//        this.nClasses = 0;
//        this.paletteName = "";
//        this.addWhiteForZero = false;
//        this.backgroundStyle = null;
//        this.backgroundStyleTitle = "";
    }

    public Geotools_StyleParameters(
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
    public Geotools_StyleParameters(Geotools_StyleParameters styleParameters) {
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
