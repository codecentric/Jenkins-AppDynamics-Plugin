package nl.codecentric.jenkins.appd.rest.types;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * POJO object for unmarshalling JSON data from the AppDynamics REST interface.
 * Maps to the following structure (part of {@link MetricData}:
 *
 *     "current": 19,
 *     "max": 54104,
 *     "min": 0,
 *     "startTimeInMillis": 1356877200000,
 *     "value": 6
 *
 *     ==> The 'value' field contains the actual value requested.
 */
public class MetricValues {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.mediumDateTime();

  private Integer current;
  private Integer max;
  private Integer min;
  private Long startTimeInMillis;
  private Integer value;
  private Integer sum;
  private Integer count;
  private Double standardDeviation;
  private Integer occurrences;
  private Boolean useRange;

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

  public String getFormattedTime() {
    return DATE_TIME_FORMATTER.print(this.startTimeInMillis);
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }

  public Integer getSum() {
    return sum;
  }

  public void setSum(final Integer sum) {
    this.sum = sum;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(final Integer count) {
    this.count = count;
  }

  public Double getStandardDeviation() {
    return standardDeviation;
  }

  public void setStandardDeviation(final Double standardDeviation) {
    this.standardDeviation = standardDeviation;
  }

  public Integer getOccurrences() {
    return occurrences;
  }

  public void setOccurrences(final Integer occurrences) {
    this.occurrences = occurrences;
  }

  public Boolean getUseRange() {
    return useRange;
  }

  public void setUseRange(final Boolean useRange) {
    this.useRange = useRange;
  }
}
