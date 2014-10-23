package at.rovo.camel.test.routes;

import java.lang.invoke.MethodHandles;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * Defines a base route for all REST services which should be configured via an
 * injected properties file.
 * <p/>
 * If the injected properties file contains a <code>ssl.config</code> field set
 * to <i>true</i> it will therefore load defined services contained in
 * <code>rest.ssl.{serviceName}.path</code> where <i>serviceName</i> is the name
 * of the service to load. The service name should be defined during
 * construction via {@link #setServiceName(String)}.
 * <p/>
 * If no <code>ssl.config</code> field was set or the field was set to
 * <i>false</i> this route will try to set a service exposed via a non secured
 * connection. The corresponding properties entry looks like this:
 * <code>rest.{serviceName}.path</code> where <i>serviceName</i> again
 * references the service name to use.
 * 
 * @author Roman Vottner
 */
public abstract class AbstractRestRoute extends RouteBuilder {

	private static final Logger LOG = LoggerFactory
			.getLogger(MethodHandles.lookup().lookupClass());
	/**
	 * The environment object used to extract application properties from
	 */
	@Resource
	private Environment env;
	/**
	 * The name of this route
	 */
	private String name = "RestRoute";
	/**
	 * The endpoint exposed by Camel
	 */
	private String endpoint;
	/**
	 * The service name as defined in the respective properties file.
	 */
	private String serviceName = "restService";

	/**
	 * Initializes a SSL connection if <code>ssl.config</code> is set to true,
	 * else a non secured connection will be initiated.
	 * <p/>
	 * It tries to map a service to its configuration in services-X.properties
	 * by inserting the <i>serviceName</i>, which can be provided via
	 * {@link #setServiceName(String)}, into either
	 * <code> rest.ssl.{serviceName}.path</code> if SSL should be used or
	 * <code>rest.{serviceName}.path</code> if no secured connection is needed.
	 */
	@Override
	public void configure() throws Exception {

		Boolean sslConfig = false;
		if (env != null) {
			// check if we should configure an SSL protected Jetty connection
			sslConfig = Boolean.valueOf(env.getProperty("ssl.config"));
		} else {
			LOG.warn("Environment file not found!");
		}

		if (sslConfig) {
			// originate from the SSL protected Jetty instance
			this.endpoint = env.getProperty("rest.ssl." + this.serviceName + ".path");
			LOG.debug("Configured Rest based route with SSL: {}", this.endpoint);
			this.name += "wSSL";
		} else {
			// originate from the non protected Jetty instance
			this.endpoint = env.getProperty("rest." + this.serviceName + ".path");
			LOG.debug("Configured Rest based route without SSL: {}",
					this.endpoint);
		}

		this.defineRoute();
	}

	/**
	 * Defines a route REST based services have to specify.
	 * 
	 * @throws Exception
	 *             All thrown exceptions while executing the route
	 */
	protected abstract void defineRoute() throws Exception;

	/**
	 * Returns a String representation of the endpoint Camel should handle for
	 * this route.
	 * 
	 * @return The endpoint definition for this route
	 */
	public String getEndpoint() {
		return this.endpoint;
	}

	/**
	 * Sets the name of the route
	 * 
	 * @param name
	 *            The new name for this route
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this route.
	 * 
	 * @return The name of this route
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Specifies the service name as set in the respective properties file which
	 * is loaded through the injected {@link Environment}.
	 * 
	 * @param serviceName
	 *            The name of the servie this route will handle
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
