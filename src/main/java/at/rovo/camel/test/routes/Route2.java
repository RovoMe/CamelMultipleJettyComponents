package at.rovo.camel.test.routes;

import org.apache.camel.Exchange;

public class Route2 extends AbstractRestRoute {

	/**
	 * Initializes a new instance of this class which defines a Camel route
	 * which listens for incoming service invocations.
	 */
	public Route2() {
		// sets the name of this bean
		this.setName("service2Rest");
		// defines the service-name as set in the properties file
		this.setServiceName("service2");
	}

	@Override
	protected void defineRoute() throws Exception {
		from(this.getEndpoint())
			.choice()
				.when(header(Exchange.HTTP_METHOD).isEqualTo("GET"))
					.log("service2 GET request received: '${in.header.CamelHttpPath}'")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("POST"))
					.log("service2 POST request received: '${in.header.CamelHttpPath}'")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("DELETE"))
					.log("service2 DELETE request received: '${in.header.CamelHttpPath}'")
			.endChoice();
	}
}
