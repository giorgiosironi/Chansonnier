/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class FormatHelper.
 */
public final class ParameterFormatHelper {

  /**
   * Private constructor to avoid instatiation.
   */
  private ParameterFormatHelper() {
  }

  /**
   * Format.
   * 
   * @param input
   *          the input
   * @param globalResult
   *          the result
   * @param parameters
   *          the parameters
   * @param localResult
   *          the local result
   * @return the string
   */
  public static String format(final String input, final Object globalResult, final String[] parameters,
    final Object localResult) {
    // OutWriter.write("Formatting..." + input);
    String out = input;
    String resultString = "null";
    if (globalResult != null) {
      resultString = globalResult.toString();
    }
    // replacing %s by result
    out = out.replace("%0", resultString);
    // replacing %1 %n by parameters
    if (out.contains("%")) {
      String newOut = "";
      final Pattern pattern = Pattern.compile("\\%(\\d+)");
      final Matcher matcher = pattern.matcher(out);
      int index = 0;
      while (matcher.find()) {
        final int propertyNum = Integer.parseInt(matcher.group(1));
        if (propertyNum > parameters.length) {
          throw new IllegalArgumentException(String.format("Parameter [%s] specified by string [%s] is not found",
            matcher.group(1), out));
        }
        newOut += out.substring(index, matcher.start());
        newOut += parameters[propertyNum - 1];
        index = matcher.end();
      }
      newOut += out.substring(index);
      out = newOut;
    }
    // OutWriter.write("Formatting ends..." + out);
    return out;
  }
}
