package org.ofbiz.plugin.handlers.opencomponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.ofbiz.plugin.Plugin;
import org.ofbiz.plugin.model.OfbizModelSingleton;
import org.ofbiz.plugin.ofbiz.HasXmlDefinition;
import org.ofbiz.plugin.ofbiz.provider.OfbizItemProviderAdapterFactory;
import org.ofbiz.plugin.parser.GoToFile;

public class OpenComponentDialog extends FilteredItemsSelectionDialog {
	 private static final String DIALOG_SETTINGS = "FilteredResourcesSelectionDialogExampleSettings";
	 private List<HasXmlDefinition> currentlySelectedElements = new ArrayList<HasXmlDefinition>();

	public OpenComponentDialog(Shell shell, boolean multi,
			IContainer container, int typesMask) {
		super(shell, multi);
		setTitle("Open Ofbiz resource");
		setListLabelProvider(new ILabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getText(Object arg0) {
				if (arg0 == null) {
					return "sdf";
				}
				HasXmlDefinition definition = (HasXmlDefinition) arg0;
				return definition.getNameToShow();
			}
			
			@Override
			public Image getImage(Object arg0) {
				if (arg0 == null) {
					return null;
				}
				String className = arg0.getClass().getSimpleName();
				return Plugin.create("icons/full/obj16/" + (className.substring(0, className.length()-"Impl".length())) + ".gif").createImage();
			}
		});
		setDetailsLabelProvider(new ILabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ILabelProviderListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getText(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Image getImage(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new ItemsFilter(){

			@Override
			public boolean isConsistentItem(Object arg0) {
				return true;
			}

			@Override
			public boolean matchItem(Object arg0) {
				if (arg0 != null) {
					HasXmlDefinition hasXmlDefinition = (HasXmlDefinition) arg0;
					String markerKey = hasXmlDefinition.getNameToShow();
					if (markerKey == null) {
						return false;
					}
					return matches(markerKey);
				} else {
					return false;
				}
			}

		};
	}

	
	
	@Override
	protected StructuredSelection getSelectedItems() {
		// TODO Auto-generated method stub
		StructuredSelection selectedItems = super.getSelectedItems();
		currentlySelectedElements = selectedItems.toList();
		return selectedItems;
	}

	@Override
	protected void okPressed() {
		for (HasXmlDefinition hasXmlDefinition : currentlySelectedElements) {
			GoToFile.gotoFile(hasXmlDefinition);
		}
		super.okPressed();
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
					throws CoreException {
		Set<HasXmlDefinition> allFilesWithXmlDefinitions = OfbizModelSingleton.get().getAllFilesWithXmlDefinitions();
		for (HasXmlDefinition hasXmlDefinition : allFilesWithXmlDefinitions) {
			contentProvider.add(hasXmlDefinition, itemsFilter);
			progressMonitor.worked(1);
		}
		progressMonitor.beginTask("Searching for Ofbiz components", allFilesWithXmlDefinitions.size());
		progressMonitor.done();
	}
	public String getElementName(Object item) {
		HasXmlDefinition hasXmlDefinition = (HasXmlDefinition) item;
		return hasXmlDefinition.getNameToShow();
	}
	
	protected Comparator getItemsComparator() {
		return new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		};
	}
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Plugin.getDefault().getDialogSettings()
				.getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Plugin.getDefault().getDialogSettings()
					.addNewSection(DIALOG_SETTINGS);
		}
		return settings;
	}
}