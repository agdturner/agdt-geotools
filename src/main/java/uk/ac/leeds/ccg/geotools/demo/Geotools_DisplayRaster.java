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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.arcgrid.ArcGridReader;
import uk.ac.leeds.ccg.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkFactoryDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkFactoryDoubleArray;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridFactoryDouble;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.grids.process.Grids_ProcessorGWS;
import uk.ac.leeds.ccg.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.geotools.Geotools_Maps;
import uk.ac.leeds.ccg.geotools.Geotools_StyleParameters;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBD;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridFactoryBD;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsNotUpdatedDouble;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.vector.core.Vector_Environment;

/**
 *
 * @author geoagdt
 */
public class Geotools_DisplayRaster extends Geotools_Maps {

    protected Grids_ESRIAsciiGridExporter eage;
    protected Grids_ImageExporter ie;
    protected Grids_ProcessorGWS gp;
    protected Grids_GridFactoryDouble gf;
    protected Grids_ChunkFactoryDouble gcf;
    protected long nrows;
    protected long ncols;
    protected int chunkNRows;
    protected int chunkNCols;
    protected double cellsize;
    protected BigDecimal[] dimensions;
    protected long xllcorner;
    protected long yllcorner;
    protected TreeMap<String, String> tLookupFromPostcodeToCensusCodes;

    protected Geotools_StyleParameters styleParameters;
    protected int maxCellDistanceForGeneralisation;

    //protected boolean outputESRIAsciigrids;
    protected boolean handleOutOfMemoryErrors;

    public Geotools_DisplayRaster(Geotools_Environment ge) {
        super(ge);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Geotools_Environment env = new Geotools_Environment(
                    new Vector_Environment(new Grids_Environment(
                            new Generic_Environment(new Generic_Defaults()))));
            Geotools_DisplayRaster p = new Geotools_DisplayRaster(env);
            p.run();
        } catch (Exception | Error e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     *
     */
    public void run() throws IOException, Exception {
        Grids_GridDouble g = createTestData();
        runSingleColour();
        //runDifference();
    }

        public Grids_GridDouble createTestData() throws ClassNotFoundException, Exception {
        System.out.println("createTestData");
        Grids_Processor gp = new Grids_Processor(env.ge);
        gf = gp.gridFactoryDouble;
        Grids_GridDouble g = (Grids_GridDouble) gf.create(10, 10);
        g.setCell(0, 0, 1.0d);
        g.setCell(0, 1, 2.0d); 
        g.setCell(0, 2, 1.6d);
        g.setCell(0, 3, 12.0d);
        g.setCell(0, 4, 101.0d);
        g.setCell(0, 5, 2003.0d);
        g.setCell(0, 6, 30004.0d);
        g.setCell(0, 7, 300006.0d);
        g.setCell(0, 8, 3000007.0d);
        g.setCell(0, 9, 30000008.0d);
        g.setCell(1, 0, 1.2d);
        g.setCell(1, 1, 12.0d); 
        g.setCell(1, 2, 120.0d);
        g.setCell(1, 3, 1200.0d);
        g.setCell(1, 4, 12000.0d);
        g.setCell(1, 5, 104.0d);
        g.setCell(1, 6, 100.6d);
        g.setCell(1, 7, 100.08d);
        g.setCell(1, 8, 100.009d);
        g.setCell(1, 9, 100.0001d);
        g.setCell(2, 0, 9.0d);
        g.setCell(2, 1, 4.0d); 
        g.setCell(2, 2, 1.0d);
        g.setCell(2, 3, 1.0d);
        g.setCell(2, 4, 3.0d);
        g.setCell(2, 5, 2.0d);
        g.setCell(2, 6, 3.0d);
        g.setCell(2, 7, 6.0d);
        g.setCell(2, 8, 2.0d);
        g.setCell(2, 9, 9.0d);
        g.setCell(3, 0, 49.0d);
        g.setCell(3, 1, 4.0d); 
        g.setCell(3, 2, 1.0d);
        g.setCell(3, 3, 1.0d);
        g.setCell(3, 4, 3.0d);
        g.setCell(3, 5, 2.0d);
        g.setCell(3, 6, 3.0d);
        g.setCell(3, 7, 6.0d);
        g.setCell(3, 8, 2.0d);
        g.setCell(3, 9, 16.0d);
        g.setCell(4, 0, 36.0d);
        g.setCell(4, 1, 4.0d); 
        g.setCell(4, 2, 1.0d);
        g.setCell(4, 3, 1.0d);
        g.setCell(4, 4, 3.0d);
        g.setCell(4, 5, 2.0d);
        g.setCell(4, 6, 3.0d);
        g.setCell(4, 7, 6.0d);
        g.setCell(4, 8, 2.0d);
        g.setCell(4, 9, 25.0d);
        g.setCell(5, 0, 25.0d);
        g.setCell(5, 1, 4.0d); 
        g.setCell(5, 2, 1.0d);
        g.setCell(5, 3, 1.0d);
        g.setCell(5, 4, 3.0d);
        g.setCell(5, 5, 2.0d);
        g.setCell(5, 6, 3.0d);
        g.setCell(5, 7, 6.0d);
        g.setCell(5, 8, 2.0d);
        g.setCell(5, 9, 36.0d);
        g.setCell(6, 0, 16.0d);
        g.setCell(6, 1, 4.0d); 
        g.setCell(6, 2, 1.0d);
        g.setCell(6, 3, 1.0d);
        g.setCell(6, 4, 3.0d);
        g.setCell(6, 5, 2.0d);
        g.setCell(6, 6, 3.0d);
        g.setCell(6, 7, 6.0d);
        g.setCell(6, 8, 2.0d);
        g.setCell(6, 9, 49.0d);
        g.setCell(7, 0, 9.0d);
        g.setCell(7, 1, 4.0d); 
        g.setCell(7, 2, 1.0d);
        g.setCell(7, 3, 1.0d);
        g.setCell(7, 4, 3.0d);
        g.setCell(7, 5, 2.0d);
        g.setCell(7, 6, 3.0d);
        g.setCell(7, 7, 6.0d);
        g.setCell(7, 8, 2.0d);
        g.setCell(7, 9, 64.0d);
        g.setCell(8, 0, 4.0d);
        g.setCell(8, 1, 4.0d); 
        g.setCell(8, 2, 1.0d);
        g.setCell(8, 3, 1.0d);
        g.setCell(8, 4, 3.0d);
        g.setCell(8, 5, 2.0d);
        g.setCell(8, 6, 3.0d);
        g.setCell(8, 7, 6.0d);
        g.setCell(8, 8, 2.0d);
        g.setCell(8, 9, 81.0d);
        g.setCell(9, 0, 1.0d);
        g.setCell(9, 1, 4.0d); 
        g.setCell(9, 2, 9.0d);
        g.setCell(9, 3, 16.0d);
        g.setCell(9, 4, 25.0d);
        g.setCell(9, 5, 36.0d);
        g.setCell(9, 6, 49.0d);
        g.setCell(9, 7, 64.0d);
        g.setCell(9, 8, 81.0d);
        g.setCell(9, 9, 100.0d);
        g.log(5, 5);
        Grids_ESRIAsciiGridExporter e = new Grids_ESRIAsciiGridExporter(env.ge);
        mapDirectory = Paths.get("C:/Temp");
        Path dirIn = Paths.get(mapDirectory.toString(), "input");
        String nameOfGrid = "test";
        Path asciigridFile = Paths.get(dirIn.toString(), nameOfGrid + ".asc");
        e.toAsciiFile(g, asciigridFile);
        return g;
    }
        
    public Grids_GridBD createTestData1() throws ClassNotFoundException, Exception {
        System.out.println("createTestData");
        Grids_GridBD g = (Grids_GridBD) gf.create(10, 10);
        g.setCell(0, 0, BigDecimal.valueOf(1.0d));
        g.setCell(0, 1, BigDecimal.valueOf(2.0d)); 
        g.setCell(0, 2, BigDecimal.valueOf(1.6d));
        g.setCell(0, 3, BigDecimal.valueOf(12.0d));
        g.setCell(0, 4, BigDecimal.valueOf(101.0d));
        g.setCell(0, 5, BigDecimal.valueOf(2003.0d));
        g.setCell(0, 6, BigDecimal.valueOf(30004.0d));
        g.setCell(0, 7, BigDecimal.valueOf(300006.0d));
        g.setCell(0, 8, BigDecimal.valueOf(3000007.0d));
        g.setCell(0, 9, BigDecimal.valueOf(30000008.0d));
        g.setCell(1, 0, BigDecimal.valueOf(1.2d));
        g.setCell(1, 1, BigDecimal.valueOf(12.0d)); 
        g.setCell(1, 2, BigDecimal.valueOf(120.0d));
        g.setCell(1, 3, BigDecimal.valueOf(1200.0d));
        g.setCell(1, 4, BigDecimal.valueOf(12000.0d));
        g.setCell(1, 5, BigDecimal.valueOf(104.0d));
        g.setCell(1, 6, BigDecimal.valueOf(100.6d));
        g.setCell(1, 7, BigDecimal.valueOf(100.08d));
        g.setCell(1, 8, BigDecimal.valueOf(100.009d));
        g.setCell(1, 9, BigDecimal.valueOf(100.0001d));
        g.setCell(2, 0, BigDecimal.valueOf(9.0d));
        g.setCell(2, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(2, 2, BigDecimal.valueOf(1.0d));
        g.setCell(2, 3, BigDecimal.valueOf(1.0d));
        g.setCell(2, 4, BigDecimal.valueOf(3.0d));
        g.setCell(2, 5, BigDecimal.valueOf(2.0d));
        g.setCell(2, 6, BigDecimal.valueOf(3.0d));
        g.setCell(2, 7, BigDecimal.valueOf(6.0d));
        g.setCell(2, 8, BigDecimal.valueOf(2.0d));
        g.setCell(2, 9, BigDecimal.valueOf(9.0d));
        g.setCell(3, 0, BigDecimal.valueOf(49.0d));
        g.setCell(3, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(3, 2, BigDecimal.valueOf(1.0d));
        g.setCell(3, 3, BigDecimal.valueOf(1.0d));
        g.setCell(3, 4, BigDecimal.valueOf(3.0d));
        g.setCell(3, 5, BigDecimal.valueOf(2.0d));
        g.setCell(3, 6, BigDecimal.valueOf(3.0d));
        g.setCell(3, 7, BigDecimal.valueOf(6.0d));
        g.setCell(3, 8, BigDecimal.valueOf(2.0d));
        g.setCell(3, 9, BigDecimal.valueOf(16.0d));
        g.setCell(4, 0, BigDecimal.valueOf(36.0d));
        g.setCell(4, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(4, 2, BigDecimal.valueOf(1.0d));
        g.setCell(4, 3, BigDecimal.valueOf(1.0d));
        g.setCell(4, 4, BigDecimal.valueOf(3.0d));
        g.setCell(4, 5, BigDecimal.valueOf(2.0d));
        g.setCell(4, 6, BigDecimal.valueOf(3.0d));
        g.setCell(4, 7, BigDecimal.valueOf(6.0d));
        g.setCell(4, 8, BigDecimal.valueOf(2.0d));
        g.setCell(4, 9, BigDecimal.valueOf(25.0d));
        g.setCell(5, 0, BigDecimal.valueOf(25.0d));
        g.setCell(5, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(5, 2, BigDecimal.valueOf(1.0d));
        g.setCell(5, 3, BigDecimal.valueOf(1.0d));
        g.setCell(5, 4, BigDecimal.valueOf(3.0d));
        g.setCell(5, 5, BigDecimal.valueOf(2.0d));
        g.setCell(5, 6, BigDecimal.valueOf(3.0d));
        g.setCell(5, 7, BigDecimal.valueOf(6.0d));
        g.setCell(5, 8, BigDecimal.valueOf(2.0d));
        g.setCell(5, 9, BigDecimal.valueOf(36.0d));
        g.setCell(6, 0, BigDecimal.valueOf(16.0d));
        g.setCell(6, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(6, 2, BigDecimal.valueOf(1.0d));
        g.setCell(6, 3, BigDecimal.valueOf(1.0d));
        g.setCell(6, 4, BigDecimal.valueOf(3.0d));
        g.setCell(6, 5, BigDecimal.valueOf(2.0d));
        g.setCell(6, 6, BigDecimal.valueOf(3.0d));
        g.setCell(6, 7, BigDecimal.valueOf(6.0d));
        g.setCell(6, 8, BigDecimal.valueOf(2.0d));
        g.setCell(6, 9, BigDecimal.valueOf(49.0d));
        g.setCell(7, 0, BigDecimal.valueOf(9.0d));
        g.setCell(7, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(7, 2, BigDecimal.valueOf(1.0d));
        g.setCell(7, 3, BigDecimal.valueOf(1.0d));
        g.setCell(7, 4, BigDecimal.valueOf(3.0d));
        g.setCell(7, 5, BigDecimal.valueOf(2.0d));
        g.setCell(7, 6, BigDecimal.valueOf(3.0d));
        g.setCell(7, 7, BigDecimal.valueOf(6.0d));
        g.setCell(7, 8, BigDecimal.valueOf(2.0d));
        g.setCell(7, 9, BigDecimal.valueOf(64.0d));
        g.setCell(8, 0, BigDecimal.valueOf(4.0d));
        g.setCell(8, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(8, 2, BigDecimal.valueOf(1.0d));
        g.setCell(8, 3, BigDecimal.valueOf(1.0d));
        g.setCell(8, 4, BigDecimal.valueOf(3.0d));
        g.setCell(8, 5, BigDecimal.valueOf(2.0d));
        g.setCell(8, 6, BigDecimal.valueOf(3.0d));
        g.setCell(8, 7, BigDecimal.valueOf(6.0d));
        g.setCell(8, 8, BigDecimal.valueOf(2.0d));
        g.setCell(8, 9, BigDecimal.valueOf(81.0d));
        g.setCell(9, 0, BigDecimal.valueOf(1.0d));
        g.setCell(9, 1, BigDecimal.valueOf(4.0d)); 
        g.setCell(9, 2, BigDecimal.valueOf(9.0d));
        g.setCell(9, 3, BigDecimal.valueOf(16.0d));
        g.setCell(9, 4, BigDecimal.valueOf(25.0d));
        g.setCell(9, 5, BigDecimal.valueOf(36.0d));
        g.setCell(9, 6, BigDecimal.valueOf(49.0d));
        g.setCell(9, 7, BigDecimal.valueOf(64.0d));
        g.setCell(9, 8, BigDecimal.valueOf(81.0d));
        g.setCell(9, 9, BigDecimal.valueOf(100.0d));
        g.log(5, 5);
        return g;
    }
    
    /**
     * Positive values are shown in red, zero values are shown white, negative
     * values are shown in blue.
     */
    public void runDifference() throws IOException, Exception {
        // If showMapsInJMapPane is true, the maps are presented in individual 
        // JMapPanes
        //showMapsInJMapPane = false;
        showMapsInJMapPane = true;
        //outputESRIAsciigrids = false;
        imageWidth = 1000;
        // Initialise styleParameters
        /*
         * YlOrRd,PRGn,PuOr,RdGy,Spectral,Grays,PuBuGn,RdPu,BuPu,YlOrBr,Greens,
         * BuGn,Accents,GnBu,PuRd,Purples,RdYlGn,Paired,Blues,RdBu,Oranges,
         * RdYlBu,PuBu,OrRd,Set3,Set2,Set1,Reds,PiYG,Dark2,YlGn,BrBG,YlGnBu,
         * Pastel2,Pastel1
         */
//        ColorBrewer brewer = ColorBrewer.instance();
//        //String[] paletteNames = brewer.getPaletteNames(0, nClasses);
//        String[] paletteNames = brewer.getPaletteNames();
//        for (int i = 0; i < paletteNames.length; i++) {
//            System.out.println(paletteNames[i]);
//        }
        styleParameters = new Geotools_StyleParameters();
        styleParameters.setnClasses(5);
        styleParameters.setPaletteName("Reds");
        styleParameters.setPaletteName2("Blues");
        styleParameters.setAddWhiteForZero(true);
        styleParameters.setForegroundStyleName(0, "Foreground Style 0");
//        styleParameters.setForegroundStyles(DW_Style.createDefaultPointStyle());
        mapDirectory = Paths.get("/scratch02/", "test");
        Files.createDirectories(mapDirectory);
        imageWidth = 1000;

        /*Grid Parameters
         *_____________________________________________________________________
         */
        handleOutOfMemoryErrors = true;
        Path processorDir = Paths.get(mapDirectory.toString(), "processor");
        Files.createDirectories(processorDir);
        eage = new Grids_ESRIAsciiGridExporter(env.ge);
        ie = new Grids_ImageExporter(env.ge);
        gp = new Grids_ProcessorGWS(env.ge);
        gcf = new Grids_ChunkFactoryDoubleArray();
        chunkNRows = 300;//250; //64
        chunkNCols = 350;//300; //64
        gf = gp.gridFactoryDouble;
        gf.setChunkNRows(chunkNRows);
        gf.setChunkNCols(chunkNCols);
        gf.setNoDataValue(-9999d);
        gf.setDimensions(new Grids_Dimensions(chunkNRows, chunkNCols));
        gf.setDefaultChunkFactory(gcf);
//        Currently only equal interval implemented
//        // Jenks runs
//        styleParameters.setClassificationFunctionName("Jenks");
//        commonStyling = true;
//        individualStyling = true;
//        // Quantile runs
//        styleParameters.setClassificationFunctionName("Quantile");
//        styleParameters.setStylesNull();
//        commonStyling = true;
        individualStyling = true;
        // Equal Interval runs
        styleParameters.setClassificationFunctionName("EqualInterval");
        styleParameters.setStylesNull();
        Path dirOut = Paths.get(mapDirectory.toString(), "output",
                styleParameters.getClassificationFunctionName());
        Path dirIn = Paths.get(mapDirectory.toString(), "input");
//        String nameOfGrid;
//        nameOfGrid = "test";
//        Path asciigridFile = new File(dirIn, nameOfGrid + ".asc");
//        Grids_GridDouble g = (Grids_GridDouble) gf.create(asciigridFile);
//
//        System.out.println(g.toString(handleOutOfMemoryErrors));
//        String nameOfGrid2;
//        nameOfGrid2 = "test2";
//        Path asciigridFile2 = new File(dirIn, nameOfGrid2 + ".asc");
//
//        Grids_GridDouble g2 = (Grids_GridDouble) gf.create(asciigridFile2);
//
//        System.out.println(g2.toString(handleOutOfMemoryErrors));

        String nameOfGrid = "test3";
        Path differenceAsciigridFile = Paths.get(dirIn.toString(),
                nameOfGrid + ".asc");
        Grids_GridDouble g = gf.create(new Generic_Path(
                differenceAsciigridFile));

//        gp.addToGrid(g, g2, -1.0d, handleOutOfMemoryErrors);
//        
//        System.out.println(g.toString(handleOutOfMemoryErrors));
//        
//        Grids_ESRIAsciiGridExporter eage;
//        eage = new Grids_ESRIAsciiGridExporter();
//        eage.toAsciiFile(g, differenceAsciigridFile, handleOutOfMemoryErrors);
        ArcGridReader agr = getArcGridReader(differenceAsciigridFile);
        GridCoverage2D gc = getGridCoverage2D(agr);

        int index = 0;
        boolean scaleToFirst = false;
        String outname = nameOfGrid + "GeoToolsOutput";
        double normalisation = 100.0d;
        env.outputToImageUsingGeoToolsAndSetCommonStyle(normalisation,
                styleParameters, index, outname, g, gc, dirOut, imageWidth,
                showMapsInJMapPane, scaleToFirst);
        if (!scaleToFirst) {
            styleParameters.setStyle(nameOfGrid, null, index);
        }
        if (agr != null) {
            agr.dispose();
        }
    }

    /**
     * Positive values are shown in red, zero values are shown white.
     */
    public void runSingleColour() throws IOException, Exception {
        // If showMapsInJMapPane is true, the maps are presented in individual 
        // JMapPanes
        //showMapsInJMapPane = false;
        showMapsInJMapPane = true;
        //outputESRIAsciigrids = false;
        imageWidth = 1000;
        // Initialise styleParameters
        /*
         * YlOrRd,PRGn,PuOr,RdGy,Spectral,Grays,PuBuGn,RdPu,BuPu,YlOrBr,Greens,
         * BuGn,Accents,GnBu,PuRd,Purples,RdYlGn,Paired,Blues,RdBu,Oranges,
         * RdYlBu,PuBu,OrRd,Set3,Set2,Set1,Reds,PiYG,Dark2,YlGn,BrBG,YlGnBu,
         * Pastel2,Pastel1
         */
//        ColorBrewer brewer = ColorBrewer.instance();
//        //String[] paletteNames = brewer.getPaletteNames(0, nClasses);
//        String[] paletteNames = brewer.getPaletteNames();
//        for (int i = 0; i < paletteNames.length; i++) {
//            System.out.println(paletteNames[i]);
//        }
        styleParameters = new Geotools_StyleParameters();
        styleParameters.setnClasses(9);
        styleParameters.setPaletteName("Reds");
        styleParameters.setnClasses(9);
        styleParameters.setPaletteName("Blues");
        styleParameters.setAddWhiteForZero(true);
        styleParameters.setForegroundStyleName(0, "Foreground Style 0");
//        styleParameters.setForegroundStyles(DW_Style.createDefaultPointStyle());
        mapDirectory = Paths.get("C:/Temp");
//        mapDirectory = Paths.get("M:/projects/Inactive/Geomorphometry/ClippedDEMs/ProcessedClip_DEMs");
//        mapDirectory = Paths.get("/scratch02/", "test");
        Files.createDirectories(mapDirectory);
        imageWidth = 1000;

        /**
         * Grid Parameters
         */
        handleOutOfMemoryErrors = true;
        Path processorDir = Paths.get(mapDirectory.toString(), "processor");
        Files.createDirectories(processorDir);
        eage = new Grids_ESRIAsciiGridExporter(env.ge);
        ie = new Grids_ImageExporter(env.ge);
        gp = new Grids_ProcessorGWS(env.ge);
        gcf = new Grids_ChunkFactoryDoubleArray();
        chunkNRows = 300;//250; //64
        chunkNCols = 350;//300; //64
        gf = gp.gridFactoryDouble;
        gf.setChunkNRows(chunkNRows);
        gf.setChunkNCols(chunkNCols);
        gf.setNoDataValue(-9999d);
        gf.setDimensions(new Grids_Dimensions(chunkNRows, chunkNCols));
        gf.setDefaultChunkFactory(gcf);
//        Currently only equal interval implemented
//        // Jenks runs
//        styleParameters.setClassificationFunctionName("Jenks");
//        commonStyling = true;
//        individualStyling = true;
//        // Quantile runs
//        styleParameters.setClassificationFunctionName("Quantile");
//        styleParameters.setStylesNull();
//        commonStyling = true;
        individualStyling = true;
        // Equal Interval runs
        styleParameters.setClassificationFunctionName("EqualInterval");
        styleParameters.setStylesNull();
        Path dirOut = Paths.get(mapDirectory.toString(), "output",
                styleParameters.getClassificationFunctionName());
        Path dirIn = Paths.get(mapDirectory.toString(), "input");
        String nameOfGrid;
        nameOfGrid = "test";
//        nameOfGrid = "c1984";
        Path asciigridFile = Paths.get(dirIn.toString(), nameOfGrid + ".asc");
        ArcGridReader agr = getArcGridReader(asciigridFile);
        GridCoverage2D gc = getGridCoverage2D(agr);
        Grids_GridDouble g = gf.create(new Generic_Path(asciigridFile));
        int index = 0;
        boolean scaleToFirst = false;
        String outname = nameOfGrid + "GeoToolsOutput";
        double normalisation = 100.0d;
        env.outputToImageUsingGeoToolsAndSetCommonStyle(
                normalisation,
                styleParameters,
                index,
                outname,
                g,
                gc,
                dirOut,
                imageWidth,
                showMapsInJMapPane,
                scaleToFirst);
        if (!scaleToFirst) {
            styleParameters.setStyle(nameOfGrid, null, index);
        }
        if (agr != null) {
            agr.dispose();
        }
    }

}
