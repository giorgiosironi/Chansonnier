/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.queue.worker.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.eclipse.smila.connectivity.queue.worker.config package. 
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

    private final static QName _QueueWorkerConnectionsConfig_QNAME = new QName("http://www.eclipse.org/smila/queue", "QueueWorkerConnectionsConfig");
    private final static QName _QueueWorkerRouterConfig_QNAME = new QName("http://www.eclipse.org/smila/queue", "QueueWorkerRouterConfig");
    private final static QName _QueueWorkerRecordRecyclerJob_QNAME = new QName("http://www.eclipse.org/smila/queue", "QueueWorkerRecordRecyclerJob");
    private final static QName _QueueWorkerListenerConfig_QNAME = new QName("http://www.eclipse.org/smila/queue", "QueueWorkerListenerConfig");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.smila.connectivity.queue.worker.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link BrokerConnectionType }
     * 
     */
    public BrokerConnectionType createBrokerConnectionType() {
        return new BrokerConnectionType();
    }

    /**
     * Create an instance of {@link RecordRecyclerRuleType }
     * 
     */
    public RecordRecyclerRuleType createRecordRecyclerRuleType() {
        return new RecordRecyclerRuleType();
    }

    /**
     * Create an instance of {@link RecordRecyclerTaskListType }
     * 
     */
    public RecordRecyclerTaskListType createRecordRecyclerTaskListType() {
        return new RecordRecyclerTaskListType();
    }

    /**
     * Create an instance of {@link ListenerConfigType }
     * 
     */
    public ListenerConfigType createListenerConfigType() {
        return new ListenerConfigType();
    }

    /**
     * Create an instance of {@link RouterRuleType }
     * 
     */
    public RouterRuleType createRouterRuleType() {
        return new RouterRuleType();
    }

    /**
     * Create an instance of {@link SendType }
     * 
     */
    public SendType createSendType() {
        return new SendType();
    }

    /**
     * Create an instance of {@link ListenerTaskListType }
     * 
     */
    public ListenerTaskListType createListenerTaskListType() {
        return new ListenerTaskListType();
    }

    /**
     * Create an instance of {@link ListenerRuleType }
     * 
     */
    public ListenerRuleType createListenerRuleType() {
        return new ListenerRuleType();
    }

    /**
     * Create an instance of {@link QueueConnectionType }
     * 
     */
    public QueueConnectionType createQueueConnectionType() {
        return new QueueConnectionType();
    }

    /**
     * Create an instance of {@link RouterConfigType }
     * 
     */
    public RouterConfigType createRouterConfigType() {
        return new RouterConfigType();
    }

    /**
     * Create an instance of {@link ProcessType }
     * 
     */
    public ProcessType createProcessType() {
        return new ProcessType();
    }

    /**
     * Create an instance of {@link ConnectionsConfigType }
     * 
     */
    public ConnectionsConfigType createConnectionsConfigType() {
        return new ConnectionsConfigType();
    }

    /**
     * Create an instance of {@link RecordRecyclerConfigType }
     * 
     */
    public RecordRecyclerConfigType createRecordRecyclerConfigType() {
        return new RecordRecyclerConfigType();
    }

    /**
     * Create an instance of {@link RouterTaskListType }
     * 
     */
    public RouterTaskListType createRouterTaskListType() {
        return new RouterTaskListType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConnectionsConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.eclipse.org/smila/queue", name = "QueueWorkerConnectionsConfig")
    public JAXBElement<ConnectionsConfigType> createQueueWorkerConnectionsConfig(ConnectionsConfigType value) {
        return new JAXBElement<ConnectionsConfigType>(_QueueWorkerConnectionsConfig_QNAME, ConnectionsConfigType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouterConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.eclipse.org/smila/queue", name = "QueueWorkerRouterConfig")
    public JAXBElement<RouterConfigType> createQueueWorkerRouterConfig(RouterConfigType value) {
        return new JAXBElement<RouterConfigType>(_QueueWorkerRouterConfig_QNAME, RouterConfigType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordRecyclerConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.eclipse.org/smila/queue", name = "QueueWorkerRecordRecyclerJob")
    public JAXBElement<RecordRecyclerConfigType> createQueueWorkerRecordRecyclerJob(RecordRecyclerConfigType value) {
        return new JAXBElement<RecordRecyclerConfigType>(_QueueWorkerRecordRecyclerJob_QNAME, RecordRecyclerConfigType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListenerConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.eclipse.org/smila/queue", name = "QueueWorkerListenerConfig")
    public JAXBElement<ListenerConfigType> createQueueWorkerListenerConfig(ListenerConfigType value) {
        return new JAXBElement<ListenerConfigType>(_QueueWorkerListenerConfig_QNAME, ListenerConfigType.class, null, value);
    }

}

// CHECKSTYLE:ON
