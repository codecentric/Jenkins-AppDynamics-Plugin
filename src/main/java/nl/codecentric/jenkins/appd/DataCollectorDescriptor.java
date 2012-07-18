package nl.codecentric.jenkins.appd;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import hudson.model.Hudson;

/** Basic descriptor class for any Data Collectors. */
public abstract class DataCollectorDescriptor extends Descriptor<DataCollector> {

  /** Internal unique ID that distinguishes a parser. */
  public final String getId() {
    return getClass().getName();
  }

  /** Returns all the registered {@link DataCollectorDescriptor}s. */
  public static DescriptorExtensionList<DataCollector, DataCollectorDescriptor> all() {
    return Hudson.getInstance().<DataCollector, DataCollectorDescriptor>getDescriptorList(DataCollector.class);
  }

  public static DataCollectorDescriptor getById(String id) {
    for (DataCollectorDescriptor d : all())
      if (d.getId().equals(id))
        return d;
    return null;
  }

}
