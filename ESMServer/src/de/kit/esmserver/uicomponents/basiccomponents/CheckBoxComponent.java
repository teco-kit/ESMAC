package de.kit.esmserver.uicomponents.basiccomponents;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class CheckBoxComponent extends CustomComponent {
	private static final long serialVersionUID = 140776448699702033L;
	private GridLayout gridLayout;
	private Label label;
	private CheckBox checkBox;

	public CheckBoxComponent(String caption) {
		super();
		gridLayout = new GridLayout(2, 1);
		label = new Label(caption);
		checkBox = new CheckBox();
		gridLayout.addComponent(label);
		gridLayout.addComponent(checkBox);
		gridLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		gridLayout.setComponentAlignment(checkBox, Alignment.MIDDLE_CENTER);
		setCompositionRoot(gridLayout);
	}

	@Override
	public String getCaption() {
		return label.getValue();
	}

	@Override
	public void setCaption(String caption) {
		label.setValue(caption);
	}

	public Boolean getValue() {
		return checkBox.getValue();
	}

}
