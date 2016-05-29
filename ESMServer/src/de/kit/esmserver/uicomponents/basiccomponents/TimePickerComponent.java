package de.kit.esmserver.uicomponents.basiccomponents;

import java.util.Calendar;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.kit.esmserver.uicomponents.validators.NotNullValidator;

public class TimePickerComponent extends CustomComponent {
	private static final long serialVersionUID = 2631916032068736806L;
	private ComboBox hourComboBox;
	private ComboBox minuteComboBox;
	private ComboBox secondComboBox;
	private Label labelCaption;
	private HorizontalLayout horizontalLayout;
	private NotNullValidator notNullValidator;

	public TimePickerComponent(String caption) {
		super();
		notNullValidator = new NotNullValidator();
		horizontalLayout = new HorizontalLayout();
		hourComboBox = getComboBox(0, 23);
		hourComboBox.addValidator(notNullValidator);
		minuteComboBox = getComboBox(0, 59);
		minuteComboBox.addValidator(notNullValidator);
		secondComboBox = getComboBox(0, 59);
		secondComboBox.addValidator(notNullValidator);
		labelCaption = new Label(caption);
		horizontalLayout.addComponent(labelCaption);
		horizontalLayout.addComponent(hourComboBox);
		horizontalLayout.addComponent(new Label(":"));
		horizontalLayout.addComponent(minuteComboBox);
		horizontalLayout.addComponent(new Label(":"));
		horizontalLayout.addComponent(secondComboBox);
		setAlignmentCenter();
		setCompositionRoot(horizontalLayout);
	}

	@Override
	public String getCaption() {
		return labelCaption.getValue();
	}

	@Override
	public void setCaption(String caption) {
		labelCaption.setValue(caption);
	}

	private void setAlignmentCenter() {
		for (Component component : horizontalLayout) {
			horizontalLayout.setComponentAlignment(component,
					Alignment.MIDDLE_CENTER);
		}

	}

	private ComboBox getComboBox(int begin, int end) {
		ComboBox comboBox = new ComboBox();
		comboBox.setWidth("80");
		for (int i = begin; i <= end; i++) {
			if (i < 10) {
				comboBox.addItem("0" + i);
			} else {
				comboBox.addItem(i);
			}

		}
		return comboBox;
	}

	public String getValue() {
		return hourComboBox.getValue() + ":" + minuteComboBox.getValue() + ":"
				+ secondComboBox.getValue();
	}

	public Calendar getCalendarValue() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(hourComboBox.getValue().toString()));
		cal.set(Calendar.MINUTE,
				Integer.parseInt(minuteComboBox.getValue().toString()));
		cal.set(Calendar.SECOND,
				Integer.parseInt(secondComboBox.getValue().toString()));
		return cal;
	}
}
