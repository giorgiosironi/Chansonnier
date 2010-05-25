/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.performancecounters;

import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.error.ErrorsBuffer;
import org.eclipse.smila.management.performance.PerformanceCounter;

/**
 * The Class CrawlerControllerPerformanceAgent.
 */
public interface AgentControllerPerformanceAgent extends ManagementAgent {
  
  /**
   * Gets the attachment bytes transfered.
   * 
   * @return the attachment bytes transfered
   */
  PerformanceCounter getAttachmentBytesTransfered();
  
  /**
   * Gets the attachment transfer rate.
   * 
   * @return the attachment transfer rate
   */
  PerformanceCounter getAttachmentTransferRate();
  
  /**
   * Gets the average records processing time.
   * 
   * @return the average records processing time
   */
  PerformanceCounter getAverageRecordsProcessingTime();
  
  /**
   * Gets the average delta indices processing time.
   * 
   * @return the average records processing time
   */
  PerformanceCounter getAverageDeltaIndicesProcessingTime();
  
  /**
   * Gets the records.
   * 
   * @return the records
   */
  PerformanceCounter getRecords();

  /**
   * Gets the exceptions.
   * 
   * @return the exceptions
   */
  PerformanceCounter getExceptions();

  /**
   * Gets the exceptions critical.
   * 
   * @return the exceptions critical
   */
  PerformanceCounter getExceptionsCritical();

  /**
   * Gets the delta indices.
   * 
   * @return the delta indices
   */
  PerformanceCounter getDeltaIndices();

  /**
   * Gets the error buffer.
   * 
   * @return the error buffer
   */
  ErrorsBuffer getErrorBuffer();

}
