
// CHECKSTYLE:OFF

package it.polimi.smilaintegration.youtubegrabber.messages;

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