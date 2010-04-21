/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * NullPrintWriter. 
 */
public class NullPrintWriter extends PrintWriter {

  /**
   * Default Constructor.
   */
  public NullPrintWriter() {
    super(new ByteArrayOutputStream());
  }
  
  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Appendable#append(char)
   */
  @Override
  public PrintWriter append(char arg0) {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Appendable#append(java.lang.CharSequence, int, int)
   */
  @Override
  public PrintWriter append(CharSequence arg0, int arg1, int arg2) {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Appendable#append(java.lang.CharSequence)
   */
  @Override
  public PrintWriter append(CharSequence arg0) {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#checkError()
   */
  @Override
  public boolean checkError() {
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.Closeable#close()
   */
  @Override
  public void close() {
    super.close();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.Flushable#flush()
   */
  @Override
  public void flush() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#format(java.util.Locale, java.lang.String, java.lang.Object[])
   */
  @Override
  public PrintWriter format(Locale arg0, String arg1, Object... arg2) {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#format(java.lang.String, java.lang.Object[])
   */
  @Override
  public PrintWriter format(String arg0, Object... arg1) {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(boolean)
   */
  @Override
  public void print(boolean arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(char)
   */
  @Override
  public void print(char arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(char[])
   */
  @Override
  public void print(char[] arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(double)
   */
  @Override
  public void print(double arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(float)
   */
  @Override
  public void print(float arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(int)
   */
  @Override
  public void print(int arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(long)
   */
  @Override
  public void print(long arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(java.lang.Object)
   */
  @Override
  public void print(Object arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#print(java.lang.String)
   */
  @Override
  public void print(String arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#printf(java.util.Locale, java.lang.String, java.lang.Object[])
   */
  @Override
  public PrintWriter printf(Locale arg0, String arg1, Object... arg2) {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#printf(java.lang.String, java.lang.Object[])
   */
  @Override
  public PrintWriter printf(String arg0, Object... arg1) {
    return this;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println()
   */
  @Override
  public void println() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(boolean)
   */
  @Override
  public void println(boolean arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(char)
   */
  @Override
  public void println(char arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(char[])
   */
  @Override
  public void println(char[] arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(double)
   */
  @Override
  public void println(double arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(float)
   */
  @Override
  public void println(float arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(int)
   */
  @Override
  public void println(int arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(long)
   */
  @Override
  public void println(long arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(java.lang.Object)
   */
  @Override
  public void println(Object arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#println(java.lang.String)
   */
  @Override
  public void println(String arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.PrintWriter#setError()
   */
  @Override
  protected void setError() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.Writer#write(char[], int, int)
   */
  @Override
  public void write(char[] arg0, int arg1, int arg2) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.Writer#write(char[])
   */
  @Override
  public void write(char[] arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.Writer#write(int)
   */
  @Override
  public void write(int arg0) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.Writer#write(java.lang.String, int, int)
   */
  @Override
  public void write(String arg0, int arg1, int arg2) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.io.Writer#write(java.lang.String)
   */
  @Override
  public void write(String arg0) {
  }

}
