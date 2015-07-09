package de.kit.esmserver.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.SAXException;

import com.vaadin.server.VaadinService;

import de.kit.esmserver.uicomponents.AbstractWritableComponent;
import de.kit.esmserver.uicomponents.RuleComponent;
import de.kit.esmserver.uicomponents.ScreenComponent;
import de.kit.esmserver.uicomponents.SensorAndNotificationComponent;

public class XMLWriter {

	public File writeXMlToFile(List<AbstractWritableComponent> writables) {
		String basepath = VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath()
				+ "/WEB-INF/xsd";
		File file = new File(basepath + "/masterarbeit.xml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			XMLStreamWriter writer = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(new FileOutputStream(file), "UTF-8");
			writer.writeStartDocument();
			writer.writeStartElement("root");
			writer.writeAttribute("voluntary", getVoluntaryValue(writables));

			writer.writeStartElement("questions");
			for (ScreenComponent screenComponent : getListForClass(writables,
					ScreenComponent.class)) {
				screenComponent.writeXML(writer);
			}
			writer.writeEndElement();

			writer.writeStartElement("rules");
			for (RuleComponent ruleComponent : getListForClass(writables,
					RuleComponent.class)) {
				ruleComponent.writeXML(writer);
			}
			writer.writeEndElement();

			for (SensorAndNotificationComponent sensorAndNotificationComponent : getListForClass(
					writables, SensorAndNotificationComponent.class)) {
				sensorAndNotificationComponent.writeXML(writer);
			}

			writer.writeEndElement();
			writer.writeEndDocument();
		} catch (FileNotFoundException | XMLStreamException
				| FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			XMLValidator.validate(file.getAbsolutePath(), basepath
					+ "/masterarbeit.xsd");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file;

	}

	private String getVoluntaryValue(List<AbstractWritableComponent> writables) {
		for (AbstractWritableComponent abstractWritableComponent : writables) {
			if (abstractWritableComponent instanceof SensorAndNotificationComponent) {
				return ((SensorAndNotificationComponent) abstractWritableComponent)
						.getVoluntaryValue().toLowerCase();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractWritableComponent> List<T> getListForClass(
			List<AbstractWritableComponent> writableComponents,
			Class<T> className) {
		List<T> classComponents = new ArrayList<T>();
		for (AbstractWritableComponent abstractWritableComponent : writableComponents) {
			if (className.isInstance(abstractWritableComponent)) {
				classComponents.add((T) abstractWritableComponent);
			}
		}
		return classComponents;
	}
}
