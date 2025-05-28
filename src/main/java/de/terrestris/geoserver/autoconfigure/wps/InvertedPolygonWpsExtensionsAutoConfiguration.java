package de.terrestris.geoserver.autoconfigure.wps;

import de.terrestris.geoserver.wps.InvertedPolygon;
import org.geoserver.wps.WPSFactoryExtension;
import org.geoserver.wps.gs.GeoServerProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

/**
 * Spring-boot autoconfiguration, enabling the following WPS {@link GeoServerProcess processes}
 *
 * Vanilla GeoServer {@literal applicationContext.xml} at the time of writing:
 * <pre>
 * {@code
 * <beans>
 *   <bean id="invertedPolygons" class="de.terrestris.geoserver.wps.InvertedPolygon"/>
 * </beans>
 * }
 * </pre>
 */

@AutoConfiguration
@ConditionalOnClass(WPSFactoryExtension.class)
public class InvertedPolygonWpsExtensionsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(InvertedPolygonWpsExtensionsAutoConfiguration.class);

  @Bean
  InvertedPolygon invertedPolygonWps() {
    return new InvertedPolygon();
  }

    @PostConstruct
    void log(){
        log.info("Inverted polygon WPS processes loaded");
    }

}
