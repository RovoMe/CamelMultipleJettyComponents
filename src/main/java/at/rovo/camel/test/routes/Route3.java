package at.rovo.camel.test.routes;

import org.apache.camel.Exchange;

public class Route3 extends AbstractRestRoute {

	/**
	 * Initializes a new instance of this class which defines a Camel route
	 * which listens for incoming service invocations.
	 */
	public Route3() {
		// sets the name of this bean
		this.setName("service3Rest");
		// defines the service-name as set in the properties file
		this.setServiceName("service3");
	}

	@Override
	protected void defineRoute() throws Exception {
		from(this.getEndpoint())
			.choice()
				.when(header(Exchange.HTTP_METHOD).isEqualTo("GET"))
					.log("service3 GET request received: '${in.header.CamelHttpPath}'")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("POST"))
					.log("service3 POST request received: '${in.header.CamelHttpPath}'")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("DELETE"))
					.log("service3 DELETE request received: '${in.header.CamelHttpPath}'")
			.endChoice();
	}
}
