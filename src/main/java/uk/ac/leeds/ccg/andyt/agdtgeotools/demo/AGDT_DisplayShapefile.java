/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.agdtgeotools.demo;

import java.io.File;
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
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Shapefile;

/**
 *
 * @author geoagdt
 */
public class AGDT_DisplayShapefile {
    
    private SimpleFeatureSource featureSource;
    private MapContent mc;
    
    public AGDT_DisplayShapefile(){}
    
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
        String name = "high_water_polyline.shp";
        File dir = new File("M:/Projects/PFIHack/data/input/OrdnanceSurvey/bdline_essh_gb/Data/GB/");

        File shapefile = AGDT_Geotools.getShapefile(dir, name, false);
        try {
            displayShapefile(shapefile);
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
    
    
    private void displayShapefile(File f) throws Exception {
        FileDataStore store = FileDataStoreFinder.getDataStore(f);
        featureSource = store.getFeatureSource();

        // Create a map context and add our shapefile to it
        mc = new MapContent();
        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        mc.layers().add(layer);

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
