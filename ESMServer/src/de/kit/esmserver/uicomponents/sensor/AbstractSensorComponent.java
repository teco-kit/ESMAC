package de.kit.esmserver.uicomponents.sensor;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

import de.kit.esmserver.uicomponents.AbstractWritableComponent;
import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public abstract class AbstractSensorComponent extends AbstractWritableComponent {

	private static final long serialVersionUID = -6654967359731306136L;
	private Label labelConjunction;
	private ComboBox comboBoxConjunction;
	private ComboBox comboboxOperator;
	private Label labelOperator;
	// private CheckBoxComponent negated;
	private HorizontalLayout horizontalLayout;

	public AbstractSensorComponent() {
		this.horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponent(panel);

		labelOperator = new Label("Operator");
		comboboxOperator = new ComboBox();
		comboboxOperator.setImmediate(true);
		comboboxOperator.setValidationVisible(true);
		comboboxOperator.addValidator(new NotNullValidator());
		comboboxOperator.setWidth("80");

		labelConjunction = new Label("conjunction");
		labelConjunction.setVisible(false);
		comboBoxConjunction = new ComboBox();
		comboBoxConjunction.addItem("AND");
		comboBoxConjunction.addItem("OR");
		comboBoxConjunction.setImmediate(true);
		comboBoxConjunction.setValidationVisible(true);
		comboBoxConjunction.addValidator(new NotNullValidator());
		comboBoxConjunction.setWidth("100");
		comboBoxConjunction.setVisible(false);
		// negated = new CheckBoxComponent("negated");

		panel.setWidth(String.valueOf(UI.getCurrent().getPage()
				.getBrowserWindowWidth() - 850));
		setCompositionRoot(horizontalLayout);

	}

	protected Panel buildMainLayout() {
		if (panel.getContent() instanceof GridLayout) {
			GridLayout gridLayout = (GridLayout) panel.getContent();
			// gridLayout.addComponent(negated);
			gridLayout.addComponent(labelConjunction);
			gridLayout.addComponent(comboBoxConjunction);
		} else if (panel.getContent() instanceof HorizontalLayout) {
			HorizontalLayout horizontalLayout = (HorizontalLayout) panel
					.getContent();
			// horizontalLayout.addComponent(negated);
			horizontalLayout.addComponent(labelConjunction);
			horizontalLayout.addComponent(comboBoxConjunction);
		}

		return panel;
	}

	@Override
	public boolean isValid() {
		if (comboBoxConjunction.isVisible()) {
			return comboboxOperator.isValid() && comboBoxConjunction.isValid();
		} else {
			return comboboxOperator.isValid();
		}

	}

	protected ComboBox getComboboxOperator() {
		return comboboxOperator;
	}

	protected ComboBox getConjunctionOperator() {
		return comboBoxConjunction;
	}

	protected Label getLabelOperator() {
		return labelOperator;
	}

	public void enableConjunction() {
		comboBoxConjunction.setVisible(true);
		labelConjunction.setVisible(true);
	}

	protected void setRestrictedOperatorComboBox() {
		comboboxOperator.removeAllItems();
		comboboxOperator.addItem("=");
		comboboxOperator.addItem("≠");
	}

	protected void setFullOperatorComboBox() {
		comboboxOperator.removeAllItems();
		comboboxOperator.addItem("=");
		comboboxOperator.addItem("≠");
		comboboxOperator.addItem(">");
		comboboxOperator.addItem("<");
		comboboxOperator.addItem(">=");
		comboboxOperator.addItem("<=");
	}

	public boolean isConjunction() {
		return labelConjunction.isVisible();
	}

	public String getConjunction() {
		return comboBoxConjunction.getValue().toString().toLowerCase();
	}

	public String getOperator() {
		if (comboboxOperator.getValue().equals("≠")) {
			return "!=";
		} else {
			return comboboxOperator.getValue().toString();
		}
	}

	// public Boolean isNegated() {
	// return negated.getValue();
	// }

	public void addComponentToLayout(Component component) {
		horizontalLayout.addComponent(component);
	}

	public void disableConjunction() {
		comboBoxConjunction.setVisible(false);
		labelConjunction.setVisible(false);

	}
}
