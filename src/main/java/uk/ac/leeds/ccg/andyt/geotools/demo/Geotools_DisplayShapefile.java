/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.geotools.demo;

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
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Shapefile;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Object;

/**
 *
 * @author geoagdt
 */
public class Geotools_DisplayShapefile extends Geotools_Object {

    public Geotools_DisplayShapefile() {
    }

    public Geotools_DisplayShapefile(Geotools_Environment ge) {
        super(ge);
    }

    public static void main(String[] args) {
        new Geotools_DisplayShapefile().run();
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
        files = new ArrayList<>();
        String name;
        File dir;
        File f;

        name = "high_water_polyline.shp";
        dir = new File(
                "M:/Projects/PFIHack/data/input/OrdnanceSurvey/bdline_essh_gb/Data/GB/");
        f = env.getShapefile(dir, name, false);
        files.add(f);

        name = "test.shp";
        dir = new File(
                "M:/test/");
        f = env.getShapefile(dir, name, false);
        files.add(f);

        try {
            displayShapefiles(files);
        } catch (Exception ex) {
            Logger.getLogger(Geotools_DisplayShapefile.class.getName()).log(Level.SEVERE, null, ex);
        }
//        Geotools_Shapefile aAGDT_Shapefile = new Geotools_Shapefile(shapefile);
//        MapContent mc;
//        mc = Geotools_Environment.createMapContent(aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile);
//        Geotools_Shapefile aAGDT_Shapefile = new Geotools_Shapefile(shapefile);
//        MapContent mc;
//        mc = Geotools_Environment.createMapContent(aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile);
//        Geotools_Shapefile aAGDT_Shapefile = new Geotools_Shapefile(shapefile);
//        MapContent mc;
//        mc = Geotools_Environment.createMapContent(aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile);
//        Geotools_Shapefile aAGDT_Shapefile = new Geotools_Shapefile(shapefile);
//        MapContent mc;
//        mc = Geotools_Environment.createMapContent(aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile, aAGDT_Shapefile);
    }

    protected void displayShapefiles(ArrayList<File> files) throws Exception {
        displayShapefiles(files, 800, 600, null);
    }

    /**
     * @param files
     * @param displayWidth
     * @param displayHeight
     * @param re Used to set MapViewport
     * @throws Exception 
     */
    protected void displayShapefiles(
            ArrayList<File> files,
            int displayWidth,
            int displayHeight,
            ReferencedEnvelope re) throws Exception {
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

        if (re != null) {
            MapViewport mvp;
            mvp = mc.getViewport();
            mvp.setBounds(re);
            mc.setViewport(mvp);
        }
        
        mapFrame.setVisible(true);
    }
}
