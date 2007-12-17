/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui;

import org.apache.commons.lang.ArrayUtils;
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for setting Selenium Runner options.
 * @author Christian Schwarz
 */
public class SeleniumSettingsPage extends WizardPage {

	private static final String SELENIUM_RUNNER_BROWSER_TYPE = "SeleniumRunnerBrowserType";
	private static final String SELENIUM_RUNNER_PORT_NUMBER = "SeleniumRunnerPortNumber";
	private static final String PLEASE_ENTER_A_PORT_NUMBER = "Choose Browser. Enter another port number if the current doesn't work.";
	public static final int DEFAULT_PORT = 4444;
	public static final BrowserType DEFAULT_BROWSER = BrowserType.FIREFOX;
	private Label portLabel;
	private Text portText;
	private Label browserLabel;
	private Combo browserCombo;
	private CubicTestProjectSettings settings;
	private int port = DEFAULT_PORT;
	private BrowserType browserType = DEFAULT_BROWSER;
	
	protected SeleniumSettingsPage(CubicTestProjectSettings settings) {
		super("Set CubicSeleniumServerPort");
		this.settings = settings;
		
	}

	public void createControl(Composite parent) {
		
		Composite content = new Composite(parent, SWT.NULL);
		
		GridData gridData = new GridData();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		browserLabel = new Label(content, SWT.NONE);
		browserLabel.setText("Label");
		createBrowserCombo(content);
		
		portLabel = new Label(content, SWT.NONE);
		portLabel.setText("Port number:");
		portLabel.setLayoutData(gridData);
		portText = new Text(content, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.minimumWidth = 50;
		portText.setLayoutData(gridData);
		portText.setTextLimit(5);
		
		try{
			port = SeleniumExporterPlugin.getDefault().getDialogSettings().getInt(SELENIUM_RUNNER_PORT_NUMBER);
		}
		catch(NumberFormatException nfe){
			try {
				port = settings.getInteger(SeleniumUtils.getPluginPropertyPrefix(), "defaultPortNumber", DEFAULT_PORT);
			} catch (Exception ignore) {
			}
		}
		portText.setText(port + "");
		portText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				try{
					port = Integer.parseInt(portText.getText());
					setPageComplete(true);
					setMessage(PLEASE_ENTER_A_PORT_NUMBER);
					setErrorMessage(null);
					SeleniumExporterPlugin.getDefault().
					getDialogSettings().put(SELENIUM_RUNNER_PORT_NUMBER, port);
				}
				catch(NumberFormatException ex){
					setErrorMessage("Please enter a number (" + portText.getText() + 
							" is not a number)");
					setPageComplete(false);
				}
				
			}
		});
		
		content.setLayout(gridLayout);
		setMessage(PLEASE_ENTER_A_PORT_NUMBER);
		setPageComplete(true);
		
		setControl(content);
	}
	
	public int getPort(){
		return port;
	}
	
	public BrowserType getBrowserType(){
		
		return browserType;
	}

	/**
	 * This method initializes browserCombo	
	 * @param content 
	 *
	 */
	private void createBrowserCombo(Composite content) {
		browserCombo = new Combo(content, SWT.NONE | SWT.READ_ONLY);
		for (BrowserType browserType : BrowserType.values()) {
			browserCombo.add(browserType.getDisplayName());
		}
	
		browserCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				browserType = BrowserType.values()[browserCombo.getSelectionIndex()];
				SeleniumExporterPlugin.getDefault().getDialogSettings().put(SELENIUM_RUNNER_BROWSER_TYPE,
							browserCombo.getSelectionIndex());
			}
		});
		int storedBrowserTypeIndex = 0;
		try {
			storedBrowserTypeIndex = SeleniumExporterPlugin.getDefault().getDialogSettings().getInt(SELENIUM_RUNNER_BROWSER_TYPE);
			if (storedBrowserTypeIndex < 0 || storedBrowserTypeIndex > BrowserType.values().length - 1) {
				storedBrowserTypeIndex = 0;
			}
			browserType = BrowserType.values()[storedBrowserTypeIndex];
		} 
		catch(NumberFormatException nfe) {
			try {
				browserType = BrowserType.fromId(settings.getString(SeleniumUtils.getPluginPropertyPrefix(), "defaultBrowserType",
						DEFAULT_BROWSER.getId()));
				storedBrowserTypeIndex = ArrayUtils.indexOf(BrowserType.values(), browserType);
			} catch (Exception ignore) {
			}
		}
		browserCombo.select(storedBrowserTypeIndex);
	}
}