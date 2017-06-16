/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.agdtgeotools.demo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.TreeSetFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import static uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Maps.png_String;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Point;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;

/**
 *
 * @author geoagdt
 */
public class AGDT_CreatePointShapefile {

    public AGDT_CreatePointShapefile() {
    }

    public static void main(String[] args) {
        new AGDT_CreatePointShapefile().run();
    }

    public void run() {
        // Todo
        File dir = new File(
                "M:/Test");
        dir.mkdirs();

        SimpleFeatureType aPointSFT = null;
        try {
            aPointSFT = DataUtilities.createType(
                    "POINT",
                    "the_geom:Point:srid=27700," + "name:String," + "number:Integer," + "number2:Integer");
            //srid=27700 is the Great_Britain_National_Grid
        } catch (SchemaException ex) {
            Logger.getLogger(AGDT_CreatePointShapefile.class.getName()).log(Level.SEVERE, null, ex);
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
        
        AGDT_Point p1;
        AGDT_Point p2;
        AGDT_Point p3;
        AGDT_Point p4;
        AGDT_Point p5;
        p1 = new AGDT_Point(x1, y1);
        p2 = new AGDT_Point(x1, y2);
        p3 = new AGDT_Point(x2, y2);
        p4 = new AGDT_Point(x2, y1);
        p5 = new AGDT_Point(x3, y3);

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
            sfb.add(number*number);
            feature = sfb.buildFeature(name);
            tsfc.add(feature);
        }
        File outputShapeFile = AGDT_Geotools.getOutputShapefile(
                dir,
                "test");
        AGDT_Shapefile.transact(
                outputShapeFile,
                aPointSFT,
                tsfc,
                new ShapefileDataStoreFactory());
    }

}
