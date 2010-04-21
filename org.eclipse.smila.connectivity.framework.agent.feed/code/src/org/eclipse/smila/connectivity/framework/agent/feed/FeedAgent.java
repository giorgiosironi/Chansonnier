/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.agent.feed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.AbstractAgent;
import org.eclipse.smila.connectivity.framework.AgentException;
import org.eclipse.smila.connectivity.framework.agent.feed.messages.Attribute;
import org.eclipse.smila.connectivity.framework.agent.feed.messages.Process;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig.Attributes;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;
import org.eclipse.smila.connectivity.framework.util.AgentThreadState;
import org.eclipse.smila.connectivity.framework.util.ConnectivityHashFactory;
import org.eclipse.smila.connectivity.framework.util.ConnectivityIdFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;

/**
 * Implementation of a FeedAgent based on ROME and ROME Fetcher. It's important to note that records returned by the
 * FeedAgent may contain nested MObjects as attribute values. Check out the documentation to see which attributes return
 * nested MObjects.
 */
public class FeedAgent extends AbstractAgent {

  /**
   * Constant to compute millisecond values.
   */
  private static final long MILLI_SECOND_MULTIPLIER = 1000;

  /**
   * The record factory.
   */
  private final RecordFactory _factory = RecordFactory.DEFAULT_INSTANCE;

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(FeedAgent.class);

  /**
   * The update interval in milliseconds.
   */
  private long _updateInterval;

  /**
   * The feed urls.
   */
  private List<String> _feedUrls;

  /**
   * The _attributes.
   */
  private Attribute[] _attributes;

  /**
   * Default Constructor.
   */
  public FeedAgent() {
    super();
    if (_log.isDebugEnabled()) {
      _log.debug("Creating FeedAgent instance");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    try {
      Thread.currentThread().setContextClassLoader(SyndFeed.class.getClassLoader());
      final FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
      final FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);
      while (!isStopThread()) {
        try {
          for (final String feedUrl : _feedUrls) {
            fetch(feedFetcher, feedUrl);
          }
          Thread.sleep(_updateInterval);
        } catch (InterruptedException e) {
          if (_log.isDebugEnabled()) {
            _log.debug("FeedAgent thread was interrupted", e);
          }
        }
      } // while
    } catch (Throwable t) {
      getAgentState().setLastError(t);
      getAgentState().setState(AgentThreadState.Aborted);
      throw new RuntimeException(t);
    } finally {
      try {
        stopThread();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see AbstractAgent#initialize()
   */
  protected void initialize() throws AgentException {
    // convert updateInterval to milliseconds
    _updateInterval =
      ((Process) getConfig().getProcess()).getUpdateInterval().longValue() * MILLI_SECOND_MULTIPLIER;
    _feedUrls = ((Process) getConfig().getProcess()).getFeedUrl();

    // read in configured attributes/attachments
    final Attributes attributes = getConfig().getAttributes();
    final List<IAttribute> attrs = attributes.getAttribute();
    _attributes = attrs.toArray(new Attribute[attrs.size()]);
  }

  /**
   * Fetches the feed with the given feed URL using the given FeedFetcher. For each feed entry a record is created and
   * send to the AgentController.
   * 
   * @param feedFetcher
   *          the FeedFetcher
   * @param feedUrl
   *          the URL of the feed
   */
  private void fetch(final FeedFetcher feedFetcher, final String feedUrl) {
    try {
      final SyndFeed feed = feedFetcher.retrieveFeed(new URL(feedUrl));
      if (feed != null) {
        if (_log.isInfoEnabled()) {
          _log.info("Fetched feed " + feedUrl);
        }
        final List<SyndEntryImpl> entries = feed.getEntries();
        if (entries != null) {
          for (SyndEntryImpl entry : entries) {
            try {
              if (entry != null) {

                final List<org.eclipse.smila.datamodel.record.Attribute> idAttributes =
                  new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
                final List<org.eclipse.smila.datamodel.record.Attribute> hashAttributes =
                  new ArrayList<org.eclipse.smila.datamodel.record.Attribute>();
                final Map<String, byte[]> hashAttachments = new HashMap<String, byte[]>();
                readIdAndHashAttributesAndAttachments(feed, entry, idAttributes, hashAttributes, hashAttachments);

                final String hash = createHash(hashAttributes, hashAttachments);
                final Record record = createRecord(feed, entry, idAttributes);
                if (_log.isTraceEnabled()) {
                  _log.trace("created record " + record.getId());
                } // if
                getControllerCallback().add(getSessionId(), getConfig().getDeltaIndexing(), record, hash);
              }
            } catch (AgentException e) {
              getAgentState().setLastError(e);
              if (_log.isErrorEnabled()) {
                _log
                  .error("Error during creation of record for entry " + entry.getUri() + " of feed " + feedUrl, e);
              }
            } catch (InvalidTypeException e) {
              getAgentState().setLastError(e);
              if (_log.isErrorEnabled()) {
                _log
                  .error("Error during creation of record for entry " + entry.getUri() + " of feed " + feedUrl, e);
              }
            }
          } // for
        } // if
      } // if
    } catch (FetcherException e) {
      getAgentState().setLastError(e);
      if (_log.isErrorEnabled()) {
        _log.error("HTTP error during fetching of feed " + feedUrl, e);
      }
    } catch (FeedException e) {
      getAgentState().setLastError(e);
      if (_log.isErrorEnabled()) {
        _log.error("Error during fetching of feed " + feedUrl + ". The feed is invalid", e);
      }
    } catch (IOException e) {
      getAgentState().setLastError(e);
      if (_log.isErrorEnabled()) {
        _log.error("TCP error during fetching of feed " + feedUrl, e);
      }
    } catch (IllegalArgumentException e) {
      getAgentState().setLastError(e);
      if (_log.isErrorEnabled()) {
        _log.error("Error during fetching of feed " + feedUrl + ". The URL is invalid", e);
      }
      throw e;
    }
  }

  /**
   * Create a new record from the given feed and entry.
   * 
   * @param feed
   *          a SyndFeed
   * @param entry
   *          a SyndEntryImpl
   * @param idAttributes
   *          a list of id attributes
   * @return a Record object
   * @throws AgentException
   *           if any error occurs
   * @throws InvalidTypeException
   *           if any error occurs
   */
  private Record createRecord(final SyndFeed feed, final SyndEntryImpl entry,
    final List<org.eclipse.smila.datamodel.record.Attribute> idAttributes) throws AgentException,
    InvalidTypeException {

    // compute id
    final Id id =
      ConnectivityIdFactory.getInstance().createId(getConfig().getDataSourceID(),
        idAttributes.toArray(new org.eclipse.smila.datamodel.record.Attribute[idAttributes.size()]));

    // create record
    final Record record = _factory.createRecord();
    record.setId(id);
    final MObject metadata = _factory.createMetadataObject();
    record.setMetadata(metadata);
    fillRecord(feed, entry, record);

    return record;
  }

  /**
   * Computes the delta indexing hash.
   * 
   * @param hashAttributes
   *          a list of hash attributes
   * @param hashAttachments
   *          a list of hash attachments
   * @return a String containing the hash
   */
  private String createHash(final List<org.eclipse.smila.datamodel.record.Attribute> hashAttributes,
    final Map<String, byte[]> hashAttachments) {

    return ConnectivityHashFactory.getInstance().createHash(
      hashAttributes.toArray(new org.eclipse.smila.datamodel.record.Attribute[hashAttributes.size()]),
      hashAttachments);
  }

  /**
   * Reads attributes/attachments to create Id and hash from. Fills the given containers with Attribute objects.
   * 
   * @param feed
   *          a SyndFeed
   * @param entry
   *          a SyndEntryImpl
   * @param idAttributes
   *          a List to add attributes to create the Id from to
   * @param hashAttributes
   *          a List to add attributes to create the hash from to
   * @param hashAttachments
   *          a Map to add attachments to create the hash from to
   * @throws AgentException
   *           if any error occurs
   * @throws InvalidTypeException
   *           if any error occurs
   */
  private void readIdAndHashAttributesAndAttachments(final SyndFeed feed, final SyndEntryImpl entry,
    final List<org.eclipse.smila.datamodel.record.Attribute> idAttributes,
    final List<org.eclipse.smila.datamodel.record.Attribute> hashAttributes,
    final Map<String, byte[]> hashAttachments) throws AgentException, InvalidTypeException {
    for (final Attribute attributeDef : _attributes) {
      if (attributeDef.isKeyAttribute() || attributeDef.isHashAttribute()) {
        if (attributeDef.isAttachment()) {
          final byte[] value = readAttachment(feed, entry, attributeDef);
          hashAttachments.put(attributeDef.getName(), value);
        } else {
          final Object value = readAttribute(feed, entry, attributeDef);
          if (value != null) {
            final org.eclipse.smila.datamodel.record.Attribute attribute = createAttribute(attributeDef, value);
            if (attributeDef.isKeyAttribute()) {
              idAttributes.add(attribute);
            }
            if (attributeDef.isHashAttribute()) {
              hashAttributes.add(attribute);
            }
          } // if
        } // else
      } // if
    } // for
  }

  /**
   * Creates an Attribute using the given value as Literal values or as object value.
   * 
   * @param attributeDef
   *          the attribute definition
   * @param value
   *          the value of the attribute
   * @return an Attribute
   * @throws InvalidTypeException
   *           if any error occurs
   */
  private org.eclipse.smila.datamodel.record.Attribute createAttribute(final Attribute attributeDef,
    final Object value) throws InvalidTypeException {
    final org.eclipse.smila.datamodel.record.Attribute attribute = _factory.createAttribute();
    attribute.setName(attributeDef.getName());
    if (value instanceof MObject) {
      attribute.setObject((MObject) value);
    } else if (value instanceof Collection) {
      attribute.setObjects((Collection<MObject>) value);
    } else if (value instanceof Object[]) {
      for (Object element : (Object[]) value) {
        final Literal literal = _factory.createLiteral();
        literal.setValue(element);
        attribute.addLiteral(literal);
      }
    } else {
      final Literal literal = _factory.createLiteral();
      literal.setValue(value);
      attribute.addLiteral(literal);
    }
    return attribute;
  }

  /**
   * Fills the record with the configured attributes/attachments using the data from the given MockAgentData object.
   * 
   * @param feed
   *          a SyndFeed
   * @param entry
   *          a SyndEntryImpl
   * @param record
   *          the Record object to fill the data in
   * @throws AgentException
   *           if any error occurs
   * @throws InvalidTypeException
   *           if any error occurs
   */
  private void fillRecord(final SyndFeed feed, final SyndEntryImpl entry, final Record record)
    throws AgentException, InvalidTypeException {
    for (final Attribute attributeDef : _attributes) {
      if (attributeDef.isAttachment()) {
        final byte[] value = readAttachment(feed, entry, attributeDef);
        record.setAttachment(attributeDef.getName(), value);
      } else {
        final Object value = readAttribute(feed, entry, attributeDef);
        if (value != null) {
          final org.eclipse.smila.datamodel.record.Attribute attribute = createAttribute(attributeDef, value);
          record.getMetadata().setAttribute(attributeDef.getName(), attribute);
        } // if
      } // else
    } // for

  }

  /**
   * Reads the value for a given attribute from the MockAgentData object.
   * 
   * @param feed
   *          a SyndFeed
   * @param entry
   *          a SyndEntryImpl
   * @param attributeDef
   *          the attribute to read
   * @return a Serializable object (String or Date) or a MObject
   * @throws AgentException
   *           if any error occurs
   */
  private Object readAttribute(final SyndFeed feed, final SyndEntryImpl entry, final Attribute attributeDef)
    throws AgentException {
    switch (attributeDef.getFeedAttributes()) {
      // feed attributes
      case FEED_AUTHORS:
        return getPersons(feed.getAuthors(), feed.getAuthor());
      case FEED_CATEGORIES:
        return getCategories(feed.getCategories());
      case FEED_CONTRIBUTORS:
        return getPersons(feed.getContributors(), null);
      case FEED_COPYRIGHT:
        return feed.getCopyright();
      case FEED_DESCRIPTION:
        return feed.getDescription();
      case FEED_ENCODING:
        return feed.getEncoding();
      case FEED_TYPE:
        return feed.getFeedType();
      case FEED_IMAGE:
        return getImage(feed.getImage());
      case FEED_LANGUAGE:
        return feed.getLanguage();
      case FEED_LINKS:
        return getLinks(feed.getLinks(), feed.getLink());
      case FEED_PUBLISH_DATE:
        return feed.getPublishedDate();
      case FEED_TITLE:
        return feed.getTitle();
      case FEED_URI:
        return feed.getUri();

        // feed entry attributes
      case AUTHORS:
        return getPersons(entry.getAuthors(), entry.getAuthor());
      case CATEGORIES:
        return getCategories(entry.getCategories());
      case CONTENTS:
        return getContents(entry.getContents());
      case CONTRIBUTORS:
        return getPersons(entry.getContributors(), null);
      case DESCRIPTION:
        return getContent(entry.getDescription());
      case ENCLOSURES:
        return getEnclosures(entry.getEnclosures());
      case LINKS:
        return getLinks(entry.getLinks(), entry.getLink());
      case PUBLISH_DATE:
        return entry.getPublishedDate();
      case TITLE:
        return entry.getTitle();
      case URI:
        return entry.getUri();
      case UPDATE_DATE:
        return entry.getUpdatedDate();
      default:
        throw new RuntimeException("Unknown feed attributes type " + attributeDef.getFeedAttributes());
    }
  }

  /**
   * Creates an MObject containing all image information as attributes.
   * 
   * @param feedImage
   *          a SyndImage
   * @return a MObject
   */
  private MObject getImage(final SyndImage feedImage) {
    if (feedImage != null) {
      try {
        final MObject mobject = _factory.createMetadataObject();
        setAttribute(mobject, createSubAttribute("Link", feedImage.getLink()));
        setAttribute(mobject, createSubAttribute("Title", feedImage.getTitle()));
        setAttribute(mobject, createSubAttribute("Url", feedImage.getUrl()));
        setAttribute(mobject, createSubAttribute("Description", feedImage.getDescription()));
      } catch (InvalidTypeException e) {
        if (_log.isErrorEnabled()) {
          _log.error("Error while creating MObject ", e);
        }
      }
    }
    return null;
  }

  /**
   * Creates an List of MObject, where each MObject contains all person information as attributes. If persons is empty
   * the fallbackValue will be used to create a single MObject with a person name
   * 
   * @param persons
   *          a List of SyndPerson
   * @param fallbackValue
   *          a fallback person name if parameter persons is empty
   * @return a List of MObject
   */
  private List<MObject> getPersons(final List<SyndPerson> persons, final String fallbackValue) {
    if (persons != null && !persons.isEmpty()) {
      final ArrayList<MObject> personList = new ArrayList<MObject>();
      for (SyndPerson person : persons) {
        try {
          final MObject mobject = _factory.createMetadataObject();
          setAttribute(mobject, createSubAttribute("Email", person.getEmail()));
          setAttribute(mobject, createSubAttribute("Name", person.getName()));
          setAttribute(mobject, createSubAttribute("Uri", person.getUri()));
          personList.add(mobject);
        } catch (InvalidTypeException e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error while creating Person MObject ", e);
          }
        }
      }
      return personList;
    } else if (fallbackValue != null) {
      final ArrayList<MObject> personList = new ArrayList<MObject>();
      try {
        final MObject mobject = _factory.createMetadataObject();
        setAttribute(mobject, createSubAttribute("Name", fallbackValue));
        personList.add(mobject);
        return personList;
      } catch (InvalidTypeException e) {
        if (_log.isErrorEnabled()) {
          _log.error("Error while creating Person MObject ", e);
        }
      }
    }
    return null;
  }

  /**
   * Creates an List of MObject, where each MObject contains all category information as attributes.
   * 
   * @param categories
   *          a List of SyndCategory
   * @return a List of MObject
   */
  private List<MObject> getCategories(final List<SyndCategory> categories) {
    if (categories != null) {
      final ArrayList<MObject> categoryList = new ArrayList<MObject>();
      for (SyndCategory category : categories) {
        try {
          final MObject mobject = _factory.createMetadataObject();
          setAttribute(mobject, createSubAttribute("Name", category.getName()));
          setAttribute(mobject, createSubAttribute("TaxanomyUri", category.getTaxonomyUri()));
          categoryList.add(mobject);
        } catch (InvalidTypeException e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error while creating Category MObject ", e);
          }
        }
      }
      return categoryList;
    }
    return null;
  }

  /**
   * Creates an List of MObject, where each MObject contains all enclosure information as attributes.
   * 
   * @param enclosures
   *          a List of SyndEnclosure
   * @return a List of MObject
   */
  private List<MObject> getEnclosures(final List<SyndEnclosure> enclosures) {
    if (enclosures != null) {
      final ArrayList<MObject> enclosureList = new ArrayList<MObject>();
      for (SyndEnclosure enclosure : enclosures) {
        try {
          final MObject mobject = _factory.createMetadataObject();
          setAttribute(mobject, createSubAttribute("Type", enclosure.getType()));
          setAttribute(mobject, createSubAttribute("Url", enclosure.getUrl()));
          setAttribute(mobject, createSubAttribute("Length", enclosure.getLength()));
          enclosureList.add(mobject);
        } catch (InvalidTypeException e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error while creating Category MObject ", e);
          }
        }
      }
      return enclosureList;
    }
    return null;
  }

  /**
   * Creates an List of MObject, where each MObject contains all link information as attributes. If links is empty the
   * fallbackValue will be used to create a single MObject with a link href
   * 
   * @param links
   *          a List of SyndLink
   * @param fallbackValue
   *          a fallback link href if parameter links is empty
   * @return a List of MObject
   */
  private List<MObject> getLinks(final List<SyndLink> links, final String fallbackValue) {
    if (links != null && !links.isEmpty()) {
      final ArrayList<MObject> linkList = new ArrayList<MObject>();
      for (SyndLink link : links) {
        try {
          final MObject mobject = _factory.createMetadataObject();
          setAttribute(mobject, createSubAttribute("Href", link.getHref()));
          setAttribute(mobject, createSubAttribute("Hreflang", link.getHreflang()));
          setAttribute(mobject, createSubAttribute("Rel", link.getRel()));
          setAttribute(mobject, createSubAttribute("Title", link.getTitle()));
          setAttribute(mobject, createSubAttribute("Type", link.getType()));
          setAttribute(mobject, createSubAttribute("Length", link.getLength()));
          linkList.add(mobject);
        } catch (InvalidTypeException e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error while creating Link MObject ", e);
          }
        }
      }
      return linkList;

    } else if (fallbackValue != null) {
      final ArrayList<MObject> linkList = new ArrayList<MObject>();
      try {
        final MObject mobject = _factory.createMetadataObject();
        setAttribute(mobject, createSubAttribute("Href", fallbackValue));
        linkList.add(mobject);
        return linkList;
      } catch (InvalidTypeException e) {
        if (_log.isErrorEnabled()) {
          _log.error("Error while creating Person MObject ", e);
        }
      }
    }

    return null;
  }

  /**
   * Creates an List of MObject, where each MObject contains all contents information as attributes.
   * 
   * @param contents
   *          a List of SyndContent
   * @return a List of MObject
   */
  private List<MObject> getContents(final List<SyndContent> contents) {
    if (contents != null) {
      final ArrayList<MObject> contentList = new ArrayList<MObject>();
      for (SyndContent content : contents) {
        final MObject mobject = getContent(content);
        if (mobject != null) {
          contentList.add(mobject);
        }
      }
      return contentList;
    }
    return null;
  }

  /**
   * Creates an MObject containing all content information as attributes.
   * 
   * @param content
   *          a SyndContent
   * @return a MObject
   */
  private MObject getContent(final SyndContent content) {
    if (content != null) {
      try {
        final MObject mobject = _factory.createMetadataObject();
        setAttribute(mobject, createSubAttribute("Mode", content.getMode()));
        setAttribute(mobject, createSubAttribute("Value", content.getValue()));
        String type = content.getType();
        if ("html".equals(type)) {
          type = "text/html";
        } else if ("xml".equals(type)) {
          type = "text/xml";
        } else if ("text".equals(type)) {
          type = "text/Plain";
        }
        setAttribute(mobject, createSubAttribute("Type", type));
        return mobject;
      } catch (InvalidTypeException e) {
        if (_log.isErrorEnabled()) {
          _log.error("Error while creating Category MObject ", e);
        }
      }
    }
    return null;
  }

  /**
   * Creates an Attribute that is used in a nested MObject
   * 
   * @param name
   *          the name of the attribute
   * @param value
   *          the value of the attribute Object or Object[]
   * @return an Attribute
   * @throws InvalidTypeException
   *           if any error occurs
   */
  private org.eclipse.smila.datamodel.record.Attribute createSubAttribute(final String name, final Object value)
    throws InvalidTypeException {
    if (value != null) {
      final org.eclipse.smila.datamodel.record.Attribute attribute = _factory.createAttribute();
      attribute.setName(name);
      if (value instanceof Object[]) {
        for (Object element : (Object[]) value) {
          final Literal literal = _factory.createLiteral();
          literal.setValue(element);
          attribute.addLiteral(literal);
        }
      } else {
        final Literal literal = _factory.createLiteral();
        literal.setValue(value);
        attribute.addLiteral(literal);
      }
      return attribute;
    }
    return null;
  }

  /**
   * Sets the given attribute in the given MObject.
   * 
   * @param mobject
   *          the MObject to set the attribute in
   * @param attribute
   *          the Attribute to set
   */
  private void setAttribute(final MObject mobject, final org.eclipse.smila.datamodel.record.Attribute attribute) {
    if (mobject != null && attribute != null) {
      mobject.setAttribute(attribute.getName(), attribute);
    }
  }

  /**
   * Reads the value for a given attribute as an attachment (byte[]) from the MockAgentData object.
   * 
   * @param feed
   *          a SyndFeed
   * @param entry
   *          a SyndEntryImpl
   * @param attribute
   *          the attribute to read
   * @return a byte[]
   * @throws AgentException
   *           if any error occurs
   */
  private byte[] readAttachment(final SyndFeed feed, final SyndEntryImpl entry, final Attribute attribute)
    throws AgentException {
    final Object value = readAttribute(feed, entry, attribute);
    if (value != null) {
      if (value instanceof String) {
        try {
          return ((String) value).getBytes("utf-8");
        } catch (final UnsupportedEncodingException e) {
          throw new AgentException(e);
        }
      } else if (value instanceof byte[]) {
        return (byte[]) value;
      } else {
        if (_log.isWarnEnabled()) {
          _log.warn("The value type " + value.getClass() + " of attribute " + attribute.getName()
            + " cannot be used for attachments.");
        }
      }
    }
    return null;
  }

}
