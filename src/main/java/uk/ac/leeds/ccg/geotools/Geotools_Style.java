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

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.StyleGenerator;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.function.RangedClassifier;
import org.geotools.styling.ColorMap;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
//import org.geotools.styling.SLDParser;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.dialog.JExceptionReporter;
import org.geotools.xml.styling.SLDParser;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import uk.ac.leeds.ccg.math.Math_BigDecimal;
import uk.ac.leeds.ccg.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.geotools.core.Geotools_Object;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsDouble;

/**
 *
 * @author geoagdt
 */
public class Geotools_Style extends Geotools_Object {

    public final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    public final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
    public StyleBuilder styleBuilder = new StyleBuilder(styleFactory, filterFactory);

    protected Geotools_Style() {
    }

    public Geotools_Style(Geotools_Environment ge) {
        super(ge);
    }

    /**
     *
     * @param styleParameters
     * @param style
     */
    public void setStyleParametersStyle(Object[] styleParameters, Style style) {
        styleParameters[0] = style;
    }

    /**
     * Figure out if a valid SLD file is available.
     *
     * @param file
     * @return
     */
    public File toSLDFile(File file) {
        String path = file.getAbsolutePath();
        String base = path.substring(0, path.length() - 4);
        String newPath = base + ".sld";
        File sld = new File(newPath);
        if (sld.exists()) {
            return sld;
        }
        newPath = base + ".SLD";
        sld = new File(newPath);
        if (sld.exists()) {
            return sld;
        }
        return null;
    }

    /**
     * Create a DW_Style object from a definition in a SLD document
     *
     * @param sld
     * @return
     */
    public org.geotools.styling.Style createFromSLD(File sld) {
        try {
            SLDParser sldp = new SLDParser(styleFactory, sld.toURI().toURL());
            org.geotools.styling.Style[] style = sldp.readXML();
            return style[0];
        } catch (IOException e) {
            JExceptionReporter.showDialog(e, "Problem creating style");
        }
        return null;
    }

    /**
     * @param fs
     * @return
     */
    public Style createStyle(FeatureSource fs) {
        SimpleFeatureType schema = (SimpleFeatureType) fs.getSchema();
        Class geomType = schema.getGeometryDescriptor().getType().getBinding();

        if (Polygon.class.isAssignableFrom(geomType)
                || MultiPolygon.class.isAssignableFrom(geomType)) {
            return createDefaultPolygonStyle(Color.BLUE, Color.CYAN);
        } else if (LineString.class.isAssignableFrom(geomType)
                || MultiLineString.class.isAssignableFrom(geomType)) {
            return createDefaultLineStyle();
        } else {
            return createDefaultPointStyle();
        }
    }

    public Style getPointStyle(int size, String type, Color fill,
            Color outline) {
        Style result;
        Mark mark;
        if (type.equalsIgnoreCase("Cross")) {
            mark = styleFactory.getCrossMark();
        } else {
            if (type.equalsIgnoreCase("Triangle")) {
                mark = styleFactory.getTriangleMark();
            } else {
                if (type.equalsIgnoreCase("Square")) {
                    mark = styleFactory.getSquareMark();
                } else {
                    if (type.equalsIgnoreCase("X")) {
                        mark = styleFactory.getXMark();
                    } else {
                        if (type.equalsIgnoreCase("Cross")) {
                            mark = styleFactory.getCrossMark();
                        } else {
                            mark = styleFactory.getCircleMark();
                        }
                    }
                }
            }
        }
        mark.setStroke(styleFactory.createStroke(
                filterFactory.literal(outline), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(fill)));
        result = createPointStyle(mark, size);
        return result;
    }

    /**
     * Create and returns a Style to draw point features as circles with blue
     * outlines and cyan fill.
     *
     * @param mark
     * @param size
     * @return A Style to draw point features as circles with blue outlines and
     * cyan fill.
     */
    public Style createPointStyle(
            Mark mark,
            int size) {
        Graphic gr = styleFactory.createDefaultGraphic();
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(size));
        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geometry of features.
         */
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * Create and returns a Style to draw point features as circles with blue
     * outlines and cyan fill.
     *
     * @return A Style to draw point features as circles with blue outlines and
     * cyan fill.
     */
    public Style createDefaultPointStyle() {
        Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(
                filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
        mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(5));
        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geometry of features.
         */
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * Creates and returns a Style to draw line features as thin c coloured
     * lines.
     *
     * @param c
     * @return A Style to draw line features as thin c coloured lines.
     */
    public Style createDefaultLineStyle(Color c) {
        Style style;
        Stroke stroke = getDefaultStroke(c);
        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geometry of features.
         */
        LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule;
        rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts;
        fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * Creates and returns a Style to draw line features as thin blue lines.
     *
     * @return A Style to draw line features as thin blue lines.
     */
    public Style createDefaultLineStyle() {
        return createDefaultLineStyle(Color.BLUE);
    }

    public Stroke getDefaultStroke(Color c) {
        Stroke result;
        double opacity;
        // Stroke
        opacity = 1; // 0.5; // 0.5 is partially opaque
        Literal strokeOpacityLiteral;
        strokeOpacityLiteral = filterFactory.literal(opacity);
        //opacityLiteral = null;
        // create a partially opaque outline stroke
        result = styleFactory.createStroke(
                filterFactory.literal(c),
                filterFactory.literal(1),
                strokeOpacityLiteral);
        return result;
    }

    /**
     * Create a Style to draw polygon features with a outline in outline_Color
     * and a fill in fill_Color.
     *
     * @param outline_Color
     * @param fill_Color
     * @return
     */
    public Style createDefaultPolygonStyle(
            Color outline_Color,
            Color fill_Color) {

        double opacity;
        // Stroke
        Stroke stroke = getDefaultStroke(outline_Color);

        // Fill
        opacity = 0; // 0 Is totaly clear, 0.5 is partially opaque, 1.0 is solid
        Literal fillOpacityLiteral;
        fillOpacityLiteral = filterFactory.literal(opacity);
        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(fill_Color),
                fillOpacityLiteral);

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geometry of features
         */
        PolygonSymbolizer ps;
        ps = styleFactory.createPolygonSymbolizer(
                stroke,
                fill,
                null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(ps);
        FeatureTypeStyle fts;
        fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * Create a Style to draw polygon features with a thin blue outline and a
     * cyan fill
     *
     * @param featureCollection
     * @param attributeName
     * @param styleParameters
     * @return
     */
    public Object[] createPolygonStyle(
            FeatureCollection featureCollection,
            String attributeName,
            Geotools_StyleParameters styleParameters) {
        return createPolygonStyle(
                featureCollection,
                attributeName,
                styleParameters,
                true);
    }

    /**
     * Create a Style to draw polygon features with a thin blue outline and a
     * cyan fill
     *
     * @param featureCollection
     * @param attributeName
     * @param styleParameters
     * @param doDebug
     * @return
     */
    public Object[] createPolygonStyle(
            FeatureCollection featureCollection,
            String attributeName,
            Geotools_StyleParameters styleParameters,
            boolean doDebug) {
        Object[] result = new Object[2];

        // Unpack styleParamters
        int nClasses = styleParameters.getnClasses();
        boolean addWhiteForZero = styleParameters.isAddWhiteForZero();
        String paletteName = styleParameters.getPaletteName();
        String classificationFunctionName = styleParameters.getClassificationFunctionName();

        // STEP 0 Set up Color Brewer
        ColorBrewer brewer = ColorBrewer.instance();

        // STEP 1 - call a classifier function to summarise your content
        FilterFactory2 ff;
        ff = CommonFactoryFinder.getFilterFactory2();
        PropertyName propertyName;
        propertyName = ff.property(attributeName);
        Function classify;
        classify = ff.function(
                classificationFunctionName,
                propertyName,
                ff.literal(nClasses));
//        Classifier groups;
//        groups = (Classifier) classify.evaluate(featureCollection);
        RangedClassifier groups = null;
        Color[] colorsForRenderingFeatures = null;
        if (!featureCollection.isEmpty()) {
            Object classifier = classify.evaluate(featureCollection);
            if (classifier != null) {
                groups = (RangedClassifier) classify.evaluate(featureCollection);
                //System.out.println(groups.toString());

                if (doDebug) {
                    int debug = 1;
                }

                // STEP 2 - look up a predefined palette from color brewer
                BrewerPalette palette;
                palette = brewer.getPalette(paletteName);

                colorsForRenderingFeatures = palette.getColors(nClasses);
                Color[] colorsForLegend = new Color[colorsForRenderingFeatures.length + 1];
                if (addWhiteForZero) {
                    System.arraycopy(colorsForRenderingFeatures, 0, colorsForLegend, 1, colorsForRenderingFeatures.length);
                    colorsForLegend[0] = Color.WHITE;
                } else {
                    colorsForLegend = colorsForRenderingFeatures;
                }
                // STEP 2b Sort Legend Items
                ArrayList<Geotools_LegendItem> legendItems = new ArrayList<>();
                if (addWhiteForZero) {
                    String newLabel = "0";
                    Geotools_LegendItem li;
                    li = new Geotools_LegendItem(
                            newLabel,
                            colorsForLegend[0]);
                    legendItems.add(li);
                }
                String[] titles;
                titles = groups.getTitles();
                for (int i = 0; i < titles.length; i++) {
                    //String title = titles[i];
                    Object min = groups.getMin(i);
                    Object max = groups.getMax(i);
                    //String legendItemName;

                    // Modify Label so it is only integers in legend
                    String newLabel;
                    String minString = min.toString();
                    String maxString = max.toString();
                    String[] minParts = minString.split("\\.");
                    if (minParts.length == 2) {
                        newLabel = minParts[0];
                    } else {
                        newLabel = minString;
                    }
                    newLabel += "-";
                    String[] maxParts = maxString.split("\\.");
                    if (maxParts.length == 2) {
                        newLabel += maxParts[0];
                    } else {
                        newLabel += maxString;
                    }

                    //legendItemName = "title " + title + ", min " + min + ", max " + max;
                    //System.out.println(legendItemName);
                    Geotools_LegendItem li;
                    if (addWhiteForZero) {
                        li = new Geotools_LegendItem(newLabel, colorsForLegend[i + 1]);
                    } else {
                        li = new Geotools_LegendItem(newLabel, colorsForLegend[i]);
                    }
                    legendItems.add(li);
                }
                result[1] = legendItems;
            } else {
                int debug = 1;
            }
        }
        //LegendLayer ll = new DW_ChoroplethMapLegendLayer(Color.BLACK,"label",legendItems);

        // STEP 3 - ask StyleGenerator to make a set of rules for the Classifier
        // assigning features the correct color based on height
        double opacity;
        opacity = 0.95; //0.5;//1.0; //1.0; //0.95
        int elsemode;
        elsemode = StyleGenerator.ELSEMODE_IGNORE;
        Stroke stroke;
        if (styleParameters.isDrawBoundaries()) {
            stroke = styleFactory.getDefaultStroke();
        } else {
            stroke = null;
//            stroke = styleFactory.createStroke(
//                    filterFactory.literal(Color.TRANSLUCENT),
//                    filterFactory.literal(0),
//                    filterFactory.literal(1.0));
        }
        String typeID;
        typeID = "Generated FeatureTypeStyle for " + nClasses + " "
                + classificationFunctionName + " classes in palette "
                + paletteName;
        FeatureType featureType;
        featureType = featureCollection.getSchema();
        GeometryDescriptor geometryDescriptor;
        geometryDescriptor = featureType.getGeometryDescriptor();
        Style style;
        style = styleFactory.createStyle();
        if (groups != null) {
            FeatureTypeStyle featureTypeStyle;
            featureTypeStyle = Geotools_StyleGenerator.createFeatureTypeStyle(
                    groups,
                    propertyName,
                    colorsForRenderingFeatures,
                    typeID,
                    geometryDescriptor,
                    elsemode,
                    opacity,
                    stroke);
            style.featureTypeStyles().add(featureTypeStyle);
        }
//        List<FeatureTypeStyle> ftss = style.featureTypeStyles();
//            Iterator<FeatureTypeStyle> ite = ftss.iterator();
//            while (ite.hasNext()){
//                FeatureTypeStyle fts = ite.next();
//                Description d = fts.getDescription();
//            }
        result[0] = style;
        return result;
    }

    /**
     * Assuming min is 0.
     *
     * @param g
     * @param cov
     * @param nClasses
     * @param paletteName
     * @param addWhiteForZero
     * @return
     * @throws java.lang.Exception If Encountered.
     */
    public Style getStyle(Grids_GridNumber g, GridCoverage cov, int nClasses,
            String paletteName, boolean addWhiteForZero) throws Exception {
        String[] classNames;
        double[] breaks;
        Grids_StatsDouble stats = ((Grids_StatsDouble) g.getStats());
        double min = stats.getMin(true);
        double max = stats.getMax(true);
        double interval = (max - min) / (double) nClasses;
        double minInterval = min;
        double maxInterval = min + interval;
        if (addWhiteForZero) {
            nClasses++;
            classNames = new String[nClasses];
            breaks = new double[nClasses];
            classNames[0] = "0";
            minInterval = 0.0d;
            maxInterval = 0.1d;//Math.nextUp(minInterval);
            breaks[0] = minInterval;
            minInterval = maxInterval;
            maxInterval = minInterval + interval;
            for (int i = 1; i < nClasses; i++) {
                if (i < nClasses - 1) {
                    classNames[i] = "" + minInterval + " - " + maxInterval;
                    breaks[i] = minInterval;
                    minInterval += interval;
                    maxInterval += interval;
                } else {
                    classNames[i] = "" + minInterval + " - " + max;
                    breaks[i] = minInterval;
                }
            }
        } else {
            classNames = new String[nClasses];
            breaks = new double[nClasses];
            for (int i = 0; i < nClasses; i++) {
                if (i < nClasses - 1) {
                    classNames[i] = "" + minInterval + " - " + maxInterval;
                    breaks[i] = minInterval;
                    minInterval += interval;
                    maxInterval += interval;
                } else {
                    classNames[i] = "" + minInterval + " - " + max;
                    breaks[i] = minInterval;
                }
            }
        }
//        Function classify;
//        classify = ff.function(
//                classificationFunctionName,
//                ff.literal(nClasses));
//        RangedClassifier rc = null;
//        rc = (RangedClassifier) classify.evaluate(cov);
//        for (int i = 0; i < nClasses; i++) {
//            double min = (Double) rc.getMin(i);
//            double max = (Double) rc.getMax(i);
//            classNames[i] = "" + min + " - " + max;
//            breaks[i] = min;
//        }
        ColorBrewer cb;
        cb = ColorBrewer.instance();
        BrewerPalette bp;
        bp = cb.getPalette(paletteName);
        Color[] colors;
        if (addWhiteForZero) {
            Color[] dummyColors = bp.getColors(nClasses - 1);
            colors = new Color[nClasses];
            colors[0] = Color.WHITE;
            System.arraycopy(dummyColors, 0, colors, 1, nClasses - 1);
        } else {
            colors = bp.getColors(nClasses);
        }
        StyleBuilder sb;
        sb = new StyleBuilder();
        ColorMap cm;
        cm = sb.createColorMap(classNames, breaks, colors, ColorMap.TYPE_RAMP);
        Style style;
        style = sb.createStyle(sb.createRasterSymbolizer(cm, 1));
        return style;
//        StyleBuilder sb = new StyleBuilder();
//        double interval = 1;
//        double min = 0;
//        ColorBrewer brewer = ColorBrewer.instance();
//        brewer.loadPalettes();
//        BrewerPalette[] palettes = brewer.getPalettes(ColorBrewer.SEQUENTIAL);
//        Color[] colors = palettes[1].getColors(5);
//        double[] breaks = new double[]{min, min + interval, min + 2 * interval, min + 3 * interval,
//            min + 4 * interval};
//        ColorMap map = sb.createColorMap(new String[]{"1", "2", "3", "4", "5"}, breaks, colors,
//                ColorMap.TYPE_RAMP);
//        Style style;
//        style = sb.createStyle(sb.createRasterSymbolizer(map, 1));
//        return style;
    }

    /**
     * Assuming min is 0.
     *
     * @param n normalisation
     * @param g
     * @param cov
     * @param type
     * @param nClasses
     * @param paletteName
     * @param addWhiteForZero
     * @return
         * @throws java.lang.Exception If Encountered.
 */
    public Object[] getStyleAndLegendItems(double n, Grids_GridNumber g,
            GridCoverage cov, String type, int nClasses, String paletteName,
            boolean addWhiteForZero) throws Exception {
        if (type.equalsIgnoreCase("EqualInterval")) {
            return getEqualIntervalStyleAndLegendItems(n, g, cov, nClasses,
                    paletteName, addWhiteForZero);
        }
        if (type.equalsIgnoreCase("Quantile")) {
            return getQuantileStyleAndLegendItems(n, (Grids_GridDouble) g, cov,
                    nClasses, paletteName, addWhiteForZero);
        }
        return null;
    }

    /**
     * Assuming min is 0.
     *
     * @param n normalisation
     * @param g
     * @param cov
     * @param type
     * @param nClasses
     * @param paletteName
     * @param paletteName2
     * @param addWhiteForZero
     * @return
        * @throws java.lang.Exception If Encountered.
  */
    public Object[] getStyleAndLegendItems(double n, Grids_GridNumber g,
            GridCoverage cov, String type, int nClasses, String paletteName,
            String paletteName2, boolean addWhiteForZero) throws Exception {
        if (type.equalsIgnoreCase("EqualInterval")) {
            return getEqualIntervalStyleAndLegendItems(n, g, cov, nClasses,
                    paletteName, paletteName2, addWhiteForZero);
        }
//        if (type.equalsIgnoreCase("Quantile")) {
//            return getQuantileStyleAndLegendItems(n,
//                    (Grids_GridDouble) g,
//                    cov,
//                    nClasses,
//                    paletteName,
//                    nClasses2,
//                    paletteName2,
//                    addWhiteForZero);
//        }
        return null;
    }

    /**
     * Assuming min is 0.
     *
     * @param n normalisation
     * @param g
     * @param cov
     * @param nClasses
     * @param paletteName
     * @param addWhiteForZero
     * @return
       * @throws java.lang.Exception If Encountered.
   */
    public Object[] getEqualIntervalStyleAndLegendItems(double n,
            Grids_GridNumber g, GridCoverage cov, int nClasses,
            String paletteName, boolean addWhiteForZero) throws Exception {
        Object[] result = new Object[2];
        ArrayList<Geotools_LegendItem> legendItems;
        legendItems = new ArrayList<>();
        String[] classNames;
        double[] breaks;
        Grids_StatsDouble stats = (Grids_StatsDouble) g.getStats();
        double min = stats.getMin(true);
        double max = stats.getMax(true);
        double interval = (max - min) / (double) nClasses;
        double minInterval = min;
        double maxInterval = min + interval;
        if (addWhiteForZero) {
            nClasses++;
            classNames = new String[nClasses];
            breaks = new double[nClasses];
            classNames[0] = "0";
            minInterval = 0.0d;
            maxInterval = 0.000000001d;//Math.nextUp(minInterval);//0.1d;//Math.nextUp(minInterval);
            breaks[0] = minInterval;
            minInterval = maxInterval;
            maxInterval = minInterval + interval;
            for (int i = 1; i < nClasses; i++) {
                if (i < nClasses - 1) {
                    double roundedMinInterval;
                    roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + minInterval * 100 / n),
                            2, RoundingMode.UP).doubleValue();
                    double roundedMaxInterval;
                    roundedMaxInterval = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + maxInterval * 100 / n),
                            2, RoundingMode.UP).doubleValue();
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMaxInterval;
                    breaks[i] = minInterval;
                    minInterval += interval;
                    maxInterval += interval;
                } else {
                    double roundedMinInterval;
                    roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + minInterval * 100 / n),
                            2, RoundingMode.UP).doubleValue();
                    double roundedMax;
                    roundedMax = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + max * 100 / n),
                            2, RoundingMode.UP).doubleValue();
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMax;
                    breaks[i] = minInterval;
                }
            }
        } else {
            classNames = new String[nClasses];
            breaks = new double[nClasses];
            for (int i = 0; i < nClasses; i++) {
                if (i < nClasses - 1) {
                    double roundedMinInterval;
                    roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + minInterval * 100 / n),
                            2, RoundingMode.UP).doubleValue();
                    double roundedMaxInterval;
                    roundedMaxInterval = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + maxInterval * 100 / n),
                            2, RoundingMode.UP).doubleValue();
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMaxInterval;
                    breaks[i] = minInterval;
                    minInterval = maxInterval;
                    maxInterval += interval;
                } else {
                    double roundedMinInterval;
                    roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + minInterval * 100 / n),
                            2, RoundingMode.UP).doubleValue();

                    double roundedMax;
                    roundedMax = Math_BigDecimal.roundIfNecessary(
                            new BigDecimal("" + max * 100 / n),
                            2, RoundingMode.UP).doubleValue();
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMax;
                    breaks[i] = minInterval;
                }
            }
        }
        ColorBrewer cb;
        cb = ColorBrewer.instance();
        BrewerPalette bp;
        bp = cb.getPalette(paletteName);
        Color[] colors;
        if (addWhiteForZero) {
            Color[] dummyColors = bp.getColors(nClasses - 1);
            colors = new Color[nClasses];
            colors[0] = Color.WHITE;
            System.arraycopy(dummyColors, 0, colors, 1, nClasses - 1);
        } else {
            colors = bp.getColors(nClasses);
        }
        StyleBuilder sb;
        sb = new StyleBuilder();
        ColorMap cm;
        cm = sb.createColorMap(classNames, breaks, colors, ColorMap.TYPE_RAMP);
        Style style;
        style = sb.createStyle(sb.createRasterSymbolizer(cm, 1));
        result[0] = style;
        for (int i = 0; i < nClasses; i++) {
            Geotools_LegendItem li;
            li = new Geotools_LegendItem(classNames[i], colors[i]);
            legendItems.add(li);
        }
        result[1] = legendItems;
        return result;
    }

    /**
     * Assuming min is 0.
     *
     * @param n normalisation
     * @param g
     * @param cov
     * @param nClasses
     * @param paletteName
     * @param paletteName2
     * @param addWhiteForZero
     * @return
          * @throws java.lang.Exception If Encountered.
*/
    public Object[] getEqualIntervalStyleAndLegendItems(double n,
            Grids_GridNumber g, GridCoverage cov, int nClasses,
            String paletteName, String paletteName2, boolean addWhiteForZero) throws Exception {
        Object[] result = new Object[2];
        ArrayList<Geotools_LegendItem> legendItems;
        legendItems = new ArrayList<>();
        String[] classNames;
        double[] breaks;
        Grids_StatsDouble stats = (Grids_StatsDouble) g.getStats();
        double min = stats.getMin(true);
        if (min >= 0.0d) {
            return getEqualIntervalStyleAndLegendItems(n, g, cov, nClasses,
                    paletteName, addWhiteForZero);
        }
        double max = stats.getMax(true);
        int numberOfPositiveClasses;
        int numberOfNegativeClasses;
        double interval;
        if ((-min) > max) {
            interval = -min / (double) nClasses;
            // Calculate the number of intervals above and below 0
            numberOfNegativeClasses = (int) Math.ceil(-min / interval);
            numberOfPositiveClasses = nClasses;
            max = interval * numberOfPositiveClasses;
        } else {
            interval = max / (double) nClasses;
            numberOfNegativeClasses = nClasses;
            numberOfPositiveClasses = (int) Math.ceil(max / interval);
            min = -interval * numberOfNegativeClasses;
        }
        int totalClasses;
        if (addWhiteForZero) {
            totalClasses = numberOfNegativeClasses + numberOfPositiveClasses + 1;
            double minInterval = min;
            double maxInterval = min + interval;
            classNames = new String[totalClasses];
            double roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                    new BigDecimal("" + minInterval * 100 / n),
                    2, RoundingMode.UP).doubleValue();
            double roundedMaxInterval = Math_BigDecimal.roundIfNecessary(
                    new BigDecimal("" + maxInterval * 100 / n),
                    2, RoundingMode.UP).doubleValue();
            breaks = new double[totalClasses];
            int i;
            i = 0;
            //classNames[i] = "" + roundedMinInterval + " - " + roundedMaxInterval;
            classNames[i] = "< " + roundedMaxInterval;
            //breaks[i] = minInterval;
            breaks[i] = maxInterval;
            minInterval = maxInterval;
            maxInterval = minInterval + interval;
            for (i = 1; i < numberOfNegativeClasses - 1; i++) {
                roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                        new BigDecimal("" + minInterval * 100 / n),
                        2, RoundingMode.UP).doubleValue();
                roundedMaxInterval = Math_BigDecimal.roundIfNecessary(
                        new BigDecimal("" + maxInterval * 100 / n),
                        2, RoundingMode.UP).doubleValue();
                classNames[i] = "" + roundedMinInterval + " - " + roundedMaxInterval;
                //breaks[i] = minInterval;
                breaks[i] = maxInterval;
                minInterval = maxInterval;
                maxInterval += interval;
            }
            roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                    new BigDecimal("" + minInterval * 100 / n),
                    2, RoundingMode.UP).doubleValue();
            classNames[i] = "" + roundedMinInterval + " - 0";
            //breaks[i] = minInterval;

//            minInterval = maxInterval;
//            maxInterval += interval;
            minInterval = -0.00001d;//-1;//-0.1d;//-0.000000001d;//Math.nextDown(0.0d);
            breaks[i] = minInterval;
            i++;
            // Add white for values next to 0;
            classNames[i] = "0";
            maxInterval = 0.00001d;//1;//0.1d;//0.000000001d;//Math.nextUp(0.0d);
            //breaks[i] = minInterval;
            breaks[i] = maxInterval;
            int whiteIndex = i;
            minInterval = maxInterval;
            maxInterval = 0.0d + interval;
            i++;
            roundedMaxInterval = Math_BigDecimal.roundIfNecessary(
                    new BigDecimal("" + maxInterval * 100 / n),
                    2, RoundingMode.UP).doubleValue();
            classNames[i] = "0 - " + roundedMaxInterval;
            //breaks[i] = minInterval;
            breaks[i] = maxInterval;
            minInterval = maxInterval;
            maxInterval += interval;
            int j;
            for (j = 1; j < numberOfPositiveClasses - 1; j++) {
                roundedMinInterval = Math_BigDecimal.roundIfNecessary(
                        new BigDecimal("" + minInterval * 100 / n),
                        2, RoundingMode.UP).doubleValue();
                roundedMaxInterval = Math_BigDecimal.roundIfNecessary(
                        new BigDecimal("" + maxInterval * 100 / n),
                        2, RoundingMode.UP).doubleValue();
                classNames[i + j] = "" + roundedMinInterval + " - " + roundedMaxInterval;
                //breaks[i + j] = minInterval;
                breaks[i + j] = maxInterval;
                minInterval = maxInterval;
                maxInterval += interval;
            }
            classNames[i + j] = "> " + roundedMaxInterval;
            breaks[i + j] = maxInterval;
            ColorBrewer cb;
            cb = ColorBrewer.instance();
            BrewerPalette bp;
            bp = cb.getPalette(paletteName);
            BrewerPalette bp2;
            bp2 = cb.getPalette(paletteName2);
            Color[] colors;
            colors = new Color[totalClasses];
            Color[] cs;
            cs = bp.getColors(numberOfNegativeClasses);
            for (j = 0; j < cs.length; j++) {
                colors[j] = cs[cs.length - (j + 1)];
            }
            colors[whiteIndex] = Color.WHITE;
            Color[] cs2;
            cs2 = bp2.getColors(numberOfPositiveClasses);
            for (j = 0; j < cs2.length; j++) {
                colors[j + whiteIndex + 1] = cs2[j];
            }
            StyleBuilder sb;
            sb = new StyleBuilder();
            ColorMap cm;
            //cm = sb.createColorMap(classNames, breaks, colors, ColorMap.TYPE_RAMP);
            cm = sb.createColorMap(classNames, breaks, colors, ColorMap.TYPE_INTERVALS);
            Style style;
            style = sb.createStyle(sb.createRasterSymbolizer(cm, 1));
            result[0] = style;
            for (j = 0; j < totalClasses; j++) {
                Geotools_LegendItem li;
                li = new Geotools_LegendItem(classNames[j], colors[j]);
                legendItems.add(li);
            }
        }
        result[1] = legendItems;
        return result;
    }

    /**
     * Assuming min is 0.
     *
     * @param n normalisation
     * @param g
     * @param cov
     * @param nClasses
     * @param paletteName
     * @param addWhiteForZero
     * @return
       * @throws java.lang.Exception If Encountered.
   */
    public Object[] getQuantileStyleAndLegendItems(double n, Grids_GridDouble g,
            GridCoverage cov, int nClasses, String paletteName, 
            boolean addWhiteForZero) throws Exception {
        Object[] r = new Object[2];
        ArrayList<Geotools_LegendItem> legendItems = new ArrayList<>();
        String[] classNames;
        double[] breaks;
        Grids_StatsDouble gs = (Grids_StatsDouble) g.getStats();
        long nonZeroAndNonNoDataValueCount = gs.getN().longValueExact();
        System.out.println("nonZeroAndNonNoDataValueCount " + nonZeroAndNonNoDataValueCount);
        Object[] quantileClassMap = gs.getQuantileClassMap(nClasses);
        TreeMap<Integer, Double> minDouble = (TreeMap<Integer, Double>) quantileClassMap[0];
        TreeMap<Integer, Double> maxDouble = (TreeMap<Integer, Double>) quantileClassMap[1];

        TreeMap<Integer, TreeMap<Double, Long>> classMap;
        classMap = (TreeMap<Integer, TreeMap<Double, Long>>) quantileClassMap[2];
        // Get the true number of classes in the classMap
        int newClassCount = 0;
        Iterator<Integer> ite;
        ite = classMap.keySet().iterator();
        while (ite.hasNext()) {
            Integer key = ite.next();
            if (!classMap.get(key).isEmpty()) {
                newClassCount++;
            }
        }

        if (newClassCount < nClasses) {
            // Subdivide any end classes into smaller ones?
            // It might be better to ask for a larger number of classes in the 
            // first instance and then group these? 
        }

        nClasses = newClassCount;
        double min = gs.getMin(true);
        double max = gs.getMax(true);
        if (addWhiteForZero) {
            nClasses++;
            classNames = new String[nClasses];
            breaks = new double[nClasses];
            classNames[0] = "0";
            for (int i = 1; i < nClasses; i++) {
                if (i < nClasses - 1) {
                    String roundedMinInterval;
                    double minInterval;
                    minInterval = minDouble.get(i - 1);
                    roundedMinInterval = getRoundedValue(
                            n,
                            minInterval);
                    String roundedMaxInterval;
                    double maxInterval;
                    maxInterval = maxDouble.get(i - 1);
                    roundedMaxInterval = getRoundedValue(
                            n,
                            maxInterval);
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMaxInterval;
                    breaks[i] = minInterval;
                } else {
                    String roundedMinInterval;
                    double minInterval;
                    minInterval = minDouble.get(i - 1);
                    roundedMinInterval = getRoundedValue(
                            n,
                            minInterval);
                    String roundedMax = getRoundedValue(
                            n,
                            max);
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMax;
                    breaks[i] = minInterval;
                }
            }
        } else {
            classNames = new String[nClasses];
            breaks = new double[nClasses];
            for (int i = 0; i < nClasses; i++) {
                if (i < nClasses - 1) {
                    String roundedMinInterval;
                    double minInterval;
                    minInterval = minDouble.get(i);
                    roundedMinInterval = getRoundedValue(
                            n,
                            minInterval);
                    String roundedMaxInterval;
                    double maxInterval;
                    maxInterval = maxDouble.get(i);
                    roundedMaxInterval = getRoundedValue(
                            n,
                            maxInterval);
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMaxInterval;
                    breaks[i] = minInterval;
                } else {
                    String roundedMinInterval;
                    double minInterval;
                    minInterval = minDouble.get(i);
                    roundedMinInterval = getRoundedValue(
                            n,
                            minInterval);
                    String roundedMax = getRoundedValue(
                            n,
                            max);
                    classNames[i] = "" + roundedMinInterval + " - " + roundedMax;
                    breaks[i] = minInterval;
                }
            }
        }
        ColorBrewer cb;
        cb = ColorBrewer.instance();
        BrewerPalette bp;
        bp = cb.getPalette(paletteName);
        Color[] colors;
        if (addWhiteForZero) {
            Color[] dummyColors = bp.getColors(nClasses - 1);
            colors = new Color[nClasses];
            colors[0] = Color.WHITE;
            System.arraycopy(dummyColors, 0, colors, 1, nClasses - 1);
        } else {
            colors = bp.getColors(nClasses);
        }
        StyleBuilder sb;
        sb = new StyleBuilder();
        ColorMap cm;
        cm = sb.createColorMap(classNames, breaks, colors, ColorMap.TYPE_RAMP);
        Style style;
        style = sb.createStyle(sb.createRasterSymbolizer(cm, 1));
        r[0] = style;
        for (int i = 0; i < nClasses; i++) {
            Geotools_LegendItem li;
            li = new Geotools_LegendItem(classNames[i], colors[i]);
            legendItems.add(li);
        }
        r[1] = legendItems;
        return r;
    }

    private String getRoundedValue(
            double normalisation,
            double interval) {
        String result;
        if (interval == Double.NEGATIVE_INFINITY
                || interval == Double.POSITIVE_INFINITY
                || interval == Double.NaN) {
            result = "NaN";
        } else {
            result = Double.toString(Math_BigDecimal.roundIfNecessary(
                    new BigDecimal("" + interval * normalisation),
                    2, RoundingMode.UP).doubleValue());
        }
        return result;
    }

}
