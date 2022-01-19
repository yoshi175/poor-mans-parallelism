package com.vidispine.poormansparallelismservice.configuration;

import com.google.common.collect.Sets;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class EmbeddedTomcatConfiguration {

    @Value("${server.port}")
    private String serverPort;

    @Value("${management.port:${server.port}}")
    private String managementPort;

    @Value("${server.additionalPorts:null}")
    private String additionalPorts;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector[] additionalConnectors = this.additionalConnectors();
        if (additionalConnectors != null && additionalConnectors.length > 0)
            tomcat.addAdditionalTomcatConnectors(additionalConnectors);
        return tomcat;

    }

    private Connector[] additionalConnectors() {
        if (StringUtils.isBlank(this.additionalPorts))
            return null;

        Set<String> defaultPorts = Sets.newHashSet(this.serverPort, this.managementPort);
        String[] ports = this.additionalPorts.split(",");
        List<Connector> result = new ArrayList<>();
        for (String port : ports) {
            if (!defaultPorts.contains(port)) {
                Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
                connector.setScheme("http");
                connector.setPort(Integer.parseInt(port));
                result.add(connector);
            }
        }
        return result.toArray(new Connector[]{});
    }


}
