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
package uk.ac.leeds.ccg.andyt.geotools;

import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Object;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Strings;

/**
 *
 * @author geoagdt
 */
public class Geotools_Maps extends Geotools_Object {

    private HashMap<String, SimpleFeatureType> pointSimpleFeatureTypes;
    private HashMap<String, SimpleFeatureType> lineSimpleFeatureTypes;
    private HashMap<String, SimpleFeatureType> polygonSimpleFeatureTypes;

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
    protected ArrayList<Geotools_Shapefile> foregroundDW_Shapefile0;
    protected Geotools_Shapefile ForegroundDW_Shapefile1;
    protected Geotools_Shapefile BackgroundDW_Shapefile;

    protected Geotools_Maps() {
    }

    public Geotools_Maps(Geotools_Environment ge) {
        super(ge);
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
    public SimpleFeatureType getSimpleFeatureType(String type, String srid) {
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

    public String getDefaultSRID() {
        return Geotools_Strings.defaultSRID;//"27700";
    }

    public HashMap<String, SimpleFeatureType> getPointSimpleFeatureTypes() {
        if (pointSimpleFeatureTypes == null) {
            pointSimpleFeatureTypes = new HashMap<>();
            //pointSimpleFeatureTypes = initPointSimpleFeatureTypes();
        }
        return pointSimpleFeatureTypes;
    }

    public SimpleFeatureType getPointSimpleFeatureType(String srid) {
        if (!getPointSimpleFeatureTypes().containsKey(srid)) {
            SimpleFeatureType sft;
            sft = initSimpleFeatureType("Point", srid);
            pointSimpleFeatureTypes.put(srid, sft);
            return sft;
        }
        return pointSimpleFeatureTypes.get(srid);
    }

    private SimpleFeatureType initSimpleFeatureType(String type, String srid) {
        SimpleFeatureType result = null;
        try {
            result = DataUtilities.createType("Location",
                    "the_geom:" + type + ":srid=" + srid + ","
                    + // <- the geometry attribute
                    "name:String," // <- a String attribute
            );
        } catch (SchemaException ex) {
            Logger.getLogger(Geotools_Maps.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public HashMap<String, SimpleFeatureType> getLineSimpleFeatureTypes() {
        if (lineSimpleFeatureTypes == null) {
            lineSimpleFeatureTypes = new HashMap<>();
        }
        return lineSimpleFeatureTypes;
    }

    public SimpleFeatureType getLineSimpleFeatureType(String srid) {
        if (!getLineSimpleFeatureTypes().containsKey(srid)) {
            SimpleFeatureType sft;
            sft = initSimpleFeatureType(                    "LineString", srid);
            lineSimpleFeatureTypes.put(                    srid, sft);
            return sft;
        }
        return lineSimpleFeatureTypes.get(srid);
    }

    public HashMap<String, SimpleFeatureType> getPolygonSimpleFeatureTypes() {
        if (polygonSimpleFeatureTypes == null) {
            polygonSimpleFeatureTypes = new HashMap<>();
        }
        return polygonSimpleFeatureTypes;
    }

    public SimpleFeatureType getPolygonSimpleFeatureType(String srid) {
        if (!getPolygonSimpleFeatureTypes().containsKey(srid)) {
            SimpleFeatureType sft;
            sft = initSimpleFeatureType(                    "Polygon", srid);
            polygonSimpleFeatureTypes.put(                    srid, sft);
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
//    public void selectAndCreateNewShapefile(
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
//        Geotools_Shapefile.transact(outputFile, sft, tsfc, sdsf);
//    }
    public SimpleFeatureType getFeatureType(SimpleFeatureType sft,
            Class<?> binding, String attributeName, String name) {
        SimpleFeatureType r;
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
        r = sftb.buildFeatureType();
        return r;
    }

    public File getOutputImageFile(File outputFile, String outputType) {
        File r;
        String filename = outputFile.getName();
        String outputImageFilename;
        outputImageFilename = filename.substring(0, filename.length() - 4)
                + "." + outputType;
        r = new File(outputFile.getParent(), outputImageFilename);
        return r;
    }

    /**
     *
     * @param sft
     * @param name
     * @return
     */
    public SimpleFeatureTypeBuilder getNewSimpleFeatureTypeBuilder(
            SimpleFeatureType sft, String name) {
        SimpleFeatureTypeBuilder r = new SimpleFeatureTypeBuilder();
        r.init(sft);
        r.setName(name);
        return r;
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
    public void addFeatureAttributeAndAddToFeatureCollection(
            SimpleFeature sf, SimpleFeatureBuilder sfb, Integer value,
            TreeSetFeatureCollection fc, String id) {
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
    public void addFeatureAttributeAndAddToFeatureCollection(
            SimpleFeature sf, SimpleFeatureBuilder sfb, Double value,
            TreeSetFeatureCollection fc, String id) {
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
    public void addFeatureToFeatureCollection(SimpleFeature sf,
            SimpleFeatureBuilder sfb, TreeSetFeatureCollection fc, String id) {
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
     * @param include This is a list of which filename indexes to include.
     * @return A Object[] result where: ----------------------------------------
     * result[0] is an Object[] with the same length as filenames.length where
     * each element i is the respective TreeMap<String, Integer> returned from
     * getLevelData(digitalWelfareDir,filenames[i]);
     * ---------------------------- result[1] is the max of all the maximum
     * counts.
     */
    protected Object[] getLevelData(File dir, String[] filenames,
            ArrayList<Integer> include) {
        Object[] r = new Object[2];
        int length = filenames.length;
        Object[] resultPart0 = new Object[length];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            boolean doLevel;
            doLevel = false;
            if (include != null) {
                if (include.contains(i)) {
                    doLevel = true;
                }
            } else {
                doLevel = true;
            }
            if (doLevel) {
                Object[] levelData = getLevelData(dir, filenames[i]);
                if (levelData != null) {
                    resultPart0[i] = levelData;
                    max = Math.max(max, (Integer) levelData[1]);
//                } else {
//                    return null;
                }
            }
        }
        r[0] = resultPart0;
        r[1] = max;
        return r;
    }

    /**
     * @param dir
     * @param filename
     * @return An Object[] result where: result[0] is a
     * {@code TreeMap<String, Integer>} with keys which are Area Codes and
     * values that are counts; result[1] is the max count.
     */
    protected Object[] getLevelData(File dir, String filename) {
        Object[] result = new Object[2];
        TreeMap<String, Integer> map = new TreeMap<>();
        result[0] = map;
        File file = new File(dir, filename);
        if (!file.exists()) {
            System.out.println("file " + file + " does not exist.");
            return null;
        }
//        System.out.println("Reading data from file " + file);
        try {
            try (BufferedReader br = env.env.io.getBufferedReader(file)) {
                StreamTokenizer st = new StreamTokenizer(br);
                env.env.io.setStreamTokenizerSyntax1(st);
                int token = st.nextToken();
                // Skip some header lines
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
            }
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
//        Geotools_Shapefile.transact(outputFile, sft, tsfc, sdsf);
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
//        Geotools_Shapefile.transact(outputFile, sft, tsfc, sdsf);
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
//        Geotools_Shapefile.transact(outputFile, sft, tsfc, sdsf);
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
//        Geotools_Shapefile.transact(outputFile, sft, tsfc, sdsf);
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

    protected void addPointFeature(Geotools_Point p, GeometryFactory gf,
            SimpleFeatureBuilder sfb, String name,
            TreeSetFeatureCollection tsfc) {
        Point point = gf.createPoint(new Coordinate(p.getX(), p.getY()));
        sfb.add(point);
        sfb.add(name);
        SimpleFeature feature = sfb.buildFeature(null);
        tsfc.add(feature);
    }

    protected void addLineFeature(Geotools_Point p1, Geotools_Point p2,
            GeometryFactory gf, SimpleFeatureBuilder sfb, String name,
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

    protected void addQuadFeature(Geotools_Point p1, Geotools_Point p2,
            Geotools_Point p3, Geotools_Point p4, GeometryFactory gf,
            SimpleFeatureBuilder sfb, String name,
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

    public TreeSetFeatureCollection getLineGridFeatureCollection(
            SimpleFeatureType sft, long nrows, long ncols,
            double xllcorner, double yllcorner, double cellsize) {
        TreeSetFeatureCollection result;
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        result = getLineGridFeatureCollection(sft, nrows, ncols, xllcorner,
                yllcorner, cellsize, gf, sfb);
        return result;
    }

    public TreeSetFeatureCollection getLineGridFeatureCollection(
            SimpleFeatureType sft, long nrows, long ncols, double xllcorner,
            double yllcorner, double cellsize, GeometryFactory gf,
            SimpleFeatureBuilder sfb) {
        TreeSetFeatureCollection result;
        result = new TreeSetFeatureCollection();
        // add row lines
        for (long row = 0; row <= nrows; row++) {
            double y;
            y = yllcorner + (row * cellsize);
            double x;
            x = xllcorner + (ncols * cellsize);
            Geotools_Point p1;
            Geotools_Point p2;
            p1 = new Geotools_Point((int) xllcorner, (int) y);
            p2 = new Geotools_Point((int) x, (int) y);
            addLineFeature(p1, p2, gf, sfb, "", result);
        }
        // add col lines
        for (long col = 0; col <= ncols; col++) {
            double x;
            x = xllcorner + (col * cellsize);
            Geotools_Point p1;
            Geotools_Point p2;
            double y;
            y = yllcorner + (nrows * cellsize);
            p1 = new Geotools_Point((int) x, (int) yllcorner);
            p2 = new Geotools_Point((int) x, (int) y);
            addLineFeature(p1, p2, gf, sfb, "", result);
        }
        return result;
    }

    public TreeSetFeatureCollection getPolyGridFeatureCollection(
            SimpleFeatureType sft,
            long nrows,
            long ncols,
            double xllcorner,
            double yllcorner,
            double cellsize) {
        TreeSetFeatureCollection result;
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        result = getPolyGridFeatureCollection(sft, nrows, ncols, xllcorner,
                yllcorner, cellsize, gf, sfb);
        return result;
    }

    public TreeSetFeatureCollection getPolyGridFeatureCollection(
            SimpleFeatureType sft, long nrows, long ncols, double xllcorner,
            double yllcorner, double cellsize, GeometryFactory gf,
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
                Geotools_Point p1;
                Geotools_Point p2;
                Geotools_Point p3;
                Geotools_Point p4;
                p1 = new Geotools_Point((int) x1, (int) y1);
                p2 = new Geotools_Point((int) x2, (int) y1);
                p3 = new Geotools_Point((int) x2, (int) y2);
                p4 = new Geotools_Point((int) x1, (int) y2);
                addQuadFeature(p1, p2, p3, p4, gf, sfb, "", result, csf);
            }
        }
        return result;
    }

    /**
     * @param dir The directory in which the shapefile is stored.
     * @param name The shapefile filename.
     * @param fc The feature collection to be turned into a shapefile.
     * @param sft
     * @return shapefile File
     */
    public File createShapefileIfItDoesNotExist(File dir, String name,
            TreeSetFeatureCollection fc, SimpleFeatureType sft) {
        File r;
        //ShapefileDataStoreFactory sdsf;
        sdsf = new ShapefileDataStoreFactory();
        r = createShapefileIfItDoesNotExist(dir, name, fc, sft, sdsf);
        return r;
    }

    /**
     * @param dir The directory in which the shapefile is stored.
     * @param name The shapefile filename.
     * @param fc The feature collection to be turned into a shapefile.
     * @param sft
     * @param sdsf
     * @return shapefile File
     */
    public File createShapefileIfItDoesNotExist(File dir, String name,
            TreeSetFeatureCollection fc, SimpleFeatureType sft,
            ShapefileDataStoreFactory sdsf) {
        File r;
        r = env.getShapefile(dir, name, true);
        if (!r.exists()) {
            Geotools_Shapefile.transact(r, sft, fc, sdsf);
        }
        return r;
    }

    /**
     *
     * @param dir
     * @param name Better to internally generate this from other parameters?
     * @param nrows
     * @param ncols
     * @param xllcorner
     * @param yllcorner
     * @param cellsize
     * @return
     */
    public File createLineGridShapefileIfItDoesNotExist(File dir, String name,
            long nrows, long ncols, double xllcorner, double yllcorner,
            double cellsize) {
        File r;
        SimpleFeatureType sft;
        sft = getLineSimpleFeatureType(getDefaultSRID());
        TreeSetFeatureCollection fc;
        fc = getLineGridFeatureCollection(sft, nrows, ncols, xllcorner, yllcorner, cellsize);
        r = createShapefileIfItDoesNotExist(dir, name, fc, sft);
        return r;
    }

    /**
     *
     * @param dir
     * @param name Better to internally generate this from other parameters?
     * @param nrows
     * @param ncols
     * @param xllcorner
     * @param yllcorner
     * @param cellsize
     * @return
     */
    public File createPolyGridShapefileIfItDoesNotExist(File dir, String name,
            long nrows, long ncols, double xllcorner, double yllcorner,
            double cellsize) {
        File r;
        SimpleFeatureType sft;
        sft = getPolygonSimpleFeatureType(getDefaultSRID());
        TreeSetFeatureCollection fc;
        fc = getPolyGridFeatureCollection(sft, nrows, ncols, xllcorner,
                yllcorner, cellsize);
        r = createShapefileIfItDoesNotExist(dir, name, fc, sft);
        return r;
    }

    /**
     * @param f
     * @return ArcGridReader
     */
    public ArcGridReader getArcGridReader(File f) {
        ArcGridReader result = null;
        try {
            result = new ArcGridReader(f);
        } catch (DataSourceException ex) {
            Logger.getLogger(Geotools_Maps.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("f.toString()" + f.toString());
        }
        return result;
    }

    /**
     *
     * @param agr
     * @return
     */
    public GridCoverage2D getGridCoverage2D(
            ArcGridReader agr) {
        GridCoverage2D r = null;
        try {
            if (agr != null) {
                r = agr.read(null);
            }
        } catch (IOException ex) {
            Logger.getLogger(Geotools_Maps.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

}
