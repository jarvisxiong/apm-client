package org.honton.chas.datadog.apm;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Trace export for jaxrs implementations
 */
@Provider
public class TraceClientFilter implements ClientRequestFilter, ClientResponseFilter {

  @Inject
  Tracer tracer;

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    URI uri = requestContext.getUri();
    tracer.exportSpan(uri.getHost() + ':' + uri.getPort(),
        requestContext.getMethod() + ':' + uri.getPath().toLowerCase(),
        (k,v) -> requestContext.getHeaders().putSingle(k, v));
  }

  @Override
  public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
    tracer.finishSpan();
  }
}
