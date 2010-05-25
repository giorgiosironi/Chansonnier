/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record;

/**
 * Interface of Record factories.
 * 
 * @author jschumacher
 * 
 */
public interface RecordFactory {
  /**
   * The Record factory for default record implementation.
   */
  RecordFactory DEFAULT_INSTANCE = RecordFactoryCreator.createDefaultFactory();

  /**
   * create a new Record.
   * 
   * @return new record.
   */
  Record createRecord();

  /**
   * create a new metadata object.
   * 
   * @return new metadata object.
   */
  MObject createMetadataObject();

  /**
   * create a new attribute object.
   * 
   * @return new attribute object.
   */
  Attribute createAttribute();

  /**
   * create a new annotation object.
   * 
   * @return new annotation object.
   */
  Annotation createAnnotation();

  /**
   * create a new literal object.
   * 
   * @return new literal object.
   */
  Literal createLiteral();
}
