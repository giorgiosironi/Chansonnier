/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.lyricwiki;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLLyricWikiParser implements LyricWikiParser {

	/* (non-Javadoc)
	 * @see it.polimi.chansonnier.driver.lyricwiki.LyricWikiParser#getLyrics(java.lang.String)
	 */
	public String getLyrics(String xmlContent) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlContent));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList lyricsNodes = doc.getElementsByTagName("lyrics");
            Node lyricsNode = lyricsNodes.item(0);
            Element lyricsElement = (Element) lyricsNode;
            return _getTextValue(lyricsElement);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Malformed document.");
        }
    }

    private static String _getTextValue(Element element)
    {
        return ((Node) element.getChildNodes().item(0)).getNodeValue();
    }

}
