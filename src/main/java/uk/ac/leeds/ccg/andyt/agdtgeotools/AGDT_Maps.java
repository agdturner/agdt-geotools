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

//import uk.ac.leeds.ccg.andyt.agdtgeotools.DW_Shapefile;
//import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.mapping.DW_Shapefile;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.mapping.AGDT_Geotools;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.TreeSetFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.data.census.Deprivation_DataHandler;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.data.census.Deprivation_DataRecord;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.data.postcode.PostcodeGeocoder;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.io.DW_Files;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.process.DW_Processor;

/**
 *
 * @author geoagdt
 */
public abstract class AGDT_Maps {

    public static final String png_String = "png";
    public static final String defaultSRID = "27700";

    private static HashMap<String, SimpleFeatureType> pointSimpleFeatureTypes;
    private static HashMap<String, SimpleFeatureType> lineSimpleFeatureTypes;
    private static HashMap<String, SimpleFeatureType> polygonSimpleFeatureTypes;

    /**
     * If showMapsInJMapPane is true, maps are presented in individual JMapPanes
     */
    protected boolean showMapsInJMapPane;
    protected int imageWidth;
    protected boolean commonStyling;
    protected boolean individualStyling;
    protected ArrayList<String> classificationFunctionNames;
    protected File mapDirectory;
    protected ShapefileDataStoreFactory sdsf;
    protected ArrayList<AGDT_Shapefile> foregroundDW_Shapefile0;
    protected AGDT_Shapefile foregroundDW_Shapefile1;
    protected AGDT_Shapefile backgroundDW_Shapefile;

    public AGDT_Maps() {
    }

    public ShapefileDataStoreFactory getShapefileDataStoreFactory() {
        if (sdsf == null) {
            sdsf = new ShapefileDataStoreFactory();
        }
        return sdsf;
    }

    /**
     * Simple convenience method.
     *
     * @param type
     * @param srid
     * @return null if type is not one of "Point", "LineString", or "Polygon"
     */
    public static SimpleFeatureType getSimpleFeatureType(
            String type,
            String srid) {
        if (srid == null) {
            srid = getDefaultSRID();
        }
        if (type.equalsIgnoreCase("Point")) {
            return getPointSimpleFeatureType(srid);
        }
        if (type.equalsIgnoreCase("LineString")) {
            return getLineSimpleFeatureType(srid);
        }
        if (type.equalsIgnoreCase("Polygon")) {
            return getPolygonSimpleFeatureType(srid);
        }
        return null;
    }

    public static String getDefaultSRID() {
        return defaultSRID;//"27700";
    }

    public static HashMap<String, SimpleFeatureType> getPointSimpleFeatureTypes() {
        if (pointSimpleFeatureTypes == null) {
            pointSimpleFeatureTypes = new HashMap<String, SimpleFeatureType>();
            //pointSimpleFeatureTypes = initPointSimpleFeatureTypes();
        }
        return pointSimpleFeatureTypes;
    }

    public static SimpleFeatureType getPointSimpleFeatureType(String srid) {
        if (!getPointSimpleFeatureTypes().containsKey(srid)) {
            SimpleFeatureType sft;
            sft = initSimpleFeatureType(
                    "Point", srid);
            pointSimpleFeatureTypes.put(
                    srid, sft);
            return sft;
        }
        return pointSimpleFeatureTypes.get(srid);
    }

    private static SimpleFeatureType initSimpleFeatureType(
            String type,
            String srid) {
        SimpleFeatureType result = null;
        try {
            result = DataUtilities.createType(
                    "Location",
                    "the_geom:" + type + ":srid=" + srid + "," + // <- the geometry attribute
                    "name:String," // <- a String attribute
            );
        } catch (SchemaException ex) {
            Logger.getLogger(AGDT_Maps.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static HashMap<String, SimpleFeatureType> getLineSimpleFeatureTypes() {
        if (lineSimpleFeatureTypes == null) {
            lineSimpleFeatureTypes = new HashMap<String, SimpleFeatureType>();
        }
        return lineSimpleFeatureTypes;
    }

    public static SimpleFeatureType getLineSimpleFeatureType(String srid) {
        if (!getLineSimpleFeatureTypes().containsKey(srid)) {
            SimpleFeatureType sft;
            sft = initSimpleFeatureType(
                    "LineString", srid);
            lineSimpleFeatureTypes.put(
                    srid, sft);
            return sft;
        }
        return lineSimpleFeatureTypes.get(srid);
    }

    public static HashMap<String, SimpleFeatureType> getPolygonSimpleFeatureTypes() {
        if (polygonSimpleFeatureTypes == null) {
            polygonSimpleFeatureTypes = new HashMap<String, SimpleFeatureType>();
        }
        return polygonSimpleFeatureTypes;
    }

    public static SimpleFeatureType getPolygonSimpleFeatureType(String srid) {
        if (!getPolygonSimpleFeatureTypes().containsKey(srid)) {
            SimpleFeatureType sft;
            sft = initSimpleFeatureType(
                    "Polygon", srid);
            polygonSimpleFeatureTypes.put(
                    srid, sft);
            return sft;
        }
        return polygonSimpleFeatureTypes.get(srid);
    }

//    /*
//     * Select and create a new shapefile.
//     *
//     * @param sdsf
//     * @param fc
//     * @param sft
//     * @param codesToSelect
//     * @param targetPropertyName
//     * @param outputFile
//     */
//    public static void selectAndCreateNewShapefile(
//            ShapefileDataStoreFactory sdsf,
//            FeatureCollection fc,
//            SimpleFeatureType sft,
//            TreeSet<String> codesToSelect,
//            //String attributeName, 
//            String targetPropertyName,
//            File outputFile) {
//        // Initialise the collection of new Features
//        TreeSetFeatureCollection tsfc;
//        tsfc = new TreeSetFeatureCollection();
//        // Create SimpleFeatureBuilder
//        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
//        SimpleFeatureBuilder sfb;
//        sfb = new SimpleFeatureBuilder(sft);
//        FeatureIterator featureIterator;
//        featureIterator = fc.features();
//        int id_int = 0;
//        while (featureIterator.hasNext()) {
//            Feature inputFeature = featureIterator.next();
//            Collection<Property> properties;
//            properties = inputFeature.getProperties();
//            Iterator<Property> itep = properties.iterator();
//            while (itep.hasNext()) {
//                Property p = itep.next();
//                //System.out.println("Property " + p.toString());
//                String propertyName = p.getName().toString();
//                //System.out.println("PropertyName " + propertyName);
//                if (propertyName.equalsIgnoreCase(targetPropertyName)) {
//                    //PropertyType propertyType = p.getType();
//                    //System.out.println("PropertyType " + propertyType);
//                    Object value = p.getValue();
//                    //System.out.println("PropertyValue " + value);
//                    String valueString = value.toString();
//                    if (codesToSelect.contains(valueString)) {
//                        if (valueString.trim().equalsIgnoreCase("E02002337")) {
//                            int debug = 1;
//                        }
//                        String id = "" + id_int;
//                        addFeatureToFeatureCollection(
//                                (SimpleFeature) inputFeature,
//                                sfb,
//                                tsfc,
//                                id);
//                        id_int++;
//                    } else {
////                        System.out.println(valueString);
//                    }
//                }
//            }
//        }
//        featureIterator.close();
//        AGDT_Shapefile.transact(outputFile, sft, tsfc, sdsf);
//    }
    public SimpleFeatureType getFeatureType(
            SimpleFeatureType sft,
            Class<?> binding,
            String attributeName,
            String name) {
        SimpleFeatureType result = null;
        SimpleFeatureTypeBuilder sftb;
        sftb = getNewSimpleFeatureTypeBuilder(sft, name);
        if (binding.equals(Integer.class)) {
            // Add the new attribute
            sftb.add(attributeName, Integer.class);
        }
        if (binding.equals(Double.class)) {
            // Add the new attribute
            sftb.add(attributeName, Double.class);
        }
        result = sftb.buildFeatureType();
        return result;
    }

    public static File getOutputImageFile(
            File outputFile,
            String outputType) {
        File result;
        String filename = outputFile.getName();
        String outputImageFilename;
        outputImageFilename = filename.substring(0, filename.length() - 4)
                + "." + outputType;
        result = new File(
                outputFile.getParent(),
                outputImageFilename);
        return result;
    }

    /**
     *
     * @param sft
     * @param name
     * @return
     */
    public SimpleFeatureTypeBuilder getNewSimpleFeatureTypeBuilder(
            SimpleFeatureType sft,
            String name) {
        SimpleFeatureTypeBuilder result = new SimpleFeatureTypeBuilder();
        result.init(sft);
        result.setName(name);
        return result;
    }

    public void summariseAttributes(SimpleFeatureType sft) {
        int attributeCount = sft.getAttributeCount();
        System.out.println("attributeIndex,attributeDescriptor");
        for (int i = 0; i < attributeCount; i++) {
            AttributeDescriptor attributeDescriptor;
            attributeDescriptor = sft.getDescriptor(i);
            System.out.println("" + i + "," + attributeDescriptor.getLocalName());
        }
    }

    /**
     * Adds sf attributes and value to sfb and builds a new SimpleFeature with
     * id.
     *
     * @param sf The SimpleFeature from which the new SimpleFeature is based.
     * @param sfb The SimpleFeatureBuilder for building the new SimpleFeature.
     * @param value The value to be assigned as an additional attribute in the
     * new SimpleFeature
     * @param fc The TreeSetFeatureCollection to which the new SimpleFeature is
     * added.
     * @param id Null permitted. This will be the id assigned to the built
     * feature.
     */
    public static void addFeatureAttributeAndAddToFeatureCollection(
            SimpleFeature sf,
            SimpleFeatureBuilder sfb,
            Integer value,
            TreeSetFeatureCollection fc,
            String id) {
        sfb.addAll(sf.getAttributes());
        sfb.add(value);
        fc.add(sfb.buildFeature(id));
    }

    /**
     * Adds sf attributes and value to sfb and builds a new SimpleFeature with
     * id.
     *
     * @param sf The SimpleFeature from which the new SimpleFeature is based.
     * @param sfb The SimpleFeatureBuilder for building the new SimpleFeature.
     * @param value The value to be assigned as an additional attribute in the
     * new SimpleFeature
     * @param fc The TreeSetFeatureCollection to which the new SimpleFeature is
     * added.
     * @param id Null permitted. This will be the id assigned to the built
     * feature.
     */
    public static void addFeatureAttributeAndAddToFeatureCollection(
            SimpleFeature sf,
            SimpleFeatureBuilder sfb,
            Double value,
            TreeSetFeatureCollection fc,
            String id) {
        sfb.addAll(sf.getAttributes());
        sfb.add(value);
        fc.add(sfb.buildFeature(id));
    }

    /**
     * Adds sf attributes to sfb and builds a new SimpleFeature with id.
     *
     * @param sf The SimpleFeature from which the new SimpleFeature is based.
     * @param sfb The SimpleFeatureBuilder for building the new SimpleFeature.
     * @param fc The TreeSetFeatureCollection to which the new SimpleFeature is
     * added.
     * @param id Null permitted. This will be the id assigned to the built
     * feature.
     */
    public static void addFeatureToFeatureCollection(
            SimpleFeature sf,
            SimpleFeatureBuilder sfb,
            TreeSetFeatureCollection fc,
            String id) {
        sfb.addAll(sf.getAttributes());
        fc.add(sfb.buildFeature(id));
    }

//    public SimpleFeatureType getOutputSimpleFeatureType(
//            SimpleFeatureType inputSimpleFeatureType) {
//        SimpleFeatureType result;
//        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
//        b.setName("Name");
//        //b.add(inType.getName());
//        //b.add("Name", org.opengis.feature.type.Name.class);
//        //b.add(Name);
//        //b.add("name", org.opengis.feature.type.Name.class);
//        //b.add("Name", PropertyType.class);
//        b.add(inputSimpleFeatureType.getGeometryDescriptor());
//        b.addAll(inputSimpleFeatureType.getAttributeDescriptors());
//        //b.add("clients", Integer.class);
//        b.add("clients", String.class);
//        result = b.buildFeatureType();
//        return result;
//    }
    /**
     * @param dir
     * @param filenames
     * @param omit This is a list of which filename indexes to omit from getting
     * the data of.
     * @return A Object[] result where: ----------------------------------------
     * result[0] is an Object[] with the same length as filenames.length where
     * each element i is the respective TreeMap<String, Integer> returned from
     * getLevelData(digitalWelfareDir,filenames[i]);
     * ---------------------------- result[1] is the max of all the maximum
     * counts.
     */
    protected static Object[] getLevelData(
            File dir,
            String[] filenames,
            ArrayList<Integer> omit) {
        Object[] result = new Object[2];
        int length = filenames.length;
        Object[] resultPart0 = new Object[length];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            boolean doLevel;
            doLevel = true;
            if (omit != null) {
                if (omit.contains(i)) {
                    doLevel = false;
                }
            }
            if (doLevel) {
                Object[] levelData = getLevelData(
                        dir,
                        filenames[i]);
                resultPart0[i] = levelData;
                max = Math.max(max, (Integer) levelData[1]);
            }
        }
        result[0] = resultPart0;
        result[1] = max;
        return result;
    }

    /**
     * @param dir
     * @param filename
     * @return An Object[] result where: ---------------------------------------
     * result[0] is a TreeMap<String, Integer> with keys which are DW_Census
     * Codes and values that are counts;
     * --------------------------------------------- result[1] is the max count.
     */
    protected static Object[] getLevelData(
            File dir,
            String filename) {
        Object[] result = new Object[2];
        TreeMap<String, Integer> map = new TreeMap<String, Integer>();
        result[0] = map;
        File file = new File(
                dir,
                filename);

//        System.out.println("Reading data from file " + file);
        try {
            BufferedReader br = Generic_StaticIO.getBufferedReader(file);
            StreamTokenizer st = new StreamTokenizer(br);
            Generic_StaticIO.setStreamTokenizerSyntax1(st);
            int token = st.nextToken();
            //Need skip some header lines
            st.nextToken();
            st.nextToken();
//            st.nextToken();
//            st.nextToken();
            int max = Integer.MIN_VALUE;
            long RecordID = 0;
            String line = "";
            while (!(token == StreamTokenizer.TT_EOF)) {
                switch (token) {
                    case StreamTokenizer.TT_EOL:
//                        if (RecordID % 100 == 0) {
//                            System.out.println(line);
//                        }
                        RecordID++;
                        break;
                    case StreamTokenizer.TT_WORD:
                        line = st.sval;
                        if (line != null) {
                            String[] split = line.split(",");
                            if (split.length > 1) {
                                String n = split[1].trim();
                                Integer value;
                                if (n.equalsIgnoreCase("null")) {
                                    value = 0;
                                } else {
                                    value = new Integer(split[1].trim());
                                }
                                map.put(split[0], value);
                                max = Math.max(max, value);
                            }
                        }
                        break;
                }
                token = st.nextToken();
            }
            result[1] = max;
            br.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

//    /*
//     * Select and create a new shapefile.
//     *
//     * @param fc
//     * @param sft
//     * @param tLSOACodes
//     * @param tLSOAData
//     * @param attributeName
//     * @param outputFile
//     * @param max
//     * @param filter If filter == true then result is clipped to the LSOA
//     * boundary.
//     */
//    public TreeMap<Integer, Integer> selectAndCreateNewShapefile(
//            ShapefileDataStoreFactory sdsf,
//            FeatureCollection fc,
//            SimpleFeatureType sft,
//            TreeSet<String> levelCodes,
//            TreeMap<String, Integer> levelData,
//            //String attributeName, 
//            String targetPropertyName,
//            File outputFile,
//            int filter,
//            boolean countClientsInAndOutOfRegion) {
//
//        TreeMap<Integer, Integer> inAndOutOfRegionCount = null;
//        if (countClientsInAndOutOfRegion) {
//            inAndOutOfRegionCount = new TreeMap<Integer, Integer>();
//            inAndOutOfRegionCount.put(0, 0);
//            inAndOutOfRegionCount.put(1, 0);
//        }
//
//        //        summariseAttributes(sft);
//        // Initialise the collection of new Features
//        TreeSetFeatureCollection tsfc;
//        tsfc = new TreeSetFeatureCollection();
//        // Create SimpleFeatureBuilder
//        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
//        SimpleFeatureBuilder sfb;
//        sfb = new SimpleFeatureBuilder(sft);
//        Set<String> keySet = levelData.keySet();
//        FeatureIterator featureIterator;
//        featureIterator = fc.features();
//        int id_int = 0;
//        while (featureIterator.hasNext()) {
//            Feature inputFeature = featureIterator.next();
//            Collection<Property> properties;
//            properties = inputFeature.getProperties();
//            Iterator<Property> itep = properties.iterator();
//            while (itep.hasNext()) {
//                Property p = itep.next();
//                //System.out.println("Property " + p.toString());
//                String propertyName = p.getName().toString();
//                //System.out.println("PropertyName " + propertyName);
//                if (propertyName.equalsIgnoreCase(targetPropertyName)) {
//                    //PropertyType propertyType = p.getType();
//                    //System.out.println("PropertyType " + propertyType);
//                    Object value = p.getValue();
//                    //System.out.println("PropertyValue " + value);
//                    String valueString = value.toString();
//                    if (filter < 3) {
//                        if (levelCodes.contains(valueString)) {
//                            // Add to inAndOutOfRegionCount
//                            Integer clientCount = levelData.get(valueString);
//                            //                            if (clientCount == null) {
//                            //                                clientCount = 0;
//                            //                            }
//                            if (clientCount != null) {
//                                String id = "" + id_int;
//                                addFeatureAttributeAndAddToFeatureCollection(
//                                        (SimpleFeature) inputFeature, sfb, clientCount, tsfc, id);
//                                id_int++;
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount, 1, clientCount);
//                                }
//                            } else {
//                                int debug = 1;
//                            }
//                        } else {
//                            // Add to inAndOutOfRegionCount
//                            Integer clientCount = levelData.get(valueString);
//                            if (clientCount != null) {
//                                // Add to inAndOutOfRegionCount
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount, 0, clientCount);
//                                }
//                            } else {
//                                int debug = 1;
//                            }
//                        }
//                    } else {
//                        if (keySet.contains(valueString) || levelCodes.contains(valueString)) {
//                            Integer clientCount = levelData.get(valueString);
//                            //                            if (clientCount == null) {
//                            //                                clientCount = 0;
//                            //                            }
//                            if (clientCount != null) {
//                                String id = "" + id_int;
//                                addFeatureAttributeAndAddToFeatureCollection(
//                                        (SimpleFeature) inputFeature, sfb, clientCount, tsfc, id);
//                                id_int++;
//                                // Add to inAndOutOfRegionCount
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount, 1, clientCount);
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        featureIterator.close();
//        AGDT_Shapefile.transact(outputFile, sft, tsfc, sdsf);
//        return inAndOutOfRegionCount;
//    }
//    /*
//     * Select and create a new shapefile.
//     *
//     * @param fc
//     * @param sft
//     * @param tLSOACodes
//     * @param tLSOAData
//     * @param attributeName
//     * @param outputFile
//     * @param max
//     * @param filter
//     * boundary.
//     */
//    public TreeMap<Integer, Integer> selectAndCreateNewShapefile(
//            ShapefileDataStoreFactory sdsf,
//            FeatureCollection fc,
//            SimpleFeatureType sft,
//            TreeSet<String> levelCodes,
//            TreeMap<String, Integer> levelData,
//            //String attributeName, 
//            String targetPropertyName,
//            File outputFile,
//            int filter,
//            boolean countClientsInAndOutOfRegion,
//            boolean density) {
//
//        TreeMap<Integer, Integer> inAndOutOfRegionCount = null;
//        if (countClientsInAndOutOfRegion) {
//            inAndOutOfRegionCount = new TreeMap<Integer, Integer>();
//            inAndOutOfRegionCount.put(0, 0);
//            inAndOutOfRegionCount.put(1, 0);
//        }
//
//        //        summariseAttributes(sft);
//        // Initialise the collection of new Features
//        TreeSetFeatureCollection tsfc;
//        tsfc = new TreeSetFeatureCollection();
//        // Create SimpleFeatureBuilder
//        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
//        SimpleFeatureBuilder sfb;
//        sfb = new SimpleFeatureBuilder(sft);
//        Set<String> keySet = levelData.keySet();
//        FeatureIterator featureIterator;
//        featureIterator = fc.features();
//        int id_int = 0;
//        while (featureIterator.hasNext()) {
//            Feature inputFeature = featureIterator.next();
//            Collection<Property> properties;
//            properties = inputFeature.getProperties();
//            Iterator<Property> itep;
//            // get Area
//            double area = 0;
//            itep = properties.iterator();
//            while (itep.hasNext()) {
//                Property p = itep.next();
//                //System.out.println("Property " + p.toString());
//                String propertyName = p.getName().toString();
//                //System.out.println("PropertyName " + propertyName);
////                PropertyDescriptor pd;
////                pd = p.getDescriptor();
//                if (propertyName.equalsIgnoreCase("the_geom")) {
//                    Geometry g;
//                    g = (Geometry) p.getValue();
//                    area = g.getArea();
//                    try {
//                        Polygon poly;
//                        poly = (Polygon) g;
//                        area = poly.getArea();
//                    } catch (ClassCastException e) {
//                        int debug = 1;
//                    }
//                    try {
//                        MultiPolygon multipoly;
//                        multipoly = (MultiPolygon) g;
//                        area = multipoly.getArea();
//                    } catch (ClassCastException e) {
//                        int debug = 1;
//                    }
//                }
//            }
//            itep = properties.iterator();
//            while (itep.hasNext()) {
//                Property p = itep.next();
//                //System.out.println("Property " + p.toString());
//                String propertyName = p.getName().toString();
//                //System.out.println("PropertyName " + propertyName);
//                if (propertyName.equalsIgnoreCase(targetPropertyName)) {
//                    //PropertyType propertyType = p.getType();
//                    //System.out.println("PropertyType " + propertyType);
//                    Object value = p.getValue();
//                    //System.out.println("PropertyValue " + value);
//                    String valueString = value.toString();
//                    if (filter < 3) {
//                        if (levelCodes.contains(valueString)) {
//                            // Add to inAndOutOfRegionCount
//                            Integer clientCount = levelData.get(valueString);
//                            //                            if (clientCount == null) {
//                            //                                clientCount = 0;
//                            //                            }
//                            if (clientCount != null) {
//                                double densityValue;
//                                densityValue = (clientCount * 1000000) / area; // * 1000000 for 100 hectares
//                                String id = "" + id_int;
//                                addFeatureAttributeAndAddToFeatureCollection(
//                                        (SimpleFeature) inputFeature,
//                                        sfb, densityValue, tsfc, id);
//                                id_int++;
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount,
//                                            1, clientCount);
//                                }
//                            } else {
//                                int debug = 1;
//                            }
//                        } else {
//                            // Add to inAndOutOfRegionCount
//                            Integer clientCount = levelData.get(valueString);
//                            if (clientCount != null) {
//                                // Add to inAndOutOfRegionCount
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount, 0, clientCount);
//                                }
//                            } else {
//                                int debug = 1;
//                            }
//                        }
//                    } else {
//                        if (keySet.contains(valueString) || levelCodes.contains(valueString)) {
//                            Integer clientCount = levelData.get(valueString);
//                            //                            if (clientCount == null) {
//                            //                                clientCount = 0;
//                            //                            }
//                            if (clientCount != null) {
//                                double densityValue;
//                                densityValue = (clientCount * 1000000) / area; // * 1000000 for 100 hectares
//                                String id = "" + id_int;
//                                addFeatureAttributeAndAddToFeatureCollection(
//                                        (SimpleFeature) inputFeature, sfb, densityValue, tsfc, id);
//                                id_int++;
//                                // Add to inAndOutOfRegionCount
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount, 1, clientCount);
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        featureIterator.close();
//        AGDT_Shapefile.transact(outputFile, sft, tsfc, sdsf);
//        return inAndOutOfRegionCount;
//    }
//    /*
//     * Select and create a new shapefile.
//     *
//     * @param fc
//     * @param sft
//     * @param tLSOACodes
//     * @param tLSOAData
//     * @param attributeName
//     * @param outputFile
//     * @param max
//     * @param filter
//     * boundary.
//     */
//    public TreeMap<Integer, Integer> selectAndCreateNewShapefile(
//            ShapefileDataStoreFactory sdsf,
//            FeatureCollection fc,
//            SimpleFeatureType sft,
//            TreeSet<String> levelCodes,
//            TreeMap<String, Integer> levelData,
//            //String attributeName, 
//            String targetPropertyName,
//            File outputFile,
//            boolean countClientsInAndOutOfRegion,
//            boolean density) {
//
//        TreeMap<Integer, Integer> inAndOutOfRegionCount = null;
//        if (countClientsInAndOutOfRegion) {
//            inAndOutOfRegionCount = new TreeMap<Integer, Integer>();
//            inAndOutOfRegionCount.put(0, 0);
//            inAndOutOfRegionCount.put(1, 0);
//        }
//
//        //        summariseAttributes(sft);
//        // Initialise the collection of new Features
//        TreeSetFeatureCollection tsfc;
//        tsfc = new TreeSetFeatureCollection();
//        // Create SimpleFeatureBuilder
//        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
//        SimpleFeatureBuilder sfb;
//        sfb = new SimpleFeatureBuilder(sft);
//        Set<String> keySet = levelData.keySet();
//        FeatureIterator featureIterator;
//        featureIterator = fc.features();
//        int id_int = 0;
//        while (featureIterator.hasNext()) {
//            Feature inputFeature = featureIterator.next();
//            Collection<Property> properties;
//            properties = inputFeature.getProperties();
//            Iterator<Property> itep;
//            // get Area
//            double area = 0;
//            itep = properties.iterator();
//            while (itep.hasNext()) {
//                Property p = itep.next();
//                //System.out.println("Property " + p.toString());
//                String propertyName = p.getName().toString();
//                //System.out.println("PropertyName " + propertyName);
////                PropertyDescriptor pd;
////                pd = p.getDescriptor();
//                if (propertyName.equalsIgnoreCase("the_geom")) {
//                    Geometry g;
//                    g = (Geometry) p.getValue();
//                    area = g.getArea();
//                    try {
//                        Polygon poly;
//                        poly = (Polygon) g;
//                        area = poly.getArea();
//                    } catch (ClassCastException e) {
//                        int debug = 1;
//                    }
//                    try {
//                        MultiPolygon multipoly;
//                        multipoly = (MultiPolygon) g;
//                        area = multipoly.getArea();
//                    } catch (ClassCastException e) {
//                        int debug = 1;
//                    }
//                }
//            }
//            itep = properties.iterator();
//            while (itep.hasNext()) {
//                Property p = itep.next();
//                //System.out.println("Property " + p.toString());
//                String propertyName = p.getName().toString();
//                //System.out.println("PropertyName " + propertyName);
//                if (propertyName.equalsIgnoreCase(targetPropertyName)) {
//                    //PropertyType propertyType = p.getType();
//                    //System.out.println("PropertyType " + propertyType);
//                    Object value = p.getValue();
//                    //System.out.println("PropertyValue " + value);
//                    String valueString = value.toString();
//                    // Always filter if (filter) {
//                        if (levelCodes.contains(valueString)) {
//                            // Add to inAndOutOfRegionCount
//                            Integer clientCount = levelData.get(valueString);
//                            //                            if (clientCount == null) {
//                            //                                clientCount = 0;
//                            //                            }
//                            if (clientCount != null) {
//                                double densityValue;
//                                densityValue = (clientCount * 1000000) / area; // * 1000000 for 100 hectares
//                                String id = "" + id_int;
//                                addFeatureAttributeAndAddToFeatureCollection(
//                                        (SimpleFeature) inputFeature,
//                                        sfb, densityValue, tsfc, id);
//                                id_int++;
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount,
//                                            1, clientCount);
//                                }
//                            } else {
//                                int debug = 1;
//                            }
//                        } else {
//                            // Add to inAndOutOfRegionCount
//                            Integer clientCount = levelData.get(valueString);
//                            if (clientCount != null) {
//                                // Add to inAndOutOfRegionCount
//                                if (countClientsInAndOutOfRegion) {
//                                    Generic_Collections.addToTreeMapIntegerInteger(
//                                            inAndOutOfRegionCount, 0, clientCount);
//                                }
//                            } else {
//                                int debug = 1;
//                            }
//                        }
////                    } else {
////                        if (keySet.contains(valueString) || levelCodes.contains(valueString)) {
////                            Integer clientCount = levelData.get(valueString);
////                            //                            if (clientCount == null) {
////                            //                                clientCount = 0;
////                            //                            }
////                            if (clientCount != null) {
////                                double densityValue;
////                                densityValue = (clientCount * 1000000) / area; // * 1000000 for 100 hectares
////                                String id = "" + id_int;
////                                addFeatureAttributeAndAddToFeatureCollection(
////                                        (SimpleFeature) inputFeature, sfb, densityValue, tsfc, id);
////                                id_int++;
////                                // Add to inAndOutOfRegionCount
////                                if (countClientsInAndOutOfRegion) {
////                                    Generic_Collections.addToTreeMapIntegerInteger(
////                                            inAndOutOfRegionCount, 1, clientCount);
////                                }
////
////                            }
////                        }
////                    }
//                }
//            }
//        }
//        featureIterator.close();
//        AGDT_Shapefile.transact(outputFile, sft, tsfc, sdsf);
//        return inAndOutOfRegionCount;
//    }
//    /*
//     * Get an output file name and create the new shapefile
//     */
//    public void selectAndCreateNewShapefile(
//            ShapefileDataStoreFactory sdsf,
//            FeatureCollection fc,
//            SimpleFeatureType sft,
//            TreeSet<String> levelCodes,
//            TreeMap<String, Integer> levelData,
//            TreeMap<String, Integer> pop,
//            double multiplier,
//            //String attributeName,
//            String targetPropertyName,
//            File outputFile,
//            int max,
//            int filter) {
//        TreeSetFeatureCollection tsfc;
//        tsfc = new TreeSetFeatureCollection();
//        // Create SimpleFeatureBuilder
//        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
//        SimpleFeatureBuilder sfb;
//        sfb = new SimpleFeatureBuilder(sft);
//        int id_int = 0;
//        FeatureIterator featureIterator;
//        featureIterator = fc.features();
//        Set<String> keySet = levelData.keySet();
//        while (featureIterator.hasNext()) {
//            Feature inputFeature = featureIterator.next();
//            Collection<Property> properties;
//            properties = inputFeature.getProperties();
//            Iterator<Property> itep = properties.iterator();
//            while (itep.hasNext()) {
//                Property p = itep.next();
//                //System.out.println("Property " + p.toString());
//                String propertyName = p.getName().toString();
//                //System.out.println("PropertyName " + propertyName);
//                if (propertyName.equalsIgnoreCase(targetPropertyName)) {
//                    //PropertyType propertyType = p.getType();
//                    //System.out.println("PropertyType " + propertyType);
//                    Object value = p.getValue();
//                    //System.out.println("PropertyValue " + value);
//                    String valueString = value.toString();
//                    if (filter < 3) {
//                        if (levelCodes.contains(valueString)) {
//                            Integer clientCount = levelData.get(valueString);
//                            //                            if (clientCount == null) {
//                            //                                clientCount = 0;
//                            //                            }
//                            if (clientCount != null) {
//                                Integer population = pop.get(valueString);
//                                double clientRate = (double) clientCount * multiplier / (double) population;
//                                String id = "" + id_int;
//                                addFeatureAttributeAndAddToFeatureCollection(
//                                        (SimpleFeature) inputFeature, sfb, clientRate, tsfc, id);
//                                id_int++;
//                            }
//                        }
//                    } else {
//                        if (keySet.contains(valueString) || levelCodes.contains(valueString)) {
//                            Integer clientCount = levelData.get(valueString);
//                            //                            if (clientCount == null) {
//                            //                                clientCount = 0;
//                            //                            }
//                            if (clientCount != null) {
//                                Integer population = pop.get(valueString);
//                                double clientRate = (double) clientCount * multiplier / (double) population;
//                                String id = "" + id_int;
//                                addFeatureAttributeAndAddToFeatureCollection(
//                                        (SimpleFeature) inputFeature, sfb, clientRate, tsfc, id);
//                                id_int++;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        featureIterator.close();
//        AGDT_Shapefile.transact(outputFile, sft, tsfc, sdsf);
//    }
    public String getOutName(String filename, String attributeName, int filter) {
        String result = filename.substring(0, filename.length() - 4);
        result += attributeName;
        if (filter == 0) {
            result += "ClippedToLeedsLAD";
        }
        if (filter == 1) {
            result += "ClippedToLeedsAndNeighbouringLADs";
        }
        if (filter == 2) {
            result += "ClippedToLeedsAndNearNeighbouringLADs";
        }
        if (filter == 3) {
            result += "";
        }
        return result;
    }

    protected static void addPointFeature(
            AGDT_Point p,
            GeometryFactory gf,
            SimpleFeatureBuilder sfb,
            String name,
            TreeSetFeatureCollection tsfc) {
        Point point = gf.createPoint(new Coordinate(p.getX(), p.getY()));
        sfb.add(point);
        sfb.add(name);
        SimpleFeature feature = sfb.buildFeature(null);
        tsfc.add(feature);
    }

    protected static void addLineFeature(
            AGDT_Point p1,
            AGDT_Point p2,
            GeometryFactory gf,
            SimpleFeatureBuilder sfb,
            String name,
            TreeSetFeatureCollection tsfc) {
        Coordinate[] coordinates;
        coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(p1.getX(), p1.getY());
        coordinates[1] = new Coordinate(p2.getX(), p2.getY());
        LineString line = gf.createLineString(coordinates);
        sfb.add(line);
        sfb.add(name);
        SimpleFeature feature = sfb.buildFeature(null);
        tsfc.add(feature);
    }

    protected static void addQuadFeature(
            AGDT_Point p1,
            AGDT_Point p2,
            AGDT_Point p3,
            AGDT_Point p4,
            GeometryFactory gf,
            SimpleFeatureBuilder sfb,
            String name,
            TreeSetFeatureCollection tsfc,
            CoordinateSequenceFactory csf) {
        Coordinate[] coordinates;
        coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(p1.getX(), p1.getY());
        coordinates[1] = new Coordinate(p2.getX(), p2.getY());
        coordinates[2] = new Coordinate(p3.getX(), p3.getY());
        coordinates[3] = new Coordinate(p4.getX(), p4.getY());
        coordinates[4] = new Coordinate(p1.getX(), p1.getY());
        CoordinateSequence cs;
        cs = csf.create(coordinates);
        LinearRing lr;
        lr = new LinearRing(cs, gf);
        Polygon quad;
        //quad = gf.createPolygon(coordinates);
        quad = gf.createPolygon(lr, null);
        sfb.add(quad);
        sfb.add(name);
        SimpleFeature feature = sfb.buildFeature(null);
        tsfc.add(feature);
    }

    public static TreeSetFeatureCollection getLineGridFeatureCollection(
            SimpleFeatureType sft,
            long nrows,
            long ncols,
            double xllcorner,
            double yllcorner,
            double cellsize) {
        TreeSetFeatureCollection result;
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        result = getLineGridFeatureCollection(
                sft,
                nrows,
                ncols,
                xllcorner,
                yllcorner,
                cellsize,
                gf,
                sfb);
        return result;
    }

    public static TreeSetFeatureCollection getLineGridFeatureCollection(
            SimpleFeatureType sft,
            long nrows,
            long ncols,
            double xllcorner,
            double yllcorner,
            double cellsize,
            GeometryFactory gf,
            SimpleFeatureBuilder sfb) {
        TreeSetFeatureCollection result;
        result = new TreeSetFeatureCollection();
        // add row lines
        for (long row = 0; row <= nrows; row++) {
            double y;
            y = yllcorner + (row * cellsize);
            double x;
            x = xllcorner + (ncols * cellsize);
            AGDT_Point p1;
            AGDT_Point p2;
            p1 = new AGDT_Point((int) xllcorner, (int) y);
            p2 = new AGDT_Point((int) x, (int) y);
            addLineFeature(p1, p2, gf, sfb, "", result);
        }
        // add col lines
        for (long col = 0; col <= ncols; col++) {
            double x;
            x = xllcorner + (col * cellsize);
            AGDT_Point p1;
            AGDT_Point p2;
            double y;
            y = yllcorner + (nrows * cellsize);
            p1 = new AGDT_Point((int) x, (int) yllcorner);
            p2 = new AGDT_Point((int) x, (int) y);
            addLineFeature(p1, p2, gf, sfb, "", result);
        }
        return result;
    }

    public static TreeSetFeatureCollection getPolyGridFeatureCollection(
            SimpleFeatureType sft,
            long nrows,
            long ncols,
            double xllcorner,
            double yllcorner,
            double cellsize) {
        TreeSetFeatureCollection result;
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        result = getPolyGridFeatureCollection(
                sft,
                nrows,
                ncols,
                xllcorner,
                yllcorner,
                cellsize,
                gf,
                sfb);
        return result;
    }

    public static TreeSetFeatureCollection getPolyGridFeatureCollection(
            SimpleFeatureType sft,
            long nrows,
            long ncols,
            double xllcorner,
            double yllcorner,
            double cellsize,
            GeometryFactory gf,
            SimpleFeatureBuilder sfb) {
        TreeSetFeatureCollection result;
        result = new TreeSetFeatureCollection();
        CoordinateSequenceFactory csf;
        csf = CoordinateArraySequenceFactory.instance();
        // add row lines
        for (long row = 0; row < nrows; row++) {
            for (long col = 0; col < ncols; col++) {
                double y1;
                double y2;
                double x1;
                double x2;
                y1 = yllcorner + (row * cellsize);
                y2 = y1 + cellsize;
                x1 = xllcorner + (col * cellsize);
                x2 = x1 + cellsize;
                AGDT_Point p1;
                AGDT_Point p2;
                AGDT_Point p3;
                AGDT_Point p4;
                p1 = new AGDT_Point((int) x1, (int) y1);
                p2 = new AGDT_Point((int) x2, (int) y1);
                p3 = new AGDT_Point((int) x2, (int) y2);
                p4 = new AGDT_Point((int) x1, (int) y2);
                addQuadFeature(p1, p2, p3, p4, gf, sfb, "", result, csf);
            }
        }
        return result;
    }

    /**
     * @param dir The directory in which the shapefile is stored.
     * @param shapefileFilename The shapefile filename.
     * @param fc The feature collection to be turned into a shapefile.
     * @param sft
     * @return shapefile File
     */
    public static File createShapefileIfItDoesNotExist(
            File dir,
            String shapefileFilename,
            TreeSetFeatureCollection fc,
            SimpleFeatureType sft) {
        File result;
        ShapefileDataStoreFactory sdsf;
        sdsf = new ShapefileDataStoreFactory();
        result = createShapefileIfItDoesNotExist(
                dir,
                shapefileFilename,
                fc,
                sft,
                sdsf);
        return result;
    }

    /**
     * @param dir The directory in which the shapefile is stored.
     * @param shapefileFilename The shapefile filename.
     * @param fc The feature collection to be turned into a shapefile.
     * @param sft
     * @param sdsf
     * @return shapefile File
     */
    public static File createShapefileIfItDoesNotExist(
            File dir,
            String shapefileFilename,
            TreeSetFeatureCollection fc,
            SimpleFeatureType sft,
            ShapefileDataStoreFactory sdsf) {
        File result;
        result = AGDT_Geotools.getShapefile(dir, shapefileFilename);
        if (!result.exists()) {
            AGDT_Shapefile.transact(
                    result,
                    sft,
                    fc,
                    sdsf);
        }
        return result;
    }

    public static File createLineGridShapefileIfItDoesNotExist(
            File dir,
            String shapefileFilename, // Better to internally generate this from other parameters?
            long nrows,
            long ncols,
            double xllcorner,
            double yllcorner,
            double cellsize) {
        File result;
        SimpleFeatureType sft;
        sft = getLineSimpleFeatureType(getDefaultSRID());
        TreeSetFeatureCollection fc;
        fc = AGDT_Maps.getLineGridFeatureCollection(
                sft,
                nrows,
                ncols,
                xllcorner,
                yllcorner,
                cellsize);
        result = createShapefileIfItDoesNotExist(
                dir,
                shapefileFilename,
                fc,
                sft);
        return result;
    }

    public static File createPolyGridShapefileIfItDoesNotExist(
            File dir,
            String shapefileFilename, // Better to internally generate this from other parameters?
            long nrows,
            long ncols,
            double xllcorner,
            double yllcorner,
            double cellsize) {
        File result;
        SimpleFeatureType sft;
        sft = getPolygonSimpleFeatureType(getDefaultSRID());
        TreeSetFeatureCollection fc;
        fc = getPolyGridFeatureCollection(
                sft,
                nrows,
                ncols,
                xllcorner,
                yllcorner,
                cellsize);
        result = createShapefileIfItDoesNotExist(
                dir,
                shapefileFilename,
                fc,
                sft);
        return result;
    }

    /**
     * @param f
     * @return ArcGridReader
     */
    public static ArcGridReader getArcGridReader(File f) {
        ArcGridReader result = null;
        try {
            result = new ArcGridReader(f);
        } catch (DataSourceException ex) {
            Logger.getLogger(AGDT_Maps.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("f.toString()" + f.toString());
        }
        return result;
    }

    /**
     *
     * @param agr
     * @return
     */
    public static GridCoverage2D getGridCoverage2D(
            ArcGridReader agr) {
        GridCoverage2D result = null;
        try {
            if (agr != null) {
                result = agr.read(null);
            }
        } catch (IOException ex) {
            Logger.getLogger(AGDT_Maps.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
