package de.kit.esmserver.uicomponents;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.kit.esmserver.uicomponents.sensor.AbstractSensorComponent;
import de.kit.esmserver.uicomponents.sensor.AccelerometerComponent;
import de.kit.esmserver.uicomponents.sensor.AmbientLightComponent;
import de.kit.esmserver.uicomponents.sensor.BluetoothComponent;
import de.kit.esmserver.uicomponents.sensor.GeofenceComponent;
import de.kit.esmserver.uicomponents.sensor.NotificationComponent;
import de.kit.esmserver.uicomponents.sensor.ScreenActivityComponent;
import de.kit.esmserver.uicomponents.sensor.TelephonComponent;
import de.kit.esmserver.uicomponents.sensor.TimeComponent;
import de.kit.esmserver.uicomponents.sensor.UseractivityComponent;
import de.kit.esmserver.uicomponents.sensor.WeatherComponent;

@SuppressWarnings("serial")
public class RuleComponent extends AbstractWritableComponent {
	private List<AbstractSensorComponent> expressions;
	private VerticalLayout mainLayout;

	public RuleComponent(String count) {
		expressions = new ArrayList<AbstractSensorComponent>();
		Panel p = buildMainLayout();
		p.setCaption("Rule " + count);
		setCompositionRoot(p);
	}

	private Panel buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth(String.valueOf(UI.getCurrent().getPage()
				.getBrowserWindowWidth() - 650));
		DragAndDropWrapper dragAndDropWrapper = new DragAndDropWrapper(
				mainLayout);
		dragAndDropWrapper.setDragStartMode(DragStartMode.NONE);
		dragAndDropWrapper.setDropHandler(new DropHandler() {

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

			@Override
			public void drop(DragAndDropEvent event) {
				if (event.getTransferable().getSourceComponent() instanceof Tree) {
					DataBoundTransferable t = (DataBoundTransferable) event
							.getTransferable();
					switch (String.valueOf(t.getItemId())) {
					case "Weather":
						WeatherComponent weather = new WeatherComponent();
						weather.setAlignmentMiddleCenter();
						expressions.add(weather);
						mainLayout.addComponent(weather);
						break;
					case "ScreenActivity":
						ScreenActivityComponent screenActivityComponent = new ScreenActivityComponent();
						screenActivityComponent.setAlignmentMiddleCenter();
						expressions.add(screenActivityComponent);
						mainLayout.addComponent(screenActivityComponent);
						break;
					case "AmbientLight":
						AmbientLightComponent ambientLightComponent = new AmbientLightComponent();
						ambientLightComponent.setAlignmentMiddleCenter();
						expressions.add(ambientLightComponent);
						mainLayout.addComponent(ambientLightComponent);
						break;
					case "Geofence":
						GeofenceComponent geofenceComponent = new GeofenceComponent();
						geofenceComponent.setAlignmentMiddleCenter();
						expressions.add(geofenceComponent);
						mainLayout.addComponent(geofenceComponent);
						break;
					case "Time":
						TimeComponent timeComponent = new TimeComponent();
						timeComponent.setAlignmentMiddleCenter();
						expressions.add(timeComponent);
						mainLayout.addComponent(timeComponent);
						break;
					case "UserActivity":
						UseractivityComponent useractivityComponent = new UseractivityComponent();
						useractivityComponent.setAlignmentMiddleCenter();
						expressions.add(useractivityComponent);
						mainLayout.addComponent(useractivityComponent);
						break;
					case "Telephon":
						TelephonComponent telephonComponent = new TelephonComponent();
						telephonComponent.setAlignmentMiddleCenter();
						expressions.add(telephonComponent);
						mainLayout.addComponent(telephonComponent);
						break;
					case "Notification":
						NotificationComponent notificationComponent = new NotificationComponent();
						notificationComponent.setAlignmentMiddleCenter();
						expressions.add(notificationComponent);
						mainLayout.addComponent(notificationComponent);
						break;
					case "Bluetooth":
						BluetoothComponent bluetoothComponent = new BluetoothComponent();
						bluetoothComponent.setAlignmentMiddleCenter();
						expressions.add(bluetoothComponent);
						mainLayout.addComponent(bluetoothComponent);
						break;
					case "Accelerometer":
						AccelerometerComponent accelerometerComponent = new AccelerometerComponent();
						accelerometerComponent.setAlignmentMiddleCenter();
						expressions.add(accelerometerComponent);
						mainLayout.addComponent(accelerometerComponent);
						break;

					default:
						break;
					}

				}
				addNavigationButtonsToLayout(expressions.get(expressions.size() - 1));
				if (expressions.size() > 1) {
					((AbstractSensorComponent) expressions.get(expressions
							.size() - 2)).enableConjunction();
				}

			}
		});
		panel.setContent(dragAndDropWrapper);
		panel.setCaption("Rules");
		return panel;
	}

	@Override
	public boolean isValid() {
		boolean isValid = true;
		for (AbstractWritableComponent component : expressions) {
			isValid = isValid && component.isValid();
		}
		if (isValid && !expressions.isEmpty()) {
			return true;
		} else {
			showNotificationForFailure();
			return false;
		}
	}

	@Override
	public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
		if (expressions.size() > 0) {
			writer.writeStartElement("rule");
			writeRecursiveConjunctionOrExpression(writer, expressions.get(0));
			writer.writeEndElement();
		}
	}

	private void writeRecursiveConjunctionOrExpression(XMLStreamWriter writer,
			AbstractSensorComponent abstractSensorComponent)
			throws XMLStreamException {
		if (abstractSensorComponent.isConjunction()) {
			writer.writeStartElement("sensorconjunction");
			buildSensorExpressionXML(writer, abstractSensorComponent);
			writer.writeStartElement("conjunction");
			writer.writeCharacters(abstractSensorComponent.getConjunction());
			writer.writeEndElement();
			writeRecursiveConjunctionOrExpression(writer,
					expressions.get(expressions
							.indexOf(abstractSensorComponent) + 1));
			writer.writeEndElement();
		} else {
			buildSensorExpressionXML(writer, abstractSensorComponent);
		}
	}

	private void buildSensorExpressionXML(XMLStreamWriter writer,
			AbstractSensorComponent abstractSensorComponent)
			throws XMLStreamException {
		writer.writeStartElement("sensorexpression");
		// writer.writeAttribute("negated", abstractSensorComponent.isNegated()
		// .toString());
		writer.writeAttribute("negated", "false");
		abstractSensorComponent.writeXML(writer);
		writer.writeEndElement();
	}

	private void addNavigationButtonsToLayout(
			final AbstractSensorComponent customComponent) {
		Button upButton = new Button("˄");
		Button downButton = new Button("˅");
		Button removeButton = new Button("-");

		upButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				int index = expressions.indexOf(customComponent);
				if (index > 0) {
					expressions.remove(customComponent);
					expressions.add(index - 1, customComponent);
					mainLayout.removeComponent(customComponent);
					mainLayout.addComponent(customComponent, index - 1);
				}
				if (index == expressions.size() - 1) {
					customComponent.enableConjunction();
					expressions.get(index).disableConjunction();
				}

			}
		});

		downButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				int index = expressions.indexOf(customComponent);
				if (index < expressions.size() - 1) {
					expressions.remove(customComponent);
					expressions.add(index + 1, customComponent);
					mainLayout.removeComponent(customComponent);
					mainLayout.addComponent(customComponent, index + 1);
				}
				if (index + 1 == expressions.size() - 1) {
					customComponent.disableConjunction();
					expressions.get(index).enableConjunction();
				}
			}
		});

		removeButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				expressions.remove(customComponent);
				mainLayout.removeComponent(customComponent);
				if (expressions.size() > 0) {
					((AbstractSensorComponent) expressions.get(expressions
							.size() - 1)).disableConjunction();
				}
			}
		});
		customComponent.addComponentToLayout(upButton);
		customComponent.addComponentToLayout(downButton);
		customComponent.addComponentToLayout(removeButton);

	}

	private void showNotificationForFailure() {
		if (expressions.isEmpty()) {
			Notification.show(
					"There must be one or more sensorexpressions in a rule!",
					Notification.Type.ERROR_MESSAGE);
		} else if (checkComponentsForDuplicates()) {
			return;
		} else {
			Notification.show(
					"There are sensorfields which are invalid or empty.",
					Notification.Type.ERROR_MESSAGE);
		}

	}

	private boolean checkComponentsForDuplicates() {
		for (AbstractWritableComponent component : expressions) {
			if (component instanceof BluetoothComponent) {
				if (((BluetoothComponent) component)
						.checkAnswersForDuplicates()) {
					Notification
							.show("Please avoid duplicated answers in bluetooth sensorexpression!",
									Notification.Type.ERROR_MESSAGE);
					return true;
				}
			} else if (component instanceof NotificationComponent) {
				if (((NotificationComponent) component)
						.checkAnswersForDuplicates()) {
					Notification
							.show("Please avoid duplicated answers in notification sensorexpression!",
									Notification.Type.ERROR_MESSAGE);
					return true;
				}
			}
		}
		return false;
	}

}
