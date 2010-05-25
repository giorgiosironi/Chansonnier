/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.jms;

import java.io.StringReader;

import javax.jms.InvalidSelectorException;
import javax.jms.Message;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.selector.SelectorParser;
import org.apache.commons.lang.StringUtils;

/**
 * The Class MessageSelectorEvaluationHelper.
 */
public class MessageSelectorEvalHelper {

  /**
   * The _boolean expression.
   */
  private final BooleanExpression _booleanExpression;

  /**
   * Instantiates a new message selector evaluation helper.
   * 
   * @param expression
   *          the expression
   * 
   * @throws MessageSelectorEvalException
   *           the message selector evaluation exception
   */
  public MessageSelectorEvalHelper(final String expression) throws MessageSelectorEvalException {
    if (StringUtils.isBlank(expression)) {
      _booleanExpression = null;
      return;
    }
    // this is needed to support older and newer versions of ActiveMQ
    final SelectorParser parser = new SelectorParser(new StringReader(""));
    try {
      _booleanExpression = parser.parse(expression);
    } catch (final InvalidSelectorException e) {
      throw new MessageSelectorEvalException(e);
    }
  }

  /**
   * Creates the dummy message.
   * 
   * @return the message
   */
  public static Message createDummyMessage() {
    return new ActiveMQBytesMessage();
  }

  /**
   * Evaluate.
   * 
   * @param message
   *          the message
   * 
   * @return true, if successful
   * 
   * @throws MessageSelectorEvalException
   *           the message selector evaluation exception
   */
  public boolean evaluate(final Message message) throws MessageSelectorEvalException {
    if (_booleanExpression == null) {
      return true;
    }
    try {
      final MessageEvaluationContext context = new MessageEvaluationContext();
      context.setMessageReference((MessageReference) message);
      return (Boolean) _booleanExpression.evaluate(context);
    } catch (final Throwable e) {
      throw new MessageSelectorEvalException(e);
    }
  }

}
