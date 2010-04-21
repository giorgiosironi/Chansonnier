/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.HttpProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.FollowLinksType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite;

/**
 * The Class TestConfiguration.
 * @author Alexander Eliseyev
 */
public class TestConfiguration extends TestCase {
  
  /**
   * Test web site configuration.
   * 
   * @throws Exception
   *           the exception
   */
  public void testWebSiteConfiguration() throws Exception {
    final WebSite.Robotstxt robotstxt = new WebSite.Robotstxt();
    robotstxt.setValue("ROBOTSTXT_VALUE");
    robotstxt.setAgentNames("ROBOTSTXT_AGENT_NAMES");
    
    final WebSite.Seeds seeds = new WebSite.Seeds();
    seeds.setFollowLinks(FollowLinksType.FOLLOW);
    
    final WebSite.Proxy proxy = new WebSite.Proxy();
    final WebSite.Proxy.ProxyServer proxyServer = new WebSite.Proxy.ProxyServer();
    proxyServer.setHost("google.com");
    proxyServer.setPort("80");
    proxyServer.setLogin("login");
    proxyServer.setPassword("password");
    proxy.setProxyServer(proxyServer);
    
    final WebSite.Authentication authentication = new WebSite.Authentication();
    
    final WebSite webSite = new WebSite();
    webSite.setRobotstxt(robotstxt);
    webSite.setSeeds(seeds);
    webSite.setProxy(proxy);
    webSite.setAuthentication(authentication);
    
    final Configuration configuration = new Configuration();
    configuration.loadConfiguration(webSite);
    
    assertEquals("CLASSIC", configuration.get(HttpProperties.ROBOTSTXT_POLICY));
    assertEquals("ROBOTSTXT_VALUE", configuration.get(HttpProperties.ROBOTSTXT_VALUE));
    assertEquals("ROBOTSTXT_AGENT_NAMES", configuration.get(HttpProperties.ROBOTSTXT_AGENT_NAMES));
    
    assertEquals(FollowLinksType.FOLLOW, configuration.getFollowLinks());
    
    assertEquals("google.com", configuration.get(HttpProperties.PROXY_HOST));
    assertEquals("80", configuration.get(HttpProperties.PROXY_PORT));
    assertEquals("login", configuration.get(HttpProperties.PROXY_LOGIN));
    assertEquals("password", configuration.get(HttpProperties.PROXY_PASSWORD));
  }
  
  /**
   * Test configuration.
   * 
   * @throws Exception
   *           the exception
   */
  public void testConfiguration() throws Exception {
    final Properties testProps = new Properties();
    testProps.put("key1", "value1");
    
    final Configuration configuration = new Configuration(testProps);
    assertEquals("value1", configuration.get("key1"));
  }

  
}
