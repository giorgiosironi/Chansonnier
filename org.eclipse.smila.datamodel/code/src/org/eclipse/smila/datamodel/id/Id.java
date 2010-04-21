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

package org.eclipse.smila.datamodel.id;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for Ids of SMILA records. A record Id must contain:
 * <ul>
 * <li>data source : symbolic unique name for the source of this record. It does not contain any access details itself,
 * but identifes a data source configuraton somewhere else in the system that contains the necessary details.
 * <li>key of source object in data source, relative to the definitions of the data source. Source objects can have
 * multiple named key values, e.g. in database tables with a primary key consisting of multiple columns. The data source
 * configuration is necessary to interpret the key information correctly.
 * </ul>
 * 
 * During processing, the record Id may be be extended:
 * <ul>
 * <li> Element: part of a container, e.g. path in archive (what about recursion: part of part of part...), attachment
 * index in mails, etc. The element is identified by another key which is relative to the container element.
 * <li>Fragment: identified by page number, section number, section name, etc.
 * </ul>
 * 
 * Because Ids must be immutable objects to be used as hash keys, extending an Id means to create a new Id with the
 * additional part. Ids which contain fragment names already cannot be extended by new element keys.
 * 
 * @author jschumacher
 * 
 */
public interface Id extends Serializable {
  /**
   * name of the data source containing the object.
   * 
   * @return data source name.
   */
  String getSource();

  /**
   * key of the source object with respect to the data source configuration.
   * 
   * @return source object key.
   */
  Key getKey();

  /**
   * check if this Id contains element keys.
   * 
   * @return true if this contains element keys, else false.
   */
  boolean hasElementKeys();

  /**
   * get the list of container element keys. The implementation must ensure that a modification of this list does not
   * modify the Id.
   * 
   * @return list of container element keys.
   */
  List<? extends Key> getElementKeys();

  /**
   * check if this Id contains fragment names.
   * 
   * @return true if this contains fragment names, else false.
   */
  boolean hasFragmentNames();

  /**
   * get the list of fragment names. The implementation must ensure that a modification of this list does not modify the
   * Id.
   * 
   * @return list of fragment names.
   */
  List<String> getFragmentNames();

  /**
   * create a new Id from this Id by adding a container element key.
   * 
   * @param elementKey
   *          the container element key
   * @return an extended for the container element.
   * @throws IdHandlingException
   *           if this cannot be extended with element keys, because it contains fragment names already.
   */
  Id createElementId(Key elementKey) throws IdHandlingException;

  /**
   * create a new Id from this Id by adding a simple unnamed container element key.
   * 
   * @param elementName
   *          the key value of the container element
   * @return an extended for the container element.
   * @throws IdHandlingException
   *           if this cannot be extended with element keys, because it contains fragment names already.
   */
  Id createElementId(String elementName) throws IdHandlingException;

  /**
   * create a new Id from this Id by adding a fragment name.
   * 
   * @param framentName
   *          name of the fragment
   * @return an extended Id for the fragment.
   */
  Id createFragmentId(String framentName);

  /**
   * create a new Id for the containing compund of this Id. This is the Id derived by removing the last fragment name,
   * if this Id has fragments or the last container element key, if it only has elements keys. If this Id has neither
   * element keys nor fragment names, an exception is thrown.
   * 
   * @return Id of the compound containing this Id.
   * @throws IdHandlingException
   *           if this is not a compund part Id.
   */
  Id createCompoundId() throws IdHandlingException;

  /**
   * Create a hash string for this Id that can be used by databases as simple primary key. The implementation must make
   * sure that different Ids lead to different Id hashes.
   * 
   * @return a hash string for this Id
   */
  String getIdHash();

}
