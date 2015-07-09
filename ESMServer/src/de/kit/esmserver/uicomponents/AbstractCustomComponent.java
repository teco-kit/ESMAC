package de.kit.esmserver.uicomponents;

import java.util.Iterator;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;

public class AbstractCustomComponent extends CustomComponent {

	private static final long serialVersionUID = -8359590602786347920L;
	protected Panel panel;

	public AbstractCustomComponent() {
		this.panel = new Panel();
	}

	public void setAlignmentMiddleCenter() {
		if (panel.getContent() instanceof Layout.AlignmentHandler) {
			Layout layout = (Layout) panel.getContent();
			for (Iterator<Component> iterator = layout.iterator(); iterator
					.hasNext();) {
				Component component = (Component) iterator.next();
				Layout.AlignmentHandler alignmentHandler = (Layout.AlignmentHandler) layout;
				alignmentHandler.setComponentAlignment(component,
						Alignment.MIDDLE_CENTER);
			}
		}
	}

}
