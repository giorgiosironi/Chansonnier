/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the UrlNormalizerFactory.java from Nutch 0.8.1 (see below the licene). 
 * The original File was modified by the Smila Team 
 **********************************************************************************************************************/
/** 
 * Copyright 2005 The Apache Software Foundation 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.eclipse.smila.connectivity.framework.crawler.web.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;

/**
 * Factory to create a UrlNormalizer from "urlnormalizer.class" configuration property.
 */
public class UrlNormalizerFactory {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(UrlNormalizerFactory.class);

  /** The configuration. */
  private final Configuration _conf;

  /**
   * The Constructor.
   * 
   * @param conf
   *          Configuration
   */
  public UrlNormalizerFactory(Configuration conf) {
    _conf = conf;
  }

  /**
   * Return the default UrlNormalizer implementation.
   * 
   * @return UrlNormalizer
   */
  public UrlNormalizer getNormalizer() {
    String urlNormalizer = null;

    UrlNormalizer normalizer = (UrlNormalizer) _conf.getObject(UrlNormalizer.class.getName());

    if (normalizer == null) {
      urlNormalizer = _conf.get("urlnormalizer.class");
      if (LOG.isInfoEnabled()) {
        LOG.info("Using URL normalizer: " + urlNormalizer);
      }

      Class<?> normalizerClass;
      try {
        normalizerClass = Class.forName(urlNormalizer);

        normalizer = (UrlNormalizer) normalizerClass.newInstance();
        normalizer.setConf(_conf);
        _conf.setObject(UrlNormalizer.class.getName(), normalizer);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Couldn't create " + urlNormalizer, e);
      } catch (InstantiationException e) {
        throw new RuntimeException("Couldn't create " + urlNormalizer, e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Couldn't create " + urlNormalizer, e);
      }
    }
    return normalizer;
  }
}
