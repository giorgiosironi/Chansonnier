/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 * 
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.impl;

import org.eclipse.smila.connectivity.ConnectivityManager;
import org.eclipse.smila.connectivity.framework.State;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;

/**
 * Utility class that creates and sets the annotation ConnectivityManager.ANNOTATION_JOB_ID.
 */
public final class JobIdHelper {

  /**
   * Private construction to avoid creation of instances.
   */
  private JobIdHelper() {
  }

  /**
   * Set the jobId annotation on the given record, using the jobId provided in state. If any of the parameters is null
   * or state does not contain a job id the method does quietly nothing.
   * 
   * @param record
   *          the record
   * @param state
   *          a State object containing a job id
   */
  public static void setJobIdAnnotation(final Record record, final State state) {
    if (state != null) {
      setJobIdAnnotation(record, state.getJobId());
    }
  }

  /**
   * Set the given jobId annotation on the given record. If any of the parameters is null or jobId is an empty String
   * the method does quietly nothing.
   * 
   * @param record
   *          the record
   * @param jobId
   *          the id of the job
   */
  public static void setJobIdAnnotation(final Record record, final String jobId) {
    if (record != null && record.getMetadata() != null && jobId != null && jobId.trim().length() > 0) {
      final Annotation annotation = record.getFactory().createAnnotation();
      annotation.addAnonValue(jobId);
      record.getMetadata().setAnnotation(ConnectivityManager.ANNOTATION_JOB_ID, annotation);
    }
  }
}
