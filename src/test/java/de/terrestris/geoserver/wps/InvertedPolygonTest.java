package de.terrestris.geoserver.wps;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import static org.junit.jupiter.api.Assertions.*;

class InvertedPolygonTest {

  private InvertedPolygon process;
  private GeometryFactory geometryFactory;

  @BeforeEach
  void setUp() {
    process = new InvertedPolygon();
    geometryFactory = new GeometryFactory();
  }

  @Test
  void testExecuteWithSinglePolygon() throws Exception {
    ListFeatureCollection fc = createFeatureCollection(createSquare(10, 11, 50, 51));

    String result = process.execute(fc);

    assertNotNull(result, "Result should not be null");
    assertFalse(result.isEmpty(), "Result should not be empty");
    // Result should be valid GeoJSON containing coordinates
    assertTrue(result.contains("coordinates"), "Result should contain GeoJSON coordinates");
  }

  @Test
  void testExecuteWithMultiplePolygons() throws Exception {
    ListFeatureCollection fc = createFeatureCollection(
      createSquare(10, 11, 50, 51),
      createSquare(20, 21, 40, 41)
    );

    String result = process.execute(fc);

    assertNotNull(result, "Result should not be null");
    assertTrue(result.contains("coordinates"), "Result should contain GeoJSON coordinates");
  }

  @Test
  void testExecuteWithEmptyCollection() throws Exception {
    ListFeatureCollection fc = createFeatureCollection();

    String result = process.execute(fc);

    assertNotNull(result, "Result should not be null for empty collection");
    // Should return the full world envelope as GeoJSON
    assertTrue(result.contains("coordinates"), "Result should contain GeoJSON coordinates");
  }

  @Test
  void testResultDiffersFromFullEnvelope() throws Exception {
    ListFeatureCollection emptyFc = createFeatureCollection();
    String fullEnvelope = process.execute(emptyFc);

    ListFeatureCollection withPolygon = createFeatureCollection(createSquare(10, 11, 50, 51));
    String invertedResult = process.execute(withPolygon);

    assertNotNull(fullEnvelope);
    assertNotNull(invertedResult);
    assertNotEquals(fullEnvelope, invertedResult,
      "Inverted polygon should differ from full envelope");
  }

  /**
   * Creates a simple square polygon in EPSG:4326.
   */
  private Polygon createSquare(double minX, double minY, double maxX, double maxY) {
    Coordinate[] coords = new Coordinate[]{
      new Coordinate(minX, minY),
      new Coordinate(maxX, minY),
      new Coordinate(maxX, maxY),
      new Coordinate(minX, maxY),
      new Coordinate(minX, minY)
    };
    LinearRing ring = geometryFactory.createLinearRing(coords);
    return geometryFactory.createPolygon(ring);
  }

  /**
   * Creates a FeatureCollection with the given polygons, CRS set to EPSG:4326.
   */
  private ListFeatureCollection createFeatureCollection(Polygon... polygons) throws Exception {
    SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
    typeBuilder.setName("TestType");
    typeBuilder.setCRS(CRS.decode("EPSG:4326"));
    typeBuilder.add("geom", Polygon.class);
    SimpleFeatureType type = typeBuilder.buildFeatureType();

    ListFeatureCollection fc = new ListFeatureCollection(type);
    SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);

    for (int i = 0; i < polygons.length; i++) {
      featureBuilder.add(polygons[i]);
      SimpleFeature feature = featureBuilder.buildFeature("feature-" + i);
      fc.add(feature);
    }

    return fc;
  }
}
