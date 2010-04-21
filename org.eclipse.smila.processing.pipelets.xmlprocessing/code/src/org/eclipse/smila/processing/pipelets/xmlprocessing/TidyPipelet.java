/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.NullPrintWriter;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

/**
 * Pipelet that performs an Tidy transformation on an attribute or attachment value. The possible properties are:
 * <ul>
 * <li>tidyFile: the name of the tidy configuration file (optional)</li>
 * <li>inputName: name of the Attribute/Attachment to apply the XSL transformation to</li>
 * <li>outputName: name of the Attribute/Attachment to store the XSL transformation in</li>
 * <li>inputType: the type (Attribute or Attachment of the inputName</li>
 * <li>outputType: the type (Attribute or Attachment of the outputtName</li>
 * </ul> 
 */
public class TidyPipelet extends AXmlTransformationPipelet {
  /**
   * The name of the tidy config file used for the transformation.
   */
  public static final String PROP_TIDY_FILE = "tidyFile";

  /**
   * Tidy file.
   */
  private String _tidyFile;

  /**
   * Tidyr.
   */
  private Tidy _tidy;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    super.configure(configuration);
    _tidyFile = (String) configuration.getPropertyFirstValue(PROP_TIDY_FILE);
    _tidy = new Tidy();

    // configure tidy
    if (_tidyFile != null) {
      final File f = new File(_tidyFile);
      if (!f.exists()) {
        throw new ProcessingException("unable to load tidy configuration file [" + _tidyFile + "]");
      }
      _tidy.setConfigurationFromFile(_tidyFile);

    } else {
      _tidy.setXHTML(true);
      _tidy.setQuiet(true);
      _tidy.setErrout(new NullPrintWriter());
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      for (Id id : recordIds) {
        ByteArrayInputStream bais = null;
        try {
          final byte[] input = readInput(blackboard, id);
          if (input != null && input.length > 0) {
            bais = new ByteArrayInputStream(input);
            final Document result = _tidy.parseDOM(bais, null);
            storeDocument(blackboard, id, result);
          }
        } catch (Exception e) {
          if (_log.isWarnEnabled()) {
            _log.warn("unable to tidy document " + id, e);
          }
        } finally {
          if (bais != null) {
            try {
              bais.close();
            } catch (final IOException e) {
              if (_log.isWarnEnabled()) {
                _log.warn("error closing tidy InputStream", e);
              }
            }
          }
        }
      } // for
    } // if
    return recordIds;
  }
}
