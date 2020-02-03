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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import uk.ac.leeds.ccg.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.geotools.core.Geotools_Object;

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
        try {
            new Geotools_DisplayShapefile().run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void run() throws IOException {
//        String name = "SG_DataZone_Bdry_2011.shp";
//        Path dir = new File("/scratch02/IslandStories/Input/Census/2011/MSOA/BoundaryData/");
//        String name = "OutputArea2011_PartRemoved.shp";
//        Path dir = new File("/scratch02/IslandStories/Input/Census/2011/OA/BoundaryData/");
//        String name = "OA_2011_EoR_Argyll_and_Bute.shp";
//        Path dir = new File("/scratch02/IslandStories/Input/Census/2011/OA/BoundaryData/");
//        String name = "IZ_2001_EoR_Argyll___Bute.shp";
//        Path dir = new File("/scratch02/IslandStories/Input/Census/2011/IZ/BoundaryData/");
//        String name = "DZ_2011_EoR_Argyll___Bute.shp";
//        Path dir = new File("/scratch02/IslandStories/Input/Census/2011/DZ/BoundaryData/");
        //String name = "LeedsPostcodeUnitPolyShapefile.shp";
        //Path dir = new File("/scratch02/DigitalWelfare/Generated/Postcode/");
//        String name = "county_region.shp";
        ArrayList<Path> files = new ArrayList<>();
        String name;
        Path dir;
        Path f;

//        name = "high_water_polyline.shp";
//        dir = Paths.get("M:/Projects/PFIHack/data/input/OrdnanceSurvey/bdline_essh_gb/Data/GB/");
//        f = env.getShapefile(dir, name, false);
//        files.add(f);
//
//        name = "test.shp";
//        dir = Paths.get("M:/test/");
//        f = env.getShapefile(dir, name, false);
//        files.add(f);
        
        f = Paths.get("C:", "Users", "geoagdt", "data", "data", "electoral",
                "Westminster_Parliamentary_Constituencies_December_2018_UK_BUC",
                "Westminster_Parliamentary_Constituencies_December_2018_UK_BUC.shp");
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

    protected void displayShapefiles(ArrayList<Path> files) throws Exception {
        displayShapefiles(files, 800, 600, null);
    }

    /**
     * @param files
     * @param displayWidth
     * @param displayHeight
     * @param re Used to set MapViewport
     * @throws Exception
     */
    public void displayShapefiles(ArrayList<Path> files,
            int displayWidth, int displayHeight, 
            ReferencedEnvelope re) throws Exception {
        MapContent mc = new MapContent();
        Iterator<Path> ite = files.iterator();
        while (ite.hasNext()) {
            Path f = ite.next();
            FileDataStore fds = FileDataStoreFinder.getDataStore(f.toFile());
            SimpleFeatureSource fs = fds.getFeatureSource();
//        CoordinateReferenceSystem crs;
//        crs = store.getSchema().getCoordinateReferenceSystem();
//        System.out.println(crs.toWKT());
//        System.out.println(crs.toString());
            Style style = SLD.createSimpleStyle(fs.getSchema());
            Layer layer = new FeatureLayer(fs, style);
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
            MapViewport mvp = mc.getViewport();
            mvp.setBounds(re);
            mc.setViewport(mvp);
        }
        mapFrame.setVisible(true);
    }
}
