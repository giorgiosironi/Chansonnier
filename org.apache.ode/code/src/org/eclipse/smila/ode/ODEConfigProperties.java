/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ode;

import java.io.File;
import java.util.Properties;

import org.apache.ode.il.config.OdeConfigProperties;

/**
 * extends the default OdeConfigProperties to support a "persistence" property to switch from EclipseLink based
 * persistence for ProcessStore and BpelDAOs (which is SMILAs default) to the original OpenJPA or Hibernate based
 * persistence. You have to add the necessary JARs to the classpath of this bundle to use this.
 *
 * @author jschumacher
 *
 */
public class ODEConfigProperties extends OdeConfigProperties {

  /**
   * name of property configuring the persistence implementation to use: "persistence".
   */
  public static final String PROP_PERSISTENCE = "persistence";

  /**
   * value to switch to OpenJPA persistence. This is the default value.
   */
  public static final String PERSISTENCE_OPENJPA = "openjpa";

  /**
   * value to switch to EclipseLink persistence.
   */
  public static final String PERSISTENCE_ECLIPSELINK = "eclipselink";

  /**
   * value to switch to Hibernate persistence.
   */
  public static final String PERSISTENCE_HIBERNATE = "hibernate";

  /**
   * name of embedded JPA database.
   */
  private static final String VALUE_DBEMB_JPA = "jpadb";

  /**
   * name of BPELDAO factory class to use for use with EclipseLink.
   */
  // private static final String VALUE_DAOCF_ECLIPSELINK = BPELDAOConnectionFactoryImpl.class.getName();

  /**
   * name of BPELDAO factory class to use for use with OpenJPA.
   */
  private static final String VALUE_DAOCF_OPENJPA = "org.apache.ode.dao.jpa.BPELDAOConnectionFactoryImpl";

  /**
   * name of embedded hibernate database.
   */
  private static final String VALUE_DBEMB_HIB = "hibdb";

  /**
   * name of BPELDAO factory class to use for use with Hibernate.
   */
  private static final String VALUE_DAOCF_HIB = "org.apache.ode.daohib.bpel.BpelDAOConnectionFactoryImpl";

  /**
   * load properties from given file using the given prefix.
   *
   * @param cfgFile
   *          property file.
   * @param prefix
   *          property name prefix.
   */
  public ODEConfigProperties(final File cfgFile, final String prefix) {
    super(cfgFile, prefix);
  }

  /**
   * use given properties and prefix.
   *
   * @param props
   *          property map
   * @param prefix
   *          property name prefix.
   */
  public ODEConfigProperties(final Properties props, final String prefix) {
    super(props, prefix);
  }

  /**
   * @return the value of the persistence property, if set, else the default value.
   * @see #PERSISTENCE_OPENJPA
   * @see #PERSISTENCE_ECLIPSELINK
   * @see #PERSISTENCE_HIBERNATE
   */
  public String getPersistence() {
    return getProperty(PROP_PERSISTENCE, PERSISTENCE_OPENJPA);
  }

  /**
   * @return true if default OpenJPA persistence should be used, else false.
   */
  public boolean useOpenJPAPersistence() {
    return "openjpa".equalsIgnoreCase(getPersistence());
  }

  /**
   * @return true if default EclipseLink persistence should be used, else false.
   */
  public boolean useEclipseLinkPersistence() {
    return "eclipselink".equalsIgnoreCase(getPersistence());
  }

  /**
   * @return true if Hibernate persistence should be used, else false.
   */
  public boolean useHibernatePersistence() {
    return "hibernate".equalsIgnoreCase(getPersistence());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.ode.il.config.OdeConfigProperties#getDbEmbeddedName()
   */
  @Override
  public String getDbEmbeddedName() {
    if (useHibernatePersistence()) {
      return VALUE_DBEMB_HIB;
    } else {
      return VALUE_DBEMB_JPA;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.ode.il.config.OdeConfigProperties#getDAOConnectionFactory()
   */
  @Override
  public String getDAOConnectionFactory() {
    String daoCF = getProperty(PROP_DAOCF);
    if (daoCF == null) {
      if (useEclipseLinkPersistence()) {
        // not supported currently, fallback to OpenJPA
        // daoCF = VALUE_DAOCF_ECLIPSELINK;
        daoCF = VALUE_DAOCF_OPENJPA;
      } else if (useHibernatePersistence()) {
        daoCF = VALUE_DAOCF_HIB;
      } else {
        daoCF = VALUE_DAOCF_OPENJPA;
      }
    }
    return daoCF;
  }
}
