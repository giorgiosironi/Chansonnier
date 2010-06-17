/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.core.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

public abstract class ProcessingServiceTest extends DeclarativeServiceTestCase {

	private Blackboard _blackboard;

	protected void setUp() throws Exception {
		super.setUp();
	    forceStartBundle("org.eclipse.smila.blackboard");
	    forceStartBundle("org.eclipse.smila.processing");
	    final BlackboardFactory factory = getService(BlackboardFactory.class);
	    assertNotNull("no BlackboardFactory service found.", factory);
	    _blackboard = factory.createPersistingBlackboard();
	    assertNotNull("no Blackboard created", _blackboard);
	    init();
	}
	
	abstract protected void init() throws Exception;

	protected Id createBlackboardRecord(String source, String key) {
		final Id id = IdFactory.DEFAULT_INSTANCE.createId(source, key);
		_log.info("Invalidating and re-creating test record on blackboard.");
		_log.info("This may cause an exception to be logged that can be safely ignored.");
		_blackboard.invalidate(id);
		_blackboard.create(id);
		return id;
	}

	/**
	 * @return the blackboard
	 */
	protected Blackboard getBlackboard() {
	    return _blackboard;
	}

	/**
	 * Set the attribute value.
	 * 
	 * @param id
	 *          the record id
	 * @param path
	 *          the attribute path
	 * @throws BlackboardAccessException
	 *           if any error occurs
	 */
	protected void setAttribute(final Id id, final Path path, String value)
			throws BlackboardAccessException {
				final Literal literal = getBlackboard().createLiteral(id);
				literal.setStringValue(value);
				getBlackboard().setLiteral(id, path, literal);
			}

	/**
	   * Gets the attribute value.
	   * 
	   * @param id
	   *          the record id
	   * @param path
	   *          the attribute path
	   * @return a Literal containing the attribute value
	   * @throws BlackboardAccessException
	   *           if any error occurs
	   */
	protected Literal getAttribute(final Id id, final Path path)
			throws BlackboardAccessException {
				return getBlackboard().getLiteral(id, path);
			}
	
	protected List<Literal> getAttributes(final Id id, final Path path)
	throws BlackboardAccessException {
		return getBlackboard().getLiterals(id, path);
	}
	
	protected Annotation getAnnotation(Id id, Path path, String name) throws BlackboardAccessException {
		return getBlackboard().getAnnotation(id, path, name);
	}
	
	  /**
	   * Set the attachment value.
	   * 
	   * @param id
	   *          the record id
	   * @param name
	   *          name of the attachment
	   * @throws BlackboardAccessException
	   *           if any error occurs
	   * @throws UnsupportedEncodingException
	   *           if any error occurs
	   */
	  protected void setAttachment(final Id id, final String name, File file) throws BlackboardAccessException,
	    UnsupportedEncodingException {
		  getBlackboard().setAttachmentFromFile(id, name, file);
	  }

	
	  /**
	   * Gets the attachment value.
	   * 
	   * @param id
	   *          the record id
	   * @param name
	   *          the name of the attachment
	   * @throws BlackboardAccessException
	   *           if any error occurs
	   * @throws UnsupportedEncodingException
	   *           if any error occurs
	   */
	  protected File getAttachment(final Id id, final String name) throws BlackboardAccessException,
	    UnsupportedEncodingException {
		  return getBlackboard().getAttachmentAsFile(id, name);
	  }

}