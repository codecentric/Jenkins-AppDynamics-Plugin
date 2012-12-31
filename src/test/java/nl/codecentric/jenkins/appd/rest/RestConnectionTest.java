package nl.codecentric.jenkins.appd.rest;

import static org.junit.Assert.*;
import nl.codecentric.jenkins.appd.rest.types.MetricData;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * TODO update header from template
 */
public class RestConnectionTest {
  private final ObjectMapper jsonMapper = new ObjectMapper();

  private final String jsonOutput = "      [{\n" +
      "        \"frequency\": \"TEN_MIN\",\n" +
      "        \"metricPath\": \"Overall Application Performance|Average Response Time (ms)\",\n" +
      "        \"metricValues\": [\n" +
      "            {\n" +
      "                \"current\": 19,\n" +
      "                \"max\": 54104,\n" +
      "                \"min\": 0,\n" +
      "                \"startTimeInMillis\": 1356877200000,\n" +
      "                \"value\": 6\n" +
      "            },\n" +
      "            {\n" +
      "                \"current\": 5,\n" +
      "                \"max\": 54098,\n" +
      "                \"min\": 0,\n" +
      "                \"startTimeInMillis\": 1356877800000,\n" +
      "                \"value\": 6\n" +
      "            }\n" +
      "        ]\n" +
      "    }]\n";

  @Test
  public void testJsonParsing() throws IOException {

    List<MetricData> metricList = jsonMapper.readValue(jsonOutput, new TypeReference<List<MetricData>>() {});

    assertEquals(1, metricList.size());
    MetricData resultData = metricList.get(0);
    assertEquals("TEN_MIN", resultData.getFrequency());
    assertEquals(2, resultData.getMetricValues().size());
    assertEquals(5, resultData.getMetricValues().get(1).getCurrent().intValue());
  }
}
