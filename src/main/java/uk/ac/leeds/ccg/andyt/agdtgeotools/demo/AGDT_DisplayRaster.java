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
package uk.ac.leeds.ccg.andyt.agdtgeotools.demo;

import java.io.File;
import java.math.BigDecimal;
import java.util.TreeMap;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.geometry.Envelope2D;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCellDoubleChunkFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellDoubleChunkArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.GridStatistics0;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.exchange.ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.exchange.ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessorGWS;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Geotools;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_Maps;
import uk.ac.leeds.ccg.andyt.agdtgeotools.AGDT_StyleParameters;

/**
 *
 * @author geoagdt
 */
public class AGDT_DisplayRaster extends AGDT_Maps {

    protected Grids_Environment ge;
    protected ESRIAsciiGridExporter eage;
    protected ImageExporter ie;
    protected Grid2DSquareCellProcessorGWS gp;
    protected Grid2DSquareCellDoubleFactory gf;
    protected AbstractGrid2DSquareCellDoubleChunkFactory gcf;
    protected long nrows;
    protected long ncols;
    protected int chunkNRows;
    protected int chunkNCols;
    protected double cellsize;
    protected BigDecimal[] dimensions;
    protected long xllcorner;
    protected long yllcorner;
    protected TreeMap<String, String> tLookupFromPostcodeToCensusCodes;

    protected AGDT_StyleParameters styleParameters;
    protected int maxCellDistanceForGeneralisation;

    //protected boolean outputESRIAsciigrids;
    protected boolean handleOutOfMemoryErrors;

    public AGDT_DisplayRaster() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new AGDT_DisplayRaster().run();
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
        styleParameters = new AGDT_StyleParameters();
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
        ge = new Grids_Environment();
        eage = new ESRIAsciiGridExporter(ge);
        ie = new ImageExporter(ge);
        gp = new Grid2DSquareCellProcessorGWS(ge);
        gp.set_Directory(processorDir, false, handleOutOfMemoryErrors);
        gcf = new Grid2DSquareCellDoubleChunkArrayFactory();
        chunkNRows = 300;//250; //64
        chunkNCols = 350;//300; //64
        gf = new Grid2DSquareCellDoubleFactory(
                processorDir,
                chunkNRows,
                chunkNCols,
                gcf,
                -9999d,
                ge,
                handleOutOfMemoryErrors);
        gf.set_GridStatistics(new GridStatistics0());
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
//        Grid2DSquareCellDouble g;
//        g = (Grid2DSquareCellDouble) gf.create(asciigridFile);
//
//        System.out.println(g.toString(handleOutOfMemoryErrors));
//        String nameOfGrid2;
//        nameOfGrid2 = "test2";
//        File asciigridFile2 = new File(
//                dirIn,
//                nameOfGrid2 + ".asc");
//
//        Grid2DSquareCellDouble g2;
//        g2 = (Grid2DSquareCellDouble) gf.create(asciigridFile2);
//
//        System.out.println(g2.toString(handleOutOfMemoryErrors));

        String nameOfGrid;
        nameOfGrid = "test3";
        File differenceAsciigridFile;
        differenceAsciigridFile = new File(
                dirIn,
                nameOfGrid + ".asc");

        Grid2DSquareCellDouble g;
        g = (Grid2DSquareCellDouble) gf.create(differenceAsciigridFile);

//        gp.addToGrid(g, g2, -1.0d, handleOutOfMemoryErrors);
//        
//        System.out.println(g.toString(handleOutOfMemoryErrors));
//        
//        ESRIAsciiGridExporter eage;
//        eage = new ESRIAsciiGridExporter();
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
        AGDT_Geotools.outputToImageUsingGeoToolsAndSetCommonStyle(
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
        styleParameters = new AGDT_StyleParameters();
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
        ge = new Grids_Environment();
        eage = new ESRIAsciiGridExporter(ge);
        ie = new ImageExporter(ge);
        gp = new Grid2DSquareCellProcessorGWS(ge);
        gp.set_Directory(processorDir, false, handleOutOfMemoryErrors);
        gcf = new Grid2DSquareCellDoubleChunkArrayFactory();
        chunkNRows = 300;//250; //64
        chunkNCols = 350;//300; //64
        gf = new Grid2DSquareCellDoubleFactory(
                processorDir,
                chunkNRows,
                chunkNCols,
                gcf,
                -9999d,
                ge,
                handleOutOfMemoryErrors);
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
        Grid2DSquareCellDouble g;
        g = (Grid2DSquareCellDouble) gf.create(asciigridFile);
        int index = 0;
        boolean scaleToFirst = false;
        String outname = nameOfGrid + "GeoToolsOutput";
        double normalisation;
        normalisation = 100.0d;
        AGDT_Geotools.outputToImageUsingGeoToolsAndSetCommonStyle(
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
