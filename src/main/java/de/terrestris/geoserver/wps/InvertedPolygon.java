package de.terrestris.geoserver.wps;

import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.feature.FeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

@DescribeProcess(title = "Inverted polygon", description = "Calculates the inverted polygon wrt the input geometries and the world.")
public class InvertedPolygon extends InvertedPolygonBase implements GeoServerProcess {

  @DescribeResult(description = "The inverted polygons as FeatureCollection (e.g. from WFS).", primary = true)
  public String execute(
    @DescribeParameter(name = "inputFeatures", description = "The input features")
    FeatureCollection<?, ?> inputFeatures
  ) {
    return computeInvertedPolygon(inputFeatures);
  }
}
