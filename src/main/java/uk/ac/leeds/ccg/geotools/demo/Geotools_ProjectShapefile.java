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
package uk.ac.leeds.ccg.geotools.demo;

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
//import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import uk.ac.leeds.ccg.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.geotools.core.Geotools_Object;

/**
 * Geotools_ProjectShapefile provides an exaple of how to project a shapefile
 * from
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Geotools_ProjectShapefile extends Geotools_Object {

    public Geotools_ProjectShapefile() {
    }

    public Geotools_ProjectShapefile(Geotools_Environment ge) {
        super(ge);
    }

    public static void main(String[] args) {

        try {
            File indir = new File("C:/Users/geoagdt/src/projects/saric/data/input/OSM/wales-latest-free.shp");
            File outdir = new File("C:/Users/geoagdt/src/projects/saric/data/generated/OSM/wales-latest-free.shp");
            String roadsFilenamePrefix = "gis.osm_roads_free_1";
            String dotShp = ".shp";
            String roadsFilename = roadsFilenamePrefix + dotShp;
            File indir2 = new File(indir, roadsFilename);
            File outdir2 = new File(outdir, roadsFilename);
            File infile = new File(indir2, roadsFilename);
            File outfile = new File(outdir2, roadsFilename);
            outfile.getParentFile().mkdirs();
            new Geotools_ProjectShapefile().run(infile, outfile);
        } catch (IOException | FactoryException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Adapted from:
     * http://docs.geotools.org/stable/userguide/tutorial/geometry/geometrycrs.html
     *
     * @param inFile
     * @param outFile
     */
    public void run(File inFile, File outFile) throws IOException, FactoryException {
            FileDataStore store = FileDataStoreFinder.getDataStore(inFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection features = featureSource.getFeatures();
            SimpleFeatureType schema = featureSource.getSchema();
            CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
            CoordinateReferenceSystem worldCRS = CRS.decode("EPSG:27700");
            boolean lenient = true; // allow for some error due to different datums
            MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);
            DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
            HashMap<String, Serializable> create = new HashMap<>();
            create.put("url", outFile.toURI().toURL());
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
            iterator = features.features();
            while (iterator.hasNext()) {
                // copy the contents of each feature and transform the geometry
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                try {
                    Geometry geometry2 = JTS.transform(geometry, transform);
                    copy.setDefaultGeometry(geometry2);
                } catch (MismatchedDimensionException | TransformException problem) {
                    problem.printStackTrace(System.err);
                    transaction.rollback();
                }
                writer.write();
            }
            transaction.commit();
            transaction.close();
            dataStore.dispose();
    }
}
