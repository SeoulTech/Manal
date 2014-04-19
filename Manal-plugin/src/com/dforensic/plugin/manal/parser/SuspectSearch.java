package com.dforensic.plugin.manal.parser;

import com.dforensic.plugin.manal.model.ApiDescriptor;

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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
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
				extractProjectInfo(project);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void setSuspectApi(List<ApiDescriptor> apiList) {
		mFilterMethods = apiList;
	}

	public List<ApiDescriptor> getMethodDescriptions() {
		return mMethodDetails;
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
			// Package fragments include all packages in the classpath.
			// We will only look at the package from the source folder.
			// K_BINARY would include also included JARS, e.g. rt.jar
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				System.out.println("Package " + mypackage.getElementName());
				createAST(mypackage);
				// extractClassInfo(mypackage);
			}
		}
	}

	private void createAST(IPackageFragment mypackage)
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			// now create the AST for the ICompilationUnits
			final CompilationUnit parse = parse(unit);
			MethodVisitor visitor = new MethodVisitor();
			parse.accept(visitor);

			for (MethodDeclaration method : visitor.getMethods()) {
				System.out.print("Method name: " + method.getName()
						+ " Return type: " + method.getReturnType2());
				extractStatements(parse, method);
			}
		}
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

	private void extractStatements(CompilationUnit cu, MethodDeclaration method) {
		Block body = method.getBody();
		if (body != null) {
			for (Statement smt : (List<Statement>) body.statements()) {
				if (smt instanceof ExpressionStatement) {
					Expression esn = ((ExpressionStatement) smt)
							.getExpression();
					if (esn instanceof MethodInvocation) {
						MethodInvocation inv = (MethodInvocation) esn;
						filterMethod(cu, inv);
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
