package nl.codecentric.jenkins.appd.rest.types;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO object for unmarshalling JSON data from the AppDynamics REST interface.
 * Maps to the following structure:
 *
 * frequency": "TEN_MIN",
 * "metricPath": "Overall Application Performance|Average Response Time (ms)",
 * "metricValues": [
 *   {
 *     "current": 19,
 *     "max": 54104,
 *     "min": 0,
 *     "startTimeInMillis": 1356877200000,
 *     "value": 6
 *   },
 *   {
 *     "current": 5,
 *     "max": 54098,
 *     "min": 0,
 *     "startTimeInMillis": 1356877800000,
 *     "value": 6
 *   }
 * ]
 */
public class MetricData {

  private String frequency;
  private String metricPath;
  private List<MetricValues> metricValues = new ArrayList<MetricValues>();

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public String getMetricPath() {
    return metricPath;
  }

  public void setMetricPath(String metricPath) {
    this.metricPath = metricPath;
  }

  public List<MetricValues> getMetricValues() {
    return metricValues;
  }

  public void setMetricValues(List<MetricValues> metricValues) {
    this.metricValues = metricValues;
  }
}
