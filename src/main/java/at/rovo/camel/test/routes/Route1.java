package at.rovo.camel.test.routes;

import org.apache.camel.Exchange;

public class Route1 extends AbstractRestRoute {

	/**
	 * Initializes a new instance of this class which defines a Camel route
	 * which listens for incoming service invocations.
	 */
	public Route1() {
		// sets the name of this bean
		this.setName("service1Rest");
		// defines the service-name as set in the properties file
		this.setServiceName("service1");
	}

	@Override
	protected void defineRoute() throws Exception {
		from(this.getEndpoint())
			.choice()
				.when(header(Exchange.HTTP_METHOD).isEqualTo("GET"))
					.log("service1 GET request received: '${in.header.CamelHttpPath}'")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("POST"))
					.log("service1 POST request received: '${in.header.CamelHttpPath}'")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("DELETE"))
					.log("service1 DELETE request received: '${in.header.CamelHttpPath}'")
			.endChoice();
	}
}
