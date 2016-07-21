/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.agdtgeotools.demo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.TreeSetFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import static uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Maps.png_String;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Point;

/**
 *
 * @author geoagdt
 */
public class AGDT_CreatePointShapefile {
    
    public AGDT_CreatePointShapefile(){}
    
    public static void main(String[] args) {
        new AGDT_CreatePointShapefile().run();
    }
    
    public void run() {
        // Todo
    }
}
