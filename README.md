## Get an inverted polygon ##

A WPS for GeoServer that can be used to obtain a polygon with holes. It roughly works as follows:

* input is a GeoJSON feature collection (named `inputFeatures`)
* a polygon spanning the whole world in EPSG:3857 is created
* one by one the input geometries are subtracted from the polygon using the JTS `difference` operation

The resulting geometry will then be output as a GeoJSON geometry and be provided as the `result` output parameter.

## Download ##

Download the latest version from [here](https://nexus.terrestris.de/#browse/browse:public:de%2Fterrestris%2Fgeoserver%2Fwps%2Finverted-polygon-wps).

## Installation in GeoServer Cloud

GeoServer Cloud is distributed as Docker images with a pre-configured set of extensions.
In order to install an additional extension, you need to mount the required resources, such
as jar files, in the running containers and instruct the java launcher to use them.

In docker-compose it'd be as easy as bind-mounting the WPS `myWPS.jar`
on the wps container's `/opt/app/bin/BOOT-INF/lib/myWPS.jar`, but trying
to do the same in Kubernetes would override all jars in that folder.

The recommended way is to mount the jar files somewhere else
(for example, in `/opt/app/plugins/`) and set the `JAVA_OPTS` environment variable to
include this location, like in the following example:

```
  wps:
    image: geoservercloud/geoserver-cloud-wps:1.1.0
    environment:
      JAVA_OPTS: "-cp /opt/app/bin:/opt/app/bin/BOOT-INF/lib/*:/opt/app/plugins/myWPS.jar"
    volumes:
      - ./myWPS.jar:/opt/app/plugins/myWPS.jar
```

## cURL example:
`curl -X POST -H "Content-Type: application/xml" --data-binary @req.xml http://localhost:9090/geoserver/wps`

Contents of `req.xml`:
```
<wps:Execute version="1.0.0" service="WPS" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.opengis.net/wps/1.0.0" xmlns:wfs="http://www.opengis.net/wfs" xmlns:wps="http://www.opengis.net/wps/1.0.0" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:wcs="http://www.opengis.net/wcs/1.1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xsi:schemaLocation="http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd">
  <ows:Identifier>gs:InvertedPolygon</ows:Identifier>
  <wps:DataInputs>
    <wps:Input>
      <ows:Identifier>inputFeatures</ows:Identifier>
      <wps:Data>
        <wps:LiteralData>{
            "type": "FeatureCollection",
            "features": [
                {
                    "type": "Feature",
                    "geometry": {
                        "type": "Polygon",
                        "coordinates": [
                            [
                                [
                                    0.0,
                                    0.0
                                ],
                                [
                                    10.0,
                                    0.0
                                ],
                                [
                                    10.0,
                                    10.0
                                ],
                                [
                                    0.0,
                                    10.0
                                ],
                                [
                                    0.0,
                                    0.0
                                ]
                            ]
                        ]
                    },
                    "properties": {
                        "prop0": "value0",
                        "prop1": 7
                    }
                }
            ]
        }</wps:LiteralData>
      </wps:Data>
    </wps:Input>
  </wps:DataInputs>
  <wps:ResponseForm>
    <wps:RawDataOutput mimeType="application/octet-stream">
      <ows:Identifier>result</ows:Identifier>
    </wps:RawDataOutput>
  </wps:ResponseForm>
</wps:Execute>
```
