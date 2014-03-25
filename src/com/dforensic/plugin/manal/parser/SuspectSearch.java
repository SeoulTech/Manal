package com.dforensic.plugin.manal.parser;

import com.dforensic.plugin.manal.model.ApiDescription;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import java.util.ArrayList;
import java.util.List;

public class SuspectSearch {
    
    private List<ApiDescription> mMethodDetails = new ArrayList<ApiDescription>();

    public void run() {
        // Get the root of the workspace
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject[] projects = root.getProjects();
        // Loop over all projects
        for (IProject project : projects) {
            try {
                extractProjectInfo(project);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    private void extractProjectInfo(IProject project) throws CoreException,
            JavaModelException {
        System.out.println("Working in project " + project.getName());
        // check if we have a Java project
        if (project.isNatureEnabled(JavaCore.NATURE_ID)) {
            IJavaProject javaProject = JavaCore.create(project);
            extractPackageInfos(javaProject);
        }
    }

    private void extractPackageInfos(IJavaProject javaProject)
            throws JavaModelException {
        IPackageFragment[] packages = javaProject.getPackageFragments();
        for (IPackageFragment mypackage : packages) {
            // Package fragments include all packages in the
            // classpath
            // We will only look at the package from the source
            // folder
            // K_BINARY would include also included JARS, e.g.
            // rt.jar
            if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
                System.out.println("Package " + mypackage.getElementName());
                extractClassInfo(mypackage);

            }

        }
    }

    private void extractClassInfo(IPackageFragment mypackage)
            throws JavaModelException {
        for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
            extractClassDetails(unit);

        }
    }

    private void extractClassDetails(ICompilationUnit unit)
            throws JavaModelException {
        System.out.println("Source file " + unit.getElementName());
        extractMethods(unit);
    }
    
    private void extractMethods(ICompilationUnit unit) throws JavaModelException {
        IType[] allTypes = unit.getAllTypes();
        for (IType type : allTypes) {
            extractMethodDetails(type);
        }
    }

    private void extractMethodDetails(IType type) throws JavaModelException {
        IMethod[] methods = type.getMethods();
        for (IMethod method : methods) {

            ApiDescription apiDesc = new ApiDescription();
            apiDesc.setMethodName(method.getElementName());
            apiDesc.setReturnType(method.getReturnType());
            apiDesc.setSignature(method.getSignature());
        }
    }
    
    public List<ApiDescription> getMethodDescriptions() {
    	return mMethodDetails;
    }

}
