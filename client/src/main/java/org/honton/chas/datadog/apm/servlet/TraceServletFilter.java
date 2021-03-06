package org.honton.chas.datadog.apm.servlet;

import org.honton.chas.datadog.apm.SpanBuilder;
import org.honton.chas.datadog.apm.TraceOperation;
import org.honton.chas.datadog.apm.Tracer;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Trace import for http requests
 */
@WebFilter("/*")
public class TraceServletFilter implements Filter {

  private Tracer tracer;

  @Inject
  void setTracer(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    final HttpServletRequest req = (HttpServletRequest) request;
    final HttpServletResponse resp = (HttpServletResponse) response;

    SpanBuilder sb = tracer.importSpan(name -> req.getHeader(name));
    try {
      sb.resource(normalize(req.getServerName()) + ':' + req.getServerPort())
        .operation(req.getMethod() + ' ' + URLDecoder.decode(req.getRequestURI(), "UTF-8"))
        .type(TraceOperation.WEB);
      filterChain.doFilter(request, response);
    } finally {
      int status = resp.getStatus();
      sb.error(status<200 || status>=400);
      tracer.closeSpan(sb);
    }
  }

  private static String normalize(String host) {
    if(Character.isDigit(host.charAt(0))) {
      return ':' + host;
    }
    return host;
  }

  @Override
  public void destroy() {
  }
}
