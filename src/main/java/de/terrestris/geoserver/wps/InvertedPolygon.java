package de.terrestris.geoserver.wps;

import org.geoserver.wps.gs.GeoServerProcess;
import org.geoserver.wps.process.RawData;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.referencing.CRS;
import org.geotools.util.logging.Logging;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.TopologyException;
import org.opengis.feature.Feature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

@DescribeProcess(title = "Inverted polygon", description = "Calculates the inverted polygon wrt the input geometries and the world.")
public class InvertedPolygon implements GeoServerProcess {

  private static final Logger LOGGER = Logging.getLogger(InvertedPolygon.class);

  @DescribeResult(description = "The inverted polygons as GeoJSON.", primary = true)
  public String execute(
    @DescribeParameter(
      name = "inputFeatures",
      description = "The input features"
    ) RawData inputFeatures
  ) {
    FeatureJSON reader = new FeatureJSON();
    GeometryJSON writer = new GeometryJSON();
    try {
      CoordinateReferenceSystem source = CRS.decode("EPSG:4326");
      CoordinateReferenceSystem target = CRS.decode("EPSG:3857");
      MathTransform transform = CRS.findMathTransform(source, target);
      FeatureIterator<?> collection = reader.readFeatureCollection(inputFeatures.getInputStream()).features();
      GeometryFactory factory = new GeometryFactory();
      Envelope envelope = new Envelope(-20000000d, 20000000d, -20000000d, 20000000d);
      Geometry polygon = factory.toGeometry(envelope);

      while (collection.hasNext()) {
        Feature feature = collection.next();
        Object value = feature.getDefaultGeometryProperty().getValue();
        if (value instanceof Geometry) {
          Geometry geometry = JTS.transform((Geometry) value, transform);
          try {
            polygon = polygon.difference(geometry);
          } catch (TopologyException e) {
            LOGGER.fine("Ignoring a geometry due to topologic errors: " + e.getMessage());
          }
        }
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      writer.write(polygon, bos);
      return new String(bos.toByteArray(), UTF_8);
    } catch (IOException | FactoryException | TransformException e) {
      LOGGER.info("Unable to read feature collection: " + e.getLocalizedMessage());
      LOGGER.log(Level.FINE, "Stack trace:", e);
      return null;
    }
  }

}
