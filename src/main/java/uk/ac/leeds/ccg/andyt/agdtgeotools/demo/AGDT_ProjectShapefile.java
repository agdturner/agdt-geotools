/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.agdtgeotools.demo;

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author geoagdt
 */
public class AGDT_ProjectShapefile {

    public AGDT_ProjectShapefile() {
    }

    public static void main(String[] args) {
        File indir;
        indir = new File(
                "C:/Users/geoagdt/src/projects/saric/data/input/OSM/wales-latest-free.shp");
        File outdir;
        outdir = new File("C:/Users/geoagdt/src/projects/saric/data/generated/OSM/wales-latest-free.shp");
        String roadsFilenamePrefix;
        roadsFilenamePrefix = "gis.osm_roads_free_1";
        String dotShp;
        dotShp = ".shp";
        String roadsFilename;
        roadsFilename = roadsFilenamePrefix + dotShp;
        File indir2;
        indir2 = new File(
                indir,
                roadsFilename);
        File outdir2;
        outdir2 = new File(
                outdir,
                roadsFilename);
        File infile;
        infile = new File(
                indir2,
                roadsFilename);
        File outfile;
        outfile = new File(
                outdir2,
                roadsFilename);
        outfile.getParentFile().mkdirs();
        new AGDT_ProjectShapefile().run(infile, outfile);
    }

    /**
     * Adapted from:
     * http://docs.geotools.org/stable/userguide/tutorial/geometry/geometrycrs.html
     * @param inFile
     * @param outFile 
     */
    public void run(File inFile, File outFile) {
        try {
            FileDataStore store;
            SimpleFeatureSource featureSource;
            SimpleFeatureCollection featureCollection;
            MapContent map;
            SimpleFeatureType schema;

            store = FileDataStoreFinder.getDataStore(inFile);
            featureSource = store.getFeatureSource();
            featureCollection = featureSource.getFeatures();
            map = new MapContent();
            schema = featureSource.getSchema();

            CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
            CoordinateReferenceSystem worldCRS = null;
            try {
                worldCRS = CRS.decode("EPSG:27700");
            } catch (FactoryException ex) {
                Logger.getLogger(AGDT_ProjectShapefile.class.getName()).log(Level.SEVERE, null, ex);
            }
            boolean lenient = true; // allow for some error due to different datums
            MathTransform transform = null;
            try {
                transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);
            } catch (FactoryException ex) {
                Logger.getLogger(AGDT_ProjectShapefile.class.getName()).log(Level.SEVERE, null, ex);
            }

            DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
            HashMap<String, Serializable> create = new HashMap<String, Serializable>();
            try {
                create.put("url", outFile.toURI().toURL());
            } catch (MalformedURLException ex) {
                Logger.getLogger(AGDT_ProjectShapefile.class.getName()).log(Level.SEVERE, null, ex);
            }
            create.put("create spatial index", Boolean.TRUE);
            DataStore dataStore = factory.createNewDataStore(create);
            SimpleFeatureType featureType = SimpleFeatureTypeBuilder.retype(schema, worldCRS);
            dataStore.createSchema(featureType);

            //Get the name of the new Shapefile, which will be used to open the FeatureWriter
            String createdName = dataStore.getTypeNames()[0];

            Transaction transaction;
            transaction = new DefaultTransaction("Reproject");
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer;
            writer = dataStore.getFeatureWriterAppend(createdName, transaction);
            SimpleFeatureIterator iterator;
            iterator = featureCollection.features();
            while (iterator.hasNext()) {
                // copy the contents of each feature and transform the geometry
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                try {
                    Geometry geometry2 = JTS.transform(geometry, transform);
                    copy.setDefaultGeometry(geometry2);
                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                }
                writer.write();
            }
            transaction.commit();
            transaction.close();
            dataStore.dispose();
        } catch (IOException ex) {
            Logger.getLogger(AGDT_ProjectShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
