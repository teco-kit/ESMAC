package de.kit.esmserver.uicomponents.sensor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import de.kit.esmserver.uicomponents.validators.IntegerValidator;
import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class NotificationComponent extends AbstractSensorComponent {
	private static final long serialVersionUID = 6703917462061699663L;
	private Label keyLabel;
	private ComboBox keyComboBox;
	private HorizontalLayout horizontalLayout;
	private TextField countTextfield;
	private List<ComboBox> packageList;
	private NotNullValidator notNullValidator;

	@SuppressWarnings("serial")
	public NotificationComponent() {
		notNullValidator = new NotNullValidator();
		packageList = new ArrayList<ComboBox>();
		buildMainLayout();

		keyComboBox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				switch (String.valueOf(event.getProperty().getValue())) {
				case "count":
					setBackToBaseLayout();
					horizontalLayout.addComponent(new Label("count"));
					horizontalLayout.addComponent(countTextfield);
					setFullOperatorComboBox();
					NotificationComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					break;
				case "package":
					setBackToBaseLayout();
					horizontalLayout.addComponent(new Label("Package Name"));
					ComboBox firstComboBox = getNewPackageComboBox();
					packageList.clear();
					packageList.add(firstComboBox);
					horizontalLayout.addComponent(firstComboBox);
					Button addPackageButton = new Button("+");
					Button removePackageButton = new Button("-");
					initializeClickListenerForButtons(addPackageButton,
							removePackageButton);
					horizontalLayout.addComponent(removePackageButton);
					horizontalLayout.addComponent(addPackageButton);
					setRestrictedOperatorComboBox();
					NotificationComponent.super.buildMainLayout();
					setAlignmentMiddleCenter();
					break;

				default:
					break;
				}
			}

			private void initializeClickListenerForButtons(
					Button addPackageButton, Button removePackageButton) {
				addPackageButton.addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						ComboBox newPackageComboBox = getNewPackageComboBox();
						packageList.add(newPackageComboBox);
						if (getLabelOperator().isEnabled()) {
							int index = horizontalLayout.getComponentCount() - 4;
							horizontalLayout.addComponent(newPackageComboBox,
									index);
							horizontalLayout.addComponent(new Label("or"),
									index);
							setAlignmentMiddleCenter();
						} else {
							int index = horizontalLayout.getComponentCount() - 2;
							horizontalLayout.addComponent(newPackageComboBox,
									index);
							horizontalLayout.addComponent(new Label("or"),
									index);
							setAlignmentMiddleCenter();
						}

					}
				});

				removePackageButton.addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						if (packageList.size() > 1) {
							if (getLabelOperator().isEnabled()) {
								Component label = horizontalLayout
										.getComponent(horizontalLayout
												.getComponentCount() - 6);
								Component component = horizontalLayout
										.getComponent(horizontalLayout
												.getComponentCount() - 5);
								packageList.remove(component);
								horizontalLayout.removeComponent(component);
								horizontalLayout.removeComponent(label);
							} else {
								Component label = horizontalLayout
										.getComponent(horizontalLayout
												.getComponentCount() - 4);
								Component component = horizontalLayout
										.getComponent(horizontalLayout
												.getComponentCount() - 3);
								packageList.remove(component);
								horizontalLayout.removeComponent(component);
								horizontalLayout.removeComponent(label);
							}
						}
					}
				});
			}
		});
	}

	@Override
	protected Panel buildMainLayout() {
		horizontalLayout = new HorizontalLayout();
		countTextfield = new TextField();
		countTextfield.setImmediate(true);
		countTextfield.setValidationVisible(true);
		countTextfield.addValidator(new IntegerValidator(false,false));

		keyLabel = new Label("Key");
		keyComboBox = new ComboBox();
		keyComboBox.addItem("count");
		keyComboBox.addItem("package");
		keyComboBox.setImmediate(true);
		keyComboBox.setValidationVisible(true);
		keyComboBox.addValidator(notNullValidator);

		setRestrictedOperatorComboBox();
		setBackToBaseLayout();
		panel.setContent(horizontalLayout);
		panel.setCaption("Notification");
		return super.buildMainLayout();
	}

	private ComboBox getNewPackageComboBox() {
		ComboBox comboBox = new ComboBox();
		comboBox.setNewItemsAllowed(true);
		// http://www.nupex.de/index.php/de/studie-nutzungsverhalten/ergebnisse-der-ersten-nupex-feld-studie
		comboBox.addItem("Whatsapp");
		comboBox.addItem("Facebook");
		comboBox.addItem("Kontacte");
		comboBox.addItem("Browser");
		comboBox.addItem("Play-Store");
		comboBox.addItem("SMS/MMS");
		// auf 10 aufgefüllt mit
		// http://www.chip.de/news/Top-Apps-Deutschland-Mehr-Tanken-stuermt-Android-Charts_60441749.html
		comboBox.addItem("Google Maps");
		comboBox.addItem("Youtube");
		comboBox.addItem("Ebay");
		comboBox.addItem("GoogleSearch");

		comboBox.setImmediate(true);
		comboBox.setValidationVisible(true);
		comboBox.addValidator(notNullValidator);

		return comboBox;
	}

	private void setBackToBaseLayout() {
		horizontalLayout.removeAllComponents();
		horizontalLayout.addComponent(keyLabel);
		horizontalLayout.addComponent(keyComboBox);
		horizontalLayout.addComponent(getLabelOperator());
		horizontalLayout.addComponent(getComboboxOperator());
	}

	@Override
	public boolean isValid() {
		switch (String.valueOf(keyComboBox.getValue())) {
		case "count":
			return super.isValid() && keyComboBox.isValid()
					&& countTextfield.isValid();

		case "package":
			boolean isValid = super.isValid() && keyComboBox.isValid();
			for (ComboBox comboBox : packageList) {
				isValid = isValid && comboBox.isValid();
			}
			return isValid && !checkAnswersForDuplicates();
		}
		return false;
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("notifications");
		switch (String.valueOf(keyComboBox.getValue())) {
		case "count":
			writer.writeEmptyElement("count");
			writer.writeAttribute("operator", getOperator());
			writer.writeAttribute("value", countTextfield.getValue());
			break;
		case "package":
			writer.writeStartElement("package");
			writer.writeAttribute("operator", getOperator());
			for (ComboBox comboBox : packageList) {
				writer.writeStartElement("packageName");
				writer.writeCharacters(getPackageForName(comboBox));
				writer.writeEndElement();
			}
			writer.writeEndElement();
			break;
		}
		writer.writeEndElement();

	}

	private String getPackageForName(ComboBox comboBox) {
		switch (String.valueOf(comboBox.getValue())) {
		case "Whatsapp":
			return "com.whatsapp";
		case "Facebook":
			return "com.facebook.katana";
		case "Kontakte":
			return "com.google.android.contacts";
		case "Browser":
			return "com.android.browser";
		case "Play-Store":
			return "com.android.vending";
		case "SMS/MMS":
			return "com.android.mms";
		case "Google Maps":
			return "com.google.android.apps.maps";
		case "Youtube":
			return "com.google.android.apps.youtube";
		case "Ebay":
			return "com.ebay.mobile";
		case "GoogleSearch":
			return "com.google.android.googlequicksearchbox ";
		default:
			return String.valueOf(comboBox.getValue());
		}
	}

	public boolean checkAnswersForDuplicates() {
		Set<String> listStrings = new HashSet<String>();
		for (ComboBox value : packageList) {
			if (!listStrings.add(getPackageForName(value))) {
				return true;
			}
		}
		return false;
	}
}
