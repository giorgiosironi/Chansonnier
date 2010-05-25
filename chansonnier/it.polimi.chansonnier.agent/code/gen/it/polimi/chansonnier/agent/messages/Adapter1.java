/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/

// CHECKSTYLE:OFF

package it.polimi.chansonnier.agent.messages;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.smila.connectivity.framework.schema.config.MimeTypeAttributeType;

public class Adapter1
    extends XmlAdapter<String, MimeTypeAttributeType>
{


    public MimeTypeAttributeType unmarshal(String value) {
        return (org.eclipse.smila.connectivity.framework.schema.config.MimeTypeAttributeType.fromValue(value));
    }

    public String marshal(MimeTypeAttributeType value) {
        return (org.eclipse.smila.connectivity.framework.schema.config.MimeTypeAttributeType.toValue(value));
    }

}

// CHECKSTYLE:ON
