/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.utils.xml.stax;

/**
 * Utility class for a tag that marks a snippet boundary.
 */
public class MarkerTag {
  /**
   * name of the tag.
   */
  private String _name;

  /**
   * Boolean flag if the tag is a start tag (false) or a end tag (true).
   */
  private boolean _isEndTag;

  /**
   * Conversion Constructor. Creates a MarkerTag with the given name and isEndtag flag.
   * 
   * @param name
   *          the name of the tag
   * @param isEndTag
   *          boolean flag if the tag is a start tag (false) or a end tag (true).
   */
  public MarkerTag(final String name, boolean isEndTag) {
    if (name == null) {
      throw new IllegalArgumentException("parameter name is null");
    }
    if (name.trim().length() == 0) {
      throw new IllegalArgumentException("parameter name is an empty String");
    }
    _name = name;
    _isEndTag = isEndTag;
  }

  /**
   * Get the tag name.
   * 
   * @return the tag name
   */
  public String getName() {
    return _name;
  }

  /**
   * Returns if the MarkerTag is a end tag (true) or not (false).
   * 
   * @return if the MarkerTag is a end tag (true) or not (false)
   */
  public boolean isEndTag() {
    return _isEndTag;
  }
}
