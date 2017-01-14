package org.honton.chas.datadog.apm.example.client;

import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.honton.chas.datadog.apm.TraceConfiguration;

@ApplicationScoped
public class TraceConfigurationFactory {

  /**
   * Get the configuration.
   * @return The configuration
   */
  @Produces
  static TraceConfiguration getDefault() {
    return new TraceConfiguration(
      "greetings-client",
      "http://localhost:7777",
      TimeUnit.MINUTES.toMillis(1));
  }
}