/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the BasicUrlNormalizer.java from Nutch 0.8.1 (see below the licene). 
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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Pattern;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configured;

/** Converts URLs to a normal form . */
public class BasicUrlNormalizer extends Configured implements UrlNormalizer {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(BasicUrlNormalizer.class);
  
  /** The perl5 compiler. */
  private final Perl5Compiler _compiler = new Perl5Compiler();

  /** The matchers. */
  private final ThreadLocal<PatternMatcher> _matchers = new ThreadLocal<PatternMatcher>() {
    @Override
    protected synchronized PatternMatcher initialValue() {
      return new Perl5Matcher();
    }
  };

  /** The _relative path rule. */
  private Rule _relativePathRule;

  /** The _leading relative path rule. */
  private Rule _leadingRelativePathRule;

  /**
   * Class that is used to convert URLs to normal form.
   */
  public BasicUrlNormalizer() {
    try {
      // this pattern tries to find spots like "/xx/../" in the URL, which
      // could be replaced by "/" xx consists of chars, different then "/"
      // (slash) and needs to have at least one char different from "."
      _relativePathRule = new Rule();
      _relativePathRule.setPattern((Perl5Pattern) _compiler.compile("(/[^/]*[^/.]{1}[^/]*/\\.\\./)",
        Perl5Compiler.READ_ONLY_MASK));
      _relativePathRule.setSubstitution(new Perl5Substitution("/"));

      // this pattern tries to find spots like leading "/../" in the URL,
      // which could be replaced by "/"
      _leadingRelativePathRule = new Rule();
      _leadingRelativePathRule.setPattern((Perl5Pattern) _compiler.compile("^(/\\.\\./)+",
        Perl5Compiler.READ_ONLY_MASK));
      _leadingRelativePathRule.setSubstitution(new Perl5Substitution("/"));

    } catch (MalformedPatternException exception) {
      if (LOG.isErrorEnabled()) {
        LOG.error(exception);
      }
      throw new RuntimeException(exception);
    }
  }

  /**
   * {@inheritDoc}
   */
  public String normalize(String urlString) throws MalformedURLException {
    if ("".equals(urlString)) {
      return urlString;
    }
    
    // remove extra spaces
    urlString = urlString.trim(); 

    final URL url = new URL(urlString);

    final String protocol = url.getProtocol();
    String host = url.getHost();
    int port = url.getPort();
    String file = url.getFile();

    boolean changed = false;

    if (!urlString.startsWith(protocol)) {
      changed = true;
    }

    if ("http".equals(protocol) || "ftp".equals(protocol)) {

      if (host != null) {
        final String newHost = host.toLowerCase(); // lower case host
        if (!host.equals(newHost)) {
          host = newHost;
          changed = true;
        }
      }

      if (port == url.getDefaultPort()) { // uses default port
        port = -1; // so don't specify it
        changed = true;
      }

      if (file == null || "".equals(file)) { // add a slash
        file = "/";
        changed = true;
      }

      if (url.getRef() != null) { // remove the reference
        changed = true;
      }

      // check for unnecessary use of "/../"
      final String file2 = substituteUnnecessaryRelativePaths(file);

      if (!file.equals(file2)) {
        changed = true;
        file = file2;
      }

    }

    if (changed) {
      urlString = new URL(protocol, host, port, file).toString();
    }

    return urlString;
  }

  /**
   * Substitute unnecessary relative paths.
   * 
   * All substitutions will be done step by step, to ensure that certain constellations will be normalized, too
   * 
   * For example: "/aa/bb/../../cc/../foo.html will be normalized in the following manner: "/aa/bb/../../cc/../foo.html"
   * "/aa/../cc/../foo.html" "/cc/../foo.html" "/foo.html"
   * 
   * The normalization also takes care of leading "/../", which will be replaced by "/", because this is a rather a sign
   * of bad webserver configuration than of a wanted link. For example, urls like "http://www.foo.com/../" should return
   * a http 404 error instead of redirecting to "http://www.foo.com".
   * 
   * @param file
   *          the file
   * 
   * @return the string
   */
  private String substituteUnnecessaryRelativePaths(String file) {
    String fileWorkCopy = file;
    int oldLen = file.length();
    int newLen = oldLen - 1;

    final Perl5Matcher matcher = (Perl5Matcher) _matchers.get();

    while (oldLen != newLen) {
      // substitue first occurence of "/xx/../" by "/"
      oldLen = fileWorkCopy.length();
      fileWorkCopy =
        Util.substitute(matcher, _relativePathRule.getPattern(), _relativePathRule.getSubstitution(), fileWorkCopy,
          1);

      // remove leading "/../"
      fileWorkCopy =
        Util.substitute(matcher, _leadingRelativePathRule.getPattern(), _leadingRelativePathRule.getSubstitution(),
          fileWorkCopy, 1);
      newLen = fileWorkCopy.length();
    }

    return fileWorkCopy;
  }

  /**
   * Class which holds a compiled pattern and its corresponding substitution string.
   */
  private static class Rule {

    /** The pattern. */
    private Perl5Pattern _pattern;

    /** The substitution. */
    private Perl5Substitution _substitution;

    /**
     * Gets the pattern.
     * 
     * @return the pattern
     */
    public Perl5Pattern getPattern() {
      return _pattern;
    }

    /**
     * Sets the pattern.
     * 
     * @param pattern
     *          the new pattern
     */
    public void setPattern(Perl5Pattern pattern) {
      _pattern = pattern;
    }

    /**
     * Gets the substitution.
     * 
     * @return the substitution
     */
    public Perl5Substitution getSubstitution() {
      return _substitution;
    }

    /**
     * Sets the substitution.
     * 
     * @param substitution
     *          the new substitution
     */
    public void setSubstitution(Perl5Substitution substitution) {
      _substitution = substitution;
    }
  }

}
