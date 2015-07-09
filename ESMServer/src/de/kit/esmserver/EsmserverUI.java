package de.kit.esmserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.Area;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.kit.esmserver.uicomponents.AbstractCustomComponent;
import de.kit.esmserver.uicomponents.AbstractWritableComponent;
import de.kit.esmserver.uicomponents.RuleComponent;
import de.kit.esmserver.uicomponents.ScreenComponent;
import de.kit.esmserver.uicomponents.SensorAndNotificationComponent;
import de.kit.esmserver.uicomponents.basiccomponents.TreeComponent;
import de.kit.esmserver.uicomponents.basiccomponents.TreeComponent.Type;
import de.kit.esmserver.uicomponents.windows.SubmitWindow;
import de.kit.esmserver.uicomponents.windows.WelcomeWindow;
import de.kit.esmserver.xml.XMLWriter;

@SuppressWarnings("serial")
@Theme("esmserver")
public class EsmserverUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = EsmserverUI.class)
	public static class Servlet extends VaadinServlet {
	}

	private List<AbstractWritableComponent> abstractCustomComponents = new ArrayList<AbstractWritableComponent>();
	private int i = 0;
	Button nextButton;
	Button submitButton;
	Button backButton;
	private TreeComponent ruleTree = new TreeComponent(Type.Rule);
	private TreeComponent questionTree = new TreeComponent(Type.Question);
	private VerticalLayout treeAndScreenLayout;
	private VerticalLayout treeAndRuleLayout;
	private GridLayout layout;

	@Override
	protected void init(VaadinRequest request) {
		layout = new GridLayout(4, 1);
		layout.setMargin(true);
		setContent(layout);
		final VerticalLayout verticalLayout = new VerticalLayout();
		initializeViews(verticalLayout);
		initializeNavigationButtons();
		initializeSubmitButton();
		enableButtons();
		initilizeScreenCounter();
		initilizeRuleCounter();
		layout.addComponent(treeAndScreenLayout);
		layout.addComponent(verticalLayout);
		layout.addComponent(new Label());
		layout.addComponent(new Label());
		layout.addComponent(new Label());
		layout.addComponent(new Label());
		layout.addComponent(backButton);
		layout.addComponent(nextButton);
		setOnBeforeUnload("You are closing the ESM-App-Configurator. All changes will be discarded!");
		UI.getCurrent().addWindow(new WelcomeWindow());

	}

	private void setOnBeforeUnload(String message) {
		String script = "window.onbeforeunload = function (e) {if (e) { e.returnValue = \"%s\";} return \"%s\";};";
		JavaScript.getCurrent()
				.execute(String.format(script, message, message));
	}

	private void initializeViews(final VerticalLayout verticalLayout) {
		ScreenComponent firstScreen = new ScreenComponent(getScreenCount() + 1
				+ "");
		abstractCustomComponents.add(firstScreen);
		verticalLayout.addComponent(firstScreen);
		abstractCustomComponents
				.add(new RuleComponent(getRuleCount() + 1 + ""));
		abstractCustomComponents.add(new SensorAndNotificationComponent());
	}

	private void initializeNavigationButtons() {
		nextButton = new Button("next");
		nextButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (abstractCustomComponents.get(i).isValid()) {
					i++;
					changeLayout(layout);
					layout.removeComponent(1, 0);
					layout.addComponent(abstractCustomComponents.get(i), 1, 0);
					enableButtons();
				}

			}
		});

		backButton = new Button("back");
		backButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (i >= 0) {
					i--;
					changeLayout(layout);
					layout.removeComponent(1, 0);
					layout.addComponent(abstractCustomComponents.get(i), 1, 0);
					enableButtons();
				}
			}
		});
	}

	private void initializeSubmitButton() {
		submitButton = new Button("submit");
		final FileDownloader downloader = new FileDownloader(new FileResource(
				new File("TEMP"))) {

			@Override
			public boolean handleConnectorRequest(VaadinRequest request,
					VaadinResponse response, String path) throws IOException {
				if (abstractCustomComponents.get(
						abstractCustomComponents.size() - 1).isValid()) {
					XMLWriter writer = new XMLWriter();
					File resource = writer
							.writeXMlToFile(abstractCustomComponents);
					this.setFileDownloadResource(new FileResource(resource));
					boolean handled = super.handleConnectorRequest(request,
							response, path);
					resource.delete();
					return handled;
				} else {
					return false;
				}

			}

		};
		downloader.extend(submitButton);
		submitButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (abstractCustomComponents.get(
						abstractCustomComponents.size() - 1).isValid()) {
					SubmitWindow window = new SubmitWindow();
					UI.getCurrent().addWindow(window);
				} else {
					Notification.show("There are invalid fields!",
							Notification.Type.ERROR_MESSAGE);
				}

			}
		});
	}

	private void initilizeRuleCounter() {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		Label ruleCounterLabel = new Label("Rules");
		Button decreaseRulesButton = new Button("-");
		final TextField ruleCounterTextfield = new TextField();
		Button increaseRulesButton = new Button("+");

		ruleCounterTextfield.setEnabled(false);
		ruleCounterTextfield.setValue(getRuleCount() + "");
		ruleCounterTextfield.setWidth("40");

		increaseRulesButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (getRuleCount() < 99) {
					int screenAndRuleCount = getRuleCount() + getScreenCount();
					abstractCustomComponents.add(screenAndRuleCount,
							new RuleComponent(getRuleCount() + 1 + ""));
					ruleCounterTextfield.setValue(getRuleCount() + "");
					enableButtons();
				}
			}
		});

		decreaseRulesButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (getRuleCount() > 1) {
					int screenAndRuleCount = getRuleCount() + getScreenCount();
					abstractCustomComponents.remove(screenAndRuleCount - 1);
					ruleCounterTextfield.setValue(getRuleCount() + "");
					enableButtons();
					if (i == getRuleCount() + getScreenCount()) {
						i--;
						layout.removeComponent(1, 0);
						layout.addComponent(abstractCustomComponents.get(i), 1,
								0);
						enableButtons();
					}
				}
			}
		});

		horizontalLayout.addComponent(ruleCounterLabel);
		horizontalLayout.addComponent(decreaseRulesButton);
		horizontalLayout.addComponent(ruleCounterTextfield);
		horizontalLayout.addComponent(increaseRulesButton);

		horizontalLayout.setComponentAlignment(ruleCounterLabel,
				Alignment.MIDDLE_CENTER);
		horizontalLayout.setComponentAlignment(decreaseRulesButton,
				Alignment.MIDDLE_CENTER);
		horizontalLayout.setComponentAlignment(ruleCounterTextfield,
				Alignment.MIDDLE_CENTER);
		horizontalLayout.setComponentAlignment(increaseRulesButton,
				Alignment.MIDDLE_CENTER);

		treeAndRuleLayout = new VerticalLayout();
		treeAndRuleLayout.addComponent(ruleTree.getComponent());
		treeAndRuleLayout.addComponent(horizontalLayout);

	}

	private void initilizeScreenCounter() {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		Label screenCountLabel = new Label("Screens");
		Button decreaseScreensButton = new Button("-");
		final TextField screenCountTextfield = new TextField();
		Button increaseScreensButton = new Button("+");

		screenCountTextfield.setEnabled(false);
		screenCountTextfield.setValue(getScreenCount() + "");
		screenCountTextfield.setWidth("40");

		increaseScreensButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (getScreenCount() < 99) {
					abstractCustomComponents.add(getScreenCount(),
							new ScreenComponent(getScreenCount() + 1 + ""));
					screenCountTextfield.setValue(getScreenCount() + "");
					enableButtons();
				}
			}
		});

		decreaseScreensButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (getScreenCount() > 1) {
					abstractCustomComponents.remove(getScreenCount() - 1);
					screenCountTextfield.setValue(getScreenCount() + "");
					enableButtons();
					if (i == getScreenCount()) {
						i--;
						layout.removeComponent(1, 0);
						layout.addComponent(abstractCustomComponents.get(i), 1,
								0);
						enableButtons();
					}
				}
			}
		});

		horizontalLayout.addComponent(screenCountLabel);
		horizontalLayout.addComponent(decreaseScreensButton);
		horizontalLayout.addComponent(screenCountTextfield);
		horizontalLayout.addComponent(increaseScreensButton);

		horizontalLayout.setComponentAlignment(screenCountLabel,
				Alignment.MIDDLE_CENTER);
		horizontalLayout.setComponentAlignment(decreaseScreensButton,
				Alignment.MIDDLE_CENTER);
		horizontalLayout.setComponentAlignment(screenCountTextfield,
				Alignment.MIDDLE_CENTER);
		horizontalLayout.setComponentAlignment(increaseScreensButton,
				Alignment.MIDDLE_CENTER);

		treeAndScreenLayout = new VerticalLayout();
		treeAndScreenLayout.addComponent(questionTree.getComponent());
		treeAndScreenLayout.addComponent(horizontalLayout);
	}

	protected void enableButtons() {
		Area area;
		int col;
		int row;
		backButton.setEnabled(!(i == 0));
		if (i < abstractCustomComponents.size() - 1) {
			if (submitButton.getParent() != null) {
				area = layout.getComponentArea(submitButton);
				col = area.getColumn1();
				row = area.getRow1();
				layout.removeComponent(submitButton);
				layout.addComponent(nextButton, col, row);
			}
		} else {
			area = layout.getComponentArea(nextButton);
			col = area.getColumn1();
			row = area.getRow1();
			layout.removeComponent(nextButton);
			layout.addComponent(submitButton, col, row);
		}
		setCaptionFromButtons();

	}

	private void setCaptionFromButtons() {
		if (i < abstractCustomComponents.size() - 1) {
			AbstractWritableComponent nextComponent = abstractCustomComponents
					.get(i + 1);
			if (nextComponent instanceof ScreenComponent) {
				nextButton.setCaption("next screen");
			} else if (nextComponent instanceof RuleComponent) {
				nextButton.setCaption("next rule");
			} else if (nextComponent instanceof SensorAndNotificationComponent) {
				nextButton.setCaption("next");
			}
		}
		if (i != 0) {
			AbstractWritableComponent previousComponent = abstractCustomComponents
					.get(i - 1);
			if (previousComponent instanceof ScreenComponent) {
				backButton.setCaption("previous screen");
			} else if (previousComponent instanceof RuleComponent) {
				backButton.setCaption("previous rule");
			} else if (previousComponent instanceof SensorAndNotificationComponent) {
				backButton.setCaption("previous");
			}
		}

	}

	private int getScreenCount() {
		int i = 0;
		for (AbstractCustomComponent component : abstractCustomComponents) {
			if (component instanceof ScreenComponent) {
				i++;
			}
		}
		return i;
	}

	private int getRuleCount() {
		int i = 0;
		for (AbstractCustomComponent component : abstractCustomComponents) {
			if (component instanceof RuleComponent) {
				i++;
			}
		}
		return i;
	}

	private void changeLayout(final GridLayout layout) {
		if (abstractCustomComponents.get(i) instanceof RuleComponent) {
			layout.removeComponent(0, 0);
			layout.addComponent(treeAndRuleLayout, 0, 0);
		} else if (abstractCustomComponents.get(i) instanceof ScreenComponent) {
			layout.removeComponent(0, 0);
			layout.addComponent(treeAndScreenLayout, 0, 0);
		} else {
			layout.removeComponent(0, 0);
			VerticalLayout dummyComponent = new VerticalLayout();
			dummyComponent.setWidth("300");
			layout.addComponent(dummyComponent, 0, 0);
		}
	}
}