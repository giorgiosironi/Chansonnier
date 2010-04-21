package org.eclipse.smila.datamodel.tools;

public class NameValuePair {

  private String _name = null;

  private String _value = null;

  public NameValuePair(String name, String value) {
    _name = name;
    _value = value;
  }

  public String getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

}
