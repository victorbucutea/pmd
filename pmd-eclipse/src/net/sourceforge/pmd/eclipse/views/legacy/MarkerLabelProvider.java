package net.sourceforge.pmd.eclipse.views.legacy;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Implements a table label provider for displaying violations in the
 * violation view.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/10/24 22:45:58  phherlin
 * Integrating Sebastian Raffel's work
 * Move orginal Violations view to legacy
 *
 * Revision 1.4  2003/12/09 00:14:59  phherlin
 * Merging from v2 development
 *
 * Revision 1.3  2003/08/11 21:56:29  phherlin
 * Adding a label for the default package
 *
 * Revision 1.2  2003/08/11 21:14:21  phherlin
 * Fixing exception when refreshing violations table
 *
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class MarkerLabelProvider implements ITableLabelProvider {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.views.MarkerLabelProvider");
    private static final String KEY_IMAGE_ERROR = "error";
    private static final String KEY_IMAGE_WARN = "warn";
    private static final String KEY_IMAGE_INFO = "info";

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        Image image = null;

        if ((element instanceof IMarker) && (columnIndex == 0)) {
            IMarker marker = (IMarker) element;
            int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            if (severity == IMarker.SEVERITY_ERROR) {
                image = PMDPlugin.getDefault().getImage(KEY_IMAGE_ERROR, PMDPlugin.ICON_ERROR);
            } else if (severity == IMarker.SEVERITY_WARNING) {
                image = PMDPlugin.getDefault().getImage(KEY_IMAGE_WARN, PMDPlugin.ICON_WARN);
            } else if (severity == IMarker.SEVERITY_INFO) {
                image = PMDPlugin.getDefault().getImage(KEY_IMAGE_INFO, PMDPlugin.ICON_INFO);
            }
        }

        return image;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";

        try {
            if (element instanceof IMarker) {
                IMarker marker = (IMarker) element;
                IJavaElement javaElement = JavaCore.create(marker.getResource());
                ICompilationUnit compilationUnit = null;
                if ((javaElement != null) && (javaElement.exists()) && (javaElement instanceof ICompilationUnit)) {
                    compilationUnit = (ICompilationUnit) javaElement;
                }

                if (columnIndex == 0) {
                    int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                    int priority = marker.getAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
                    if (severity == IMarker.SEVERITY_ERROR) {
                        result = priority == IMarker.PRIORITY_HIGH ? "1" : "2";
                    } else if (severity == IMarker.SEVERITY_WARNING) {
                        result = priority == IMarker.PRIORITY_HIGH ? "3" : "4";
                    } else if (severity == IMarker.SEVERITY_INFO) {
                        result = "5";
                    } 
                } else if (columnIndex == 1) {
                    result = marker.getAttribute(IMarker.MESSAGE, "");
                } else if (columnIndex == 2) {
                    result = marker.getAttribute(PMDPlugin.KEY_MARKERATT_RULENAME, "");
                } else if (columnIndex == 3) {
                    result =
                        compilationUnit == null ? marker.getResource().getName() : compilationUnit.getTypes()[0].getElementName();
                } else if (columnIndex == 4) {
                    if (compilationUnit == null) {
                        result = marker.getResource().getProjectRelativePath().removeLastSegments(1).toString();
                    } else {
                        IPackageDeclaration[] packageDeclarations = compilationUnit.getPackageDeclarations();
                        if (packageDeclarations.length > 0) {
                            result = packageDeclarations[0].getElementName();
                        } else {
                            result = PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_VIEW_DEFAULT_PACKAGE, "");
                        }
                    }
                } else if (columnIndex == 5) {
                    result = marker.getResource().getProject().getName();
                } else if (columnIndex == 6) {
                    int lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, 0);
                    if (lineNumber != 0) {
                        result = String.valueOf(lineNumber);
                    }
                }
            }
        } catch (JavaModelException e) {
            IJavaModelStatus status = e.getJavaModelStatus();
            PMDPlugin.getDefault().logError(status);
            log.warn("Ignoring Java Model Exception : " + status.getMessage() );
            if (log.isDebugEnabled()) {
                log.debug("   code : " + status.getCode());
                log.debug("   severity : " + status.getSeverity());
                IJavaElement[] elements = status.getElements();
                for (int i = 0; i < elements.length; i++) {
                    log.debug("   element : " + elements[i].getElementName() + " (" + elements[i].getElementType() + ")");
                }
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object, String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
    }

}