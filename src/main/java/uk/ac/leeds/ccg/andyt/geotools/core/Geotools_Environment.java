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
package uk.ac.leeds.ccg.andyt.geotools.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.opengis.feature.simple.SimpleFeature;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_LegendItem;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_LegendLayer;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Maps;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Style;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_StyleParameters;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * A class for holding various useful methods for doing things with
 * Geotools_Environment Objects.
 *
 * @author geoagdt
 */
public class Geotools_Environment extends Generic_Environment {

    protected Geotools_Maps Maps;

    protected Grids_Environment ge;

    protected Geotools_Style Style;

    public Geotools_Maps getMaps() {
        if (Maps == null) {
            Maps = new Geotools_Maps(this);
        }
        return Maps;
    }

    /**
     * Initialises ge.
     *
     * @param dir
     */
    public final void initGrids_Environment(File dir) {
        ge = new Grids_Environment(dir);
    }

    public Grids_Environment getGrids_Environment() {
        return ge;
    }

    public Geotools_Style getStyle() {
        if (Style == null) {
            Style = new Geotools_Style(this);
        }
        return Style;
    }

    public int getMapContentImageHeight(
            MapContent mc,
            int imageWidth) {
        int result;
        ReferencedEnvelope re = mc.getMaxBounds();
        double height = re.getHeight();
        double width = re.getWidth();
        //System.out.println("height " + height + ", width " + width);
        double heightToWidth = height / width;
        result = (int) (imageWidth * heightToWidth);
        return result;
    }

    /**
     * polygon0 PostcodeSector polygon1 OA polygon2 LSOA polygon3
     * postcodeUnitPoly / MSOA polygon4 polyGrid, line0 lineGrid points0
     * PostcodeUnitPoint points1 PostcodeSectorPoint
     *
     * @param polygon0
     * @param polygon1
     * @param polygon2
     * @param polygon3
     * @param polygon4
     * @param line0
     * @param points0
     * @param points1
     * @return
     */
    public MapContent createMapContent(
            Geotools_Shapefile polygon0,
            Geotools_Shapefile polygon1,
            Geotools_Shapefile polygon2,
            Geotools_Shapefile polygon3,
            Geotools_Shapefile polygon4,
            Geotools_Shapefile line0,
            Geotools_Shapefile points0,
            Geotools_Shapefile points1) {
        MapContent result;
        result = new MapContent();

        if (polygon0 != null) {
            // Add polygon layer 0 to mc
            // -------------------------
            Style polygon0Style;
            polygon0Style = getStyle().createDefaultPolygonStyle(
                    Color.BLUE, Color.WHITE);
            FeatureLayer polygon0layer = new FeatureLayer(
                    polygon0.getFeatureSource(), polygon0Style);
            result.addLayer(polygon0layer);
        }

        if (polygon3 != null) {
            // Add polygon layer 3 to mc
            // -------------------------
            Color c;
            //c = Color.DARK_GRAY;
            c = Color.PINK;
            Style polygon3Style;
            polygon3Style = getStyle().createDefaultPolygonStyle(c, null);
            FeatureLayer polygon3layer = new FeatureLayer(
                    polygon3.getFeatureSource(), polygon3Style);
            result.addLayer(polygon3layer);
        }

        if (polygon1 != null) {
            // Add polygon layer 1 to mc
            // -------------------------
            Color c;
            //c = Color.LIGHT_GRAY;
            c = Color.BLACK;
            Style polygon1Style;
            polygon1Style = getStyle().createDefaultPolygonStyle(c, null);
            FeatureLayer polygon1layer = new FeatureLayer(
                    polygon1.getFeatureSource(), polygon1Style);
            result.addLayer(polygon1layer);
        }

        if (polygon2 != null) {
            // Add polygon layer 2 to mc
            // -------------------------
            Style polygon2Style;
            polygon2Style = getStyle().createDefaultPolygonStyle(
                    Color.GRAY, null);
            FeatureLayer polygon2layer = new FeatureLayer(
                    polygon2.getFeatureSource(), polygon2Style);
            result.addLayer(polygon2layer);
        }

        if (polygon4 != null) {
            // Add polygon layer 4 to mc
            // -------------------------
            Color c;
            //c = Color.BLACK;
            c = Color.LIGHT_GRAY;
            Style polygon4Style;
            polygon4Style = getStyle().createDefaultPolygonStyle(c, null);
            FeatureLayer polygon4layer = new FeatureLayer(
                    polygon4.getFeatureSource(), polygon4Style);
            result.addLayer(polygon4layer);
        }

        if (line0 != null) {
            // Add line layer 0 to mc
            // -------------------------
            Style line0Style;
            line0Style = getStyle().createDefaultLineStyle();
            FeatureLayer line0layer = new FeatureLayer(
                    line0.getFeatureSource(), line0Style);
            result.addLayer(line0layer);
        }

        // Add point layer 0 to mc
        // -----------------------
        Style pointStyle0;
        pointStyle0 = getStyle().createDefaultPointStyle();
        FeatureLayer pointsFeatureLayer0;
        pointsFeatureLayer0 = points0.getFeatureLayer(
                pointStyle0);
        result.addLayer(pointsFeatureLayer0);

        // Add point layer 1 to mc
        // -----------------------
        int size;
        size = 6;
        String type;
        type = "Circle";
        Color fill;
        fill = Color.GREEN;
        Color outline;
        outline = Color.DARK_GRAY;
        Style pointStyle1;
        pointStyle1 = getStyle().getPointStyle(
                size,
                type,
                fill,
                outline);
        FeatureLayer pointsFeatureLayer1;
        pointsFeatureLayer1 = points1.getFeatureLayer(
                pointStyle1);
        result.addLayer(pointsFeatureLayer1);
        return result;
    }

    /**
     * polygon0 PostcodeSector polygon1 OA polygon2 LSOA polygon3
     * postcodeUnitPoly / MSOA polygon4 polyGrid, line0 lineGrid points0
     * PostcodeUnitPoint points1 PostcodeSectorPoint
     *
     * @param polygon
     * @return
     */
    public MapContent createMapContent(
            Geotools_Shapefile polygon) {
        MapContent result;
        result = new MapContent();

        if (polygon != null) {
            // Add polygon layer to result
            // ---------------------------
            Style polygon0Style;
            polygon0Style = getStyle().createDefaultPolygonStyle(
                    Color.BLUE, Color.WHITE);
            FeatureLayer polygon0layer = new FeatureLayer(
                    polygon.getFeatureSource(), polygon0Style);
            result.addLayer(polygon0layer);
        }
        return result;
    }

    /**
     * @param mapDirectory
     * @param name
     * @param fileExtension
     * @return File
     */
    public File getOutputFile(
            File mapDirectory,
            String name,
            String fileExtension) {
        File outDirectory = new File(
                mapDirectory,
                name);
        outDirectory.mkdirs();
        File file = new File(
                outDirectory,
                name + "." + fileExtension);
        return file;
    }

    /**
     * Shapefiles are best stored in a directory with the full shapefile name
     * for GeoTools.
     *
     * @param dir
     * @param name
     * @return File
     */
    public File getInputShapefile(
            File dir,
            String name) {
        File result;
        File outDirectory = new File(
                dir,
                name);
        String shapefileFilename = name + ".shp";
        result = getShapefile(dir, shapefileFilename, false);
        return result;
    }

    /**
     * Shapefiles are best stored in a directory with the full shapefile name
     * for GeoTools.
     *
     * @param dir
     * @param name
     * @return File
     */
    public File getOutputShapefile(
            File dir,
            String name) {
        File result;
        File outDirectory = new File(
                dir,
                name);
        outDirectory.mkdirs();
        String shapefileFilename = name + ".shp";
        result = getShapefile(dir, shapefileFilename, true);
        return result;
    }

    public File getShapefile(
            File dir,
            String shapefileFilename,
            boolean mkdirs) {
        File result;
        File shapefileDir;
        shapefileDir = new File(
                dir,
                shapefileFilename);
        if (mkdirs) {
            // Could add extra logic here to deal with issues if directory or a file 
            // of this name already exists...
            if (!shapefileDir.exists()) {
                shapefileDir.mkdirs();
            }
        }
        result = new File(
                shapefileDir,
                shapefileFilename);
        return result;
    }

//    public void addFeatureLayer(
//            MapContent mc,
//            File shapefile,
//            Style style,
//            String title) {
//        FeatureLayer fl;
//        fl = DW_Shapefile.getFeatureLayer(shapefile, style, title);
//        mc.addLayer(fl);
//    }
    /**
     * Produces a verbal comparison of sf1 and sf2 printing the results via
     * System.out.
     *
     * @param sf1 One of two simple features to compare.
     * @param sf2 Two of two simple features to compare.
     */
    public void compareFeatures(
            SimpleFeature sf1,
            SimpleFeature sf2) {
        String sf1String = sf1.toString();
        System.out.println("sf1String " + sf1String);
        String sf2String = sf2.toString();
        System.out.println("sf2String " + sf2String);
        if (sf1String.equalsIgnoreCase(sf2String)) {
            System.out.println("sf1String.equalsIgnoreCase(sf2String)");
        } else {
            System.out.println("! sf1String.equalsIgnoreCase(sf2String)");
        }
        int sf1AttributeCount = sf1.getAttributeCount();
        int sf2AttributeCount = sf2.getAttributeCount();
        System.out.println("sf1AttributeCount " + sf1AttributeCount);
        System.out.println("sf2AttributeCount " + sf2AttributeCount);
        List<Object> sf1Attributes = sf1.getAttributes();
        List<Object> sf2Attributes = sf2.getAttributes();
        int i;
        Iterator<Object> ite;
        i = 0;
        ite = sf1Attributes.iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            System.out.println("sf1Attribute[" + i + "].getClass() = " + o.getClass());
            i++;
        }
        i = 0;
        ite = sf2Attributes.iterator();
        while (ite.hasNext()) {
            Object o = ite.next();
            System.out.println("sf2Attribute[" + i + "].getClass() = " + o.getClass());
            i++;
        }
    }

    /**
     *
     * @param ge
     * @param mapContent
     * @param imageWidth
     * @param imageHeight
     * @param outputImageFile
     * @param outputType
     */
    public void writeImageFile(
            Grids_Environment ge,
            MapContent mapContent,
            int imageWidth,
            int imageHeight,
            File outputImageFile,
            String outputType) {
        try {
            writeImageFile(
                    mapContent,
                    imageWidth,
                    imageHeight,
                    outputImageFile,
                    outputType);
        } catch (OutOfMemoryError oome) {
            if (ge.HOOME) {
                ge.clearMemoryReserve();
                ge.swapChunk(true);
                ge.initMemoryReserve();
                writeImageFile(ge, mapContent, imageWidth, imageHeight,
                        outputImageFile, outputType);
            } else {
                throw oome;
            }
        }
    }

    /**
     * Render and output the mapContent and save to a file.
     *
     * @param mapContent The map to be written.
     * @param imageWidth The width of the image to be produced.
     * @param imageHeight The height of the image to be produced.
     * @param outputImageFile The file to be written to.
     * @param outputType The image file type e.g. "png"
     */
    public void writeImageFile(
            MapContent mapContent,
            int imageWidth,
            int imageHeight,
            File outputImageFile,
            String outputType) {
        // Initialise a renderer
        StreamingRenderer renderer;
        renderer = new StreamingRenderer();
        renderer.setMapContent(mapContent);
        Rectangle rectangle = new Rectangle(imageWidth, imageHeight);
        //System.out.println(rectangle.height + " " + rectangle.width);
        BufferedImage bufferedImage;
        bufferedImage = new BufferedImage(
                rectangle.width,
                rectangle.height,
                //BufferedImage.TYPE_INT_RGB);
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        // Set white background
        //graphics2D.setComposite(AlphaComposite.Clear);
        graphics2D.setBackground(Color.white);
        graphics2D.fillRect(0, 0, imageWidth, imageHeight);
        //graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0));
        // Render onto background
        //graphics2D.setComposite(AlphaComposite.Src);
        renderer.paint(graphics2D, rectangle, mapContent.getMaxBounds());
//        // Wait a bit to try to be sure that the painting is finished. It's a bugger when it isn't!
//        synchronized (outputType) {
//            try {
//                outputType.wait(2000L);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Geotools_Environment.class.getName()).log(Level.SEVERE, null, ex);
//            }
//                    }
        Generic_Visualisation.saveImage(bufferedImage, outputType, outputImageFile);
        graphics2D.dispose();
    }

//    public GridCoverage2D vectorToRaster(
//            DW_Shapefile polygons,
//            long nrows,
//            long ncols,
//            double xllcorner,
//            double yllcorner,
//            double cellsize) {
//        
//        
//    }
    /**
     * Warning this will set g to null.
     *
     * @param normalisation
     * @param styleParameters
     * @param index
     * @param outname
     * @param g
     * @param gc
     * @param foregroundDW_Shapefile0
     * @param foregroundDW_Shapefile1
     * @param backgroundDW_Shapefile
     * @param outputDir
     * @param imageWidth
     * @param showMapsInJMapPane
     * @param scaleToFirst
     */
    public void outputToImageUsingGeoToolsAndSetCommonStyle(
            double normalisation,
            Geotools_StyleParameters styleParameters,
            int index,
            String outname,
            Grids_AbstractGridNumber g,
            GridCoverage2D gc,
            ArrayList<Geotools_Shapefile> foregroundDW_Shapefile0,
            Geotools_Shapefile foregroundDW_Shapefile1,
            Geotools_Shapefile backgroundDW_Shapefile,
            File outputDir,
            int imageWidth,
            boolean showMapsInJMapPane,
            boolean scaleToFirst) {
        String png_String = "PNG";
        MapContent mc = createMapContent(
                normalisation,
                outname,
                g,
                gc,
                foregroundDW_Shapefile0,
                foregroundDW_Shapefile1,
                backgroundDW_Shapefile,
                imageWidth,
                styleParameters,
                index,
                scaleToFirst);
        /**
         * Set g to null as it is no longer needed. This is also done to free
         * memory and help prevent any unwanted OutOfMemory Errors being
         * encountered.
         */
        g = null;

        int imageHeight = getMapContentImageHeight(mc, imageWidth);
        File outputFile = getOutputFile(
                outputDir,
                outname,
                png_String);
        File outputImageFile = getMaps().getOutputImageFile(
                outputFile, png_String);

        writeImageFile(
                //g._Grids_Environment,
                mc,
                imageWidth,
                imageHeight,
                outputImageFile,
                png_String);

        // Dispose of MapContent to prevent memory leaks
        if (showMapsInJMapPane) {
            // Display mc in a JMapFrame
            JMapFrame.showMap(mc); // Need to not dispose of mc if this is to persist!
        } else {
            // Tidy up
            //gc.dispose();
            // Dispose of mc to avoid memory leaks
            //mc.removeLayer(backgroundFeatureLayer);
            List<Layer> layers = mc.layers();
            Iterator<Layer> ite = layers.iterator();
            while (ite.hasNext()) {
                Layer l = ite.next();
//                if (l.equals(backgroundFeatureLayer)) {
//                    System.out.println("Odd this was removed from MapContent!");
//                } else {
                l.preDispose();
                l.dispose();
//                }
            }
            //mc.removeLayer(backgroundFeatureLayer);
            mc.dispose();
        }
    }

    /**
     * Warning this will set g to null.
     *
     * @param normalisation
     * @param styleParameters
     * @param index
     * @param outname
     * @param g
     * @param gc
     * @param outputDir
     * @param imageWidth
     * @param showMapsInJMapPane
     * @param scaleToFirst
     */
    public void outputToImageUsingGeoToolsAndSetCommonStyle(
            double normalisation,
            Geotools_StyleParameters styleParameters,
            int index,
            String outname,
            Grids_AbstractGridNumber g,
            GridCoverage2D gc,
            File outputDir,
            int imageWidth,
            boolean showMapsInJMapPane,
            boolean scaleToFirst) {
        String png_String = "PNG";
        MapContent mc = createMapContent(
                normalisation,
                outname,
                g,
                gc,
                imageWidth,
                styleParameters,
                index,
                scaleToFirst);
        // Set g to null as it is no longer needed. 
        // This is done to prevent any unwanted OutOfMemory Errors being encountered.
        g = null;

        int imageHeight = getMapContentImageHeight(mc, imageWidth);
        File outputFile = getOutputFile(
                outputDir,
                outname,
                png_String);
        File outputImageFile = getMaps().getOutputImageFile(
                outputFile, png_String);

        writeImageFile(
                //g._Grids_Environment,
                mc,
                imageWidth,
                imageHeight,
                outputImageFile,
                png_String);

        // Dispose of MapContent to prevent memory leaks
        if (showMapsInJMapPane) {
            // Display mc in a JMapFrame
            JMapFrame.showMap(mc); // Need to not dispose of mc if this is to persist!
        } else {
            // Tidy up
            //gc.dispose();
            // Dispose of mc to avoid memory leaks
            //mc.removeLayer(backgroundFeatureLayer);
            List<Layer> layers = mc.layers();
            Iterator<Layer> ite = layers.iterator();
            while (ite.hasNext()) {
                Layer l = ite.next();
//                if (l.equals(backgroundFeatureLayer)) {
//                    System.out.println("Odd this was removed from MapContent!");
//                } else {
                l.preDispose();
                l.dispose();
//                }
            }
            //mc.removeLayer(backgroundFeatureLayer);
            mc.dispose();
        }
    }

    private MapContent createMapContent(
            double normalisation,
            String name,
            Grids_AbstractGridNumber g,
            GridCoverage2D gc,
            int imageWidth,
            Geotools_StyleParameters styleParameters,
            int index,
            boolean scaleToFirst) {
        MapContent result;
        result = new MapContent();
        // Unbox styleParameters
        Style style;
        style = styleParameters.getStyle(name, index);
        ArrayList<Geotools_LegendItem> legendItems = null;

        // Add output to mc
        // ----------------
        // If input style is null then create a basic Style to render the 
        // features
        if (style == null) {
            Object[] styleAndLegendItems;
            if (styleParameters.getPaletteName2() == null) {
//            styleAndLegendItems = DW_Style.getEqualIntervalStyleAndLegendItems(
                styleAndLegendItems = getStyle().getStyleAndLegendItems(
                        normalisation,
                        g,
                        gc,
                        styleParameters.getClassificationFunctionName(),
                        styleParameters.getnClasses(),
                        styleParameters.getPaletteName(),
                        styleParameters.isAddWhiteForZero());
            } else {
                styleAndLegendItems = getStyle().getStyleAndLegendItems(
                        normalisation,
                        g,
                        gc,
                        styleParameters.getClassificationFunctionName(),
                        styleParameters.getnClasses(),
                        styleParameters.getPaletteName(),
                        styleParameters.getPaletteName2(),
                        styleParameters.isAddWhiteForZero());
            }
            style = (Style) styleAndLegendItems[0];
            styleParameters.setStyle(name, style, index);
            legendItems = (ArrayList<Geotools_LegendItem>) styleAndLegendItems[1];
            styleParameters.setLegendItems(legendItems, index);
        } else {
            if (scaleToFirst) {
                legendItems = styleParameters.getLegendItems(index);
            }
        }
        GridCoverageLayer gcl = new GridCoverageLayer(gc, style);
        result.addLayer(gcl);

        int imageHeight = getMapContentImageHeight(result, imageWidth);

        // Add a legend
        // ------------
        if (legendItems != null) {
            boolean addLegendToTheSide = true;
            Geotools_LegendLayer ll = new Geotools_LegendLayer(
                    styleParameters,
                    "Legend",
                    legendItems,
                    result,
                    imageWidth,
                    imageHeight,
                    addLegendToTheSide);
            result.addLayer(ll);
        }

        return result;
    }

    private MapContent createMapContent(
            double normalisation,
            String name,
            Grids_AbstractGridNumber g,
            GridCoverage2D gc,
            ArrayList<Geotools_Shapefile> foregroundShapefiles,
            Geotools_Shapefile foregroundDW_Shapefile1,
            Geotools_Shapefile backgroundDW_Shapefile,
            int imageWidth,
            Geotools_StyleParameters styleParameters,
            int index,
            boolean scaleToFirst) {
        MapContent result;
        result = new MapContent();
        // Unbox styleParameters
        Style style;
        style = styleParameters.getStyle(name, index);
        ArrayList<Geotools_LegendItem> legendItems = null;

        if (styleParameters.isDrawBoundaries()) {
            FeatureLayer backgroundFeatureLayer;
            backgroundFeatureLayer = backgroundDW_Shapefile.getFeatureLayer(
                    styleParameters.getBackgroundStyle());
            result.addLayer(backgroundFeatureLayer);
        }

        // Add output to mc
        // ----------------
        // If input style is null then create a basic Style to render the 
        // features
        if (style == null) {
            Object[] styleAndLegendItems;
//            styleAndLegendItems = DW_Style.getEqualIntervalStyleAndLegendItems(
            styleAndLegendItems = getStyle().getStyleAndLegendItems(
                    normalisation,
                    g,
                    gc,
                    styleParameters.getClassificationFunctionName(),
                    styleParameters.getnClasses(),
                    styleParameters.getPaletteName(),
                    styleParameters.isAddWhiteForZero());
            style = (Style) styleAndLegendItems[0];
            styleParameters.setStyle(name, style, index);
            legendItems = (ArrayList<Geotools_LegendItem>) styleAndLegendItems[1];
            styleParameters.setLegendItems(legendItems, index);
        } else {
            if (scaleToFirst) {
                legendItems = styleParameters.getLegendItems(index);
            }
        }
        GridCoverageLayer gcl = new GridCoverageLayer(gc, style);
        result.addLayer(gcl);

        // Add foreground0
        // ---------------
        addForeground0(
                result,
                styleParameters,
                foregroundShapefiles);

        // Add foreground1
        // ---------------
        if (foregroundDW_Shapefile1 != null) {
            FeatureLayer foregroundFeatureLayer1;
            foregroundFeatureLayer1 = foregroundDW_Shapefile1.getFeatureLayer(
                    styleParameters.getForegroundStyle1());
            result.addLayer(foregroundFeatureLayer1);
        }

        int imageHeight = getMapContentImageHeight(result, imageWidth);

        // Add a legend
        // ------------
        if (legendItems != null) {
            boolean addLegendToTheSide = true;
            Geotools_LegendLayer ll = new Geotools_LegendLayer(
                    styleParameters,
                    "Legend",
                    legendItems,
                    result,
                    imageWidth,
                    imageHeight,
                    addLegendToTheSide);
            result.addLayer(ll);
        }

        return result;
    }

    // Add foreground0
    // ---------------
    private void addForeground0(
            MapContent result,
            Geotools_StyleParameters styleParameters,
            ArrayList<Geotools_Shapefile> foregroundDW_Shapefile0) {
        if (foregroundDW_Shapefile0 != null) {
            Iterator<Geotools_Shapefile> ite;
            ite = foregroundDW_Shapefile0.iterator();
            int indexx = 0;
            while (ite.hasNext()) {
                Geotools_Shapefile sf = ite.next();
                FeatureLayer foregroundFeatureLayer0;
                foregroundFeatureLayer0 = sf.getFeatureLayer(
                        styleParameters.getForegroundStyle0().get(indexx));
                result.addLayer(foregroundFeatureLayer0);
                indexx++;
            }
        }
    }
}
