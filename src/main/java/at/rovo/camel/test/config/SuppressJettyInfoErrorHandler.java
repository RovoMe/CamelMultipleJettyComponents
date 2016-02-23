package at.rovo.camel.test.config;

import org.eclipse.jetty.server.handler.ErrorHandler;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

/**
 * Custom implementation of a Jetty {@link ErrorHandler} in order to suppress the <em>Powered by
 * Jetty</em> element within the error page
 */
public class SuppressJettyInfoErrorHandler extends ErrorHandler {
	@Override
	protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks)
	    throws IOException
	{
	  String uri= request.getRequestURI();

	  writeErrorPageMessage(request,writer,code,message,uri);
	  if (showStacks)
	    writeErrorPageStacks(request,writer);
	}
}
