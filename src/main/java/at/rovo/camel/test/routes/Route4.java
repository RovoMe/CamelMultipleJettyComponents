package at.rovo.camel.test.routes;

import org.apache.camel.builder.RouteBuilder;

public class Route4 extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		restConfiguration()
//			.component("jetty:https://localhost:8383/api/v1/service4?handlers=#jettyAuthHandler&matchOnUriPrefix=true");
			.component("jetty")
			.scheme("https")
			.host("localhost")
			.port(8383)
			.contextPath("/api/v1")
			.componentProperty("handlers", "#jettyAuthHandler")
			.componentProperty("matchOnUriPrefix", "true");
		
		rest("/service4")
			.id("RestDSL-TestRoute")
			.get().route().log("Service4 GET request received").endRest()
			.post().route().log("Service4 POST request received").endRest()
			.delete().route().log("Service4 DELETE request received").endRest();
	}
}
