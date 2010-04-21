package org.eclipse.smila.connectivity.framework.agent.jobfile;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;

/**
 * Interface JobFileHandler that is used by JobFileReader to add or delete parsed Records and Ids.
 */
public interface JobFileHandler {
  /**
   * Add the given record.
   * 
   * @param record
   *          the record to add
   */
  void add(final Record record);

  /**
   * Delete the given id.
   * 
   * @param id
   *          the Id to add
   */
  void delete(final Id id);
}
