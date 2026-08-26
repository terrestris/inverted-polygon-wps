package de.terrestris.geoserver.wps;

import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.util.logging.Logging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

@DescribeProcess(title = "Inverted polygon (String input)", description = "Calculates the inverted polygon from a GeoJSON string.")
public class InvertedPolygonString extends InvertedPolygonBase implements GeoServerProcess {

  private static final Logger LOGGER = Logging.getLogger(InvertedPolygonString.class);

  @DescribeResult(description = "The inverted polygons as GeoJSON.", primary = true)
  public String execute(
    @DescribeParameter(name = "inputFeatures", description = "The input features as GeoJSON string")
    String inputFeatures
  ) {
    FeatureJSON featureReader = new FeatureJSON(new GeometryJSON());
    try {
      FeatureCollection<?, ?> fc = featureReader.readFeatureCollection(
        new ByteArrayInputStream(inputFeatures.getBytes(UTF_8))
      );
      return computeInvertedPolygon(fc);
    } catch (IOException e) {
      LOGGER.info("Unable to parse GeoJSON input: " + e.getLocalizedMessage());
      LOGGER.log(Level.FINE, "Stack trace:", e);
      return null;
    }
  }
}
