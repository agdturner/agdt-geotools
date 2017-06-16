/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.agdtgeotools.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;

/**
 *
 * @author geoagdt
 */
public class AGDT_DisplayShapefile {

    public AGDT_DisplayShapefile() {
    }

    public static void main(String[] args) {
        new AGDT_DisplayShapefile().run();
    }

    public void run() {
//        String name = "SG_DataZone_Bdry_2011.shp";
//        File dir = new File("/scratch02/IslandStories/Input/Census/2011/MSOA/BoundaryData/");
//        String name = "OutputArea2011_PartRemoved.shp";
//        File dir = new File("/scratch02/IslandStories/Input/Census/2011/OA/BoundaryData/");
//        String name = "OA_2011_EoR_Argyll_and_Bute.shp";
//        File dir = new File("/scratch02/IslandStories/Input/Census/2011/OA/BoundaryData/");
//        String name = "IZ_2001_EoR_Argyll___Bute.shp";
//        File dir = new File("/scratch02/IslandStories/Input/Census/2011/IZ/BoundaryData/");
//        String name = "DZ_2011_EoR_Argyll___Bute.shp";
//        File dir = new File("/scratch02/IslandStories/Input/Census/2011/DZ/BoundaryData/");
        //String name = "LeedsPostcodeUnitPolyShapefile.shp";
        //File dir = new File("/scratch02/DigitalWelfare/Generated/Postcode/");
//        String name = "county_region.shp";
        ArrayList<File> files;
        files = new ArrayList<File>();
        String name;
        File dir;
        File f;
        
        name = "high_water_polyline.shp";
        dir = new File(
                "M:/Projects/PFIHack/data/input/OrdnanceSurvey/bdline_essh_gb/Data/GB/");
        f = AGDT_Geotools.getShapefile(dir, name, false);
        files.add(f);
        
        name = "test.shp";
        dir = new File(
         "M:/test/");
        f = AGDT_Geotools.getShapefile(dir, name, false);
        files.add(f);
        
        try {
            displayShapefiles(files);
        } catch (Exception ex) {
            Logger.getLogger(AGDT_DisplayShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
//        AGDT_Shapefile aAGDT_Shapefile = new AGDT_Shapefile(shapefile);
//        MapContent mc;
//        mc = AGDT_Geotools.createMapContent(aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile);
//        AGDT_Shapefile aAGDT_Shapefile = new AGDT_Shapefile(shapefile);
//        MapContent mc;
//        mc = AGDT_Geotools.createMapContent(aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile);
    }

    protected void displayShapefiles(ArrayList<File> files) throws Exception {
        // Create a map context

        MapContent mc;
        mc = new MapContent();

        Iterator<File> ite;
        ite = files.iterator();
        while (ite.hasNext()) {
            File f;
            f = ite.next();
            FileDataStore fds;
            fds = FileDataStoreFinder.getDataStore(f);
            SimpleFeatureSource fs;
            fs = fds.getFeatureSource();

//        CoordinateReferenceSystem crs;
//        crs = store.getSchema().getCoordinateReferenceSystem();
//        System.out.println(crs.toWKT());
//        System.out.println(crs.toString());
            Style style;
            style = SLD.createSimpleStyle(fs.getSchema());
            Layer layer;
            layer = new FeatureLayer(fs, style);
            mc.layers().add(layer);
        }
        // Create a JMapFrame with custom toolbar buttons
        JMapFrame mapFrame = new JMapFrame(mc);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);

//        JToolBar toolbar = mapFrame.getToolBar();
//        toolbar.addSeparator();
//        toolbar.add(new JButton(new ValidateGeometryAction()));
//        toolbar.add(new JButton(new ExportShapefileAction()));
        // Display the map frame. When it is closed the application will exit
        mapFrame.setSize(800, 600);
        mapFrame.setVisible(true);
    }
}
