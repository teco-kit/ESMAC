package de.kit.esmserver.uicomponents.windows;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SubmitWindow extends Window {
	private static final long serialVersionUID = -1229907809528224235L;

	public SubmitWindow(long code) {
		super(
				"Please do the following steps to start your Experience Sampling App:");
		
		System.out.println(code);

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponent(new Label(
				"1) Open the ESM app"));
		verticalLayout.addComponent(new Label("2) Go to the add new configrations file section."));
		verticalLayout
				.addComponent(new Label(
						"3) Write down the following code in the input."));
		verticalLayout.addComponent(new Label("CODE : " + code));
		verticalLayout
				.addComponent(new Label(
						"4) Restart the ESM app"));
		verticalLayout.setMargin(true);

		center();
		setModal(true);
		setClosable(true);
		setContent(verticalLayout);
	}

}
