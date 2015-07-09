package de.kit.esmserver.uicomponents.windows;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SubmitWindow extends Window {
	private static final long serialVersionUID = -1229907809528224235L;

	public SubmitWindow() {
		super(
				"Please do the following steps to start your Experience Sampling App:");

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponent(new Label(
				"1) Install the ESMDummy App on your Device."));
		verticalLayout.addComponent(new Label("2) Start the ESMDummy App."));
		verticalLayout
				.addComponent(new Label(
						"3) Copy the masterarbeit.xml to your external sdcard in the 'de.kit.esmdummy' directory."));
		verticalLayout.addComponent(new Label("4) Restart the ESMDummy App."));
		verticalLayout
				.addComponent(new Label(
						"5) The ESMDummy App is now running with your customizations."));
		verticalLayout.setMargin(true);

		center();
		setModal(true);
		setClosable(true);
		setContent(verticalLayout);
	}

}
