package at.rovo.camel.test.routes;

import org.apache.camel.builder.RouteBuilder;

public class Route3 extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {

		restConfiguration()
			.component("jetty")
			.scheme("https")
			.host("localhost")
			.port(8383)
			.contextPath("/api/v1")
//			.componentProperty("handlers", "jettyAuthHandler") // is ignored for some reason
			.endpointProperty("handlers", "jettyAuthHandler") // works if only a single rest-dsl route is using this handler
			.endpointProperty("matchOnUriPrefix", "true");
		
		rest("/api/v1/service3")
			.get().route().routeId("'RestDSL-GET TestRoute'").log("Service3 GET request received").endRest();
		
		    // enabling one or both parts below HTTP operations together with the handler endpoint property will result in the following exception:
		    // java.lang.IllegalStateException: No LoginService for org.eclipse.jetty.security.authentication.BasicAuthenticator@1426427e in at.rovo.camel.test.auth.JettyBasicAuthAuthorizationHandler@5bd76887
		
//			.post().route().routeId("'RestDSL-POST TestRoute'").log("Service3 POST request received").endRest();
//			.delete().route().routeId("'RestDSL-DELETE TestRoute'").log("Service3 DELETE request received").endRest();
	}
}
