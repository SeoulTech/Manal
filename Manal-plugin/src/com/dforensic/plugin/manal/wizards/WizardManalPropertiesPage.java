/*
 *  <Manal project is an eclipse plugin for the automation of malware analysis.>
 *  Copyright (C) <2014>  <Nikolay Akatyev, Hojun Son>
 *  This file is part of Manal project.
 *
 *  Manal project is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *  Manal project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Manal project. If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Contact information of contributors:
 *  - Nikolay Akatyev: nikolay.akatyev@gmail.com
 *  - Hojun Son: smuoon4680@gmail.com
 */
package com.dforensic.plugin.manal.wizards;

import java.io.File;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.android.ide.eclipse.adt.AdtPlugin;
import com.android.ide.eclipse.adt.internal.preferences.AdtPrefs;

/**
 * The "New" properties wizard page allows setting the properties of the Manal
 * project for suspect analysis. That properties are a project name, a path to
 * an apk, a path to the android SDK.
 */

public class WizardManalPropertiesPage extends WizardPage {
//	private Text projectNameText;
	private Text apkFileText;
	private Text sourcePrjDirectoryText;
	private Text androidDirectoryText;
	private Composite parent;

	public Composite getParent() {
		return this.parent;
	}

	public WizardManalPropertiesPage() {
		super("wizardPage");
		setTitle("Set properties of suspect analysis project");
		setDescription("Set path to apk, output folder and path to anroid "
				+ "platform for consturction of the project.");
	}

	// Refer to the article below
	// http://www.eclipse.org/articles/article.php?file=Article-Understanding-Layouts/index.html
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		this.parent = parent;
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label label = null;
		GridData gd = null;
//		// Input project name
//		label = new Label(container, SWT.NULL);
//		label.setText("&Project name:");
//
//		projectNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		projectNameText.setLayoutData(gd);
//		projectNameText.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//
//			}
//		});
//		
//		// Put an empty label to fill the 3rd column
//		label = new Label(container, SWT.NULL);

		// Input apk path
		label = new Label(container, SWT.NULL);
		label.setText("&Apk file:");

		apkFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		apkFileText.setLayoutData(gd);
		apkFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// Even if one of fields is ok
				// need to check another one.
				dialogChanged();
			}
		});

		Button browseApkBtn = new Button(container, SWT.PUSH);
		browseApkBtn.setText("Browse...");
		browseApkBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseApk();
			}
		});

		// Input decompiled project path
		label = new Label(container, SWT.NULL);
		label.setText("&Output:");
		
		sourcePrjDirectoryText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		sourcePrjDirectoryText.setLayoutData(gd);
		sourcePrjDirectoryText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// Even if one of fields is ok
				// need to check another one.
				dialogChanged();
			}
		});

		Button browseSourceDirectoryBtn = new Button(container, SWT.PUSH);
		browseSourceDirectoryBtn.setText("Browse...");
		browseSourceDirectoryBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseDecompiledSource();
			}
		});
		
		// Input android platform path
		label = new Label(container, SWT.NULL);
		label.setText("An&droid platforms:");
				
		androidDirectoryText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		androidDirectoryText.setLayoutData(gd);
		
		Button browseAndroidDirectoryBtn = new Button(container, SWT.PUSH);
		browseAndroidDirectoryBtn.setText("Browse...");
		browseAndroidDirectoryBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseAndroidPlatform();
			}
		});

		dialogChanged();
		setControl(container);
		
		initControlValues();
	}

	// Refer to the following FAQ
	// http://wiki.eclipse.org/FAQ_How_do_I_prompt_the_user_to_select_a_file_or_a_directory%3F
	private void handleBrowseAndroidPlatform() {
		//get object which represents the workspace  
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		//get location of workspace (java.io.File)  
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String workspaceDirPath = null;
		if (workspaceDirectory != null) {
			workspaceDirPath = workspaceDirectory.getPath();
		}
		DirectoryDialog dirDialog = new DirectoryDialog(getShell());
		dirDialog.setText("Select Android Platform Directory");
		dirDialog.setFilterPath(workspaceDirPath);
		String selected = dirDialog.open();
		System.out.println(selected); //decompiled project

		if (selected != null) {
			androidDirectoryText.setText(selected);
		}
	}
	
	private void handleBrowseDecompiledSource() {
		//get object which represents the workspace  
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		//get location of workspace (java.io.File)  
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();
		String workspaceDirPath = null;
		if (workspaceDirectory != null) {
			workspaceDirPath = workspaceDirectory.getPath();
		}
		DirectoryDialog dirDialog = new DirectoryDialog(getShell());
		dirDialog.setText("Select Output Directory");
		dirDialog.setFilterPath(workspaceDirPath);
		String selected = dirDialog.open();
		System.out.println(selected); // decompiled project

		if (selected != null) {
			sourcePrjDirectoryText.setText(selected);
		}
	}
	
	private void handleBrowseApk() {
		FileDialog fileDialog = new FileDialog(getShell());
		fileDialog.setText("Select Apk File");
		fileDialog.setFilterExtensions(new String[] { "*.apk" });
		fileDialog.setFilterNames(new String[] { "apk files(*.apk)" });
		String selected = fileDialog.open();
		System.out.println(selected); //apkname

		if (selected != null) {
			apkFileText.setText(selected);
		}
	}

	private void dialogApkChanged() {
		String apkFileName = getApkFileName();

		if (apkFileName.length() == 0) {
			updateStatus("Apk file name must be specified");
			return;
		}
		
//		if (apkFileName.replace('\\', '/').indexOf('/', 1) > 0) {
//			updateStatus("Apk file name must be valid");
//			return;
//		}
		updateStatus(null);
	}
	
	private void dialogSourceDirectoryChanged() {
		String directoryName = getDecompiledSourceDirectoryName();

		if (directoryName.length() == 0) {
			updateStatus("Output directory for the decompiled apk must be specified");
			return;
		}
		
//		if (directoryName.replace('\\', '/').indexOf('/', 1) > 0) {
//			updateStatus("Eclipse project directory of the decompiled apk must be valid");
//			return;
//		}
		updateStatus(null);
	}
	
	private void dialogChanged() {
		dialogApkChanged();
		dialogSourceDirectoryChanged();
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	/**
	 * Fills initial values of UI elements.
	 */
	private void initControlValues() {
		String path = AdtPrefs.getPrefs().getOsSdkFolder();
		System.out.println(path);
	}

//	public String getProjectName() {
//		return projectNameText.getText();
//	}

	public String getApkFileName() {
		return apkFileText.getText();
	}
	
	public String getDecompiledSourceDirectoryName() {
		return sourcePrjDirectoryText.getText();
	}
	
	public String getAndroidDirectoryName() {
		return androidDirectoryText.getText();
	}
}