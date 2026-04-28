package fr.recia.glc.configuration.cas;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomAttributeHandler extends DefaultHandler {

    private Map<String, Object> attributes;
    private boolean foundAttributes;
    private String currentAttribute;
    private StringBuilder value;

    @Override
    public void startDocument() throws SAXException {
        this.attributes = new HashMap<>();
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if ("attributes".equals(localName)) {
            this.foundAttributes = true;
        } else if (this.foundAttributes) {
            this.value = new StringBuilder();
            this.currentAttribute = localName;
        }
    }

    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        if (this.currentAttribute != null) {
            value.append(chars, start, length);
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if ("attributes".equals(localName)) {
            this.foundAttributes = false;
            this.currentAttribute = null;
        } else if (this.foundAttributes) {
            final Object o = this.attributes.get(this.currentAttribute);

            if (o == null) {
                this.attributes.put(this.currentAttribute, this.value.toString());
            } else {
                final List<Object> items;
                if (o instanceof List) {
                    items = (List<Object>) o;
                } else {
                    items = new LinkedList<>();
                    items.add(o);
                    this.attributes.put(this.currentAttribute, items);
                }
                items.add(this.value.toString());
            }
        }
    }

}
