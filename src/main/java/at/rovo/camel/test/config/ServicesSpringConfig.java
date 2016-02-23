package at.rovo.camel.test.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.component.jetty9.JettyHttpComponent9;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import at.rovo.camel.test.auth.JettyBasicAuthAuthorizationHandler;
import at.rovo.camel.test.routes.Route1;
import at.rovo.camel.test.routes.Route2;
import at.rovo.camel.test.routes.Route3;

@Configuration
@Profile("default")
@PropertySource("classpath:services.properties")
public class ServicesSpringConfig extends CamelConfiguration {
	
	@Resource
	protected Environment env;
	
	  @Override
	  protected void setupCamelContext(CamelContext camelContext) throws Exception {
	    super.setupCamelContext(camelContext);
	    
	    camelContext.addComponent("jetty", jettyHttpComponent());
	    
	    final PropertiesComponent pc = 
	    		new PropertiesComponent("classpath:" + env.getProperty("propertyfile"));
	    camelContext.addComponent("properties", pc);
	  }
	
	  /**
	   * Configures a SSLContextParameter used by Camel's embedded Jetty server 
	   * to set up a HTTPS/SSL secured connection.
	   *
	   * @return The configured SSLContextParameter
	   */
	  @Bean(name = "sslContextParameters")
	  public SSLContextParameters sslContextParameters() {
	    String keyStore = env.getProperty("ssl.keyStore.resource");
	    URL keyStoreUrl = this.getClass().getResource(keyStore);

	    // http://camel.apache.org/jetty.html
	    KeyStoreParameters ksp = new KeyStoreParameters();
	    ksp.setResource(keyStoreUrl.getPath());
	    ksp.setPassword(env.getProperty("ssl.keyStore.password"));

	    KeyManagersParameters kmp = new KeyManagersParameters();
	    kmp.setKeyStore(ksp);
	    kmp.setKeyPassword(env.getProperty("ssl.key.password"));

	    SSLContextParameters scp = new SSLContextParameters();
	    scp.setKeyManagers(kmp);

	    return scp;
	  }
	  
	  @Bean(name = "jettyAuthHandler")
	  public JettyBasicAuthAuthorizationHandler jettyBasicAuthAuthorizationHandler() {
	    return new JettyBasicAuthAuthorizationHandler();
	  }
	  
	  @Bean(name = "jettyErrorHandler")
	  public SuppressJettyInfoErrorHandler jettyErrorHandler() {
	    return new SuppressJettyInfoErrorHandler();
	  }
	  
	  public JettyHttpComponent jettyHttpComponent() {
		  JettyHttpComponent jetty = new JettyHttpComponent9();
		  jetty.setSslContextParameters(sslContextParameters());
		  return jetty;
	  }
	  
	  @Override
	  public List<RouteBuilder> routes() {
	    List<RouteBuilder> routes = new ArrayList<>();

	    // "REST" routes
	    routes.add(route1());
	    routes.add(route2());
	    routes.add(route3());
	    routes.add(route2());

	    return routes;
	  }
	  
	  @Bean
	  public Route1 route1() {
		  return new Route1();
	  }
	  
	  @Bean
	  public Route2 route2() {
		  return new Route2();
	  }
	  
	  @Bean
	  public Route3 route3() {
		  return new Route3();
	  }
}
