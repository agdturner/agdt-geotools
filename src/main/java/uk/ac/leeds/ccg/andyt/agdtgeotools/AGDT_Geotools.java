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
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.generic.visualisation.Generic_Visualisation;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * A class for holding various useful methods for doing things with AGDT_Geotools
 Objects.
 *
 * @author geoagdt
 */
public class AGDT_Geotools {

    public static int getMapContentImageHeight(
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
     * polygon0 PostcodeSector
     * polygon1 OA
     * polygon2 LSOA
     * polygon3 postcodeUnitPoly / MSOA
     * polygon4 polyGrid,
     * line0 lineGrid
     * points0 PostcodeUnitPoint
     * points1 PostcodeSectorPoint
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
    public static MapContent createMapContent(
            AGDT_Shapefile polygon0,
            AGDT_Shapefile polygon1,
            AGDT_Shapefile polygon2,
            AGDT_Shapefile polygon3,
            AGDT_Shapefile polygon4,
            AGDT_Shapefile line0,
            AGDT_Shapefile points0,
            AGDT_Shapefile points1) {
        MapContent result;
        result = new MapContent();

        if (polygon0 != null) {
            // Add polygon layer 0 to mc
            // -------------------------
            Style polygon0Style;
            polygon0Style = AGDT_Style.createDefaultPolygonStyle(
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
            polygon3Style = AGDT_Style.createDefaultPolygonStyle(c, null);
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
            polygon1Style = AGDT_Style.createDefaultPolygonStyle(c, null);
            FeatureLayer polygon1layer = new FeatureLayer(
                    polygon1.getFeatureSource(), polygon1Style);
            result.addLayer(polygon1layer);
        }

        if (polygon2 != null) {
            // Add polygon layer 2 to mc
            // -------------------------
            Style polygon2Style;
            polygon2Style = AGDT_Style.createDefaultPolygonStyle(
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
            polygon4Style = AGDT_Style.createDefaultPolygonStyle(c, null);
            FeatureLayer polygon4layer = new FeatureLayer(
                    polygon4.getFeatureSource(), polygon4Style);
            result.addLayer(polygon4layer);
        }

        if (line0 != null) {
            // Add line layer 0 to mc
            // -------------------------
            Style line0Style;
            line0Style = AGDT_Style.createDefaultLineStyle();
            FeatureLayer line0layer = new FeatureLayer(
                    line0.getFeatureSource(), line0Style);
            result.addLayer(line0layer);
        }

        // Add point layer 0 to mc
        // -----------------------
        Style pointStyle0;
        pointStyle0 = AGDT_Style.createDefaultPointStyle();
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
        pointStyle1 = AGDT_Style.getPointStyle(
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
     * @param mapDirectory
     * @param name
     * @param fileExtension
     * @return File
     */
    public static File getOutputFile(
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
    public static File getOutputShapefile(
            File dir,
            String name) {
        File result;
        File outDirectory = new File(
                dir,
                name);
        outDirectory.mkdirs();
        String shapefileFilename = name + ".shp";
        result = getShapefile(dir, shapefileFilename);
        return result;
    }

    public static File getShapefile(
            File dir,
            String shapefileFilename) {
        File result;
        File shapefileDir;
        shapefileDir = new File(
                dir,
                shapefileFilename);
        // Could add extra logic here to deal with issues if directory or a file 
        // of this name already exists...
        if (!shapefileDir.exists()) {
            shapefileDir.mkdirs();
        }
        result = new File(
                shapefileDir,
                shapefileFilename);
        return result;
    }

//    public static void addFeatureLayer(
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
    public static void compareFeatures(
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
    public static void writeImageFile(
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
            if (ge._HandleOutOfMemoryError_boolean) {
                ge.clear_MemoryReserve();
                ge.swapToFile_Grid2DSquareCellChunk(true);
                ge.init_MemoryReserve(true);
                writeImageFile(
                        ge,
                        mapContent,
                        imageWidth,
                        imageHeight,
                        outputImageFile,
                        outputType);
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
    public static void writeImageFile(
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
        Generic_Visualisation.saveImage(bufferedImage, outputType, outputImageFile);
        graphics2D.dispose();
    }

//    public static GridCoverage2D vectorToRaster(
//            DW_Shapefile polygons,
//            long nrows,
//            long ncols,
//            double xllcorner,
//            double yllcorner,
//            double cellsize) {
//        
//        
//    }
}
