package de.kit.esmserver.uicomponents.windows;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WelcomeWindow extends Window {
	private static final long serialVersionUID = -4496169632230650977L;

	public WelcomeWindow() {
		super("Welcome to the ESM-App-Configurator");

		VerticalLayout verticalLayout = new VerticalLayout();
		Label label = new Label(
				"Here you can config your personal Experience Sampling App with Questions, Event-Rules and Loggingsensors."
						+ "\n"
						+ "Please use drag and drop from the Tree to the Canvas. After that you can configure them in the Canvas."
						+ "\n"
						+ "You can configure more than one Screen or Rule, if you click on the Counter under the Tree."
						+ "\n"
						+ "On the last page you can configure the Notification which will come, if one of your configured Rules becomes true."
						+ "\n"
						+ "Also you can configure a cooldown time and a maximum of Notifications.");
		label.setContentMode(ContentMode.PREFORMATTED);
		verticalLayout.addComponent(label);
		verticalLayout.setMargin(true);

		center();
		setModal(true);
		setClosable(true);
		setContent(verticalLayout);
	}
}
