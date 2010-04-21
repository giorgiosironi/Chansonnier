/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the RobotRulesParser.java from Nutch 0.8.1 (see below the licene). 
 * The original File was modified by the Smila Team 
 **********************************************************************************************************************/
/**
 * Copyright 2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.eclipse.smila.connectivity.framework.crawler.web.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configurable;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.HttpProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.Robotstxt;

/**
 * This class handles the parsing of <code>robots.txt</code> files. It emits RobotRules objects, which describe the
 * download permissions as described in RobotRulesParser.
 */
public class RobotRulesParser implements Configurable {

  /** The Constant BUFSIZE. */
  private static final int BUFSIZE = 2048;

  /** The Constant USER_AGENT. */
  private static final String USER_AGENT = "User-agent:";

  /** The Constant ALLOW. */
  private static final String ALLOW = "Allow:";

  /** The Constant DISALLOW. */
  private static final String DISALLOW = "Disallow:";

  /** The Constant CRAWL_DELAY. */
  private static final String CRAWL_DELAY = "Crawl-delay:";

  /** The Constant COLON. */
  private static final String COLON = ":";

  /** The Constant SEMICOLON. */
  private static final String SEMICOLON = ";";

  /** The Constant LOG. */
  private static final Log LOG = LogFactory.getLog(RobotRulesParser.class);

  /** The Constant CACHE. */
  private static final Hashtable<String, RobotRuleSet> CACHE = new Hashtable<String, RobotRuleSet>();

  /** The Constant CHARACTER_ENCODING. */
  private static final String CHARACTER_ENCODING = "UTF-8";

  /** The Constant NO_PRECEDENCE. */
  private static final int NO_PRECEDENCE = Integer.MAX_VALUE;

  /** The Constant EMPTY_RULES. */
  private static final RobotRuleSet EMPTY_RULES = new RobotRuleSet();

  /** The Constant FORBID_ALL_RULES. */
  private static final RobotRuleSet FORBID_ALL_RULES = getForbidAllRules();

  /** The _allow forbidden. */
  private boolean _allowForbidden;

  /** The _conf. */
  private Configuration _conf;

  /** The _policy. */
  private Robotstxt _policy;

  /** The _policy value. */
  private String _policyValue;

  /** The _robot names. */
  private Map<String, Integer> _robotNames;

  /**
   * This class holds the rules which were parsed from a robots.txt file, and can test paths against those rules.
   */
  public static class RobotRuleSet {

    /** The _tmp entries. */
    private List<RobotsEntry> _tmpEntries = new ArrayList<RobotsEntry>();

    /** The _entries. */
    private RobotsEntry[] _entries;

    /** The _expire time. */
    private long _expireTime;

    /** The _crawl delay. */
    private long _crawlDelay = -1;

    /**
     * The Class RobotsEntry.
     */
    private class RobotsEntry {

      /** The _prefix. */
      private final String _prefix;

      /** The _allowed. */
      private final boolean _allowed;

      /**
       * Instantiates a new robots entry.
       * 
       * @param prefix
       *          the prefix
       * @param allowed
       *          the allowed
       */
      RobotsEntry(final String prefix, final boolean allowed) {
        _prefix = prefix;
        _allowed = allowed;
      }

      /**
       * Gets the prefix.
       * 
       * @return the prefix
       */
      public String getPrefix() {
        return _prefix;
      }

      /**
       * Checks if is allowed.
       * 
       * @return true, if is allowed
       */
      public boolean isAllowed() {
        return _allowed;
      }
    }

    /**
     * Adds the prefix.
     * 
     * @param prefix
     *          the prefix
     * @param allow
     *          the allow
     */
    private void addPrefix(final String prefix, final boolean allow) {
      if (_tmpEntries == null) {
        _tmpEntries = new ArrayList<RobotsEntry>();
        if (_entries != null) {
          for (int i = 0; i < _entries.length; i++) {
            _tmpEntries.add(_entries[i]);
          }
        }
        _entries = null;
      }

      _tmpEntries.add(new RobotsEntry(prefix, allow));
    }

    /**
     * Clear prefixes.
     */
    private void clearPrefixes() {
      if (_tmpEntries == null) {
        _tmpEntries = new ArrayList<RobotsEntry>();
        _entries = null;
      } else {
        _tmpEntries.clear();
      }
    }

    /**
     * Change when the rule set goes stale.
     * 
     * @param expireTime
     *          time when the rule set goes stale
     */
    public void setExpireTime(final long expireTime) {
      _expireTime = expireTime;
    }

    /**
     * Get expire time.
     * 
     * @return long
     */
    public long getExpireTime() {
      return _expireTime;
    }

    /**
     * Get Crawl-Delay, in milliseconds. This returns -1 if not set.
     * 
     * @return long crawlDelay
     */
    public long getCrawlDelay() {
      return _crawlDelay;
    }

    /**
     * Set Crawl-Delay, in milliseconds.
     * 
     * @param crawlDelay
     *          long
     */
    public void setCrawlDelay(final long crawlDelay) {
      this._crawlDelay = crawlDelay;
    }

    /**
     * Returns <code>false</code> if the <code>robots.txt</code> file prohibits us from accessing the given
     * <code>path</code>, or <code>true</code> otherwise.
     * 
     * @param path
     *          String
     * 
     * @return boolean
     */
    public boolean isAllowed(String path) {
      try {
        path = URLDecoder.decode(path, CHARACTER_ENCODING);
      } catch (final UnsupportedEncodingException e) {
        // just ignore it- we can still try to match
        // path prefixes
        LOG.debug("Couldn't decode the path specified: " + path);
      }

      if (_entries == null) {
        _entries = new RobotsEntry[_tmpEntries.size()];
        _entries = _tmpEntries.toArray(_entries);
        _tmpEntries = null;
      }

      int pos = 0;
      final int end = _entries.length;
      while (pos < end) {
        if (path.startsWith(_entries[pos].getPrefix())) {
          return _entries[pos].isAllowed();
        }
        pos++;
      }

      return true;
    }

    /**
     * Returns readable representation of robot rules.
     * 
     * @return String
     */
    @Override
    public String toString() {
      isAllowed("x"); // force String[] representation
      final StringBuffer buf = new StringBuffer();
      for (int i = 0; i < _entries.length; i++) {
        if (_entries[i].isAllowed()) {
          buf.append("Allow: " + _entries[i].getPrefix() + System.getProperty("line.separator"));
        } else {
          buf.append("Disallow: " + _entries[i].getPrefix() + System.getProperty("line.separator"));
        }
      }
      return buf.toString();
    }

  }

  /**
   * Instantiates a new robot rules parser.
   */
  RobotRulesParser() {
  }

  /**
   * Creates a new <code>RobotRulesParser</code> which will use the supplied <code>_robotNames</code> when choosing
   * which stanza to follow in <code>robots.txt</code> files. Any name in the array may be matched. The order of the
   * <code>_robotNames</code> determines the precedence- if many names are matched, only the rules associated with the
   * robot name having the smallest index will be used.
   * 
   * @param robotNames
   *          the robot names
   */
  RobotRulesParser(final String[] robotNames) {
    setRobotNames(robotNames);
  }

  /**
   * Creates new RobotRulesParser with the given configuration.
   * 
   * @param conf
   *          Configuration
   */
  public RobotRulesParser(final Configuration conf) {
    setConf(conf);
  }

  /**
   * {@inheritDoc}
   */
  public void setConf(final Configuration conf) {
    _conf = conf;
    _policy = Robotstxt.valueOf(conf.get(HttpProperties.ROBOTSTXT_POLICY).toUpperCase());
    _policyValue = conf.get(HttpProperties.ROBOTSTXT_VALUE);
    _allowForbidden = conf.getBoolean(HttpProperties.ROBOTSTXT_403_ALLOW, false);

    // Grab the agent names we advertise to robots files.
    final String agentName = conf.get(HttpProperties.AGENT_NAME);
    final String agentNames = conf.get(HttpProperties.ROBOTSTXT_AGENT_NAMES);
    final StringTokenizer tok = new StringTokenizer(agentNames, SEMICOLON);
    final List<String> agents = new ArrayList<String>();
    while (tok.hasMoreTokens()) {
      agents.add(tok.nextToken().trim());
    }
    //
    // If there are no agents for robots-parsing, use our
    // default agent-string. If both are present, our agent-string
    // should be the first one we advertise to robots-parsing.
    //
    if (agents.size() == 0) {
      agents.add(agentName);
      if (LOG.isDebugEnabled()) {
        LOG.debug("No agents listed in AgentNames attribute!");
      }
    } else if (!(agents.get(0)).equalsIgnoreCase(agentName)) {
      agents.add(0, agentName);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Agent we advertise (" + agentName + ") not listed first in AgentNames attribute!");
      }
    }
    setRobotNames(agents.toArray(new String[agents.size()]));
  }

  /**
   * {@inheritDoc}
   */
  public Configuration getConf() {
    return _conf;
  }

  /**
   * Sets the robot names.
   * 
   * @param robotNames
   *          the new robot names
   */
  private void setRobotNames(final String[] robotNames) {
    this._robotNames = new HashMap<String, Integer>();
    for (int i = 0; i < robotNames.length; i++) {
      this._robotNames.put(robotNames[i].toLowerCase(), new Integer(i));
    }
    // always make sure "*" is included
    if (!this._robotNames.containsKey("*")) {
      this._robotNames.put("*", new Integer(robotNames.length));
    }
  }

  /**
   * Returns a {@link RobotRuleSet} object which encapsulates the rules parsed from the supplied
   * <code>robotContent</code>.
   * 
   * @param robotContent
   *          the robot content
   * 
   * @return the robot rule set
   */
  RobotRuleSet parseRules(final byte[] robotContent) {
    if (robotContent == null) {
      return EMPTY_RULES;
    }
    final String content = new String(robotContent);

    final StringTokenizer lineParser = new StringTokenizer(content, "\n\r");

    RobotRuleSet bestRulesSoFar = null;
    int bestPrecedenceSoFar = NO_PRECEDENCE;

    RobotRuleSet currentRules = new RobotRuleSet();
    int currentPrecedence = NO_PRECEDENCE;

    boolean addRules = false; // in stanza for our robot
    boolean doneAgents = false; // detect multiple agent lines

    while (lineParser.hasMoreTokens()) {
      String line = lineParser.nextToken();

      // trim out comments and whitespace
      final int hashPos = line.indexOf("#");
      if (hashPos >= 0) {
        line = line.substring(0, hashPos);
      }
      line = line.trim();

      if ((line.length() >= USER_AGENT.length())
        && (line.substring(0, USER_AGENT.length()).equalsIgnoreCase(USER_AGENT))) {
        if (doneAgents) {
          if (currentPrecedence < bestPrecedenceSoFar) {
            bestPrecedenceSoFar = currentPrecedence;
            bestRulesSoFar = currentRules;
            currentPrecedence = NO_PRECEDENCE;
            currentRules = new RobotRuleSet();
          }
          addRules = false;
        }
        doneAgents = false;

        String agentNames = line.substring(line.indexOf(COLON) + 1);
        agentNames = agentNames.trim();
        final StringTokenizer agentTokenizer = new StringTokenizer(agentNames);

        while (agentTokenizer.hasMoreTokens()) {
          // for each agent listed, see if it's us:
          final String agentName = agentTokenizer.nextToken().toLowerCase();

          final Integer precedenceInt = _robotNames.get(agentName);

          if (precedenceInt != null) {
            final int precedence = precedenceInt.intValue();
            if ((precedence < currentPrecedence) && (precedence < bestPrecedenceSoFar)) {
              currentPrecedence = precedence;
            }
          }
        }

        if (currentPrecedence < bestPrecedenceSoFar) {
          addRules = true;
        }

      } else if ((line.length() >= DISALLOW.length())
        && (line.substring(0, DISALLOW.length()).equalsIgnoreCase(DISALLOW))) {

        doneAgents = true;
        String path = line.substring(line.indexOf(COLON) + 1);
        path = path.trim();
        try {
          path = URLDecoder.decode(path, CHARACTER_ENCODING);
        } catch (final UnsupportedEncodingException e) {
          LOG.warn("error parsing robots rules- can't decode path: " + path);
        }
        if (path.length() == 0) { // "empty rule"
          if (addRules) {
            currentRules.clearPrefixes();
          }
        } else { // rule with path
          if (addRules) {
            currentRules.addPrefix(path, false);
          }
        }

      } else if ((line.length() >= ALLOW.length()) && (line.substring(0, ALLOW.length()).equalsIgnoreCase(ALLOW))) {

        doneAgents = true;
        String path = line.substring(line.indexOf(COLON) + 1);
        path = path.trim();

        if (path.length() == 0) {
          // "empty rule"- treat same as empty disallow
          if (addRules) {
            currentRules.clearPrefixes();
          }
        } else { // rule with path
          if (addRules) {
            currentRules.addPrefix(path, true);
          }
        }
      } else if ((line.length() >= CRAWL_DELAY.length())
        && (line.substring(0, CRAWL_DELAY.length()).equalsIgnoreCase(CRAWL_DELAY))) {
        doneAgents = true;
        long crawlDelay = -1;
        final String delay = line.substring("Crawl-Delay:".length(), line.length()).trim();
        if (delay.length() > 0) {
          try {
            crawlDelay = Long.parseLong(delay) * Configuration.MILLIS_PER_SECOND; // sec to millisec
          } catch (final NumberFormatException exception) {
            LOG.info("can not parse Crawl-Delay:" + exception.toString());
          }
          currentRules.setCrawlDelay(crawlDelay);
        }
      }
    }

    if (currentPrecedence < bestPrecedenceSoFar) {
      bestPrecedenceSoFar = currentPrecedence;
      bestRulesSoFar = currentRules;
    }

    if (bestPrecedenceSoFar == NO_PRECEDENCE) {
      return EMPTY_RULES;
    }
    return bestRulesSoFar;
  }

  /**
   * Returns a <code>RobotRuleSet</code> object appropriate for use when the <code>robots.txt</code> file is empty
   * or missing; all requests are _allowed.
   * 
   * @return the empty rules
   */
  static RobotRuleSet getEmptyRules() {
    return EMPTY_RULES;
  }

  /**
   * Returns a <code>RobotRuleSet</code> object appropriate for use when the <code>robots.txt</code> file is not
   * fetched due to a <code>403/Forbidden</code> response; all requests are disallowed.
   * 
   * @return the forbid all rules
   */
  static RobotRuleSet getForbidAllRules() {
    final RobotRuleSet rules = new RobotRuleSet();
    rules.addPrefix("", false);
    return rules;
  }

  /**
   * Gets the robot rules set.
   * 
   * @param http
   *          the HTTP
   * @param url
   *          the URL
   * 
   * @return the robot rules set
   */
  private RobotRuleSet getRobotRulesSet(final HttpBase http, final URL url) {

    final String host = url.getHost().toLowerCase(); // normalize to lower case

    RobotRuleSet robotRules = CACHE.get(host);

    boolean cacheRule = true;

    if (robotRules == null) { // cache miss
      if (LOG.isTraceEnabled()) {
        LOG.trace("cache miss " + url);
      }
      try {

        final URL robotstxtUrl = new URL(url, "/robots.txt");
        final Response response = http.getResponse(robotstxtUrl.toString());

        if (response.getCode() == HttpResponseCode.CODE_200) { // found rules: parse them
          robotRules = parseRules(response.getContent());
        } else if ((response.getCode() == HttpResponseCode.CODE_403) && (!_allowForbidden)) {
          robotRules = FORBID_ALL_RULES; // use forbid all
        } else if (response.getCode() >= HttpResponseCode.CODE_500) {
          cacheRule = false;
          robotRules = EMPTY_RULES;
        } else {
          robotRules = EMPTY_RULES; // use default rules
        }
      } catch (final Exception exception) {
        LOG.info("Couldn't get robots.txt for " + url + ": " + exception.toString());
        cacheRule = false;
        robotRules = EMPTY_RULES;
      }

      if (cacheRule) {
        CACHE.put(host, robotRules); // cache rules for host
      }
    }
    return robotRules;
  }

  /**
   * Gets the robot rules set.
   * 
   * @param robotsFile
   *          the robots file
   * @param url
   *          the URL
   * 
   * @return the robot rules set
   */
  private RobotRuleSet getRobotRulesSet(final String robotsFile, final URL url) {
    final String host = url.getHost().toLowerCase(); // normalize to lower case

    RobotRuleSet robotRules = CACHE.get(host);

    boolean cacheRule = true;

    if (robotRules == null) { // cache miss
      LOG.debug("Robotstxt cache miss " + url);
      try {
        final FileInputStream robotsIn = new FileInputStream(robotsFile);

        final List<byte[]> bufs = new ArrayList<byte[]>();
        byte[] buf = new byte[BUFSIZE];
        int totBytes = 0;

        int rsize = robotsIn.read(buf);
        while (rsize >= 0) {
          totBytes += rsize;
          if (rsize != BUFSIZE) {
            final byte[] tmp = new byte[rsize];
            System.arraycopy(buf, 0, tmp, 0, rsize);
            bufs.add(tmp);
          } else {
            bufs.add(buf);
            buf = new byte[BUFSIZE];
          }
          rsize = robotsIn.read(buf);
        }

        final byte[] robotsBytes = new byte[totBytes];
        int pos = 0;

        for (int i = 0; i < bufs.size(); i++) {
          final byte[] currBuf = bufs.get(i);
          final int currBufLen = currBuf.length;
          System.arraycopy(currBuf, 0, robotsBytes, pos, currBufLen);
          pos += currBufLen;
        }

        robotRules = parseRules(robotsBytes);
        LOG.debug("Rules from file:" + robotsFile);
        LOG.debug(robotRules);
      } catch (final IOException exception) {
        LOG.error("Error reading robots.txt file: " + robotsFile);
        cacheRule = false;
        robotRules = EMPTY_RULES;
      }
      if (cacheRule) {
        CACHE.put(host, robotRules); // cache rules for host
      }
    }
    return robotRules;
  }

  /**
   * Returns <code>true</code> if the URL is allowed for fetching and <code>false</code> otherwise.
   * 
   * @param http
   *          {@link HttpBase} object that is used to get the robots.txt contents.
   * @param url
   *          URL to be checked.
   * 
   * @return boolean
   */
  public boolean isAllowed(final HttpBase http, final URL url) {
    String path = url.getPath(); // check rules
    if ((path == null) || "".equals(path)) {
      path = "/";
    }

    if (_policy.equals(Robotstxt.IGNORE)) {
      return true;
    } else if (_policy.equals(Robotstxt.CLASSIC)) {
      return getRobotRulesSet(http, url).isAllowed(path);
    } else if (_policy.equals(Robotstxt.CUSTOM)) {
      return getRobotRulesSet(_policyValue, url).isAllowed(path);
    } else if (_policy.equals(Robotstxt.SET)) {
      final String[] sd = _policyValue.split(SEMICOLON);
      setRobotNames(sd);
      return getRobotRulesSet(http, url).isAllowed(path);
    }
    return getRobotRulesSet(http, url).isAllowed(path);
  }

  /**
   * Returns a Crawl-Delay value extracted from robots.txt file.
   * 
   * @param http
   *          {@link HttpBase} object that is used to get the robots.txt contents.
   * @param url
   *          URL
   * 
   * @return long Crawl-Delay in milliseconds; -1 if not set.
   */
  public long getCrawlDelay(final HttpBase http, final URL url) {
    return getRobotRulesSet(http, url).getCrawlDelay();
  }
}
