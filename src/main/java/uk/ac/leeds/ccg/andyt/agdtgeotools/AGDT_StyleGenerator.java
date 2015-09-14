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

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.geotools.brewer.color.StyleGenerator;
import org.geotools.factory.CommonFactoryFinder;

import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.function.Classifier;
import org.geotools.filter.function.ExplicitClassifier;
import org.geotools.filter.function.RangedClassifier;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author geoagdt
 */
public class AGDT_StyleGenerator extends StyleGenerator {

    private static final java.util.logging.Logger LOGGER = org.geotools.util.logging.Logging.getLogger("org.geotools.brewer.color");

    private static FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
    private static StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    private static StyleBuilder sb = new StyleBuilder(sf, ff);

    /**
     * Merges a classifier, array of colors and other data into a
     * FeatureTypeStyle object. Yes, this constructor is insane and likely to
     * change very soon.
     *
     * @param classifier
     * @param colors
     * @param typeId semantic type identifier, which will be prefixed with
     * "colorbrewer:"
     * @param geometryAttrType
     * @param elseMode
     * @param opacity
     * @param stroke
     * @return
     * @throws IllegalFilterException
     */
    //@Override
    public static FeatureTypeStyle createFeatureTypeStyle(
            Classifier classifier,
            Expression expression, Color[] colors, String typeId,
            GeometryDescriptor geometryAttrType, int elseMode, double opacity, Stroke stroke)
            throws IllegalFilterException {

        //answer goes here
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();

        // update the number of classes
        int numClasses = classifier.getSize();

        //        if (elseMode == ELSEMODE_IGNORE) {
        //            numClasses++;
        //        }
        //numeric
        if (classifier instanceof RangedClassifier) {
            RangedClassifier ranged = (RangedClassifier) classifier;
            Object localMin = null;
            Object localMax = null;
            int i;
            Rule rule;
            // First class
            i = 0;
            localMin = ranged.getMin(i);
            localMin = Double.MIN_VALUE;
            localMin = ranged.getMin(i);
            localMax = ranged.getMax(i);
            rule = createRuleRanged(
                    ranged,
                    expression,
                    localMin,
                    localMax,
                    geometryAttrType,
                    i,
                    elseMode,
                    colors,
                    opacity,
                    stroke);
            fts.addRule(rule);
            // For each middle classes
            for (i = 1; i < ranged.getSize() - 1; i++) {
                // obtain min/max values
                localMin = ranged.getMin(i);
                localMax = ranged.getMax(i);
                rule = createRuleRanged(
                        ranged,
                        expression,
                        localMin,
                        localMax,
                        geometryAttrType,
                        i,
                        elseMode,
                        colors,
                        opacity,
                        stroke);
                fts.addRule(rule);
            }
            // For last class
            i = ranged.getSize() - 1;
            // obtain min/max values
            localMin = ranged.getMin(i);
            localMax = Double.MAX_VALUE;
            //localMax = ranged.getMax(i);
            rule = createRuleRanged(
                    ranged,
                    expression,
                    localMin,
                    localMax,
                    geometryAttrType,
                    i,
                    elseMode,
                    colors,
                    opacity,
                    stroke);
            fts.addRule(rule);
        } else if (classifier instanceof ExplicitClassifier) {
            ExplicitClassifier explicit = (ExplicitClassifier) classifier;

            // for each class
            for (int i = 0; i < explicit.getSize(); i++) {
                Set value = (Set) explicit.getValues(i);
                Rule rule = createRuleExplicit(explicit, expression, value, geometryAttrType, i,
                        elseMode, colors, opacity, stroke);
                fts.addRule(rule);
            }
        } else {
            LOGGER.log(Level.SEVERE, "Error: no handler for this Classifier type");
        }

        // add an else rule to capture any missing features?
        if (elseMode != ELSEMODE_IGNORE) {
            Symbolizer symb = createSymbolizer(geometryAttrType, getElseColor(elseMode, colors),
                    opacity, stroke);
            Rule elseRule = sb.createRule(symb);
            elseRule.setIsElseFilter(true);
            elseRule.setTitle("Else");
            elseRule.setName("else");
            fts.addRule(elseRule);
        }

        // sort the FeatureTypeStyle rules
        Rule[] rule = fts.getRules();

        if (elseMode == ELSEMODE_INCLUDEASMIN) {
            //move last rule to the front
            for (int i = rule.length - 1; i > 0; i--) {
                Rule tempRule = rule[i];
                rule[i] = rule[i - 1];
                rule[i - 1] = tempRule;
            }
        }

        //our syntax will be: ColorBrewer:id
        fts.setSemanticTypeIdentifiers(new String[]{"generic:geometry", "colorbrewer:" + typeId});

        return fts;
    }

    private static Rule createRuleRanged(RangedClassifier classifier, Expression expression,
            Object localMin, Object localMax, GeometryDescriptor geometryAttrType, int i,
            int elseMode, Color[] colors, double opacity, Stroke defaultStroke)
            throws IllegalFilterException {
        // 1.0 --> 1
        // (this makes our styleExpressions more readable. Note that the
        // filter always converts to double, so it doesn't care what we
        // do).
        localMin = chopInteger(localMin);
        localMax = chopInteger(localMax);

        // generate a title
        String title = classifier.getTitle(i);

        // construct filters
        Filter filter = null;

        if (localMin == localMax) {
            // build filter: =
            filter = ff.equals(expression, ff.literal(localMax));
        } else {
            // build filter: [min <= x] AND [x < max]
            Filter lowBoundFilter = null;
            Filter hiBoundFilter = null;

            if (localMin != null) {
                lowBoundFilter = ff.greaterOrEqual(expression, ff.literal(localMin));
            }
            if (localMax != null) {
                // if this is the global maximum, include the max value
                if (i == (classifier.getSize() - 1)) {
                    hiBoundFilter = ff.lessOrEqual(expression, ff.literal(localMax));
                } else {
                    hiBoundFilter = ff.less(expression, ff.literal(localMax));
                }
            }

            if ((localMin != null) && (localMax != null)) {
                filter = ff.and(lowBoundFilter, hiBoundFilter);
            } else if ((localMin == null) && (localMax != null)) {
                filter = hiBoundFilter;
            } else if ((localMin != null) && (localMax == null)) {
                filter = lowBoundFilter;
            }
        }

        Color c = getColor(elseMode, colors, i);
        if (defaultStroke == null) {
            defaultStroke = sb.createStroke(
                    sb.literalExpression(c),
                    sb.literalExpression(0),
                    sb.literalExpression(opacity));
        }
        // create a symbolizer
        Symbolizer symb = createSymbolizer(geometryAttrType, c,
                opacity, defaultStroke);

        // create a rule
        Rule rule = sb.createRule(symb);
        rule.setFilter(filter);
        rule.setTitle(title);
        rule.setName(getRuleName(i + 1));

        return rule;
    }

    /**
     * Truncates an unneeded trailing decimal zero (1.0 --> 1) by converting to
     * an Integer object.
     *
     * @param value
     *
     * @return Integer(value) if applicable
     */
    private static Object chopInteger(Object value) {
        if ((value instanceof Number) && (value.toString().endsWith(".0"))) {
            return new Integer(((Number) value).intValue());
        } else {
            return value;
        }
    }

    /**
     * Generates a quick name for each rule with a leading zero.
     *
     * @param count
     *
     */
    private static String getRuleName(int count) {
        String strVal = new Integer(count).toString();

        if (strVal.length() == 1) {
            return "rule0" + strVal;
        } else {
            return "rule" + strVal;
        }
    }

    /**
     * Obtains the colour for the indexed rule. If an else rule is also to be
     * created from the colour palette, the appropriate offset is applied.
     *
     * @param index
     */
    private static Color getColor(int elseMode, Color[] colors, int index) {
        if (elseMode == ELSEMODE_IGNORE) {
            return colors[index];
        } else if (elseMode == ELSEMODE_INCLUDEASMIN) {
            return colors[index + 1];
        } else if (elseMode == ELSEMODE_INCLUDEASMAX) {
            return colors[index];
        } else {
            return null;
        }
    }

    private static Rule createRuleExplicit(
            ExplicitClassifier explicit,
            Expression expression,
            Set value,
            GeometryDescriptor geometryAttrType,
            int i,
            int elseMode,
            Color[] colors,
            double opacity,
            Stroke defaultStroke) {
        // create a sub filter for each unique value, and merge them
        // into the logic filter
        Object[] items = value.toArray();
        Arrays.sort(items);

        String title = "";
        List<Filter> filters = new ArrayList<Filter>();
        for (int item = 0; item < items.length; item++) {

            Filter filter;
            if (items[item] == null) {
                filter = ff.isNull(expression);
            } else {
                filter = ff.equals(expression, ff.literal(items[item]));
            }

            // add to the title
            if (items[item] == null) {
                title += "NULL";
            } else {
                title += items[item].toString();
            }

            if ((item + 1) != items.length) {
                title += ", ";
            }

            filters.add(filter);
        }

        // create the symbolizer
        Symbolizer symb = createSymbolizer(geometryAttrType, getColor(elseMode, colors, i),
                opacity, defaultStroke);

        // create the rule
        Rule rule = sb.createRule(symb);

        if (filters.size() == 1) {
            rule.setFilter(filters.get(0));
        } else if (filters.size() > 1) {
            rule.setFilter(ff.or(filters));
        }

        rule.setTitle(title);
        rule.setName(getRuleName(i + 1));

        return rule;
    }

    /**
     * Creates a symbolizer for the given geometry
     *
     * @param sb
     * @param geometryAttrType
     * @param color
     * @param opacity
     * @param defaultStroke stroke used for borders
     *
     */
    private static Symbolizer createSymbolizer(
            GeometryDescriptor geometryAttrType,
            Color color,
            double opacity,
            Stroke defaultStroke) {
        Symbolizer symb;

        if (defaultStroke == null) {
            defaultStroke = sb.createStroke(color, 1, opacity);
        }

        if ((geometryAttrType.getType().getBinding() == MultiPolygon.class)
                || (geometryAttrType.getType().getBinding() == Polygon.class)) {
            Fill fill = sb.createFill(color, opacity);
            symb = sb.createPolygonSymbolizer(defaultStroke, fill);
        } else if (geometryAttrType.getType().getBinding() == LineString.class) {
            symb = sb.createLineSymbolizer(color);
        } else if ((geometryAttrType.getType().getBinding() == MultiPoint.class)
                || (geometryAttrType.getType().getBinding() == Point.class)) {
            Fill fill = sb.createFill(color, opacity);
            Mark square = sb.createMark(StyleBuilder.MARK_SQUARE, fill, defaultStroke);
            Graphic graphic = sb.createGraphic(null, square, null); //, 1, 4, 0);
            symb = sb.createPointSymbolizer(graphic);

            //TODO: handle Text and Raster
        } else {
            //we don't know what the heck you are, *snip snip* you're a line.
            symb = sb.createLineSymbolizer(color);
        }

        return symb;
    }

    private static Color getElseColor(int elseMode, Color[] colors) {
        if (elseMode == ELSEMODE_INCLUDEASMIN) {
            return colors[0];
        } else if (elseMode == ELSEMODE_INCLUDEASMAX) {
            return colors[colors.length - 1];
        } else {
            return null;
        }
    }
}
