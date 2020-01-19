/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.geotools.demo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.TreeSetFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.geotools.Geotools_Point;
import uk.ac.leeds.ccg.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.geotools.core.Geotools_Object;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.vector.core.Vector_Environment;

/**
 *
 * @author geoagdt
 */
public class Geotools_CreatePointShapefile extends Geotools_Object {

    private Geotools_CreatePointShapefile() {
    }

    protected Geotools_CreatePointShapefile(Geotools_Environment ge) {
        super(ge);
    }

    public static void main(String[] args) {
        try {
            Vector_Environment ve = new Vector_Environment(
                    new Generic_Environment(new Generic_Defaults()));
            Geotools_Environment env = new Geotools_Environment(ve,
                    ve.env.files.getDir());
            Geotools_CreatePointShapefile p = new Geotools_CreatePointShapefile(env);
            p.run();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void run() throws IOException {
        // Todo
        Path dir = Paths.get("M:/Test");
        Files.createDirectories(dir);
        
        SimpleFeatureType aPointSFT = null;
        try {
            aPointSFT = DataUtilities.createType(
                    "POINT",
                    "the_geom:Point:srid=27700," + "name:String," + "number:Integer," + "number2:Integer");
            //srid=27700 is the Great_Britain_National_Grid
        } catch (SchemaException ex) {
            Logger.getLogger(Geotools_CreatePointShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }

        TreeSetFeatureCollection tsfc;
        tsfc = new TreeSetFeatureCollection();
        SimpleFeatureBuilder sfb;
        sfb = new SimpleFeatureBuilder(aPointSFT);

        // Create SimpleFeatureBuilder
        //FeatureFactory ff = FactoryFinder.getGeometryFactories();
        GeometryFactory gF = new GeometryFactory();

        int x1 = 410000;
        int x2 = 420000;
        int x3 = 415000;
        int y1 = 350000;
        int y2 = 360000;
        int y3 = 355000;

        Geotools_Point p1;
        Geotools_Point p2;
        Geotools_Point p3;
        Geotools_Point p4;
        Geotools_Point p5;
        p1 = new Geotools_Point(x1, y1);
        p2 = new Geotools_Point(x1, y2);
        p3 = new Geotools_Point(x2, y2);
        p4 = new Geotools_Point(x2, y1);
        p5 = new Geotools_Point(x3, y3);

        Coordinate[] coords;
        Point point;
        SimpleFeature feature;
        coords = new Coordinate[5];
        coords[0] = new Coordinate(p1.getX(), p1.getY());
        coords[1] = new Coordinate(p2.getX(), p2.getY());
        coords[2] = new Coordinate(p3.getX(), p3.getY());
        coords[3] = new Coordinate(p4.getX(), p4.getY());
        coords[4] = new Coordinate(p5.getX(), p5.getY());
        for (int i = 0; i < 5; i++) {
            point = gF.createPoint(coords[i]);
            String name = "" + (i + 1);
            int number = i + 1;
            sfb.add(point);
            sfb.add(name);
            sfb.add(number);
            sfb.add(number * number);
            feature = sfb.buildFeature(name);
            tsfc.add(feature);
        }
        Path outputShapeFile = env.getOutputShapefile(dir, "test");
        Geotools_Shapefile.transact(outputShapeFile, aPointSFT, tsfc,
                new ShapefileDataStoreFactory());
    }

}
