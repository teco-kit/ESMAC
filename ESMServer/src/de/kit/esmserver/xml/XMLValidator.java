package de.kit.esmserver.xml;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;

public class XMLValidator {

	/**
	 * Validation method.
	 *
	 * @param xmlFilePath
	 *            The xml file we are trying to validate.
	 * @param xmlSchemaFilePath
	 *            The schema file we are using for the validation. This method
	 *            assumes the schema file is valid.
	 * @return True if valid, false if not valid or bad parse or exception/error
	 *         during parse.
	 */
	public static boolean validate(String xmlFilePath, String xmlSchemaFilePath)
			throws IOException, SAXException {

		// Try the validation, we assume that if there are any issues with the
		// validation
		// process that the input is invalid.
		SchemaFactory factory = new XMLSchemaFactory();
		Source schemaFile = new StreamSource(new File(xmlSchemaFilePath));
		Source xmlSource = new StreamSource(new File(xmlFilePath));
		Schema schema = factory.newSchema(schemaFile);
		Validator validator = schema.newValidator();
		validator.validate(xmlSource);
		return true;
	}
}
