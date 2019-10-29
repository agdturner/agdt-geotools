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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.generic.execution.Generic_Execution;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Object;
//import uk.ac.leeds.ccg.andyt.projects.digitalwelfare.mapping.DW_Style;

/**
 *
 * @author geoagdt
 */
public class Geotools_Shapefile extends Geotools_Object {

    private File file;
    private FileDataStore fileDataStore;

    protected Geotools_Shapefile() {
    }

    public Geotools_Shapefile(Geotools_Environment ge) {
        super(ge);
    }

//    public DW_Shapefile(File shapefile) throws MalformedURLException {
//        this(shapefile.toURI().toURL());
//    }
    public Geotools_Shapefile(
            Geotools_Environment ge,
            File shapefile) {
        super(ge);
        this.file = shapefile;
        initFileDataStore();
    }

    public final void setFile(File file) {
        this.file = file;
    }

    public void dispose() {
//        if (getFileDataStore() != null) {
//            getFileDataStore().dispose();
//        }
        if (fileDataStore != null) {
            //fileDataStore.preDispose(); // Perhaps there should be a predispose method.
            fileDataStore.dispose();
        }
    }

    public FeatureSource getFeatureSource() {
        FeatureSource fs = null;
        try {
            fs = getFileDataStore().getFeatureSource();
        } catch (IOException ex) {
            Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fs;
    }

    public FeatureLayer getFeatureLayer() {
        Style style = new Geotools_Style(env).createStyle(getFeatureSource());
        return getFeatureLayer(style);
    }

//    public static FeatureLayer getFeatureLayer(
//            File shapefile) {
//        FeatureSource fs = null;
//        try {
//            FileDataStore fds;
//            fds = getFileDataStore(shapefile);
//            fs = fds.getFeatureSource();
//        } catch (IOException ex) {
//            Logger.getLogger(DW_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Style style = DW_Style.createStyle(fs);
//        return getFeatureLayer(shapefile, style);
//    }
    public FeatureLayer getFeatureLayer(
            Style style) {
        FeatureLayer result;
        result = new FeatureLayer(
                getFeatureSource(),
                style);
        return result;
    }

//    public static FeatureLayer getFeatureLayer(
//            File shapefile,
//            Style style) {
//        FeatureLayer result;
//        FeatureSource fs = null;
//        try {
//            FileDataStore fds;
//            fds = getFileDataStore(shapefile);
//            fs = fds.getFeatureSource();
//        } catch (IOException ex) {
//            Logger.getLogger(DW_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        result = new FeatureLayer(
//                fs,
//                style);
////        // Debug
////        ReferencedEnvelope re = result.getBounds();
//        return result;
//    }
    public FeatureLayer getFeatureLayer(
            Style style,
            String title) {
        FeatureLayer result = getFeatureLayer(style);
        result.setTitle(title);
        return result;
    }

//    public static FeatureLayer getFeatureLayer(
//            File shapefile,
//            Style style,
//            String title) {
//        FeatureLayer result = getFeatureLayer(shapefile, style);
//        result.setTitle(title);
//        return result;
//    }
    /**
     *
     * @return
     */
    public FeatureCollection getFeatureCollection() {
        return getFeatureCollection(getFileDataStore());
    }

//    /**
//     * @param f is a file to be used to construct a FileDataStore from which the
//     * resulting Feature Collection and SimpleFeatureType of the result are
//     * constructed.
//     * @return Object[] result, where: -----------------------------------------
//     * result[0] is a FeatureCollection; ---------------------------------------
//     * result[1] is a SimpleFeatureType; ---------------------------------------
//     */
//    public static Object[] getFeatureCollectionAndType(File f) {
//        Object[] result = new Object[2];
//        FileDataStore fds = getFileDataStore(f);
//        FeatureCollection fc = getFeatureCollection(fds);
//        SimpleFeatureType sft = getSimpleFeatureType(fds);
//        result[0] = fc;
//        result[1] = sft;
//        return result;
//    }
    /**
     * @return A FileDataStore found via a FileDataStoreFinder.
     */
    protected FileDataStore getFileDataStore() {
        if (fileDataStore == null) {
            initFileDataStore();
        }
        return fileDataStore;
    }

    protected final void initFileDataStore() {
        File f;
        f = getFile();
        fileDataStore = getFileDataStore(f);
    }

    /**
     * @param f The File for which a FileDataStore is returned.
     * @return A FileDataStore found via a FileDataStoreFinder.
     */
    protected FileDataStore getFileDataStore(File f) {
        FileDataStore r = null;
        try {
            r = FileDataStoreFinder.getDataStore(f);
        } catch (IOException ex) {
            Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
            //} catch (NullPointerException e) {
        } catch (Exception e) {
            System.err.println("Exception (not an IOException) in DW_ShapefileDataStore.getFileDataStore(File)");
            long timeInMilliseconds = 1000;
            Generic_Execution.waitSychronized(env.env, f, timeInMilliseconds);
            return getFileDataStore(f);
        } catch (Error e) {
            System.err.println("Error in DW_ShapefileDataStore.getFileDataStore(File)");
            long timeInMilliseconds = 1000;
            Generic_Execution.waitSychronized(env.env, f, timeInMilliseconds);
            return getFileDataStore(f);
        }
        return r;
    }

    /**
     * @return A SimpleFeatureType derived from the Schema of
     * <code>fds.getFeatureSource()</code>.
     */
    public SimpleFeatureType getSimpleFeatureType() {
        SimpleFeatureType result = null;
        result = (SimpleFeatureType) getFeatureSource().getSchema();
        return result;
    }

//    /**
//     * @param fds The FileDataStore assumed to contain Features of a single
//     * SimpleFeatureType.
//     * @return A SimpleFeatureType derived from the Schema of
//     * <code>fds.getFeatureSource()</code>.
//     */
//    protected static SimpleFeatureType getSimpleFeatureType(FileDataStore fds) {
//        SimpleFeatureType result = null;
//        try {
//            FeatureSource fs;
//            fs = fds.getFeatureSource();
//            result = (SimpleFeatureType) fs.getSchema();
//        } catch (IOException ex) {
//            Logger.getLogger(DW_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return result;
//    }
//
    /**
     * @param fds The FileDataStore from which the FeatureSource Features are
     * returned as a FeatureCollection.
     * @return A FeatureCollection from fds.
     */
    protected static FeatureCollection getFeatureCollection(
            FileDataStore fds) {
        FeatureCollection result = null;
        try {
            result = fds.getFeatureSource().getFeatures();
        } catch (IOException ex) {
            Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Pushes the FeatureCollection holding SimpleFeatureType sft to the
     * shapefile f.
     *
     * @param f
     * @param sft
     * @param fc
     * @param sdsf
     */
    @Deprecated
    public static void transact(File f, SimpleFeatureType sft, 
            FeatureCollection fc, ShapefileDataStoreFactory sdsf) {
        ShapefileDataStore sds;
        sds = initialiseOutputDataStore(f, sft, sdsf);
        SimpleFeatureSource simpleFeatureSource;
        try {
            String typeName = sds.getTypeNames()[0];
            simpleFeatureSource = sds.getFeatureSource(typeName);
        } catch (IOException ex) {
            Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
            simpleFeatureSource = null;
        }
        Transaction transaction = new DefaultTransaction("create");
        if (simpleFeatureSource != null) {
            SimpleFeatureStore sfs;
            sfs = (SimpleFeatureStore) simpleFeatureSource;
            sfs.setTransaction(transaction);
            try {
                sfs.addFeatures(fc);
            } catch (IOException ex) {
                Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        commitTransaction(transaction);
        sds.dispose();
    }
    
    /**
     * New way of transacting for Geotools 21.3.
     * @param sds
     * @param TYPE
     * @param features
     * @throws IOException 
     */
    public static void transact(ShapefileDataStore sds, SimpleFeatureType TYPE,
            List<SimpleFeature> features) throws IOException {
        /*
         * Write the features to the shapefile
         */
        Transaction transaction = new DefaultTransaction("create");

        String typeName = sds.getTypeNames()[0];
        SimpleFeatureSource featureSource = sds.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
        /*
         * The Shapefile format has a couple limitations:
         * - "the_geom" is always first, and used for the geometry attribute name
         * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
         * - Attribute names are limited in length
         * - Not all data types are supported (example Timestamp represented as Date)
         *
         * Each data store has different limitations so check the resulting SimpleFeatureType.
         */
        System.out.println("SHAPE:" + SHAPE_TYPE);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            /*
             * SimpleFeatureStore has a method to add features from a
             * SimpleFeatureCollection object, so we use the ListFeatureCollection
             * class to wrap our list of features.
             */
            SimpleFeatureCollection collection;
            collection = new ListFeatureCollection(TYPE, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();
            } catch (IOException e) {
                e.printStackTrace(System.err);
                transaction.rollback();
            } finally {
                transaction.close();
            }
        } else {
            System.out.println(typeName + " does not support read/write access");
            System.exit(1);
        }
    }

    @Deprecated
    public static void commitTransaction(Transaction transaction) {
        try {
            transaction.commit();
        } catch (IOException ex) {
            // This may be caused by too many open files so let's not log this 
            // which opens another file and try to deal with it by waiting a bit.
            //Logger.getLogger(DW_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
            try {
                transaction.rollback();
                try {
                    synchronized (transaction) {
                        transaction.wait(2000L);
                    }
                } catch (InterruptedException ie) {
                    Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ie);
                }
                commitTransaction(transaction); // This may recurse infinitely!
            } catch (IOException ex1) {
                System.out.println("Oh help gromit!");
                Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (transaction != null) {
                try {
                    transaction.close();
                } catch (IOException ex) {
                    Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e) {
                    System.err.println("Exception (not IOException or NullPointerException) in DW_ShapefileDataStore.commitTransaction(Transaction))");
                } catch (Error e) {
                    System.err.println("Error in DW_ShapefileDataStore.commitTransaction(Transaction))");
                }
            }
        }
    }

    /**
     * Initialises and returns a ShapefileDataStore for output.
     *
     * @param f
     * @param sft
     * @param sdsf
     * @return
     */
    public static ShapefileDataStore initialiseOutputDataStore(
            File f,
            SimpleFeatureType sft,
            ShapefileDataStoreFactory sdsf) {
        ShapefileDataStore result = null;
        //ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<>();
        try {
            params.put("url", f.toURI().toURL());
        } catch (MalformedURLException ex) {
            Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
        params.put("create spatial index", Boolean.TRUE);
        try {
            result = (ShapefileDataStore) sdsf.createNewDataStore(params);
            result.forceSchemaCRS(DefaultGeographicCRS.WGS84);
            result.createSchema(sft);
        } catch (IOException ex) {
            Logger.getLogger(Geotools_Shapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * @return the file
     */
    public File getFile() {
        if (file == null) {
            int debug = 1;
        }
        return file;
    }

}
