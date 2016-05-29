package de.kit.esmserver.uicomponents;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class AbstractWritableComponent extends AbstractCustomComponent {
	private static final long serialVersionUID = -6961518664563704440L;

	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
	}

	public boolean isValid() {
		return false;
	}

}
