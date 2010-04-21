/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.utils.file;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author brox IT-Solutions GmbH
 * 
 */
public abstract class FileFilterUtils {

  /**
   * logger.
   * 
   */
  private static final Log LOG = LogFactory.getLog(FileFilterUtils.class);

  /**
   * Constructor.
   */
  private FileFilterUtils() {

  }

  /**
   * @param path =
   * @return String[]
   */
  public static String[] dissect(String path) {
    final char sep = File.separatorChar;
    path = path.replace('/', sep).replace('\\', sep);

    String root = null;
    final int colon = path.indexOf(':');
    if (colon > 0) {

      int next = colon + 1;
      root = path.substring(0, next);
      final char[] ca = path.toCharArray();
      root += sep;
      // remove the initial separator; the root has it.
      if (ca[next] == sep) {
        next = next + 1;
      }

      final StringBuffer sbPath = new StringBuffer();
      // Eliminate consecutive slashes after the drive spec:
      for (int i = next; i < ca.length; i++) {
        if (ca[i] != sep || ca[i - 1] != sep) {
          sbPath.append(ca[i]);
        }
      }
      path = sbPath.toString();
    } else if (path.length() > 1 && path.charAt(1) == sep) {

      int nextsep = path.indexOf(sep, 2);
      nextsep = path.indexOf(sep, nextsep + 1);

      if (nextsep > 2) {
        root = path.substring(0, nextsep + 1);

      } else {
        root = path;
      }
      path = path.substring(root.length());
    } else {
      root = File.separator;
      path = path.substring(1);
    }
    return new String[] { root, path };
  }

  /**
   * @param pattern =
   * @param str =
   * @param isCaseSensitive =
   * @return boolean =
   */
  public static boolean match(String pattern, String str, boolean isCaseSensitive) {
    final char[] patArr = pattern.toCharArray();
    final char[] strArr = str.toCharArray();
    int patIdxStart = 0;
    int patIdxEnd = patArr.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strArr.length - 1;
    char ch;

    boolean containsStar = false;
    for (int i = 0; i < patArr.length; i++) {
      if (patArr[i] == '*') {
        containsStar = true;
        break;
      }
    }

    if (!containsStar) {
      // No '*'s, so we make a shortcut
      if (patIdxEnd != strIdxEnd) {
        return false; // Pattern and string do not have the same size
      }
      for (int i = 0; i <= patIdxEnd; i++) {
        ch = patArr[i];
        if (ch != '?') {
          if (isCaseSensitive && ch != strArr[i]) {
            return false; // Character mismatch
          }
          if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[i])) {
            return false; // Character mismatch
          }
        }
      }
      return true; // String matches against pattern
    }

    if (patIdxEnd == 0) {
      return true; // Pattern contains only '*', which matches anything
    }

    // Process characters before first star

    ch = patArr[patIdxStart];

    while ((ch != '*') && strIdxStart <= strIdxEnd) {
      if (ch != '?') {
        if (isCaseSensitive && ch != strArr[strIdxStart]) {
          return false; // Character mismatch
        }
        if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])) {
          return false; // Character mismatch
        }
      }
      patIdxStart++;
      strIdxStart++;
    }
    if (strIdxStart > strIdxEnd) {
      // All characters in the string are used. Check if only '*'s are
      // left in the pattern. If so, we succeeded. Otherwise failure.
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (patArr[i] != '*') {
          return false;
        }
      }
      return true;
    }

    // Process characters after last star
    ch = patArr[patIdxEnd];

    while ((ch != '*') && strIdxStart <= strIdxEnd) {
      if (ch != '?') {
        if (isCaseSensitive && ch != strArr[strIdxEnd]) {
          return false; // Character mismatch
        }
        if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])) {
          return false; // Character mismatch
        }
      }
      patIdxEnd--;
      strIdxEnd--;
    }
    if (strIdxStart > strIdxEnd) {
      // All characters in the string are used. Check if only '*'s are
      // left in the pattern. If so, we succeeded. Otherwise failure.
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (patArr[i] != '*') {
          return false;
        }
      }
      return true;
    }

    // process pattern between stars. padIdxStart and patIdxEnd point
    // always to a '*'.
    while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
      int patIdxTmp = -1;
      for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
        if (patArr[i] == '*') {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == patIdxStart + 1) {
        // Two stars next to each other, skip the first one.
        patIdxStart++;
        continue;
      }
      // Find the pattern between padIdxStart & padIdxTmp in str between
      // strIdxStart & strIdxEnd
      final int patLength = (patIdxTmp - patIdxStart - 1);
      final int strLength = (strIdxEnd - strIdxStart + 1);
      int foundIdx = -1;
      strLoop: for (int i = 0; i <= strLength - patLength; i++) {
        for (int j = 0; j < patLength; j++) {
          ch = patArr[patIdxStart + j + 1];
          if (ch != '?') {
            if (isCaseSensitive && ch != strArr[strIdxStart + i + j]) {
              continue strLoop;
            }
            if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart + i + j])) {
              continue strLoop;
            }
          }
        }

        foundIdx = strIdxStart + i;
        break;
      }

      if (foundIdx == -1) {
        return false;
      }

      patIdxStart = patIdxTmp;
      strIdxStart = foundIdx + patLength;
    }

    // All characters in the string are used. Check if only '*'s are left
    // in the pattern. If so, we succeeded. Otherwise failure.
    for (int i = patIdxStart; i <= patIdxEnd; i++) {
      if (patArr[i] != '*') {
        return false;
      }
    }
    return true;
  }

  /**
   * @param pattern -
   * @param str -
   * @param isCaseSensitive -
   * @return boolean
   */
  public static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
    String[] patDirs = tokenizePathAsArray(pattern);
    String[] strDirs = tokenizePathAsArray(str);

    int patIdxStart = 0;
    int patIdxEnd = patDirs.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strDirs.length - 1;

    // up to first '**'
    while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
      final String patDir = patDirs[patIdxStart];
      if (patDir.equals("**")) {
        break;
      }

      if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
        patDirs = null;
        strDirs = null;
        return false;
      }
      patIdxStart++;
      strIdxStart++;
    }
    if (strIdxStart > strIdxEnd) {
      // String is exhausted
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (!patDirs[i].equals("**")) {
          patDirs = null;
          strDirs = null;
          return false;
        }
      }
      return true;
    } else {
      if (patIdxStart > patIdxEnd) {
        // String not exhausted, but pattern is. Failure.
        patDirs = null;
        strDirs = null;
        return false;
      }
    }

    // up to last '**'
    while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
      final String patDir = patDirs[patIdxEnd];
      if (patDir.equals("**")) {
        break;
      }
      if (!match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
        patDirs = null;
        strDirs = null;
        return false;
      }
      patIdxEnd--;
      strIdxEnd--;
    }
    if (strIdxStart > strIdxEnd) {
      // String is exhausted
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (!patDirs[i].equals("**")) {
          patDirs = null;
          strDirs = null;
          return false;
        }
      }
      return true;
    }

    while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
      int patIdxTmp = -1;
      for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
        if (patDirs[i].equals("**")) {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == patIdxStart + 1) {
        // '**/**' situation, so skip one
        patIdxStart++;
        continue;
      }
      // Find the pattern between padIdxStart & padIdxTmp in str between
      // strIdxStart & strIdxEnd
      final int patLength = (patIdxTmp - patIdxStart - 1);
      final int strLength = (strIdxEnd - strIdxStart + 1);
      int foundIdx = -1;
      strLoop: for (int i = 0; i <= strLength - patLength; i++) {
        for (int j = 0; j < patLength; j++) {
          final String subPat = patDirs[patIdxStart + j + 1];
          final String subStr = strDirs[strIdxStart + i + j];
          if (!match(subPat, subStr, isCaseSensitive)) {
            continue strLoop;
          }
        }

        foundIdx = strIdxStart + i;
        break;
      }

      if (foundIdx == -1) {
        patDirs = null;
        strDirs = null;
        return false;
      }

      patIdxStart = patIdxTmp;
      strIdxStart = foundIdx + patLength;
    }

    for (int i = patIdxStart; i <= patIdxEnd; i++) {
      if (!patDirs[i].equals("**")) {
        patDirs = null;
        strDirs = null;
        return false;
      }
    }

    return true;
  }

  /**
   * @param path =
   * @return String[]
   */
  private static String[] tokenizePathAsArray(String path) {
    String root = null;
    if (new File(path).isAbsolute()) {
      final String[] s = dissect(path);
      root = s[0];
      path = s[1];
    }
    final char sepSlash = '/';
    final char sepBackSlash = '\\';
    int start = 0;
    final int len = path.length();
    int count = 0;
    for (int pos = 0; pos < len; pos++) {
      if (path.charAt(pos) == sepSlash || path.charAt(pos) == sepBackSlash) {
        if (pos != start) {
          count++;
        }
        start = pos + 1;
      }
    }
    if (len != start) {
      count++;
    }
    final String[] l = new String[count + ((root == null) ? 0 : 1)];

    if (root != null) {
      l[0] = root;
      count = 1;
    } else {
      count = 0;
    }
    start = 0;
    for (int pos = 0; pos < len; pos++) {
      if (path.charAt(pos) == sepSlash || path.charAt(pos) == sepBackSlash) {
        if (pos != start) {
          final String tok = path.substring(start, pos);
          l[count++] = tok;
        }
        start = pos + 1;
      }
    }
    if (len != start) {
      final String tok = path.substring(start);
      l[count/* ++ */] = tok;
    }
    return l;
  }

}
