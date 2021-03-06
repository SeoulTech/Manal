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
package com.dforensic.plugin.manal.parser;

import com.dforensic.plugin.manal.model.ApiDescriptor;
import com.dforensic.plugin.manal.model.ProjectProperties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyze a source code.
 * 
 * Refer http://www.vogella.com/tutorials/EclipseJDT/article.html
 * http://www.eclipse
 * .org/articles/article.php?file=Article-JavaCodeManipulation_AST/index.html
 * 
 * @author Zeoo
 * 
 */
public class SuspectSearch {

	private List<ApiDescriptor> mMethodDetails = new ArrayList<ApiDescriptor>();
	/** Methods to detect in a source code. */
	private List<ApiDescriptor> mFilterMethods = null;

	public void run() {
		// Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		// Loop over all projects
		for (IProject project : projects) {
			try {
				String apkName = project.getPersistentProperty(new QualifiedName(ProjectProperties.QUALIFIER,
						ProjectProperties.getApkNameKey()));
				// String prjName = project.getPersistentProperty(new QualifiedName(ProjectProperties.QUALIFIER,
				//		ProjectProperties.getPrjNameKey()));
				// if (project.getFullPath().toString().equals(ProjectProperties.getPrjNameVal())) {
				// if ((prjName != null) && prjName.equals(ProjectProperties.getPrjNameVal())) {
				if ((apkName != null) && apkName.equals(ProjectProperties.getApkNameVal())) {
					boolean res = extractProjectInfo(project);
					if (res) {
						System.out.println("The search is achieved.");
						return;
					}
				} else {
					System.out.println("Skip the project, path is not matched.");
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

//	public void setSuspectApi(List<ApiDescriptor> apiList) {
//		mFilterMethods = apiList;
//	}

	public List<ApiDescriptor> getMethodDescriptions() {
		return mMethodDetails;
	}
	
	/**
	 * If JavaElement in the project is found
	 * for the passed API it is set as a member
	 * for it. 
	 */
	public void getJavaElementForApi(ApiDescriptor api) {
		mFilterMethods = new ArrayList<ApiDescriptor>();
		mFilterMethods.add(api);
		run();
	}

	private boolean extractProjectInfo(IProject project) throws CoreException,
			JavaModelException {
		System.out.println("Working in project " + project.getName());
		// check if we have a Java project
		if (project.isNatureEnabled(JavaCore.NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(project);
			if (extractPackageInfos(javaProject)) {
				return true;
			}
		} else {
			System.err.println("This is not Java project.");
		}
		
		return false;
	}

	private boolean extractPackageInfos(IJavaProject javaProject)
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment packageSearched : packages) {
			if (mFilterMethods != null) {
				for (ApiDescriptor api : mFilterMethods) {
					// Package fragments include all packages in the classpath.
					// We will only look at the package from the source folder.
					// K_BINARY would include also included JARS, e.g. rt.jar
					String packageName = api.getPackageNameFromSoot();
					if (packageName != null) {
						if ((packageSearched.getKind() == IPackageFragmentRoot.K_SOURCE) &&
								packageName.equals(packageSearched.getElementName())) {
							System.out.println("Package " + packageSearched.getElementName());
							if (createAST(packageSearched, api)) {
								return true;
							}
							// extractClassInfo(mypackage);
						}
					} else {
						System.err.println("Package name is NULL for " + 
								api.toString());
					}
				}
			} else {
				System.err.println("Methods for search are not initialized. " +
						"mFilterMethods is NULL.");
			}
		}
		return false;
	}

	private boolean createAST(IPackageFragment packageSearched, ApiDescriptor methodSearched)
			throws JavaModelException {
		String className = methodSearched.getClassNameFromSoot();
		if (className == null) {
			System.err.println("Can't search. Class name is NULL.");
			return false;
		}
		for (ICompilationUnit unit : packageSearched.getCompilationUnits()) {
			String unitName = unit.getElementName();
			if ((unitName != null) && unitName.contains(className)) {
				// now create the AST for the ICompilationUnits
				final CompilationUnit parse = parse(unit);
				methodSearched.setCompilationUnit(parse);
				return true;
//				MethodVisitor visitor = new MethodVisitor();
//				parse.accept(visitor);
//	
//				for (MethodDeclaration method : visitor.getMethods()) {
//					System.out.print("Method name: " + method.getName()
//							+ " Return type: " + method.getReturnType2());
//					extractStatements(parse, method, methodSearched);
//				}
			}
		}
		return false;
	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 * 
	 * @param unit
	 * @return
	 */
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}

	private void extractStatements(CompilationUnit cu, MethodDeclaration method,
			ApiDescriptor methodSearched) {
		String methodName = methodSearched.getMethodNameFromSoot();
		if (methodName == null) {
			System.err.println("Can't search a method. Method name is NULL.");
			return;
		}
		Block body = method.getBody();
		if (body != null) {
			for (Statement smt : (List<Statement>) body.statements()) {
				if (smt instanceof ExpressionStatement) {
					Expression esn = ((ExpressionStatement) smt)
							.getExpression();
					if (esn instanceof MethodInvocation) {
						// TODO when refactoring
						// go deeper to statements (not only MethodInvoke)
						MethodInvocation inv = (MethodInvocation) esn;
						if (methodName.equals(inv.getName())) {
							methodSearched.setCompilationUnit(cu);
							return;
						}
						// filterMethod(cu, inv);
						
						// SimpleName name = inv.getName();
						// ApiDescriptor apiDesc = new ApiDescriptor();
						// apiDesc.setMethodName(name.getIdentifier());
						// apiDesc.setReturnType(method.getReturnType());
						// apiDesc.setSignature(method.getSignature());
						// mMethodDetails.add(apiDesc);
					}
				}
			}
		} else {
			System.out.print(">>warning: the method doesn't have a body.");
		}
	}

	private void filterMethod(CompilationUnit cu, MethodInvocation method) {
		if (mFilterMethods != null) {
			for (ApiDescriptor desc : mFilterMethods) {
				if (desc.isSimilar(method)) {
				 	ApiDescriptor cloneDesc = desc.clone(method);
				 	cloneDesc.setCompilationUnit(cu);
				 	addMethodDetails(cloneDesc);
				}
			}
		} else {
			System.out
					.println(">>inf: the filter is not set up. Return all found methods.");
			ApiDescriptor cloneDesc = new ApiDescriptor(method);
			addMethodDetails(cloneDesc);
		}
	}

	/**
	 * Add method details to the list of return methods.
	 */
	private void addMethodDetails(ApiDescriptor desc) {
		mMethodDetails.add(desc);
	}

	/** ==============Not used======================================== */
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

	private void extractMethods(ICompilationUnit unit)
			throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			extractMethodDetails(type);
		}
	}

	private void extractMethodDetails(IType type) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {

			ApiDescriptor apiDesc = new ApiDescriptor();
			apiDesc.setMethodName(method.getElementName());
			apiDesc.setReturnType(method.getReturnType());
			apiDesc.setSignature(method.getSignature());
			mMethodDetails.add(apiDesc);
		}
	}

}
