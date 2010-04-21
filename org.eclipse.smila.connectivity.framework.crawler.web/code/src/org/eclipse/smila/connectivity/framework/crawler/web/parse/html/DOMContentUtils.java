/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the plugin/parse-html/src/java/org/apache/nutch/parse/html/DOMContentUtils.java from Nutch 0.8.1 
 * (see below the licene). 
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
// CHECKSTYLE:OFF
package org.eclipse.smila.connectivity.framework.crawler.web.parse.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.http.HttpResponse;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parser;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.js.JavascriptParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A collection of methods for extracting content from DOM trees.
 * 
 * This class holds a few utility methods for pulling content out of DOM nodes, such as getOutlinks, getText, etc.
 * 
 */
public class DOMContentUtils {

  private static final String JAVASCRIPT_PREFIX = "javascript:";

  private static final String ON = "on";

  private static final String HREF = "href";

  private static final String SCRIPT = "script";

  /** The Log. */
  private final Log _log = LogFactory.getLog(HttpResponse.class);

  private JavascriptParser _javascriptParser;

  public static class LinkParams {
    public String elName;

    public String attrName;

    public int childLen;

    public LinkParams(String elName, String attrName, int childLen) {
      this.elName = elName;
      this.attrName = attrName;
      this.childLen = childLen;
    }

    @Override
    public String toString() {
      return "LP[el=" + elName + ",attr=" + attrName + ",len=" + childLen + "]";
    }
  }

  private final Map<String, LinkParams> _linkParams = new HashMap<String, LinkParams>();

  private Configuration _conf;

  public DOMContentUtils(Configuration conf) {
    setConf(conf);
  }

  public void setConf(Configuration conf) {
    this._conf = conf;
    _linkParams.clear();
    _linkParams.put("a", new LinkParams("a", "href", 1));
    _linkParams.put("area", new LinkParams("area", "href", 0));
    if (conf.getBoolean("parser.html.form.use_action", false)) {
      _linkParams.put("form", new LinkParams("form", "action", 1));
    }
    _linkParams.put("frame", new LinkParams("frame", "src", 0));
    _linkParams.put("iframe", new LinkParams("iframe", "src", 0));
    _linkParams.put("script", new LinkParams("script", "src", 0));
    _linkParams.put("link", new LinkParams("link", "href", 0));
    // We don't parse images at this time
    // _linkParams.put("img", new LinkParams("img", "src", 0));

  }

  /**
   * This method takes a {@link StringBuffer} and a DOM {@link Node}, and will append all the content text found
   * beneath the DOM node to the <code>StringBuffer</code>.
   * 
   * <p>
   * 
   * If <code>abortOnNestedAnchors</code> is true, DOM traversal will be aborted and the <code>StringBuffer</code>
   * will not contain any text encountered after a nested anchor is found.
   * 
   * <p>
   * 
   * @return true if nested anchors were found
   */
  public boolean getText(StringBuffer sb, Node node, boolean abortOnNestedAnchors) {
    if (getTextHelper(sb, node, abortOnNestedAnchors, 0)) {
      return true;
    }
    return false;
  }

  /**
   * This is a convinience method, equivalent to {@link #getText(StringBuffer,Node,boolean) getText(sb, node, false)}.
   * 
   */
  public void getText(StringBuffer sb, Node node) {
    getText(sb, node, false);
  }

  // returns true if abortOnNestedAnchors is true and we find nested
  // anchors
  private boolean getTextHelper(StringBuffer sb, Node node, boolean abortOnNestedAnchors, int anchorDepth) {
    if ("script".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }
    if ("style".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }
    if (abortOnNestedAnchors && "a".equalsIgnoreCase(node.getNodeName())) {
      anchorDepth++;
      if (anchorDepth > 1) {
        return true;
      }
    }
    if (node.getNodeType() == Node.COMMENT_NODE) {
      return false;
    }
    if (node.getNodeType() == Node.TEXT_NODE) {
      // cleanup and trim the value
      String text = node.getNodeValue();
      text = text.replaceAll("\\s+", " ");
      text = text.trim();
      if (text.length() > 0) {
        if (sb.length() > 0) {
          sb.append(' ');
        }
        sb.append(text);
      }
    }
    boolean abort = false;
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        if (getTextHelper(sb, children.item(i), abortOnNestedAnchors, anchorDepth)) {
          abort = true;
          break;
        }
      }
    }
    return abort;
  }

  /**
   * This method takes a {@link StringBuffer} and a DOM {@link Node}, and will append the content text found beneath
   * the first <code>title</code> node to the <code>StringBuffer</code>.
   * 
   * @return true if a title node was found, false otherwise
   */
  public boolean getTitle(StringBuffer sb, Node node) {
    if ("body".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      if ("title".equalsIgnoreCase(node.getNodeName())) {
        getText(sb, node);
        return true;
      }
    }
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        if (getTitle(sb, children.item(i))) {
          return true;
        }
      }
    }
    return false;
  }

  /** If Node contains a BASE tag then it's HREF is returned. */
  public URL getBase(Node node) {

    // is this node a BASE tag?
    if (node.getNodeType() == Node.ELEMENT_NODE) {

      if ("body".equalsIgnoreCase(node.getNodeName())) {
        return null;
      }

      if ("base".equalsIgnoreCase(node.getNodeName())) {
        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
          Node attr = attrs.item(i);
          if ("href".equalsIgnoreCase(attr.getNodeName())) {
            try {
              return new URL(attr.getNodeValue());
            } catch (MalformedURLException exception) {
              logError(attr.getNodeValue(), exception);
            }
          }
        }
      }
    }

    // does it contain a base tag?
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        URL base = getBase(children.item(i));
        if (base != null) {
          return base;
        }
      }
    }

    // no.
    return null;
  }

  private boolean hasOnlyWhiteSpace(Node node) {
    String val = node.getNodeValue();
    for (int i = 0; i < val.length(); i++) {
      if (!Character.isWhitespace(val.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  // this only covers a few cases of empty links that are symptomatic
  // of nekohtml's DOM-fixup process...
  private boolean shouldThrowAwayLink(Node node, NodeList children, int childLen, LinkParams params) {
    if (childLen == 0) {
      // this has no inner structure
      if (params.childLen == 0) {
        return false;
      } else {
        return true;
      }
    } else if ((childLen == 1) && (children.item(0).getNodeType() == Node.ELEMENT_NODE)
      && (params.elName.equalsIgnoreCase(children.item(0).getNodeName()))) {
      // single nested link
      return true;

    } else if (childLen == 2) {

      Node c0 = children.item(0);
      Node c1 = children.item(1);

      if ((c0.getNodeType() == Node.ELEMENT_NODE) && (params.elName.equalsIgnoreCase(c0.getNodeName()))
        && (c1.getNodeType() == Node.TEXT_NODE) && hasOnlyWhiteSpace(c1)) {
        // single link followed by whitespace node
        return true;
      }

      if ((c1.getNodeType() == Node.ELEMENT_NODE) && (params.elName.equalsIgnoreCase(c1.getNodeName()))
        && (c0.getNodeType() == Node.TEXT_NODE) && hasOnlyWhiteSpace(c0)) {
        // whitespace node followed by single link
        return true;
      }

    } else if (childLen == 3) {
      Node c0 = children.item(0);
      Node c1 = children.item(1);
      Node c2 = children.item(2);

      if ((c1.getNodeType() == Node.ELEMENT_NODE) && (params.elName.equalsIgnoreCase(c1.getNodeName()))
        && (c0.getNodeType() == Node.TEXT_NODE) && (c2.getNodeType() == Node.TEXT_NODE) && hasOnlyWhiteSpace(c0)
        && hasOnlyWhiteSpace(c2)) {
        // single link surrounded by whitespace nodes
        return true;
      }
    }

    return false;
  }

  /**
   * This method finds all anchors below the supplied DOM <code>node</code>, and creates appropriate {@link Outlink}
   * records for each (relative to the supplied <code>base</code> URL), and adds them to the <code>outlinks</code>
   * {@link ArrayList}.
   * 
   * <p>
   * 
   * Links without inner structure (tags, text, etc) are discarded, as are links which contain only single nested links
   * and empty text nodes (this is a common DOM-fixup artifact, at least with nekohtml).
   */
  public void getOutlinks(URL base, List<Outlink> outlinks, Node node) {

    NodeList children = node.getChildNodes();
    int childLen = 0;
    if (children != null) {
      childLen = children.getLength();
    }

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      String nodeName = node.getNodeName().toLowerCase();
      LinkParams params = _linkParams.get(nodeName);
      if (params != null) {
        if (!shouldThrowAwayLink(node, children, childLen, params)) {

          StringBuffer linkText = new StringBuffer();
          getText(linkText, node, true);

          String target = null;
          boolean noFollow = false;
          boolean post = false;

          NamedNodeMap attrs = node.getAttributes();
          if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
              Node attr = attrs.item(i);
              String attrName = attr.getNodeName();
              if (params.attrName.equalsIgnoreCase(attrName)) {
                target = attr.getNodeValue();
              } else if ("rel".equalsIgnoreCase(attrName) && "nofollow".equalsIgnoreCase(attr.getNodeValue())) {
                noFollow = true;
              } else if ("method".equalsIgnoreCase(attrName) && "post".equalsIgnoreCase(attr.getNodeValue())) {
                post = true;
              }
            }
          }

          if (target != null && !noFollow && !post) {
            try {
              URL url = new URL(base, target);
              outlinks.add(new Outlink(url.toString(), linkText.toString().trim(), _conf));
            } catch (MalformedURLException exception) {
              logError(target, exception);
            }
          }
        }
        // this should not have any children, skip them
        if (params.childLen == 0) {
          return;
        }
      }
    }
    for (int i = 0; i < childLen; i++) {
      getOutlinks(base, outlinks, children.item(i));
    }
  }

  public void getJavascriptOutlinks(String base, List<Outlink> outlinks, Node node) {
    NodeList children = node.getChildNodes();
    int childLen = 0;
    if (children != null) {
      childLen = children.getLength();
    }

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      String nodeName = node.getNodeName();
      if (nodeName.equalsIgnoreCase(SCRIPT)) {
        StringBuffer script = new StringBuffer();
        if (childLen > 0) {
          for (int i = 0; i < childLen; i++) {
            if (i > 0) {
              script.append('\n');
            }
            script.append(children.item(i).getNodeValue());
          }
          Outlink[] links = _javascriptParser.getOutlinks(script.toString(), base, base);
          if (links.length > 0) {
            outlinks.addAll(Arrays.asList(links));
          }
          return;
        }
      } else {
        // process attributes that start with "on" like onclick, onmouseover etc.
        NamedNodeMap attributes = node.getAttributes();
        int attributesLength = attributes.getLength();
        for (int i = 0; i < attributesLength; i++) {
          Node attributeNode = attributes.item(i);
          Outlink[] links = null;
          if (attributeNode.getNodeName().startsWith(ON)) {
            links = _javascriptParser.getOutlinks(attributeNode.getNodeValue(), base, base);
          } else if (attributeNode.getNodeName().equalsIgnoreCase(HREF)) {
            String value = attributeNode.getNodeValue();
            if (value != null && value.toLowerCase().indexOf(JAVASCRIPT_PREFIX) != -1) {
              links = _javascriptParser.getOutlinks(value, base, base);
            }
          }
          if (links != null && links.length > 0) {
            outlinks.addAll(Arrays.asList(links));
          }
        }
      }
    }

    for (int i = 0; i < childLen; i++) {
      getJavascriptOutlinks(base, outlinks, children.item(i));
    }
  }

  public void setJavascriptParser(Parser javascriptParser) {
    _javascriptParser = (JavascriptParser) javascriptParser;
  }

  private void logError(String target, Throwable exception) {
    if (_log.isDebugEnabled()) {
      _log.debug("Error extracting the link from DOM tree: [" + target + "], Exception was: [" + exception.getMessage() + "]");
    }
  }
}
