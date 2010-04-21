/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Contributors: Andrey Basalaev (brox IT Solutions GmbH) - initial creator, Ivan Churkin (brox IT Solutions GmbH)
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.crawler.web.messages;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for WebSite complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WebSite">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserAgent" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Email" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Robotstxt" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="Policy" type="{}Robotstxt" default="Classic" />
 *                 &lt;attribute name="Value" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *                 &lt;attribute name="AgentNames" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CrawlingModel" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="Type" use="required" type="{}ModelType" />
 *                 &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CrawlScope" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Filters" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Filter" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;extension base="{}Filter">
 *                                   &lt;/extension>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Type" type="{}CrawlScope" default="Host" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CrawlLimits" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SizeLimits" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="MaxBytesDownload" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="MaxDocumentDownload" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="MaxTimeSec" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="MaxLengthBytes" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="LimitRate" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="TimeoutLimits" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Timeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="DnsTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="ConnectTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="ReadTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="900" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="WaitLimits" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Wait" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="RandomWait" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *                           &lt;attribute name="WaitRetry" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                           &lt;attribute name="MaxRetries" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Proxy" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="ProxyServer">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Host" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Login" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *                           &lt;attribute name="Password" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="AutomaticConfiguration">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Address" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Authentication" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Rfc2617" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Host" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Realm" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Login" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="HtmlForm" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="FormElements">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="FormElement" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="Key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="CredentialDomain" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="LoginUri" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="HttpMethod" use="required" type="{}HttpMethod" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SslCertificate" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ProtocolName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="TruststoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="TruststorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *                           &lt;attribute name="KeystoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="KeystorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Ssl" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="TruststoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="TruststorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Seeds">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Seed" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="FollowLinks" type="{}FollowLinksType" default="Follow" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Filters" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Filter" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{}Filter">
 *                           &lt;sequence>
 *                             &lt;element name="Refinements" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="TimeOfDay" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="From" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
 *                                               &lt;attribute name="To" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="Port" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="Number" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/extension>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="MetaTagFilters" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MetaTagFilter" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Type" use="required" type="{}HtmlMetaTagType" />
 *                           &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Content" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="WorkType" use="required" type="{}FilterWorkType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="ProjectName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Sitemaps" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="Header" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       &lt;attribute name="Referer" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       &lt;attribute name="EnableCookies" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebSite", propOrder = {
    "userAgent",
    "robotstxt",
    "crawlingModel",
    "crawlScope",
    "crawlLimits",
    "proxy",
    "authentication",
    "ssl",
    "seeds",
    "filters",
    "metaTagFilters"
})
public class WebSite
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "UserAgent")
    protected WebSite.UserAgent userAgent;
    @XmlElement(name = "Robotstxt")
    protected WebSite.Robotstxt robotstxt;
    @XmlElement(name = "CrawlingModel")
    protected WebSite.CrawlingModel crawlingModel;
    @XmlElement(name = "CrawlScope")
    protected WebSite.CrawlScope crawlScope;
    @XmlElement(name = "CrawlLimits")
    protected WebSite.CrawlLimits crawlLimits;
    @XmlElement(name = "Proxy")
    protected WebSite.Proxy proxy;
    @XmlElement(name = "Authentication")
    protected WebSite.Authentication authentication;
    @XmlElement(name = "Ssl")
    protected WebSite.Ssl ssl;
    @XmlElement(name = "Seeds", required = true)
    protected WebSite.Seeds seeds;
    @XmlElement(name = "Filters")
    protected WebSite.Filters filters;
    @XmlElement(name = "MetaTagFilters")
    protected WebSite.MetaTagFilters metaTagFilters;
    @XmlAttribute(name = "ProjectName", required = true)
    protected String projectName;
    @XmlAttribute(name = "Sitemaps")
    protected Boolean sitemaps;
    @XmlAttribute(name = "Header")
    protected String header;
    @XmlAttribute(name = "Referer")
    protected String referer;
    @XmlAttribute(name = "EnableCookies")
    protected Boolean enableCookies;

    /**
     * Gets the value of the userAgent property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.UserAgent }
     *     
     */
    public WebSite.UserAgent getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the value of the userAgent property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.UserAgent }
     *     
     */
    public void setUserAgent(WebSite.UserAgent value) {
        this.userAgent = value;
    }

    /**
     * Gets the value of the robotstxt property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.Robotstxt }
     *     
     */
    public WebSite.Robotstxt getRobotstxt() {
        return robotstxt;
    }

    /**
     * Sets the value of the robotstxt property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.Robotstxt }
     *     
     */
    public void setRobotstxt(WebSite.Robotstxt value) {
        this.robotstxt = value;
    }

    /**
     * Gets the value of the crawlingModel property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.CrawlingModel }
     *     
     */
    public WebSite.CrawlingModel getCrawlingModel() {
        return crawlingModel;
    }

    /**
     * Sets the value of the crawlingModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.CrawlingModel }
     *     
     */
    public void setCrawlingModel(WebSite.CrawlingModel value) {
        this.crawlingModel = value;
    }

    /**
     * Gets the value of the crawlScope property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.CrawlScope }
     *     
     */
    public WebSite.CrawlScope getCrawlScope() {
        return crawlScope;
    }

    /**
     * Sets the value of the crawlScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.CrawlScope }
     *     
     */
    public void setCrawlScope(WebSite.CrawlScope value) {
        this.crawlScope = value;
    }

    /**
     * Gets the value of the crawlLimits property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.CrawlLimits }
     *     
     */
    public WebSite.CrawlLimits getCrawlLimits() {
        return crawlLimits;
    }

    /**
     * Sets the value of the crawlLimits property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.CrawlLimits }
     *     
     */
    public void setCrawlLimits(WebSite.CrawlLimits value) {
        this.crawlLimits = value;
    }

    /**
     * Gets the value of the proxy property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.Proxy }
     *     
     */
    public WebSite.Proxy getProxy() {
        return proxy;
    }

    /**
     * Sets the value of the proxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.Proxy }
     *     
     */
    public void setProxy(WebSite.Proxy value) {
        this.proxy = value;
    }

    /**
     * Gets the value of the authentication property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.Authentication }
     *     
     */
    public WebSite.Authentication getAuthentication() {
        return authentication;
    }

    /**
     * Sets the value of the authentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.Authentication }
     *     
     */
    public void setAuthentication(WebSite.Authentication value) {
        this.authentication = value;
    }

    /**
     * Gets the value of the ssl property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.Ssl }
     *     
     */
    public WebSite.Ssl getSsl() {
        return ssl;
    }

    /**
     * Sets the value of the ssl property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.Ssl }
     *     
     */
    public void setSsl(WebSite.Ssl value) {
        this.ssl = value;
    }

    /**
     * Gets the value of the seeds property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.Seeds }
     *     
     */
    public WebSite.Seeds getSeeds() {
        return seeds;
    }

    /**
     * Sets the value of the seeds property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.Seeds }
     *     
     */
    public void setSeeds(WebSite.Seeds value) {
        this.seeds = value;
    }

    /**
     * Gets the value of the filters property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.Filters }
     *     
     */
    public WebSite.Filters getFilters() {
        return filters;
    }

    /**
     * Sets the value of the filters property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.Filters }
     *     
     */
    public void setFilters(WebSite.Filters value) {
        this.filters = value;
    }

    /**
     * Gets the value of the metaTagFilters property.
     * 
     * @return
     *     possible object is
     *     {@link WebSite.MetaTagFilters }
     *     
     */
    public WebSite.MetaTagFilters getMetaTagFilters() {
        return metaTagFilters;
    }

    /**
     * Sets the value of the metaTagFilters property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSite.MetaTagFilters }
     *     
     */
    public void setMetaTagFilters(WebSite.MetaTagFilters value) {
        this.metaTagFilters = value;
    }

    /**
     * Gets the value of the projectName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the value of the projectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectName(String value) {
        this.projectName = value;
    }

    /**
     * Gets the value of the sitemaps property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSitemaps() {
        if (sitemaps == null) {
            return false;
        } else {
            return sitemaps;
        }
    }

    /**
     * Sets the value of the sitemaps property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSitemaps(Boolean value) {
        this.sitemaps = value;
    }

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeader() {
        if (header == null) {
            return "";
        } else {
            return header;
        }
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeader(String value) {
        this.header = value;
    }

    /**
     * Gets the value of the referer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferer() {
        if (referer == null) {
            return "";
        } else {
            return referer;
        }
    }

    /**
     * Sets the value of the referer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferer(String value) {
        this.referer = value;
    }

    /**
     * Gets the value of the enableCookies property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnableCookies() {
        if (enableCookies == null) {
            return true;
        } else {
            return enableCookies;
        }
    }

    /**
     * Sets the value of the enableCookies property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnableCookies(Boolean value) {
        this.enableCookies = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Rfc2617" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Host" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Realm" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Login" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="HtmlForm" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="FormElements">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="FormElement" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="Key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="CredentialDomain" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="LoginUri" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="HttpMethod" use="required" type="{}HttpMethod" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="SslCertificate" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="ProtocolName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="TruststoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="TruststorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *                 &lt;attribute name="KeystoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="KeystorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "rfc2617",
        "htmlForm",
        "sslCertificate"
    })
    public static class Authentication
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "Rfc2617")
        protected List<WebSite.Authentication.Rfc2617> rfc2617;
        @XmlElement(name = "HtmlForm")
        protected List<WebSite.Authentication.HtmlForm> htmlForm;
        @XmlElement(name = "SslCertificate")
        protected List<WebSite.Authentication.SslCertificate> sslCertificate;

        /**
         * Gets the value of the rfc2617 property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the rfc2617 property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRfc2617().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WebSite.Authentication.Rfc2617 }
         * 
         * 
         */
        public List<WebSite.Authentication.Rfc2617> getRfc2617() {
            if (rfc2617 == null) {
                rfc2617 = new ArrayList<WebSite.Authentication.Rfc2617>();
            }
            return this.rfc2617;
        }

        /**
         * Gets the value of the htmlForm property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the htmlForm property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHtmlForm().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WebSite.Authentication.HtmlForm }
         * 
         * 
         */
        public List<WebSite.Authentication.HtmlForm> getHtmlForm() {
            if (htmlForm == null) {
                htmlForm = new ArrayList<WebSite.Authentication.HtmlForm>();
            }
            return this.htmlForm;
        }

        /**
         * Gets the value of the sslCertificate property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sslCertificate property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSslCertificate().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WebSite.Authentication.SslCertificate }
         * 
         * 
         */
        public List<WebSite.Authentication.SslCertificate> getSslCertificate() {
            if (sslCertificate == null) {
                sslCertificate = new ArrayList<WebSite.Authentication.SslCertificate>();
            }
            return this.sslCertificate;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="FormElements">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="FormElement" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="Key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="CredentialDomain" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="LoginUri" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="HttpMethod" use="required" type="{}HttpMethod" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "formElements"
        })
        public static class HtmlForm
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlElement(name = "FormElements", required = true)
            protected WebSite.Authentication.HtmlForm.FormElements formElements;
            @XmlAttribute(name = "CredentialDomain", required = true)
            protected String credentialDomain;
            @XmlAttribute(name = "LoginUri", required = true)
            protected String loginUri;
            @XmlAttribute(name = "HttpMethod", required = true)
            protected HttpMethod httpMethod;

            /**
             * Gets the value of the formElements property.
             * 
             * @return
             *     possible object is
             *     {@link WebSite.Authentication.HtmlForm.FormElements }
             *     
             */
            public WebSite.Authentication.HtmlForm.FormElements getFormElements() {
                return formElements;
            }

            /**
             * Sets the value of the formElements property.
             * 
             * @param value
             *     allowed object is
             *     {@link WebSite.Authentication.HtmlForm.FormElements }
             *     
             */
            public void setFormElements(WebSite.Authentication.HtmlForm.FormElements value) {
                this.formElements = value;
            }

            /**
             * Gets the value of the credentialDomain property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCredentialDomain() {
                return credentialDomain;
            }

            /**
             * Sets the value of the credentialDomain property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCredentialDomain(String value) {
                this.credentialDomain = value;
            }

            /**
             * Gets the value of the loginUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLoginUri() {
                return loginUri;
            }

            /**
             * Sets the value of the loginUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLoginUri(String value) {
                this.loginUri = value;
            }

            /**
             * Gets the value of the httpMethod property.
             * 
             * @return
             *     possible object is
             *     {@link HttpMethod }
             *     
             */
            public HttpMethod getHttpMethod() {
                return httpMethod;
            }

            /**
             * Sets the value of the httpMethod property.
             * 
             * @param value
             *     allowed object is
             *     {@link HttpMethod }
             *     
             */
            public void setHttpMethod(HttpMethod value) {
                this.httpMethod = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="FormElement" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="Key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "formElement"
            })
            public static class FormElements
                implements Serializable
            {

                private final static long serialVersionUID = 1L;
                @XmlElement(name = "FormElement", required = true)
                protected List<WebSite.Authentication.HtmlForm.FormElements.FormElement> formElement;

                /**
                 * Gets the value of the formElement property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the formElement property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getFormElement().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link WebSite.Authentication.HtmlForm.FormElements.FormElement }
                 * 
                 * 
                 */
                public List<WebSite.Authentication.HtmlForm.FormElements.FormElement> getFormElement() {
                    if (formElement == null) {
                        formElement = new ArrayList<WebSite.Authentication.HtmlForm.FormElements.FormElement>();
                    }
                    return this.formElement;
                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;attribute name="Key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class FormElement
                    implements Serializable
                {

                    private final static long serialVersionUID = 1L;
                    @XmlAttribute(name = "Key", required = true)
                    protected String key;
                    @XmlAttribute(name = "Value", required = true)
                    protected String value;

                    /**
                     * Gets the value of the key property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getKey() {
                        return key;
                    }

                    /**
                     * Sets the value of the key property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setKey(String value) {
                        this.key = value;
                    }

                    /**
                     * Gets the value of the value property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getValue() {
                        return value;
                    }

                    /**
                     * Sets the value of the value property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setValue(String value) {
                        this.value = value;
                    }

                }

            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Host" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Realm" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Login" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Rfc2617
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Host", required = true)
            protected String host;
            @XmlAttribute(name = "Port", required = true)
            protected String port;
            @XmlAttribute(name = "Realm", required = true)
            protected String realm;
            @XmlAttribute(name = "Login", required = true)
            protected String login;
            @XmlAttribute(name = "Password", required = true)
            protected String password;

            /**
             * Gets the value of the host property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getHost() {
                return host;
            }

            /**
             * Sets the value of the host property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setHost(String value) {
                this.host = value;
            }

            /**
             * Gets the value of the port property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPort() {
                return port;
            }

            /**
             * Sets the value of the port property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPort(String value) {
                this.port = value;
            }

            /**
             * Gets the value of the realm property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRealm() {
                return realm;
            }

            /**
             * Sets the value of the realm property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRealm(String value) {
                this.realm = value;
            }

            /**
             * Gets the value of the login property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLogin() {
                return login;
            }

            /**
             * Sets the value of the login property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLogin(String value) {
                this.login = value;
            }

            /**
             * Gets the value of the password property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPassword() {
                return password;
            }

            /**
             * Sets the value of the password property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPassword(String value) {
                this.password = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="ProtocolName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="TruststoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="TruststorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
         *       &lt;attribute name="KeystoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="KeystorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class SslCertificate
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "ProtocolName", required = true)
            protected String protocolName;
            @XmlAttribute(name = "Port", required = true)
            protected String port;
            @XmlAttribute(name = "TruststoreUrl", required = true)
            protected String truststoreUrl;
            @XmlAttribute(name = "TruststorePassword")
            protected String truststorePassword;
            @XmlAttribute(name = "KeystoreUrl", required = true)
            protected String keystoreUrl;
            @XmlAttribute(name = "KeystorePassword")
            protected String keystorePassword;

            /**
             * Gets the value of the protocolName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getProtocolName() {
                return protocolName;
            }

            /**
             * Sets the value of the protocolName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setProtocolName(String value) {
                this.protocolName = value;
            }

            /**
             * Gets the value of the port property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPort() {
                return port;
            }

            /**
             * Sets the value of the port property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPort(String value) {
                this.port = value;
            }

            /**
             * Gets the value of the truststoreUrl property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTruststoreUrl() {
                return truststoreUrl;
            }

            /**
             * Sets the value of the truststoreUrl property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTruststoreUrl(String value) {
                this.truststoreUrl = value;
            }

            /**
             * Gets the value of the truststorePassword property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTruststorePassword() {
                if (truststorePassword == null) {
                    return "";
                } else {
                    return truststorePassword;
                }
            }

            /**
             * Sets the value of the truststorePassword property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTruststorePassword(String value) {
                this.truststorePassword = value;
            }

            /**
             * Gets the value of the keystoreUrl property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKeystoreUrl() {
                return keystoreUrl;
            }

            /**
             * Sets the value of the keystoreUrl property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKeystoreUrl(String value) {
                this.keystoreUrl = value;
            }

            /**
             * Gets the value of the keystorePassword property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKeystorePassword() {
                if (keystorePassword == null) {
                    return "";
                } else {
                    return keystorePassword;
                }
            }

            /**
             * Sets the value of the keystorePassword property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKeystorePassword(String value) {
                this.keystorePassword = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="SizeLimits" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="MaxBytesDownload" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="MaxDocumentDownload" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="MaxTimeSec" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="MaxLengthBytes" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="LimitRate" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="TimeoutLimits" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Timeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="DnsTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="ConnectTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="ReadTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="900" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="WaitLimits" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Wait" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="RandomWait" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
     *                 &lt;attribute name="WaitRetry" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *                 &lt;attribute name="MaxRetries" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sizeLimits",
        "timeoutLimits",
        "waitLimits"
    })
    public static class CrawlLimits
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "SizeLimits")
        protected WebSite.CrawlLimits.SizeLimits sizeLimits;
        @XmlElement(name = "TimeoutLimits")
        protected WebSite.CrawlLimits.TimeoutLimits timeoutLimits;
        @XmlElement(name = "WaitLimits")
        protected WebSite.CrawlLimits.WaitLimits waitLimits;

        /**
         * Gets the value of the sizeLimits property.
         * 
         * @return
         *     possible object is
         *     {@link WebSite.CrawlLimits.SizeLimits }
         *     
         */
        public WebSite.CrawlLimits.SizeLimits getSizeLimits() {
            return sizeLimits;
        }

        /**
         * Sets the value of the sizeLimits property.
         * 
         * @param value
         *     allowed object is
         *     {@link WebSite.CrawlLimits.SizeLimits }
         *     
         */
        public void setSizeLimits(WebSite.CrawlLimits.SizeLimits value) {
            this.sizeLimits = value;
        }

        /**
         * Gets the value of the timeoutLimits property.
         * 
         * @return
         *     possible object is
         *     {@link WebSite.CrawlLimits.TimeoutLimits }
         *     
         */
        public WebSite.CrawlLimits.TimeoutLimits getTimeoutLimits() {
            return timeoutLimits;
        }

        /**
         * Sets the value of the timeoutLimits property.
         * 
         * @param value
         *     allowed object is
         *     {@link WebSite.CrawlLimits.TimeoutLimits }
         *     
         */
        public void setTimeoutLimits(WebSite.CrawlLimits.TimeoutLimits value) {
            this.timeoutLimits = value;
        }

        /**
         * Gets the value of the waitLimits property.
         * 
         * @return
         *     possible object is
         *     {@link WebSite.CrawlLimits.WaitLimits }
         *     
         */
        public WebSite.CrawlLimits.WaitLimits getWaitLimits() {
            return waitLimits;
        }

        /**
         * Sets the value of the waitLimits property.
         * 
         * @param value
         *     allowed object is
         *     {@link WebSite.CrawlLimits.WaitLimits }
         *     
         */
        public void setWaitLimits(WebSite.CrawlLimits.WaitLimits value) {
            this.waitLimits = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="MaxBytesDownload" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="MaxDocumentDownload" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="MaxTimeSec" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="MaxLengthBytes" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="LimitRate" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class SizeLimits
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "MaxBytesDownload")
            protected BigInteger maxBytesDownload;
            @XmlAttribute(name = "MaxDocumentDownload")
            protected BigInteger maxDocumentDownload;
            @XmlAttribute(name = "MaxTimeSec")
            protected BigInteger maxTimeSec;
            @XmlAttribute(name = "MaxLengthBytes")
            protected BigInteger maxLengthBytes;
            @XmlAttribute(name = "LimitRate")
            protected BigInteger limitRate;

            /**
             * Gets the value of the maxBytesDownload property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMaxBytesDownload() {
                if (maxBytesDownload == null) {
                    return new BigInteger("0");
                } else {
                    return maxBytesDownload;
                }
            }

            /**
             * Sets the value of the maxBytesDownload property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMaxBytesDownload(BigInteger value) {
                this.maxBytesDownload = value;
            }

            /**
             * Gets the value of the maxDocumentDownload property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMaxDocumentDownload() {
                if (maxDocumentDownload == null) {
                    return new BigInteger("0");
                } else {
                    return maxDocumentDownload;
                }
            }

            /**
             * Sets the value of the maxDocumentDownload property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMaxDocumentDownload(BigInteger value) {
                this.maxDocumentDownload = value;
            }

            /**
             * Gets the value of the maxTimeSec property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMaxTimeSec() {
                if (maxTimeSec == null) {
                    return new BigInteger("0");
                } else {
                    return maxTimeSec;
                }
            }

            /**
             * Sets the value of the maxTimeSec property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMaxTimeSec(BigInteger value) {
                this.maxTimeSec = value;
            }

            /**
             * Gets the value of the maxLengthBytes property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMaxLengthBytes() {
                if (maxLengthBytes == null) {
                    return new BigInteger("0");
                } else {
                    return maxLengthBytes;
                }
            }

            /**
             * Sets the value of the maxLengthBytes property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMaxLengthBytes(BigInteger value) {
                this.maxLengthBytes = value;
            }

            /**
             * Gets the value of the limitRate property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getLimitRate() {
                if (limitRate == null) {
                    return new BigInteger("0");
                } else {
                    return limitRate;
                }
            }

            /**
             * Sets the value of the limitRate property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setLimitRate(BigInteger value) {
                this.limitRate = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Timeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="DnsTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="ConnectTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="ReadTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" default="900" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class TimeoutLimits
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Timeout")
            protected BigInteger timeout;
            @XmlAttribute(name = "DnsTimeout")
            protected BigInteger dnsTimeout;
            @XmlAttribute(name = "ConnectTimeout")
            protected BigInteger connectTimeout;
            @XmlAttribute(name = "ReadTimeout")
            protected BigInteger readTimeout;

            /**
             * Gets the value of the timeout property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getTimeout() {
                if (timeout == null) {
                    return new BigInteger("0");
                } else {
                    return timeout;
                }
            }

            /**
             * Sets the value of the timeout property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setTimeout(BigInteger value) {
                this.timeout = value;
            }

            /**
             * Gets the value of the dnsTimeout property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getDnsTimeout() {
                if (dnsTimeout == null) {
                    return new BigInteger("0");
                } else {
                    return dnsTimeout;
                }
            }

            /**
             * Sets the value of the dnsTimeout property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setDnsTimeout(BigInteger value) {
                this.dnsTimeout = value;
            }

            /**
             * Gets the value of the connectTimeout property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getConnectTimeout() {
                if (connectTimeout == null) {
                    return new BigInteger("0");
                } else {
                    return connectTimeout;
                }
            }

            /**
             * Sets the value of the connectTimeout property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setConnectTimeout(BigInteger value) {
                this.connectTimeout = value;
            }

            /**
             * Gets the value of the readTimeout property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getReadTimeout() {
                if (readTimeout == null) {
                    return new BigInteger("900");
                } else {
                    return readTimeout;
                }
            }

            /**
             * Sets the value of the readTimeout property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setReadTimeout(BigInteger value) {
                this.readTimeout = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Wait" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="RandomWait" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
         *       &lt;attribute name="WaitRetry" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *       &lt;attribute name="MaxRetries" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class WaitLimits
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Wait")
            protected BigInteger wait;
            @XmlAttribute(name = "RandomWait")
            protected Boolean randomWait;
            @XmlAttribute(name = "WaitRetry")
            protected BigInteger waitRetry;
            @XmlAttribute(name = "MaxRetries")
            protected BigInteger maxRetries;

            /**
             * Gets the value of the wait property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getWait() {
                if (wait == null) {
                    return new BigInteger("0");
                } else {
                    return wait;
                }
            }

            /**
             * Sets the value of the wait property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setWait(BigInteger value) {
                this.wait = value;
            }

            /**
             * Gets the value of the randomWait property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public boolean isRandomWait() {
                if (randomWait == null) {
                    return false;
                } else {
                    return randomWait;
                }
            }

            /**
             * Sets the value of the randomWait property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setRandomWait(Boolean value) {
                this.randomWait = value;
            }

            /**
             * Gets the value of the waitRetry property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getWaitRetry() {
                if (waitRetry == null) {
                    return new BigInteger("0");
                } else {
                    return waitRetry;
                }
            }

            /**
             * Sets the value of the waitRetry property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setWaitRetry(BigInteger value) {
                this.waitRetry = value;
            }

            /**
             * Gets the value of the maxRetries property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMaxRetries() {
                if (maxRetries == null) {
                    return new BigInteger("0");
                } else {
                    return maxRetries;
                }
            }

            /**
             * Sets the value of the maxRetries property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMaxRetries(BigInteger value) {
                this.maxRetries = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Filters" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Filter" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;extension base="{}Filter">
     *                         &lt;/extension>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="Type" type="{}CrawlScope" default="Host" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "filters"
    })
    public static class CrawlScope
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "Filters")
        protected WebSite.CrawlScope.Filters filters;
        @XmlAttribute(name = "Type")
        protected org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope type;

        /**
         * Gets the value of the filters property.
         * 
         * @return
         *     possible object is
         *     {@link WebSite.CrawlScope.Filters }
         *     
         */
        public WebSite.CrawlScope.Filters getFilters() {
            return filters;
        }

        /**
         * Sets the value of the filters property.
         * 
         * @param value
         *     allowed object is
         *     {@link WebSite.CrawlScope.Filters }
         *     
         */
        public void setFilters(WebSite.CrawlScope.Filters value) {
            this.filters = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope }
         *     
         */
        public org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope getType() {
            if (type == null) {
                return org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope.HOST;
            } else {
                return type;
            }
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope }
         *     
         */
        public void setType(org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope value) {
            this.type = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="Filter" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;extension base="{}Filter">
         *               &lt;/extension>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "filter"
        })
        public static class Filters
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlElement(name = "Filter", required = true)
            protected List<WebSite.CrawlScope.Filters.Filter> filter;

            /**
             * Gets the value of the filter property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the filter property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getFilter().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link WebSite.CrawlScope.Filters.Filter }
             * 
             * 
             */
            public List<WebSite.CrawlScope.Filters.Filter> getFilter() {
                if (filter == null) {
                    filter = new ArrayList<WebSite.CrawlScope.Filters.Filter>();
                }
                return this.filter;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;extension base="{}Filter">
             *     &lt;/extension>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Filter
                extends org.eclipse.smila.connectivity.framework.crawler.web.messages.Filter
                implements Serializable
            {

                private final static long serialVersionUID = 1L;

            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="Type" use="required" type="{}ModelType" />
     *       &lt;attribute name="Value" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class CrawlingModel
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlAttribute(name = "Type", required = true)
        protected ModelType type;
        @XmlAttribute(name = "Value", required = true)
        @XmlSchemaType(name = "positiveInteger")
        protected BigInteger value;

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link ModelType }
         *     
         */
        public ModelType getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link ModelType }
         *     
         */
        public void setType(ModelType value) {
            this.type = value;
        }

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setValue(BigInteger value) {
            this.value = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Filter" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{}Filter">
     *                 &lt;sequence>
     *                   &lt;element name="Refinements" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="TimeOfDay" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="From" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
     *                                     &lt;attribute name="To" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="Port" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="Number" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/extension>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "filter"
    })
    public static class Filters
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "Filter", required = true)
        protected List<WebSite.Filters.Filter> filter;

        /**
         * Gets the value of the filter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the filter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFilter().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WebSite.Filters.Filter }
         * 
         * 
         */
        public List<WebSite.Filters.Filter> getFilter() {
            if (filter == null) {
                filter = new ArrayList<WebSite.Filters.Filter>();
            }
            return this.filter;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{}Filter">
         *       &lt;sequence>
         *         &lt;element name="Refinements" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="TimeOfDay" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="From" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
         *                           &lt;attribute name="To" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="Port" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="Number" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "refinements"
        })
        public static class Filter
            extends org.eclipse.smila.connectivity.framework.crawler.web.messages.Filter
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlElement(name = "Refinements")
            protected WebSite.Filters.Filter.Refinements refinements;

            /**
             * Gets the value of the refinements property.
             * 
             * @return
             *     possible object is
             *     {@link WebSite.Filters.Filter.Refinements }
             *     
             */
            public WebSite.Filters.Filter.Refinements getRefinements() {
                return refinements;
            }

            /**
             * Sets the value of the refinements property.
             * 
             * @param value
             *     allowed object is
             *     {@link WebSite.Filters.Filter.Refinements }
             *     
             */
            public void setRefinements(WebSite.Filters.Filter.Refinements value) {
                this.refinements = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="TimeOfDay" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="From" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
             *                 &lt;attribute name="To" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="Port" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="Number" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "timeOfDay",
                "port"
            })
            public static class Refinements
                implements Serializable
            {

                private final static long serialVersionUID = 1L;
                @XmlElement(name = "TimeOfDay")
                protected WebSite.Filters.Filter.Refinements.TimeOfDay timeOfDay;
                @XmlElement(name = "Port")
                protected WebSite.Filters.Filter.Refinements.Port port;

                /**
                 * Gets the value of the timeOfDay property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link WebSite.Filters.Filter.Refinements.TimeOfDay }
                 *     
                 */
                public WebSite.Filters.Filter.Refinements.TimeOfDay getTimeOfDay() {
                    return timeOfDay;
                }

                /**
                 * Sets the value of the timeOfDay property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link WebSite.Filters.Filter.Refinements.TimeOfDay }
                 *     
                 */
                public void setTimeOfDay(WebSite.Filters.Filter.Refinements.TimeOfDay value) {
                    this.timeOfDay = value;
                }

                /**
                 * Gets the value of the port property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link WebSite.Filters.Filter.Refinements.Port }
                 *     
                 */
                public WebSite.Filters.Filter.Refinements.Port getPort() {
                    return port;
                }

                /**
                 * Sets the value of the port property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link WebSite.Filters.Filter.Refinements.Port }
                 *     
                 */
                public void setPort(WebSite.Filters.Filter.Refinements.Port value) {
                    this.port = value;
                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;attribute name="Number" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class Port
                    implements Serializable
                {

                    private final static long serialVersionUID = 1L;
                    @XmlAttribute(name = "Number", required = true)
                    protected BigInteger number;

                    /**
                     * Gets the value of the number property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigInteger }
                     *     
                     */
                    public BigInteger getNumber() {
                        return number;
                    }

                    /**
                     * Sets the value of the number property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigInteger }
                     *     
                     */
                    public void setNumber(BigInteger value) {
                        this.number = value;
                    }

                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;attribute name="From" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
                 *       &lt;attribute name="To" use="required" type="{http://www.w3.org/2001/XMLSchema}time" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class TimeOfDay
                    implements Serializable
                {

                    private final static long serialVersionUID = 1L;
                    @XmlAttribute(name = "From", required = true)
                    @XmlSchemaType(name = "time")
                    protected XMLGregorianCalendar from;
                    @XmlAttribute(name = "To", required = true)
                    @XmlSchemaType(name = "time")
                    protected XMLGregorianCalendar to;

                    /**
                     * Gets the value of the from property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public XMLGregorianCalendar getFrom() {
                        return from;
                    }

                    /**
                     * Sets the value of the from property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setFrom(XMLGregorianCalendar value) {
                        this.from = value;
                    }

                    /**
                     * Gets the value of the to property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public XMLGregorianCalendar getTo() {
                        return to;
                    }

                    /**
                     * Sets the value of the to property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link XMLGregorianCalendar }
                     *     
                     */
                    public void setTo(XMLGregorianCalendar value) {
                        this.to = value;
                    }

                }

            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="MetaTagFilter" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Type" use="required" type="{}HtmlMetaTagType" />
     *                 &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Content" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="WorkType" use="required" type="{}FilterWorkType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "metaTagFilter"
    })
    public static class MetaTagFilters
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "MetaTagFilter", required = true)
        protected List<WebSite.MetaTagFilters.MetaTagFilter> metaTagFilter;

        /**
         * Gets the value of the metaTagFilter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the metaTagFilter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMetaTagFilter().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link WebSite.MetaTagFilters.MetaTagFilter }
         * 
         * 
         */
        public List<WebSite.MetaTagFilters.MetaTagFilter> getMetaTagFilter() {
            if (metaTagFilter == null) {
                metaTagFilter = new ArrayList<WebSite.MetaTagFilters.MetaTagFilter>();
            }
            return this.metaTagFilter;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Type" use="required" type="{}HtmlMetaTagType" />
         *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Content" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="WorkType" use="required" type="{}FilterWorkType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class MetaTagFilter
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Type", required = true)
            protected HtmlMetaTagType type;
            @XmlAttribute(name = "Name", required = true)
            protected String name;
            @XmlAttribute(name = "Content", required = true)
            protected String content;
            @XmlAttribute(name = "WorkType", required = true)
            protected FilterWorkType workType;

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link HtmlMetaTagType }
             *     
             */
            public HtmlMetaTagType getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link HtmlMetaTagType }
             *     
             */
            public void setType(HtmlMetaTagType value) {
                this.type = value;
            }

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the content property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * Sets the value of the content property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * Gets the value of the workType property.
             * 
             * @return
             *     possible object is
             *     {@link FilterWorkType }
             *     
             */
            public FilterWorkType getWorkType() {
                return workType;
            }

            /**
             * Sets the value of the workType property.
             * 
             * @param value
             *     allowed object is
             *     {@link FilterWorkType }
             *     
             */
            public void setWorkType(FilterWorkType value) {
                this.workType = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element name="ProxyServer">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Host" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Login" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *                 &lt;attribute name="Password" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="AutomaticConfiguration">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Address" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "proxyServer",
        "automaticConfiguration"
    })
    public static class Proxy
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "ProxyServer")
        protected WebSite.Proxy.ProxyServer proxyServer;
        @XmlElement(name = "AutomaticConfiguration")
        protected WebSite.Proxy.AutomaticConfiguration automaticConfiguration;

        /**
         * Gets the value of the proxyServer property.
         * 
         * @return
         *     possible object is
         *     {@link WebSite.Proxy.ProxyServer }
         *     
         */
        public WebSite.Proxy.ProxyServer getProxyServer() {
            return proxyServer;
        }

        /**
         * Sets the value of the proxyServer property.
         * 
         * @param value
         *     allowed object is
         *     {@link WebSite.Proxy.ProxyServer }
         *     
         */
        public void setProxyServer(WebSite.Proxy.ProxyServer value) {
            this.proxyServer = value;
        }

        /**
         * Gets the value of the automaticConfiguration property.
         * 
         * @return
         *     possible object is
         *     {@link WebSite.Proxy.AutomaticConfiguration }
         *     
         */
        public WebSite.Proxy.AutomaticConfiguration getAutomaticConfiguration() {
            return automaticConfiguration;
        }

        /**
         * Sets the value of the automaticConfiguration property.
         * 
         * @param value
         *     allowed object is
         *     {@link WebSite.Proxy.AutomaticConfiguration }
         *     
         */
        public void setAutomaticConfiguration(WebSite.Proxy.AutomaticConfiguration value) {
            this.automaticConfiguration = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Address" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class AutomaticConfiguration
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Address", required = true)
            protected String address;

            /**
             * Gets the value of the address property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAddress() {
                return address;
            }

            /**
             * Sets the value of the address property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAddress(String value) {
                this.address = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Host" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Port" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Login" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
         *       &lt;attribute name="Password" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ProxyServer
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlAttribute(name = "Host", required = true)
            protected String host;
            @XmlAttribute(name = "Port", required = true)
            protected String port;
            @XmlAttribute(name = "Login")
            protected String login;
            @XmlAttribute(name = "Password")
            protected String password;

            /**
             * Gets the value of the host property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getHost() {
                return host;
            }

            /**
             * Sets the value of the host property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setHost(String value) {
                this.host = value;
            }

            /**
             * Gets the value of the port property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPort() {
                return port;
            }

            /**
             * Sets the value of the port property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPort(String value) {
                this.port = value;
            }

            /**
             * Gets the value of the login property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLogin() {
                if (login == null) {
                    return "";
                } else {
                    return login;
                }
            }

            /**
             * Sets the value of the login property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLogin(String value) {
                this.login = value;
            }

            /**
             * Gets the value of the password property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPassword() {
                if (password == null) {
                    return "";
                } else {
                    return password;
                }
            }

            /**
             * Sets the value of the password property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPassword(String value) {
                this.password = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="Policy" type="{}Robotstxt" default="Classic" />
     *       &lt;attribute name="Value" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *       &lt;attribute name="AgentNames" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Robotstxt
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlAttribute(name = "Policy")
        protected org.eclipse.smila.connectivity.framework.crawler.web.messages.Robotstxt policy;
        @XmlAttribute(name = "Value")
        protected String value;
        @XmlAttribute(name = "AgentNames")
        protected String agentNames;

        /**
         * Gets the value of the policy property.
         * 
         * @return
         *     possible object is
         *     {@link org.eclipse.smila.connectivity.framework.crawler.web.messages.Robotstxt }
         *     
         */
        public org.eclipse.smila.connectivity.framework.crawler.web.messages.Robotstxt getPolicy() {
            if (policy == null) {
                return org.eclipse.smila.connectivity.framework.crawler.web.messages.Robotstxt.CLASSIC;
            } else {
                return policy;
            }
        }

        /**
         * Sets the value of the policy property.
         * 
         * @param value
         *     allowed object is
         *     {@link org.eclipse.smila.connectivity.framework.crawler.web.messages.Robotstxt }
         *     
         */
        public void setPolicy(org.eclipse.smila.connectivity.framework.crawler.web.messages.Robotstxt value) {
            this.policy = value;
        }

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            if (value == null) {
                return "";
            } else {
                return value;
            }
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the agentNames property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAgentNames() {
            if (agentNames == null) {
                return "";
            } else {
                return agentNames;
            }
        }

        /**
         * Sets the value of the agentNames property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAgentNames(String value) {
            this.agentNames = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Seed" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="FollowLinks" type="{}FollowLinksType" default="Follow" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "seed"
    })
    public static class Seeds
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "Seed", required = true)
        protected List<String> seed;
        @XmlAttribute(name = "FollowLinks")
        protected FollowLinksType followLinks;

        /**
         * Gets the value of the seed property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the seed property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSeed().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getSeed() {
            if (seed == null) {
                seed = new ArrayList<String>();
            }
            return this.seed;
        }

        /**
         * Gets the value of the followLinks property.
         * 
         * @return
         *     possible object is
         *     {@link FollowLinksType }
         *     
         */
        public FollowLinksType getFollowLinks() {
            if (followLinks == null) {
                return FollowLinksType.FOLLOW;
            } else {
                return followLinks;
            }
        }

        /**
         * Sets the value of the followLinks property.
         * 
         * @param value
         *     allowed object is
         *     {@link FollowLinksType }
         *     
         */
        public void setFollowLinks(FollowLinksType value) {
            this.followLinks = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="TruststoreUrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="TruststorePassword" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Ssl
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlAttribute(name = "TruststoreUrl", required = true)
        protected String truststoreUrl;
        @XmlAttribute(name = "TruststorePassword")
        protected String truststorePassword;

        /**
         * Gets the value of the truststoreUrl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTruststoreUrl() {
            return truststoreUrl;
        }

        /**
         * Sets the value of the truststoreUrl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTruststoreUrl(String value) {
            this.truststoreUrl = value;
        }

        /**
         * Gets the value of the truststorePassword property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTruststorePassword() {
            if (truststorePassword == null) {
                return "";
            } else {
                return truststorePassword;
            }
        }

        /**
         * Sets the value of the truststorePassword property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTruststorePassword(String value) {
            this.truststorePassword = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Version" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Description" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Url" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Email" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class UserAgent
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlAttribute(name = "Name", required = true)
        protected String name;
        @XmlAttribute(name = "Version")
        protected String version;
        @XmlAttribute(name = "Description")
        protected String description;
        @XmlAttribute(name = "Url")
        protected String url;
        @XmlAttribute(name = "Email")
        protected String email;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the version property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersion() {
            return version;
        }

        /**
         * Sets the value of the version property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersion(String value) {
            this.version = value;
        }

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Gets the value of the url property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the value of the url property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUrl(String value) {
            this.url = value;
        }

        /**
         * Gets the value of the email property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEmail() {
            return email;
        }

        /**
         * Sets the value of the email property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEmail(String value) {
            this.email = value;
        }

    }

}

// CHECKSTYLE:ON
