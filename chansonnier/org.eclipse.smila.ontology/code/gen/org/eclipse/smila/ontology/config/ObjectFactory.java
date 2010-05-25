/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.ontology.config;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.eclipse.smila.ontology.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.smila.ontology.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HttpStore }
     * 
     */
    public HttpStore createHttpStore() {
        return new HttpStore();
    }

    /**
     * Create an instance of {@link NativeStore }
     * 
     */
    public NativeStore createNativeStore() {
        return new NativeStore();
    }

    /**
     * Create an instance of {@link RepositoryConfig }
     * 
     */
    public RepositoryConfig createRepositoryConfig() {
        return new RepositoryConfig();
    }

    /**
     * Create an instance of {@link SesameConfiguration }
     * 
     */
    public SesameConfiguration createSesameConfiguration() {
        return new SesameConfiguration();
    }

    /**
     * Create an instance of {@link MemoryStore }
     * 
     */
    public MemoryStore createMemoryStore() {
        return new MemoryStore();
    }

    /**
     * Create an instance of {@link Stackable }
     * 
     */
    public Stackable createStackable() {
        return new Stackable();
    }

    /**
     * Create an instance of {@link RdbmsStore }
     * 
     */
    public RdbmsStore createRdbmsStore() {
        return new RdbmsStore();
    }

}

// CHECKSTYLE:ON
