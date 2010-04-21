/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configurable;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class handles the parsing of <code>sitemap.xml</code> files.
 */
public class SitemapParser implements Configurable {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(SitemapParser.class);

  /** The Constant CACHE. */
  private static final Hashtable<String, Outlink[]> CACHE = new Hashtable<String, Outlink[]>();

  /** The Constant EMPTY_LINKS. */
  private static final Outlink[] EMPTY_LINKS = new Outlink[0];

  /** The Constant SITEMAP_FILENAME. */
  private static final String[] SITEMAP_FILENAME = { "sitemap.xml", "sitemap.xml.gz", "sitemap.gz" };

  /** The _conf. */
  private Configuration _conf;

  /**
   * Creates new SitemapParser with the given configuration.
   * 
   * @param conf
   *          Configuration
   */
  public SitemapParser(Configuration conf) {
    setConf(conf);
  }

  /**
   * {@inheritDoc}
   */
  public void setConf(Configuration conf) {
    _conf = conf;
  }

  /**
   * {@inheritDoc}
   */
  public Configuration getConf() {
    return _conf;
  }

  /**
   * Returns a {@link Outlink} array with links extracted from site map file.
   * 
   * @param sitemapContent
   *          the site map content
   * 
   * @return the outlink[]
   */
  Outlink[] parseSitemapLinks(byte[] sitemapContent) {
    if (sitemapContent == null) {
      return EMPTY_LINKS;
    }
    final List<Outlink> sitemapLinks = new ArrayList<Outlink>();
    try {
      final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      final InputStream content = new ByteArrayInputStream(sitemapContent);
      final Document document = documentBuilder.parse(content);
      final Element element = document.getDocumentElement();
      final NodeList childNodeList = element.getChildNodes();
      for (int i = 0; i < childNodeList.getLength(); i++) {
        final Node sitemapNode = childNodeList.item(i);
        if (sitemapNode instanceof Element) {
          final Element sitemapElements = (Element) sitemapNode;
          final String sitemapElementsName = sitemapElements.getNodeName();
          if ("url".equals(sitemapElementsName)) {
            final NodeList urlItemsNodeList = sitemapElements.getChildNodes();
            for (int j = 0; j < urlItemsNodeList.getLength(); j++) {
              final Node urlItemNode = urlItemsNodeList.item(j);
              if (urlItemNode instanceof Element) {
                final Element urlItemElement = (Element) urlItemNode;
                final String urlItemElementName = urlItemElement.getNodeName();
                if ("loc".equals(urlItemElementName)) {
                  sitemapLinks.add(new Outlink(urlItemElement.getTextContent(), urlItemElement.getTextContent(),
                    _conf));
                }
              }
            }
          }
        }
      }
    } catch (MalformedURLException exception) {
      LOG.error("Error creationg outlink while parsing sitemap");
    } catch (Exception exception) {
      LOG.error("Error parsing sitemap");
      return EMPTY_LINKS;
    }

    final Outlink[] linksArray = new Outlink[sitemapLinks.size()];
    sitemapLinks.toArray(linksArray);
    return linksArray;
  }

  /**
   * Returns a set of {@link Outlink}s extracted from sitemap.xml loc tag.
   * 
   * @param http
   *          HttpBase object used to fetch sitemap.xml file.
   * @param url
   *          URL to fetch site map for.
   * 
   * @return Outlink[] Array of extracted outlinks. If no outlinks were found returns an empty array.
   */
  public Outlink[] getSitemapLinks(HttpBase http, URL url) {
    return getSitemapLinks(http, url, 0);
  }

  /**
   * Gets the site map links.
   * 
   * @param http
   *          the HTTP
   * @param url
   *          the URL
   * @param sitemapFilename
   *          the site map filename
   * 
   * @return the site map links
   */
  private Outlink[] getSitemapLinks(HttpBase http, URL url, int sitemapFilename) {

    final String host = url.getHost().toLowerCase(); // normalize to lower case

    Outlink[] sitemapLinks = CACHE.get(host);

    boolean cacheSitemap = true;

    if (sitemapLinks == null) { // cache miss
      if (LOG.isTraceEnabled()) {
        LOG.trace("cache miss " + url);
      }
      try {
        final URL sitemapUrl = new URL(url, "/" + SITEMAP_FILENAME[sitemapFilename]);
        final Response response = http.getResponse(sitemapUrl.toString());
        byte[] content;
        if (response.getCode() == HttpResponseCode.CODE_200) { // found site map: parse it
          content = response.getContent();
          sitemapLinks = parseSitemapLinks(content);
        } else if ((response.getCode() == HttpResponseCode.CODE_403)) {
          sitemapLinks = EMPTY_LINKS;
          // if sitemap.xml wasn't found try to fetch GZIP site map file (sitemap.xml.gz or sitemap.gz)
        } else if ((response.getCode() == HttpResponseCode.CODE_404)
          && (sitemapFilename < SITEMAP_FILENAME.length - 1)) {
          return getSitemapLinks(http, url, sitemapFilename + 1);
        } else if (response.getCode() >= HttpResponseCode.CODE_500) {
          cacheSitemap = false;
          sitemapLinks = EMPTY_LINKS;
        } else {
          sitemapLinks = EMPTY_LINKS;
        }
      } catch (Exception exception) {
        LOG.error("Couldn't get sitemap.xml for " + url + ": " + exception.toString());
        cacheSitemap = false;
        sitemapLinks = EMPTY_LINKS;
      }

      if (cacheSitemap) {
        CACHE.put(host, sitemapLinks); // cache site map for host
      }
    } else {
      // if site map was cached don't return site map links another time
      sitemapLinks = EMPTY_LINKS;
    }
    return sitemapLinks;
  }

}
