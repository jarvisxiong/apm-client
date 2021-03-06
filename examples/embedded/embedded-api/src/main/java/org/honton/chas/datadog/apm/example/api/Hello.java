package org.honton.chas.datadog.apm.example.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.honton.chas.datadog.apm.TraceOperation;

/**
 * Endpoint to retrieve greetings
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface Hello {

  /**
   * Get the greeting in plain text
   * @return The greeting
   */
  @GET
  @Path("/greetings")
  String greeting();

  /**
   * Echo input
   * @param input
   * @return The input
   */
  @TraceOperation(false)
  @GET
  @Path("/echo")
  String echo(@QueryParam("input") String input);
}
