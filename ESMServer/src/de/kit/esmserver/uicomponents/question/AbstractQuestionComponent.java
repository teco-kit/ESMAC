package de.kit.esmserver.uicomponents.question;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;

import de.kit.esmserver.uicomponents.AbstractWritableComponent;

public abstract class AbstractQuestionComponent extends
		AbstractWritableComponent {
	private static final long serialVersionUID = -2717787277833785157L;
	private HorizontalLayout horizontalLayout;

	public AbstractQuestionComponent() {
		this.horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponent(panel);
		panel.setWidth(String.valueOf(UI.getCurrent().getPage()
				.getBrowserWindowWidth() - 850));
		setCompositionRoot(horizontalLayout);
	}

	public void addComponentToLayout(Component component) {
		horizontalLayout.addComponent(component);
	}

	@Override
	public void setAlignmentMiddleCenter() {
		super.setAlignmentMiddleCenter();
	}

}
