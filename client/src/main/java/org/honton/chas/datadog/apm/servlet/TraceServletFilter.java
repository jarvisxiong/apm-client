package org.honton.chas.datadog.apm.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.honton.chas.datadog.apm.SpanBuilder;
import org.honton.chas.datadog.apm.Tracer;

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

    SpanBuilder sb = tracer.importSpan(new Tracer.HeaderAccessor() {
      @Override
      public String getValue(String name) {
        return req.getHeader(name);
      }
    });
    try {
      sb.resource("SR:" + req.getServerName() + ':' + req.getServerPort())
        .operation(req.getMethod() + ':' + URLDecoder.decode(req.getRequestURI(), "UTF-8"))
        .type(req.getScheme());
      filterChain.doFilter(request, response);
    } finally {
      int status = resp.getStatus();
      sb.error(status<200 || status>=400);
      tracer.closeSpan(sb);
    }
  }

  @Override
  public void destroy() {
  }
}
