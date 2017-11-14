/*
 * Copyright (C) 2014 geoagdt.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.geotools.demo;

import java.io.File;
import java.math.BigDecimal;
import java.util.TreeMap;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.arcgrid.ArcGridReader;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_ProcessorGWS;
import uk.ac.leeds.ccg.andyt.geotools.core.Geotools_Environment;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_Maps;
import uk.ac.leeds.ccg.andyt.geotools.Geotools_StyleParameters;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_GridDoubleStatisticsNotUpdated;

/**
 *
 * @author geoagdt
 */
public class Geotools_DisplayRaster extends Geotools_Maps {

    protected Grids_ESRIAsciiGridExporter eage;
    protected Grids_ImageExporter ie;
    protected Grids_ProcessorGWS gp;
    protected Grids_GridDoubleFactory gf;
    protected Grids_AbstractGridChunkDoubleFactory gcf;
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

    protected Geotools_DisplayRaster() {
    }

    public Geotools_DisplayRaster(Geotools_Environment ge) {
        super(ge);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new Geotools_DisplayRaster().run();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
//            StackTraceElement[] stes = e.getStackTrace();
//            for (StackTraceElement ste : stes) {
//                System.err.println(ste.toString());
//            }
        } catch (Error e) {
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
//            StackTraceElement[] stes = e.getStackTrace();
//            for (StackTraceElement ste : stes) {
//                System.err.println(ste.toString());
//            }
        }
    }

    /**
     *
     */
    public void run() {
        //runSingleColour();
        runDifference();
    }

    /**
     * Positive values are shown in red, zero values are shown white, negative
     * values are shown in blue.
     */
    public void runDifference() {
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
        mapDirectory = new File(
                new File("/scratch02/"),
                "test");
        mapDirectory.mkdirs();
        imageWidth = 1000;

        /*Grid Parameters
         *_____________________________________________________________________
         */
        handleOutOfMemoryErrors = true;
        File processorDir = new File(
                mapDirectory,
                "processor");
        processorDir.mkdirs();
        ge.initGrids_Environment(processorDir);
        Grids_Environment grids_environment = ge.getGrids_Environment();
        eage = new Grids_ESRIAsciiGridExporter(grids_environment);
        ie = new Grids_ImageExporter(grids_environment);
        gp = new Grids_ProcessorGWS(grids_environment);
        gp.setDirectory(processorDir, false, handleOutOfMemoryErrors);
        gcf = new Grids_GridChunkDoubleArrayFactory();
        chunkNRows = 300;//250; //64
        chunkNCols = 350;//300; //64
        gf = new Grids_GridDoubleFactory(
                grids_environment,
                //processorDir,
                grids_environment.getFiles().getGeneratedGridDoubleDir(),
                gp.GridChunkDoubleFactory,
                gp.DefaultGridChunkDoubleFactory,
                -9999d,
                chunkNRows,
                chunkNCols,
                new Grids_Dimensions(chunkNRows, chunkNCols),
                new Grids_GridDoubleStatisticsNotUpdated(grids_environment));
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
        File dirOut;
        dirOut = new File(
                mapDirectory,
                "output");
        dirOut = new File(
                dirOut,
                styleParameters.getClassificationFunctionName());
        File dirIn;
        dirIn = new File(
                mapDirectory,
                "input");
//        String nameOfGrid;
//        nameOfGrid = "test";
//        File asciigridFile = new File(
//                dirIn,
//                nameOfGrid + ".asc");
//        Grids_GridDouble g;
//        g = (Grids_GridDouble) gf.create(asciigridFile);
//
//        System.out.println(g.toString(handleOutOfMemoryErrors));
//        String nameOfGrid2;
//        nameOfGrid2 = "test2";
//        File asciigridFile2 = new File(
//                dirIn,
//                nameOfGrid2 + ".asc");
//
//        Grids_GridDouble g2;
//        g2 = (Grids_GridDouble) gf.create(asciigridFile2);
//
//        System.out.println(g2.toString(handleOutOfMemoryErrors));

        String nameOfGrid;
        nameOfGrid = "test3";
        File differenceAsciigridFile;
        differenceAsciigridFile = new File(
                dirIn,
                nameOfGrid + ".asc");

        Grids_GridDouble g;
        g = (Grids_GridDouble) gf.create(differenceAsciigridFile);

//        gp.addToGrid(g, g2, -1.0d, handleOutOfMemoryErrors);
//        
//        System.out.println(g.toString(handleOutOfMemoryErrors));
//        
//        Grids_ESRIAsciiGridExporter eage;
//        eage = new Grids_ESRIAsciiGridExporter();
//        eage.toAsciiFile(g, differenceAsciigridFile, handleOutOfMemoryErrors);
        ArcGridReader agr;
        agr = getArcGridReader(differenceAsciigridFile);
        GridCoverage2D gc;
        gc = getGridCoverage2D(agr);

        int index = 0;
        boolean scaleToFirst = false;
        String outname = nameOfGrid + "GeoToolsOutput";
        double normalisation;
        normalisation = 100.0d;
        ge.outputToImageUsingGeoToolsAndSetCommonStyle(
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

    /**
     * Positive values are shown in red, zero values are shown white.
     */
    public void runSingleColour() {
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
        mapDirectory = new File(
                new File("/scratch02/"),
                "test");
        mapDirectory.mkdirs();
        imageWidth = 1000;

        /*Grid Parameters
         *_____________________________________________________________________
         */
        handleOutOfMemoryErrors = true;
        File processorDir = new File(
                mapDirectory,
                "processor");
        processorDir.mkdirs();
        ge.initGrids_Environment(processorDir);
        Grids_Environment grids_environment = ge.getGrids_Environment();
        eage = new Grids_ESRIAsciiGridExporter(grids_environment);
        ie = new Grids_ImageExporter(grids_environment);
        gp = new Grids_ProcessorGWS(grids_environment);
        gp.setDirectory(processorDir, false, handleOutOfMemoryErrors);
        gcf = new Grids_GridChunkDoubleArrayFactory();
        chunkNRows = 300;//250; //64
        chunkNCols = 350;//300; //64
        gf = new Grids_GridDoubleFactory(
                grids_environment,
                //processorDir,
                grids_environment.getFiles().getGeneratedGridDoubleDir(),
                gp.GridChunkDoubleFactory,
                gp.DefaultGridChunkDoubleFactory,
                -9999d,
                chunkNRows,
                chunkNCols,
                new Grids_Dimensions(chunkNRows, chunkNCols),
                new Grids_GridDoubleStatisticsNotUpdated(grids_environment));
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
        File dirOut;
        dirOut = new File(
                mapDirectory,
                "output");
        dirOut = new File(
                dirOut,
                styleParameters.getClassificationFunctionName());
        File dirIn;
        dirIn = new File(
                mapDirectory,
                "input");
        String nameOfGrid;
        nameOfGrid = "test";
        File asciigridFile = new File(
                dirIn,
                nameOfGrid + ".asc");
        ArcGridReader agr;
        agr = getArcGridReader(asciigridFile);
        GridCoverage2D gc;
        gc = getGridCoverage2D(agr);
        Grids_GridDouble g;
        g = (Grids_GridDouble) gf.create(asciigridFile);
        int index = 0;
        boolean scaleToFirst = false;
        String outname = nameOfGrid + "GeoToolsOutput";
        double normalisation;
        normalisation = 100.0d;
        ge.outputToImageUsingGeoToolsAndSetCommonStyle(
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
