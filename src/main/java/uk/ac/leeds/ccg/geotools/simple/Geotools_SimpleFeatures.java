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
package uk.ac.leeds.ccg.geotools.simple;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeatureType;
import uk.ac.leeds.ccg.geotools.core.Geotools_Object;

/**
 * Geotools_SimpleFeatures
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Geotools_SimpleFeatures extends Geotools_Object {
    
    /**
     * Uses a {@link SimpleFeatureTypeBuilder} to create a SimpleFeatureType.
     * 
     * Adapted from:
     * https://docs.geotools.org/latest/userguide/tutorial/feature/csv2shp.html
     * 
     * <p>The Coordinate Reference System for the FeatureType can be set. The 
     * maximum field length for the 'name' field is also set.
     */
    private static SimpleFeatureType createFeatureType() {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

        // add attributes in order
        builder.add("the_geom", Point.class);
        builder.length(15).add("Name", String.class); // <- 15 chars width for name field
        builder.add("number", Integer.class);

        // build the type
        final SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }
}
