package org.yocto.sdk.remotetools.wizards.bsp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Enumeration;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import org.yocto.sdk.remotetools.YoctoBspElement;
import org.yocto.sdk.remotetools.YoctoJSONHelper;
import org.yocto.sdk.remotetools.YoctoBspPropertyElement;
/**
 *
 * Setting up the parameters for creating the new Yocto Bitbake project
 * 
 * @author jzhang
 */
public class PropertiesPage extends WizardPage {
	private static final String PAGE_NAME = "Properties";
	private static final String VALUES_CMD_PREFIX = "yocto-bsp list ";
	private static final String VALUES_CMD_SURFIX = " property ";
	private static final String KERNEL_CHOICE = "kernel_choice";
	private static final String DEFAULT_KERNEL = "use_default_kernel";
	private static final String SMP_PREFIX = "smp_";
	private static final String EXISTING_KBRANCH_PREFIX = "existing_kbranch_";
	private static final String NEW_KBRANCH_PREFIX = "new_kbranch_";
	private static final String BASE_KBRANCH_PREFIX = "base_kbranch_";
	private static final String QARCH_NAME = "qemuarch";
	
	private Hashtable<YoctoBspPropertyElement, Control> propertyControlMap;
	HashSet<YoctoBspPropertyElement> properties;
	private Composite composite;
	private YoctoBspElement bspElem = null;
	private boolean karch_changed = false;
	private Combo kcCombo;
	private Combo kbCombo;
	private Button newButton;
	private Button existingButton;
	private Button smpButton;
	private Composite kcContainer = null;
	private Composite scContainer = null;
	
	public PropertiesPage(YoctoBspElement element) {
		super(PAGE_NAME, "yocto-bsp properties page", null);
		//setTitle("Create new yocto bitbake project");
		//setMessage("Enter these parameters to create new Yocto Project BitBake commander project");
		this.bspElem = element;
	}

	public void test() {
		
	}
	
	public void onEnterPage(YoctoBspElement element) {
		if (!element.getValidPropertiesFile()) {
			setErrorMessage("There's no valid properties file created, please choose \"Back\" to reselect kernel architectur!");
			return;
		}
		
		if ((this.bspElem == null) || (!this.bspElem.getKarch().contentEquals(element.getKarch()))) {
			karch_changed = true;
		} else
			karch_changed = false;
		
		this.bspElem = element;
		GridLayout layout = new GridLayout(1, false);
		//this.composite.setLayout(layout);
		//this.composite.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 1;
		this.composite.setLayoutData(gd);
		String[] values;
		if (kcContainer == null) {
			kcContainer = new Composite(this.composite, SWT.NONE);
			layout = new GridLayout(3, false);
			kcContainer.setLayout(layout);
			gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.horizontalSpan = 3;
			kcContainer.setLayoutData(gd);
		
			new Label (kcContainer, SWT.NONE).setText(KERNEL_CHOICE+":");						
			kcCombo = new Combo(kcContainer, SWT.BORDER | SWT.READ_ONLY);
			kcCombo.setLayout(new GridLayout(1, false));
			kcCombo.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
			
			smpButton = new Button(kcContainer, SWT.CHECK);
			smpButton.setText("SMP Support");
			smpButton.setSelection(true);
			
			Group kernelGroup= new Group(this.composite, SWT.NONE);
			layout= new GridLayout(3, false);
			kernelGroup.setLayout(layout);
			gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.horizontalSpan= 3;
			kernelGroup.setLayoutData(gd);
			kernelGroup.setText("Kernel Branch Settings:");
			newButton = new Button(kernelGroup, SWT.RADIO);
		    newButton.setText("New");
		    newButton.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		    newButton.setSelection(true);
		    
		    existingButton = new Button(kernelGroup, SWT.RADIO);
		    existingButton.setText("Existing");
		    existingButton.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		    existingButton.setSelection(false);
		    
		    kbCombo = new Combo(kernelGroup, SWT.BORDER | SWT.READ_ONLY);
			kbCombo.setLayout(new GridLayout(1, false));
			kbCombo.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		} else {
			if (karch_changed) {
				kbCombo.removeAll();
				newButton.setSelection(true);
				existingButton.setSelection(false);
			}
		}
		if (karch_changed) {
			values = getValues(KERNEL_CHOICE);
			if (values != null)
				kcCombo.setItems(values);
		}
		kcCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged(e.widget);
			}
		});
		
		
	    newButton.addSelectionListener(new SelectionListener() {
	    	public void widgetDefaultSelected(SelectionEvent e) {}
	    	
	    	public void widgetSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
		});
	    
	   
	    existingButton.addSelectionListener(new SelectionListener() {
	    	public void widgetDefaultSelected(SelectionEvent e) {}
	    	
	    	public void widgetSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
		});
	    
	    
		kbCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged(e.widget);
			}
		});
		
		try {
			if (karch_changed) {
			properties = YoctoJSONHelper.getProperties();
			
			if (!properties.isEmpty()) {
				if (scContainer != null)  {
					scContainer.dispose();
				}
				scContainer = new Composite(this.composite, SWT.NONE);
				scContainer.setLayout(new FillLayout(SWT.VERTICAL));
				//this.composite.setLayout(new FillLayout(SWT.VERTICAL));
				gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
				gd.horizontalSpan = 1;
				ScrolledComposite sc = new ScrolledComposite(scContainer, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
				sc.setExpandHorizontal(true);
				sc.setExpandVertical(true);
			      
				Composite controlContainer = new Composite(sc, SWT.NONE);
				layout = new GridLayout(2, false);
				//ayout.marginBottom = 10;
				controlContainer.setLayout(layout);
				
				gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
				controlContainer.setLayoutData(gd);
				sc.setContent(controlContainer);
				if (!element.getQarch().isEmpty()) {
					YoctoBspPropertyElement qarch_elem = new YoctoBspPropertyElement();
					qarch_elem.setName(QARCH_NAME);
					qarch_elem.setValue(element.getQarch());
					properties.add(qarch_elem);
				}
				propertyControlMap = new Hashtable<YoctoBspPropertyElement, Control>();
				Iterator<YoctoBspPropertyElement> it = properties.iterator();
				
			    while (it.hasNext()) {
			        // Get property
			        YoctoBspPropertyElement propElem = (YoctoBspPropertyElement)it.next();
			        String type  = propElem.getType();
			        String name = propElem.getName();
			        if (type.contentEquals("edit")) {
			        	Composite textContainer = new Composite(controlContainer, SWT.NONE);
			        	layout = new GridLayout(2, false);
			        	textContainer.setLayout(layout);
			        	gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			        	gd.horizontalSpan = 2;
			        	textContainer.setLayoutData(gd);
			        	
			        	new Label (textContainer, SWT.NONE).setText(name+":");
			        	Text text = new Text(textContainer, SWT.BORDER | SWT.SINGLE);
			    		gd = new GridData(GridData.FILL_HORIZONTAL);
			    		text.setLayoutData(gd);
			    		propertyControlMap.put(propElem, (Control)text);
			    		sc.setMinSize(controlContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
			    		controlContainer.layout();
			        } else if (type.contentEquals("boolean")) {
			        	Composite booleanContainer = new Composite(controlContainer, SWT.NONE);
			        	layout = new GridLayout(1, false);
			        	booleanContainer.setLayout(layout);
			        	gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			        	gd.horizontalSpan = 1;
			        	booleanContainer.setLayoutData(gd);
			        	
			        	String default_value = propElem.getDefaultValue();
			        	Button button = new Button(booleanContainer, SWT.CHECK);
			    		button.setText(name);
			    		if (default_value.equalsIgnoreCase("y")) {
			    			button.setSelection(true);	
			    		} else 
			    			button.setSelection(false);
			    		propertyControlMap.put(propElem, (Control)button);
			    		sc.setMinSize(controlContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
			    		controlContainer.layout();
			        } else if (type.contentEquals("choicelist")) {
			        	Composite choiceContainer = new Composite(controlContainer, SWT.NONE);
			        	layout = new GridLayout(2, false);
			        	choiceContainer.setLayout(layout);
			        	gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			        	gd.horizontalSpan = 2;
			        	choiceContainer.setLayoutData(gd);
			        	
			        	new Label (choiceContainer, SWT.NONE).setText(name+":");
			        	Combo combo = new Combo(choiceContainer, SWT.BORDER | SWT.READ_ONLY);
			    		combo.setLayout(new GridLayout(1, false));
			    		combo.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
			    		values = getValues(name);
			    		if (values != null)
			    			combo.setItems(values);
			    		propertyControlMap.put(propElem, (Control)combo);
			    		sc.setMinSize(controlContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
			    		controlContainer.layout();
			        }
			    }
			   // controlContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.composite.layout(true);
		
	}
	

	public void createControl(Composite parent) {
		this.composite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		GridLayout layout = new GridLayout(2, false);
		this.composite.setLayout(layout);

		gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan= 2;
		this.composite.setLayoutData(gd);

		setControl(this.composite);
	}
	
	public boolean canFlipToNextPage() {
		return false;
	}
	
	public HashSet<YoctoBspPropertyElement> getProperties() {
		String kcSelection = kcCombo.getText();
		String kbSelection = kbCombo.getText();
		YoctoBspPropertyElement kc_element = new YoctoBspPropertyElement();
		kc_element.setName(KERNEL_CHOICE);
		kc_element.setValue(kcSelection);
		properties.add(kc_element);
		YoctoBspPropertyElement default_element = new YoctoBspPropertyElement();
		default_element.setName(DEFAULT_KERNEL);
		default_element.setValue("n");
		properties.add(default_element);
	
		kcSelection = kcSelection.replaceAll("-", "_");
		kcSelection = kcSelection.replace(".", "_");
		String smp_name = "";
		smp_name = SMP_PREFIX + kcSelection;
		YoctoBspPropertyElement smp_element = new YoctoBspPropertyElement();
		smp_element.setName(smp_name);
		if (smpButton.getSelection())
			smp_element.setValue("y");
		else
			smp_element.setValue("n");
		properties.add(smp_element);
		
		YoctoBspPropertyElement newkb_element = new YoctoBspPropertyElement();
		YoctoBspPropertyElement kb_element = new YoctoBspPropertyElement();
		String newkb_name  = "";
		newkb_name = NEW_KBRANCH_PREFIX+kcSelection;
		newkb_element.setName(newkb_name);
		if (newButton.getSelection()) {
			newkb_element.setValue("y");
			properties.add(newkb_element);
			kb_element.setName(BASE_KBRANCH_PREFIX+kcSelection);
			kb_element.setValue(kbSelection);
			properties.add(kb_element);
		} else {
			newkb_element.setValue("n");
			properties.add(newkb_element);
			kb_element.setName(EXISTING_KBRANCH_PREFIX+kcSelection);
			kb_element.setValue(kbSelection);
			properties.add(kb_element);
		}
		
		return properties;
	}
	
	public boolean validatePage() {
		if ((kcCombo != null) && (kbCombo != null)) {
			String kcSelection = kcCombo.getText();
			String kbSelection = kbCombo.getText();
			if ((kcSelection == null) || (kbSelection == null) || (kcSelection.isEmpty()) || (kbSelection.isEmpty())) {
				setErrorMessage("Please select kernel_choice and kernel_branch!");
				return false;
			}
		}
		if ((propertyControlMap != null)) { 
			if (!propertyControlMap.isEmpty()) {
				Enumeration<YoctoBspPropertyElement> keys = propertyControlMap.keys();
				while( keys.hasMoreElements() ) {
					YoctoBspPropertyElement key = (YoctoBspPropertyElement)keys.nextElement();
					Control control = propertyControlMap.get(key);
					String type = key.getType();
					
					if (type.contentEquals("edit")) {
						String text_value = ((Text)control).getText();
						if (text_value == null) {
							setErrorMessage("Field "+ key.getName() +" is not set.  All of the field on this screen must be set!");
							return false;
						} else {
							key.setValue(text_value);
						}
					} else if (type.contentEquals("choicelist")) {
						String choice_value = ((Combo)control).getText();
						if (choice_value == null) {
							setErrorMessage("Field "+ key.getName() +" is not set.  All of the field on this screen must be set!");
							return false;
						} else {
							key.setValue(choice_value);
						}
					} else {
						boolean button_select = ((Button)control).getSelection();
						if (button_select)
							key.setValue("y");
						else
							key.setValue("n");
					}
					updateProperties(key);
				}
			}
		}
		return true;
	}
	
	private void updateProperties(YoctoBspPropertyElement element) {
		Iterator<YoctoBspPropertyElement> it = properties.iterator();
		while (it.hasNext()) {
			YoctoBspPropertyElement propElem = (YoctoBspPropertyElement)it.next();
			if (propElem.getName().contentEquals(element.getName())) {
				properties.remove((Object) propElem);
				properties.add(element);
				break;
			} else 
				continue;
		}
 	}
	private void controlChanged(Widget widget) {
		 setErrorMessage(null);
		 String kb_property;
		 
		 String kernel_choice = kcCombo.getText();
		 if ((kernel_choice == null) || (kernel_choice.isEmpty())) {
			 setErrorMessage("Please selecte kernel_choice!");
			 return;
		 } else {
			 kernel_choice = kernel_choice.replaceAll("-", "_");
			 kernel_choice = kernel_choice.replace(".", "_");
		 }
		 if (widget == kcCombo) {
			 newButton.setSelection(true);
			 existingButton.setSelection(false);
			 kbCombo.removeAll();
			 
			 kb_property = BASE_KBRANCH_PREFIX+kernel_choice;
			 String[] values = getValues(kb_property);
			 kbCombo.setItems(values);
		 }
		 if (widget == kbCombo) {
			 setErrorMessage(null);
		 }
		 if (widget == newButton) {
			 
			 boolean newBranch = newButton.getSelection();
			 
			 if (newBranch) {
				 kb_property = BASE_KBRANCH_PREFIX+kernel_choice;
				 String[] values = getValues(kb_property);
				 kbCombo.setItems(values);
			 } else {
				 kb_property = EXISTING_KBRANCH_PREFIX+kernel_choice;
				 String[] values = getValues(kb_property);
				 kbCombo.setItems(values);
			 }
		 }
		 if (widget == existingButton) {
			 boolean existingBranch = existingButton.getSelection();
			
			 if (existingBranch) {
				 kb_property = EXISTING_KBRANCH_PREFIX+kernel_choice;
				 String[] values = getValues(kb_property);
				 kbCombo.setItems(values);
			 }
		 }
		 canFlipToNextPage();
		 getWizard().getContainer().updateButtons();
	}
	
	private String[] getValues(String property) {
		ArrayList<String> values = new ArrayList<String>();
		
		String values_cmd = bspElem.getMetadataLoc() + "/scripts/" + VALUES_CMD_PREFIX + bspElem.getKarch() + VALUES_CMD_SURFIX + property;
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(values_cmd);
			InputStream stdin = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(stdin);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			String error_message = "";
			
			while ( (line = br.readLine()) != null) {
				if (!line.startsWith("[")) {
					error_message = error_message + line;
					continue;
				}
				String[] items = line.split(",");
				
				String value = items[0];
				value = value.replace("[\"", "");
				value = value.replaceAll("\"$", "");
				values.add(value);
			}
			int exitVal = proc.waitFor();
			if (exitVal != 0) {
				MessageDialog.openError(getShell(),"Yocto-BSP", error_message);
				return null;
			} 
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (!values.isEmpty()) {
			String[] vitems = new String[values.size()];
			vitems = values.toArray(vitems);
			return vitems;
		} else
			return null;
	}
}