package nl.codecentric.jenkins.appd.rest.types;

/**
 * POJO object for unmarshalling JSON data from the AppDynamics REST interface.
 * Maps to the following structure (part of {@link MetricData}:
 *
 *     "current": 19,
 *     "max": 54104,
 *     "min": 0,
 *     "startTimeInMillis": 1356877200000,
 *     "value": 6
 */
public class MetricValues {
  private Integer current;
  private Integer max;
  private Integer min;
  private Long startTimeInMillis;
  private Integer value;

  public Integer getCurrent() {
    return current;
  }

  public void setCurrent(Integer current) {
    this.current = current;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public Integer getMin() {
    return min;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public Long getStartTimeInMillis() {
    return startTimeInMillis;
  }

  public void setStartTimeInMillis(Long startTimeInMillis) {
    this.startTimeInMillis = startTimeInMillis;
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }
}
