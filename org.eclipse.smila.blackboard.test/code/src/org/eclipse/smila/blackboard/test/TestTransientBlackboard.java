/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.blackboard.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.Key;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * The Class BlackboardServiceTest.
 */
public class TestTransientBlackboard extends DeclarativeServiceTestCase {

  /** This bundle id. */
  private static final String BUNDLE_ID = "org.eclipse.smila.search.api.test";

  /** The _blackboard. */
  private Blackboard _blackboard;

  /**
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    final BlackboardFactory factory = getService(org.eclipse.smila.blackboard.BlackboardFactory.class);
    assertNotNull(factory);
    _blackboard = factory.createTransientBlackboard();
    assertNotNull(_blackboard);
  }

  /**
   * test create method - just ensure that it doesn't throw an exception - there is no possibility to really test
   * anything.
   *
   * @throws Exception
   *           test fails
   */
  public void testCreate() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(getClass().getName(), "testCreate");
    _blackboard.create(id);
    assertNotNull(_blackboard.getRecord(id));
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testFilter() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(getClass().getName(), "testFilter");
    _blackboard.create(id);
    final Literal literal = _blackboard.createLiteral(id);
    literal.setValue("value");
    _blackboard.setLiteral(id, new Path("attribute"), literal);
    _blackboard.setLiteral(id, new Path("hidden-attribute"), literal);
    final Annotation annotation = _blackboard.createAnnotation(id);
    annotation.setNamedValue("name", "value");
    _blackboard.setAnnotation(id, null, "annotation", annotation);
    _blackboard.setAnnotation(id, null, "hidden-annotation", annotation);
    final Record filtered = _blackboard.getRecord(id, "filter");
    assertNotNull(filtered);
    final MObject metadata = filtered.getMetadata();
    assertNotNull(metadata);
    assertTrue(metadata.hasAttribute("attribute"));
    assertEquals(1, metadata.getAttribute("attribute").literalSize());
    assertFalse(metadata.hasAttribute("hidden-attribute"));
    assertTrue(metadata.hasAnnotation("annotation"));
    assertEquals("value", metadata.getAnnotation("annotation").getNamedValue("name"));
    assertFalse(metadata.hasAnnotation("hidden-annotation"));

    final Record full = _blackboard.getRecord(id);
    assertNotNull(full);
    final MObject fullMetadata = full.getMetadata();
    assertNotNull(fullMetadata);
    assertTrue(fullMetadata.hasAttribute("attribute"));
    assertTrue(fullMetadata.hasAttribute("hidden-attribute"));
    assertTrue(fullMetadata.hasAnnotation("annotation"));
    assertTrue(fullMetadata.hasAnnotation("hidden-annotation"));

    final Record filteredFull = _blackboard.filterRecord(full, "filter");
    assertNotNull(filteredFull);
    final MObject filteredFullMetadata = filteredFull.getMetadata();
    assertNotNull(filteredFullMetadata);
    assertTrue(filteredFullMetadata.hasAttribute("attribute"));
    assertFalse(filteredFullMetadata.hasAttribute("hidden-attribute"));
    assertTrue(filteredFullMetadata.hasAnnotation("annotation"));
    assertFalse(filteredFullMetadata.hasAnnotation("hidden-annotation"));
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testHasAttribute() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(getClass().getName(), "testHasAttribute");
    _blackboard.create(id);
    assertFalse(_blackboard.hasAttribute(id, new Path("attribute")));
    assertFalse(_blackboard.hasAttribute(id, new Path("attribute/sub")));
    final Literal literal = _blackboard.createLiteral(id);
    literal.setValue("value");
    _blackboard.setLiteral(id, new Path("attribute"), literal);
    _blackboard.setLiteral(id, new Path("attribute/sub"), literal);
    assertTrue(_blackboard.hasAttribute(id, new Path("attribute")));
    assertTrue(_blackboard.hasAttribute(id, new Path("attribute/sub")));
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testHasAttachment() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(getClass().getName(), "testHasAttachment");
    _blackboard.create(id);
    assertFalse(_blackboard.hasAttachment(id, "attachment"));
    _blackboard.setAttachment(id, "attachment", new byte[0]);
    assertTrue(_blackboard.hasAttachment(id, "attachment"));
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testRemoveLiterals() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(getClass().getName(), "testRemoveLiterals");
    _blackboard.create(id);
    final Literal literal1 = _blackboard.createLiteral(id);
    literal1.setValue("value1");
    final Literal literal2 = _blackboard.createLiteral(id);
    literal2.setValue("value2");
    _blackboard.setLiteral(id, new Path("attribute"), literal1);
    _blackboard.addLiteral(id, new Path("attribute"), literal2);
    _blackboard.setLiteral(id, new Path("attribute/sub"), literal1);
    _blackboard.addLiteral(id, new Path("attribute/sub"), literal2);
    assertEquals(2, _blackboard.getLiteralsSize(id, new Path("attribute")));
    assertEquals(2, _blackboard.getLiteralsSize(id, new Path("attribute/sub")));
    _blackboard.removeLiterals(id, new Path("attribute"));
    assertEquals(0, _blackboard.getLiteralsSize(id, new Path("attribute")));
    assertEquals(2, _blackboard.getLiteralsSize(id, new Path("attribute/sub")));
    _blackboard.removeLiterals(id, new Path("attribute/sub"));
    assertEquals(0, _blackboard.getLiteralsSize(id, new Path("attribute/sub")));
    _blackboard.removeLiterals(id, new Path("missing-attribute"));
    assertEquals(0, _blackboard.getLiteralsSize(id, new Path("missing-attribute")));
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testRemoveObject() throws Exception {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(getClass().getName(), "testRemoveObject");
    _blackboard.create(id);
    final Literal literal1 = _blackboard.createLiteral(id);
    literal1.setValue("value1");
    final Literal literal2 = _blackboard.createLiteral(id);
    literal2.setValue("value2");
    _blackboard.setLiteral(id, new Path("attribute[0]/sub"), literal1);
    _blackboard.addLiteral(id, new Path("attribute[0]/sub"), literal2);
    _blackboard.setLiteral(id, new Path("attribute[1]/sub"), literal1);
    _blackboard.addLiteral(id, new Path("attribute[1]/sub"), literal2);
    assertEquals(2, _blackboard.getObjectSize(id, new Path("attribute")));
    assertEquals(2, _blackboard.getLiteralsSize(id, new Path("attribute[0]/sub")));
    assertEquals(2, _blackboard.getLiteralsSize(id, new Path("attribute[1]/sub")));
    _blackboard.removeObject(id, new Path("attribute"));
    assertEquals(1, _blackboard.getObjectSize(id, new Path("attribute")));
    assertEquals(2, _blackboard.getLiteralsSize(id, new Path("attribute[0]/sub")));
    _blackboard.removeObject(id, new Path("attribute"));
    assertEquals(0, _blackboard.getObjectSize(id, new Path("attribute")));
  }

  /**
   * Test get literal.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetLiteral() throws Exception {
    final Id id = setTestRecord();
    Path path = new Path("author[1]");
    assertEquals(_blackboard.getLiteral(id, path).getValue().toString(), "l2");
    path = new Path("author[1]/firstName1");
    assertEquals(_blackboard.getLiteral(id, path).getValue().toString(), "Georg");
    assertNull(_blackboard.getLiteral(id, new Path("unexistingPath")));
  }

  /**
   * Test get and set literals.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetSetLiterals() throws Exception {
    final Id id = setTestRecord();
    final Path path = new Path("author/firstName");
    assertEquals(_blackboard.getLiterals(id, path).get(0).getValue().toString(), "Igor");

    final RecordFactory recordFactory = RecordFactory.DEFAULT_INSTANCE;
    final Literal newLiteral = recordFactory.createLiteral();
    newLiteral.setValue("John");
    final List<Literal> literals = new ArrayList<Literal>();
    literals.add(newLiteral);

    _blackboard.setLiterals(id, path, literals);

    assertEquals(_blackboard.getLiterals(id, path).get(0).getValue().toString(), "John");

    assertTrue(_blackboard.getLiterals(id, new Path("unexistingPath")).isEmpty());
  }

  /**
   * Test has literals.
   *
   * @throws Exception
   *           the exception
   */
  public void testHasLiterals() throws Exception {
    final Id id = setTestRecord();

    assertTrue(_blackboard.hasLiterals(id, new Path("author/firstName")));
    assertFalse(_blackboard.hasLiterals(id, new Path("wrongPath")));
  }

  /**
   * Test remove literal.
   *
   * @throws Exception
   *           the exception
   */
  public void testRemoveLiteral() throws Exception {
    final Id id = setTestRecord();
    final Path path = new Path("author/firstName");

    assertTrue(_blackboard.hasLiterals(id, path));
    _blackboard.removeLiteral(id, path);
    assertFalse(_blackboard.hasLiterals(id, path));
  }

  /**
   * Test set, has, get, remove and names annotation methods.
   *
   * @throws Exception
   *           the exception
   */
  public void testSetHasGetAndRemoveAnnotation() throws Exception {
    final Id id = setTestRecord();
    final Path path = new Path("author");

    final RecordFactory recordFactory = RecordFactory.DEFAULT_INSTANCE;

    final Annotation annotation1 = recordFactory.createAnnotation();
    annotation1.setNamedValue("name1", "value1");
    _blackboard.setAnnotation(id, path, "annotation1", annotation1);

    final Annotation annotation2 = recordFactory.createAnnotation();
    annotation2.setNamedValue("name2", "value2");
    _blackboard.setAnnotation(id, path, "annotation2", annotation2);

    assertTrue(_blackboard.hasAnnotation(id, path, "annotation1"));
    assertTrue(_blackboard.hasAnnotation(id, path, "annotation2"));
    assertFalse(_blackboard.hasAnnotation(id, path, "annotation3"));

    assertEquals(_blackboard.getAnnotation(id, path, "annotation1"), annotation1);
    assertEquals(_blackboard.getAnnotation(id, path, "annotation2"), annotation2);
    assertNull(_blackboard.getAnnotation(id, path, "annotation3"));
    assertNull(_blackboard.getAnnotation(id, new Path("wrongPath"), "annotation2"));

    final List<Annotation> annotations = _blackboard.getAnnotations(id, path, "annotation1");
    assertFalse(annotations.isEmpty());
    assertEquals(annotations.get(0), annotation1);
    assertTrue(_blackboard.getAnnotations(id, new Path("wrongPath"), "annotation1").isEmpty());

    // Annotation names
    final Iterator<String> names = _blackboard.getAnnotationNames(id, path);
    final List<String> namesList = new ArrayList<String>();
    while (names.hasNext()) {
      final String name = names.next();
      namesList.add(name);
    }
    assertFalse(namesList.isEmpty());
    assertEquals(namesList.size(), 2);
    assertTrue(namesList.contains("annotation1"));
    assertTrue(namesList.contains("annotation2"));

    assertFalse(_blackboard.getAnnotationNames(id, new Path("wrongPath")).hasNext());

    _blackboard.removeAnnotation(id, path, "annotation1");
    assertFalse(_blackboard.hasAnnotation(id, path, "annotation1"));
    assertTrue(_blackboard.hasAnnotation(id, path, "annotation2"));
    assertFalse(_blackboard.hasAnnotation(id, path, "annotation3"));
  }

  /**
   * Test set, has and remove annotations method.
   *
   * @throws Exception
   *           the exception
   */
  public void testSetHasRemoveAnnotations() throws Exception {
    final Id id = setTestRecord();
    final Path path = new Path("author");

    final RecordFactory recordFactory = RecordFactory.DEFAULT_INSTANCE;

    assertFalse(_blackboard.hasAnnotations(id, path));

    final Annotation annotation1 = recordFactory.createAnnotation();
    final Annotation annotation2 = recordFactory.createAnnotation();

    final List<Annotation> annotations = new ArrayList<Annotation>();
    annotations.add(annotation1);
    annotations.add(annotation2);

    _blackboard.setAnnotations(id, path, "testAnnotations", annotations);

    assertTrue(_blackboard.hasAnnotations(id, path));

    _blackboard.removeAnnotations(id, path);

    assertFalse(_blackboard.hasAnnotations(id, path));
  }

  /**
   * Test get literals size.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetLiteralsSize() throws Exception {
    final Id id = setTestRecord();
    final Path path = new Path("author");
    assertEquals(_blackboard.getLiteralsSize(id, path), 2);
    assertEquals(_blackboard.getLiteralsSize(id, new Path("unexistingPath")), 0);
  }

  /**
   * Test get and set object semantic type.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetObjectSemanticType() throws Exception {
    final Id id = setTestRecord();

    assertEquals("appl:Author", _blackboard.getObjectSemanticType(id, new Path("author[1]")));
    assertNull(_blackboard.getObjectSemanticType(id, new Path("unexistingAttr")));

    _blackboard.setObjectSemanticType(id, new Path("author[1]"), "appl:Author1");
    assertEquals("appl:Author1", _blackboard.getObjectSemanticType(id, new Path("author[1]")));
  }

  /**
   * Test get object size.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetObjectSize() throws Exception {
    final Id id = setTestRecord();

    assertEquals(2, _blackboard.getObjectSize(id, new Path("author[1]")));
    assertEquals(0, _blackboard.getObjectSize(id, new Path("unexistingAttr")));
  }

  /**
   * Test has and remove objects.
   *
   * @throws Exception
   *           the exception
   */
  public void testHasAndRemoveObjects() throws Exception {
    final Id id = setTestRecord();

    assertTrue(_blackboard.hasObjects(id, new Path("author[1]")));
    assertFalse(_blackboard.hasObjects(id, new Path("author[1]/firstName")));

    _blackboard.removeObjects(id, new Path("author[1]"));

    assertFalse(_blackboard.hasObjects(id, new Path("author[1]")));
  }

  /**
   * Test get attribute names.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetAttributeNames() throws Exception {
    final Id id = setTestRecord();

    Iterator<String> it;

    // Null path
    it = _blackboard.getAttributeNames(id);
    int i = 0;
    while (it.hasNext()) {
      assertEquals(it.next(), "author");
      i++;
    }
    assertEquals(i, 1);

    // Path = author[1]
    Path path = new Path("author[1]");
    it = _blackboard.getAttributeNames(id, path);
    i = 0;
    while (it.hasNext()) {
      if (i == 0) {
        assertEquals(it.next(), "firstName1");
      }
      if (i == 1) {
        assertEquals(it.next(), "lastName1");
      }
      i++;
    }
    assertEquals(i, 2);

    // Unexisting path
    path = new Path("unexistingPath");
    it = _blackboard.getAttributeNames(id, path);
    assertFalse(it.hasNext());
  }

  /**
   * Test get global note.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetAndHasGlobalNote() throws Exception {
    _blackboard.setGlobalNote("name1", "note1");
    _blackboard.setGlobalNote("name2", "note2");

    assertEquals(_blackboard.getGlobalNote("name1"), "note1");
    assertEquals(_blackboard.getGlobalNote("name2"), "note2");

    assertTrue(_blackboard.hasGlobalNote("name1"));
    assertTrue(_blackboard.hasGlobalNote("name2"));
    assertFalse(_blackboard.hasGlobalNote("name3"));
  }

  /**
   * Test get record note.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetRecordNote() throws Exception {
    final Id id = setTestRecord();
    assertEquals(_blackboard.hasRecordNote(id, "name1"), false);
    try {
      _blackboard.getRecordNote(id, "name1");
      fail("Must throw BlackboardAccessException on unexisting note");
    } catch (final BlackboardAccessException e) {
      ; //ok
    }

    _blackboard.setRecordNote(id, "name1", "note1");
    _blackboard.setRecordNote(id, "name2", "note2");
    assertEquals("note1", _blackboard.getRecordNote(id, "name1"));
    assertEquals("note2", _blackboard.getRecordNote(id, "name2"));
  }

  /**
   * Test has record note.
   *
   * @throws Exception
   *           the exception
   */
  public void testHasRecordNote() throws Exception {
    final Id id = setTestRecord();
    _blackboard.setRecordNote(id, "name1", "note1");
    assertEquals(_blackboard.hasRecordNote(id, "name1"), true);
    assertEquals(_blackboard.hasRecordNote(id, "name5"), false);
  }

  /**
   * Test set and get attachment.
   *
   * @throws Exception
   *           the exception
   */
  public void testSetAndGetAttachment() throws Exception {
    final String attachment1 = "testattachment1";
    final String attachment2 = "testattachment2";
    final Id id = setTestRecord();
    _blackboard.setAttachment(id, "test1", attachment1.getBytes());
    _blackboard.setAttachment(id, "test2", attachment2.getBytes());
    final byte[] storageAttachment1 = _blackboard.getAttachment(id, "test1");
    assertEquals(attachment1, new String(storageAttachment1));
    final byte[] storageAttachment2 = _blackboard.getAttachment(id, "test2");
    assertEquals(attachment2, new String(storageAttachment2));
  }

  /**
   * Test invalidate.
   *
   * @throws Exception
   *           the exception
   */
  public void testInvalidate() throws Exception {
    final Id id = setTestRecord();

    // Attach an attachment to check removing cached attachments
    final String attachment = "testattachment";
    _blackboard.setAttachment(id, "test", attachment.getBytes());
    _blackboard.commit(id);

    _blackboard.load(id);
    _blackboard.invalidate(id);
  }

  /**
   * Test set and get attachment from stream.
   *
   * @throws Exception
   *           the exception
   */
  public void testSetAndGetAttachmentFromStream() throws Exception {
    final String attachment = "testattachment1";
    final InputStream attachmentStream = new ByteArrayInputStream(attachment.getBytes());
    final Id id = setTestRecord();
    _blackboard.setAttachmentFromStream(id, "test1", attachmentStream);

    final InputStream storageAttachmentStream = _blackboard.getAttachmentAsStream(id, "test1");
    assertEquals(attachment, new String(IOUtils.toByteArray(storageAttachmentStream)));

    try {
      _blackboard.getAttachmentAsStream(id, "test2");
      fail("Must throw BlackboardAccessException for unexisting attachment");
    } catch (final BlackboardAccessException e) {
      ; // ok
    }

    final byte[] storageAttachment = _blackboard.getAttachment(id, "test1");
    assertEquals(attachment, new String(storageAttachment));
  }

  /**
   * Test set, get and remove attachment.
   *
   * @throws Exception
   *           the exception
   */
  public void testSetGetAndRemoveAttachment() throws Exception {
    // set attachments
    final String attachment1 = "testattachment1";
    final String attachment2 = "testattachment2";
    final Id id = setTestRecord();
    _blackboard.setAttachment(id, "test1", attachment1.getBytes());
    _blackboard.setAttachment(id, "test2", attachment2.getBytes());

    // get the attachments
    final byte[] storageAttachment1 = _blackboard.getAttachment(id, "test1");
    assertEquals(attachment1, new String(storageAttachment1));
    final byte[] storageAttachment2 = _blackboard.getAttachment(id, "test2");
    assertEquals(attachment2, new String(storageAttachment2));

    // remove the attachments
    _blackboard.removeAttachment(id, "test1");
    _blackboard.removeAttachment(id, "test2");

    // check that the attachments do no longer exist
    try {
      _blackboard.getAttachment(id, "test1");
    } catch (final BlackboardAccessException e) {
      final String expectedMsg = "Record with idHash = " + id.getIdHash() + " doesn't have the attachment [test1]";
      assertEquals(expectedMsg, e.getMessage());
    }
    try {
      _blackboard.getAttachment(id, "test2");
    } catch (final BlackboardAccessException e) {
      final String expectedMsg = "Record with idHash = " + id.getIdHash() + " doesn't have the attachment [test2]";
      assertEquals(expectedMsg, e.getMessage());
    }

  }

  /**
   * Test set record.
   *
   * @throws Exception
   *           the exception
   */
  public void testSetRecord() throws Exception {
    final Id id = setTestRecord();
    Record record = _blackboard.getRecord(id);
    final String attachment = "testSetRecord1";
    record.setAttachment(attachment, attachment.getBytes());
    // replace old record entry with new one
    _blackboard.setRecord(record);
    record = _blackboard.getRecord(id);
    assertTrue(Arrays.equals(attachment.getBytes(), record.getAttachment(attachment)));
    assertEquals(attachment, new String(_blackboard.getAttachment(id, attachment)));
  }

  /**
   * Test getting attachment as file.
   *
   * @throws Exception
   *           the exception
   */
  public void testGetAttachmentAsFile() throws Exception {
    final String attachment1 = "testGetAttachmentAsFile1";
    final String attachment2 = "testGetAttachmentAsFile2";
    final Id id = setTestRecord();
    _blackboard.setAttachment(id, attachment1, attachment1.getBytes());
    final File attachmentFile = _blackboard.getAttachmentAsFile(id, attachment1);
    assertEquals(attachmentFile.length(), attachment1.getBytes().length);

    // check that cached attachment file is removed when another attachment with the same name is set
    try {
      _blackboard.setAttachment(id, attachment1, attachment2.getBytes());
    } catch (final BlackboardAccessException exception) {
      final String expected =
        "Attachment [" + attachment1 + "] of record with idHash=" + id.getIdHash()
          + " was previously loaded by getAttachmentAsFile method";
      assertEquals(expected, exception.getMessage());
    }

    _blackboard.commit(id);
  }

  /**
   * Test setting attachment from file.
   *
   * @throws Exception
   *           the exception
   */
  public void testSetAttachmentFromFile() throws Exception {
    final String attachment1 = "testSetAttachmentFromFile1";
    final File tempDir = WorkspaceHelper.createWorkingDir(BUNDLE_ID);
    final File attachmentFile = new File(tempDir, attachment1);
    final OutputStream output = FileUtils.openOutputStream(attachmentFile);
    IOUtils.write(attachment1.getBytes(), output);
    IOUtils.closeQuietly(output);

    final Id id = setTestRecord();
    _blackboard.setAttachmentFromFile(id, attachment1, attachmentFile);

    final byte[] storageAttachment1 = _blackboard.getAttachment(id, attachment1);
    assertEquals(storageAttachment1.length, attachment1.getBytes().length);

    try {
      _blackboard.setAttachmentFromFile(id, attachment1, new File("unexistingFile"));
      fail("Must throw BlackboardAccessException");
    } catch (final BlackboardAccessException e) {
      ; //ok
    }
  }

  /**
   * Sets the test record.
   *
   * @return the id
   *
   * @throws Exception
   *           the exception
   */
  private Id setTestRecord() throws Exception {
    //CHECKSTYLE:OFF
    /*
     * Test record structure: <Record> <A n="author"> <L>l1</L> <L>l2</L> <O> <A n="firstName"> <L> <V>Igor</V> </L>
     * </A> <A n="lastName"> <L> <V>Novakovic</V> </L> </A> </O> <O st="appl:Author"> <A n="firstName1"> <L> <V>Georg</V>
     * </L> </A> <A n="lastName1"> <L> <V>Schmidt</V> </L> </A> </O> </A> <Record>
     */
    //CHECKSTYLE:ON
    final RecordFactory recordFactory = RecordFactory.DEFAULT_INSTANCE;

    // Create root attribute
    final Attribute attribute = recordFactory.createAttribute();
    attribute.setName("author");

    // Create root attribute literals
    final Literal l1 = recordFactory.createLiteral();
    l1.setValue("l1");
    final Literal l2 = recordFactory.createLiteral();
    l2.setValue("l2");
    attribute.addLiteral(l1);
    attribute.addLiteral(l2);

    // Create first metadata object
    final MObject metadata1 = recordFactory.createMetadataObject();
    // Create attribute11
    final Attribute attribute11 = recordFactory.createAttribute();
    final Literal literal11 = recordFactory.createLiteral();
    literal11.setValue("Igor");
    attribute11.setLiteral(literal11);
    // Create attribute12
    final Attribute attribute12 = recordFactory.createAttribute();
    final Literal literal12 = recordFactory.createLiteral();
    literal12.setValue("Novakovic");
    attribute12.setLiteral(literal12);
    // Set attributes to metadata1
    metadata1.setAttribute("firstName", attribute11);
    metadata1.setAttribute("lastName", attribute12);

    // Create second metadata object
    final MObject metadata2 = recordFactory.createMetadataObject();
    metadata2.setSemanticType("appl:Author");
    // Create attribute21
    final Attribute attribute21 = recordFactory.createAttribute();
    final Literal literal21 = recordFactory.createLiteral();
    literal21.setValue("Georg");
    attribute21.setLiteral(literal21);
    // Create attribute22
    final Attribute attribute22 = recordFactory.createAttribute();
    final Literal literal22 = recordFactory.createLiteral();
    literal22.setValue("Schmidt");
    attribute22.setLiteral(literal22);
    // Set attributes to metadata22
    metadata2.setAttribute("firstName1", attribute21);
    metadata2.setAttribute("lastName1", attribute22);

    // Add metadata1 and metadata2 to root attribute
    attribute.addObject(metadata1);
    attribute.addObject(metadata2);

    // Create root metadata object
    final MObject metadata = recordFactory.createMetadataObject();
    metadata.setAttribute("author", attribute);

    // Create record
    final Record record = recordFactory.createRecord();

    // Set record metadata
    record.setMetadata(metadata);

    // Create record Id
    final Key key = IdFactory.DEFAULT_INSTANCE.createKey("key");
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("source", key);
    record.setId(id);

    // Set record to the blackboard
    _blackboard.setRecord(record);

    return id;
  }
}
