package at.rovo.camel.test.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
			//.componentProperty("handlers", "jettyAuthHandler") // is ignored for some reason
			//.endpointProperty("handlers", "jettyAuthHandler") // works if only a single rest-dsl route is using this handler
			.endpointProperty("matchOnUriPrefix", "true");
		
		rest("/service3")
			.get()
				.route().routeId("'RestDSL-GET TestRoute'")
				.log("Service3 GET request received")
			.endRest()
	
			// curl -XPOST -i -k --basic -u admin:secret -H "Content-Type:" --user-agent "test client" "https://localhost:8383/api/v1/service3" -d '{ "message": "test" }'
			.post()
				.consumes("application/json")
				.route().routeId("'RestDSL-POST TestRoute'")
				.log("Service3 POST request received. Payload was: ${in.body}")
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						exchange.getIn().getHeaders().put(Exchange.CONTENT_TYPE, "plain/text");
						exchange.getIn().getHeaders().put(Exchange.HTTP_RESPONSE_CODE, 200);
						exchange.getIn().setBody("POST request succeeded!\n");
					}
				})
			.endRest()
		
			.delete()
				.route().routeId("'RestDSL-DELETE TestRoute'")
				.log("Service3 DELETE request received")
			.endRest();
	}
}
