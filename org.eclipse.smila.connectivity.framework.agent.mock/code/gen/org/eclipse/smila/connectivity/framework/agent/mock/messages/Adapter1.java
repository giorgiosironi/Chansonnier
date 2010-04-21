/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF

package org.eclipse.smila.connectivity.framework.agent.mock.messages;

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
