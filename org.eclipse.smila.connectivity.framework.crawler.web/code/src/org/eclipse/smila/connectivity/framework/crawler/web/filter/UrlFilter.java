/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.httpclient.protocol.Protocol;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.Filters.Filter.Refinements;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.Filters.Filter.Refinements.Port;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.Filters.Filter.Refinements.TimeOfDay;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;

/**
 * Abstract class that holds common for all URL filters functionality.
 * 
 */
public abstract class UrlFilter extends WorkTypeFilter<Outlink> {

  /** The Constant SECONDS_IN_MINUTE. */
  private static final int SECONDS_IN_MINUTE = 60;

  /** The value. */
  private String _value;

  /** The refinements. */
  private Refinements _refinements;

  /**
   * Empty constructor.
   * 
   */
  public UrlFilter() {

  }

  /**
   * Returns filter value.
   * 
   * @return String
   */
  public String getValue() {
    return _value;
  }

  /**
   * Assigns filter value.
   * 
   * @param value
   *          String
   */
  public void setValue(final String value) {
    _value = value;
  }

  /**
   * Returns refinements for the filter.
   * 
   * @return DRefinements
   */
  public Refinements getRefinements() {
    return _refinements;
  }

  /**
   * Assigns refinements to the filter.
   * 
   * @param refinements
   *          DRefinements
   */
  public void setRefinements(final Refinements refinements) {
    _refinements = refinements;
  }

  /**
   * Checks if the filter is enabled for the given link according to filter refinements.
   * 
   * @param link
   *          Outlink to be tested
   * @return true or false
   */
  public boolean isEnabled(final Outlink link) {
    if (_refinements != null) {

      int from = 0;
      int to = 0;
      final TimeOfDay timeOfDay = _refinements.getTimeOfDay();
      if (timeOfDay != null) {
        XMLGregorianCalendar cl = timeOfDay.getFrom();
        from =
          cl.getHour() * SECONDS_IN_MINUTE * SECONDS_IN_MINUTE + cl.getMinute() * SECONDS_IN_MINUTE
            + cl.getSecond();

        cl = timeOfDay.getTo();
        to =
          cl.getHour() * SECONDS_IN_MINUTE * SECONDS_IN_MINUTE + cl.getMinute() * SECONDS_IN_MINUTE
            + cl.getSecond();
      } else {
        return false;
      }

      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());

      final int now =
        calendar.get(Calendar.HOUR_OF_DAY) * SECONDS_IN_MINUTE * SECONDS_IN_MINUTE + calendar.get(Calendar.MINUTE)
          * SECONDS_IN_MINUTE + calendar.get(Calendar.SECOND);

      if ((now < from) || (now > to)) {
        return false;
      }

      final Port port = _refinements.getPort();
      if (port != null) {
        final int iPort = _refinements.getPort().getNumber().intValue();
        int linkPort = link.getUrl().getPort();
        // if port is default for protocol port number is set to -1 in URL object
        if (linkPort == -1) {
          linkPort = Protocol.getProtocol(link.getUrl().getProtocol()).getDefaultPort();
        }

        if ((iPort > 0) && linkPort != iPort) {
          return false;
        }
      } else {
        return false;
      }

    }

    return true;
  }
}
