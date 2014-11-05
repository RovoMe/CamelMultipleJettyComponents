package at.rovo.camel.test.routes;

import org.apache.camel.builder.RouteBuilder;

public class Route4 extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		restConfiguration()
			.component("jetty")
			.port(8383)
			.scheme("https")
			.contextPath("/api/v1")
			.endpointProperty("sslKeystore", "/security/serverKey.jks")
			.endpointProperty("sslPassword", "keystorePW")
			.endpointProperty("sslKeyPassword", "jettyPW")
//			.componentProperty("sslContextParameters", "#sslContextParameters")
			.componentProperty("handlers", "#jettyAuthHandler");
		
		rest("/service4")
			.get().route().log("Service4 GET request received").endRest()
			.post().route().log("Service4 POST request received").endRest()
			.delete().route().log("Service4 DELETE request received").endRest();
	}
}
