package at.rovo.camel.test.routes;

import org.apache.camel.builder.RouteBuilder;

public class Route4 extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		restConfiguration()
			.component("jetty")
			.scheme("https")
			.host("localhost")
			.port(8383)
			.contextPath("/api/v1")
			.endpointProperty("handlers",  "jettyAuthHandler")
			.endpointProperty("matchOnUriPrerfix",  "true");
		
		rest("/api/v1/service4")
			.get().route().routeId("'RestDSL-GET TestRoute'").log("Service4 GET request received").endRest()
			.post().route().routeId("'RestDSL-POST TestRoute'").log("Service4 POST request received").endRest()
			.delete().route().routeId("'RestDSL-DELETE TestRoute'").log("Service4 DELETE request received").endRest();
	}
}
