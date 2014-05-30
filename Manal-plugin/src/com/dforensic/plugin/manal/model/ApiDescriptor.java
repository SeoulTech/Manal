package com.dforensic.plugin.manal.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowResults.SinkInfo;
import soot.jimple.infoflow.InfoflowResults.SourceInfo;
import soot.tagkit.LineNumberTag;

/**
 * <p>
 * The class describes suspicious APIs to be searched.
 * </p>
 * <p>
 * This descriptor describes both APIs extracted from
 * an xml and API found in a source of a project.<br/>
 * TODO this class needs to understand {@link IMethod}
 *      in order to use isSimilar() for comparison. 
 * </p>
 * 
 * @author Zeoo
 *
 */
public class ApiDescriptor {
	
	private CompilationUnit mCompilationUnit = null;
	private int mLineNum = -1;
	
	public enum MethodType {CONSTRUCTOR, NORMAL};
	
	public class ParameterDescriptor {
		private String mType = null;
		/** The value is extracted from a source code. */
		private String mValue = null;
		/** 
		 * The name is extracted from an xml or 
		 * the description of an API.
		 */
		private String mName = null;
		/** 
		 * An xml describes not all parameters but
		 * only important ones. A position of the
		 * described parameter is specified.
		 */
		private int mPos = -1;
		
		/** The constructor for initialization from an xml. */
		public ParameterDescriptor(String name, String type, int pos) {
			mName = name;
			mType = type;
			mPos = pos;
		}
		
		/** The constructor for initialization from a source code. */
		public ParameterDescriptor(String value, String type) {
			mValue = value;
			mType = type;
		}
		
		public String getName() {
			return mName;
		}
		
		public void setName(String name) {
			mName = name;
		}
		
		public String getValue() {
			return mValue;
		}
		
		public void setValue(String value) {
			mValue = value;
		}
		
		public String getType() {
			return mType;
		}
		
		public void setType(String type) {
			mType = type;
		}
		
		public int getPos() {
			return mPos;
		}
		
		public void setPos(int pos) {
			mPos = pos;
		}
		
		public ParameterDescriptor clone() {
			ParameterDescriptor pd = new ParameterDescriptor(null, null);
			String copyName = null;
			String copyType = null;
			String copyValue = null;
			if (mName != null) {
				copyName = new String(mName);
			} 
			if (mValue != null) {
				copyValue = new String(mValue);
			}
			if (mType != null) {
				copyType = new String(mType);
			}
			pd.setName(copyName);
			pd.setType(copyType);
			pd.setValue(copyValue);
			pd.setPos(mPos);
			
			return pd;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (mType != null) {
				sb.append(mType).append(" ");
			}
			if (mPos != -1) {
				sb.append(mPos).append(":");
			}
			if (mName != null) {
				sb.append(mName).append(" ");
			}
			if (mValue != null) {
				sb.append("[").append(mValue).append("]");
			}
			
			return sb.toString();
		}
		
	};
		
	/* <p>Methods to add information about the method
	 * in the eclipse project.</p>
	 * <p>CompilationUnit is used to obtain JavaElement
	 * which can be opened in a java editor.</p> */
	
	public void setCompilationUnit(CompilationUnit cu) {
		mCompilationUnit = cu;
	}
	
	public CompilationUnit getCompilatioinUnit() {
		return mCompilationUnit;
	}
	
	public void setLineNumber(int ln) {
		mLineNum = ln;
	}
	
	public int getLineNumber() {
		return mLineNum;
	}
	
	private String mReturnType = null;
	private String mReturnValue = null;
	private List<ParameterDescriptor> mParams = null;
	private String mMethodName = null;
	private String mSignature = null;
	private String mPackageName = null;
	private String mClassName = null;
	/** Constructor or a normal method. */
	private MethodType mMethodType = MethodType.NORMAL;
	
	private List<ApiDescriptor> mDependencyList = null;
	private SinkInfo mRootSink = null;
	private SourceInfo mRootSource = null;
	private SootMethod mSootMethod = null;

	public ApiDescriptor() {
		
	}
	
	public ApiDescriptor(SinkInfo sinkInfo) {
		mRootSink = sinkInfo;
	}
	
	public ApiDescriptor(SourceInfo sourceInfo) {
		mRootSource = sourceInfo;
	}
	
	
	public void addDependency(ApiDescriptor method) {
		if (mDependencyList == null) {
			mDependencyList = new ArrayList<ApiDescriptor>();
		}
		mDependencyList.add(method);
	}		
	
	public List<ApiDescriptor> getDependencyList() {
		return mDependencyList;
	}
		
	public boolean isSink() {
		if (mRootSink != null) {
			return true;
		}
		return false;
	}
	
	public SinkInfo getSinkInfo() {
		return mRootSink;
	}
	
	public SourceInfo getSourceInfo() {
		return mRootSource;
	}
	
	public boolean isSource() {
		if (mRootSource != null) {
			return true;
		}
		return false;
	}
	
	public void setSootMethod(SootMethod method) {
		mSootMethod = method;
	}
	
	public String getClassNameFromSoot() {
		if (mSootMethod != null) {
			SootClass sootClass = mSootMethod.getDeclaringClass();
			if (sootClass != null) {
				String className = sootClass.getJavaStyleName();
				if (className != null) {
					// get only name without package.
					String packageName = getPackageNameFromSoot();
					if (packageName != null) {
						if (className.contains(packageName)) {
							return className.substring(packageName.length(),
									className.length());
						}
					}
					System.out.println("Problems to find package in class name.");
					int ind = className.lastIndexOf('.');
					if (ind != -1) {
						return className.substring(ind,
								className.length());
					} else {
						return className;
					}
				} else {
					System.err.println("Can't get class name. It is not initialized in " +
							"SootClass.");
					return null;
				}
			} else {
				System.err.println("Can't get class name. SootClass is not initialized.");
				return null;
			} 
		} else {
			System.err.println("Can't get class name. SootMethod is not initialized.");
			return null;
		}
	}
	
	public String getPackageNameFromSoot() {
		if (mSootMethod != null) {
			SootClass sootClass = mSootMethod.getDeclaringClass();
			if (sootClass != null) {
				return sootClass.getJavaPackageName();
			} else {
				System.err.println("Can't get package name. SootClass is not initialized.");
				return null;
			} 
		} else {
			System.err.println("Can't get package name. SootMethod is not initialized.");
			return null;
		}
	}
	
	public String getMethodNameFromSoot() {
		Value sink = mRootSink.getSink();
		if (sink instanceof InvokeExpr) {
			InvokeExpr expr = (InvokeExpr) sink;
			SootMethodRef exprRef = expr.getMethodRef(); 
			if (exprRef != null) 
			{
				//System.out.println("function name: " + exprRef.name());
				exprRef.parameterTypes();
				exprRef.returnType();
				return exprRef.name().trim();
			}
		}
		System.err.println("Can't get method name. A problem to extract it from InvokeExpr.");
		return null;
	}
	
	public int getLineNumFromSoot() {
		Stmt context = null;
		if (mRootSink != null) {
			context = mRootSink.getContext();
		} else if (mRootSource != null) {
			context = mRootSource.getContext();
		}
		
		
		if (context == null) {
			System.err.println("Context was not obtained neither from Sink nor from Source.");
			return -1;
		}
		
		if (context.hasTag("LineNumberTag")) {
            return ((LineNumberTag)context.getTag("LineNumberTag")).getLineNumber();
		}
		
		return -1;
	}
	
	public ApiDescriptor(MethodInvocation method) {
		IMethodBinding methodBinding = method.resolveMethodBinding();
		if (methodBinding != null) {
			mMethodName = methodBinding.getName();
			ITypeBinding[] argMethods = methodBinding.getParameterTypes();
			ITypeBinding retMethod = methodBinding.getReturnType();
			boolean isConstructor = methodBinding.isConstructor();
			ITypeBinding declaringClass = methodBinding.getDeclaringClass();
			
			MethodType methodType = MethodType.NORMAL;
			if (isConstructor) {
				methodType = MethodType.CONSTRUCTOR;
			}
			mMethodType = methodType;
			
			if (declaringClass != null) {
				mClassName = declaringClass.getName();
				IPackageBinding pckg = declaringClass.getPackage();
				if (pckg != null) {
					mPackageName = pckg.getName();		
				} else {
					System.out.println(">>inf: package is not specified.");
				}
			} else {
				System.out.println(">>inf: class is not specified.");
			}
			if (retMethod != null) {
				mReturnType = retMethod.getName();
			} else {
				System.out.println(">>inf: return type is not specified.");
			}
			if (argMethods != null) {
				mParams = new ArrayList<ParameterDescriptor>(argMethods.length);
				int pos = 1;
				for (ITypeBinding param : argMethods) {
					if (param != null) {
						String paramName = param.getName();
						ITypeBinding paramType = param.getTypeDeclaration();
						String paramTypeName = null;
						if (paramType != null) {
							paramTypeName = paramType.getName();
						}
						mParams.add(new ParameterDescriptor(paramName, 
								paramTypeName, pos));
						pos++;
					} else {
						System.out.println(">>error: parameter is NULL.");
					}
				}
			} else {
				System.out.println(">>inf: method parameters are not specified.");
			}
		} else {
			System.out.println(">>warning: method binding can't be resolved.");
		}
	}
	
	public String getMethodName() {
		return mMethodName;
	}

	public void setMethodName(String methodName) {
		mMethodName = methodName;
	}
	
	public String getSignature() {
            return mSignature;
        }

        public void setSignature(String signature) {
            mSignature = signature;
        }
	
	public String getReturnType() {
		return mReturnType;
	}

	public void setReturnType(String returnType) {
		mReturnType = returnType;
	}

	public String getReturnValue() {
		return mReturnValue;
	}

	public void setReturnValue(String returnValue) {
		mReturnValue = returnValue;
	}
	
	public List<ParameterDescriptor> getParams() {
		return mParams;
	}

	/**
	 * Assign the entire set as a list of parameters.
	 * 
	 * @param params
	 */
	public void setParams(List<ParameterDescriptor> params) {
		mParams = params;
	}
	
	/**
	 * Add one more parameter to the existing ones.
	 * 
	 * @param param
	 */
	public void addParam(ParameterDescriptor param) {
		if (mParams == null) {
			mParams = new ArrayList<ParameterDescriptor>();
		}
		mParams.add(param);
	}
	
	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}
	
	public String getClassName() {
		return mClassName;
	}

	public void setClassName(String className) {
		mClassName = className;
	}
	
	public MethodType getMethodType() {
		return mMethodType;
	}

	public void setMethodType(MethodType type) {
		mMethodType = type;
	}
	
	public boolean isSimilar(MethodInvocation method) {
		List<String> argTypes = new ArrayList<String>();
		// List<Expression> argsMethod = method.arguments();
		IMethodBinding methodBinding = method.resolveMethodBinding();
		if (methodBinding != null) {
			String name = methodBinding.getName();
			ITypeBinding[] argMethods = methodBinding.getParameterTypes();
			ITypeBinding retMethod = methodBinding.getReturnType();
			boolean isConstructor = methodBinding.isConstructor();
			ITypeBinding declaringClass = methodBinding.getDeclaringClass();
			return compareAttributes(name, declaringClass, argMethods, retMethod, isConstructor);
		} else {
			System.out.println(">>warning: method binding can't be resolved.");
		}
		return false;
	}
	
	private boolean compareAttributes(String name, ITypeBinding declaringClass, ITypeBinding[] args,
			ITypeBinding ret, boolean isConstructor) {
		boolean matched = false;
		if (mMethodName != null) {
			if (mMethodName.equals(name)) {
				matched = true;
			} else {
				System.out.println(">>inf: method names are not matched.");
				return false;
			}
		} else {
			System.out.println(">>inf: method name is not specified.");
		}
		MethodType methodType = MethodType.NORMAL;
		if (isConstructor) {
			methodType = MethodType.CONSTRUCTOR;
		}
		if (mMethodType.equals(methodType)) {
			matched = true;
		} else {
			System.out.println(">>inf: method type is not matched.");
			return false;
		}
		if ((mClassName != null) && declaringClass != null) {
			if (mClassName.equals(declaringClass.getName())) {
				matched = true;
			} else {
				System.out.println(">>inf: class names are not matched.");
				return false;
			}
			IPackageBinding pckg = declaringClass.getPackage();
			if ((mPackageName != null) && pckg != null) {
				if (mPackageName.equals(pckg.getName())) {
					matched = true;
				} else {
					System.out.println(">>inf: package names are not matched.");
					return false;
				}				
			} else {
				System.out.println(">>inf: package is not specified.");
			}
		} else {
			System.out.println(">>inf: class is not specified.");
		}
		if ((mReturnType != null) && (ret != null)) {
			if (mReturnType.equals(ret.getName())) {
				matched = true;
			} else {
				System.out.println(">>inf: return types are not matched.");
				return false;
			}
		} else {
			System.out.println(">>inf: return type is not specified.");
		}
		if ((mParams != null) && (args != null)) {
			for (ParameterDescriptor param : mParams) {
				if ((param != null) && (param.getPos() >= 0) &&
						(param.getPos() < args.length)) {
					String paramName = param.getName();
					if ((paramName != null) &&
							paramName.equals(args[param.getPos()].getName())) {
						matched = true;
					} else {
						System.out.println(">>inf: parameters are not matched.");
						return false;
					}					
				} else {
					System.out.println(">>error: bad configuration of parameters.");
				}
			}
		} else {
			System.out.println(">>inf: method parameters are not specified.");
		}
		
		return matched;
	}
	
	public ApiDescriptor clone(MethodInvocation method) {
		// TODO when process details, extract later values of arguments
		//	    and return here and put it into the clone.
		ApiDescriptor desc = new ApiDescriptor();
		String copyClassName = null;
		String copyMethodName = null;
		String copyPackageName = null;
		if (mClassName != null) {
			copyClassName = new String(mClassName);
		}
		if (mMethodName != null) {
			copyMethodName = new String(mMethodName);
		}
		if (mPackageName != null) {
			copyPackageName = new String(mPackageName);
		}
		desc.setClassName(copyClassName);
		desc.setMethodName(copyMethodName);
		desc.setMethodType(mMethodType);
		desc.setPackageName(copyPackageName);
		if (mParams != null) {
			for (ParameterDescriptor param : mParams) {
				ParameterDescriptor newParam = param.clone();
				desc.addParam(newParam);
			}
		}
		return desc;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		String totalValue;
		if ((mRootSink != null) || (mRootSource != null)) {
			sb.append(getMethodSignatureFromSoot());
//			String temp = mRootSink.toString();
//			//System.out.println("temp : " + temp);
//			ArrayList<String> getParaList = new ArrayList<String>();
//			//System.out.println("function name : "+ getMethodNameFromSoot());
//			String func = getMethodNameFromSoot();
//			//System.out.println("return type : " + getReturnType(temp, func));
			
//			for (ApiDescriptor apiD : getElements()) {
//				System.out.println("function name : "
//						+ apiD.getMethodNameFromSoot());
//				String func = apiD.getMethodNameFromSoot();
//				System.out.println("return type : " + getReturnType(apiD, func));
//				getParaList = getParameters(apiD);
//				
//				for(int i=0; i < getParaList.size(); i++)
//				{
//					System.out.println("parameter: " + getParaList.get(i));
//				}
//			}

//			totalValue = getReturnType(temp, func)+ " " + func + getParameters(temp, func);
//			System.out.println("totalValue : " + totalValue);
//			sb.append(/*mRootSink.toString()*/totalValue);
		} else {
			if (mMethodType.equals(MethodType.CONSTRUCTOR)) {
				sb.append("constructor\n");
			}
			if (mReturnType != null) {
				sb.append(mReturnType).append(" ");
			}
			if (mReturnValue != null) {
				sb.append("[").append(mReturnValue).append("]")
					.append(" ");
			}
			if (mPackageName != null) {
				sb.append(mPackageName).append(".");
			}
			if (mClassName != null) {
				sb.append(mClassName).append(".");
			}
			if (mMethodName != null) {
				sb.append(mMethodName).append("(\n");
			}
			if (mParams != null) {
				for (ParameterDescriptor param : mParams) {
					sb.append("\t").append(param.toString()).append("\n");
				}
			}
			if (mMethodName != null) {
				sb.append(")\n");
			}
		}
		
		return sb.toString();
	}
	
	public String getMethodSignatureFromSoot() {
		Value sink = null;
		if (mRootSink != null) {
			sink = mRootSink.getSink();
		} else if (mRootSource != null) {
			sink = mRootSource.getSource();
		}
		
		if ((sink != null) && (sink instanceof InvokeExpr)) {
			InvokeExpr expr = (InvokeExpr) sink;
			SootMethodRef exprRef = expr.getMethodRef(); 
			if (exprRef != null) 
			{
				//System.out.println("function name: " + exprRef.name());
				StringBuilder methSign = new StringBuilder();
				Type ret = exprRef.returnType();
				String retStr = null;
				if (ret != null) {
					retStr = ret.toString().trim();
				}
				if ((retStr == null) || (retStr.isEmpty())) {
					retStr = new String("void");
				}
				methSign.append(retStr).append(" ");
				methSign.append(exprRef.name().trim());
				List<Type> params = exprRef.parameterTypes();
				if ((params != null) && !params.isEmpty()) {
					methSign.append("(");
					for (Type param : params) {
						String paramStr = null;
						if (param != null) {
							paramStr = param.toString().trim();
						}
						if ((paramStr == null) || (paramStr.isEmpty())) {
							paramStr = new String("void");
						}
						methSign.append(paramStr).append(", ");
					}					
					methSign.replace(methSign.length() - 2, methSign.length(), "");
					methSign.append(")");
				} else {
					methSign.append("()");
				}
				return methSign.toString();
			}
		}
		System.err.println("Can't get method name. A problem to extract it from InvokeExpr.");
		return null;
	}
	
	private String getParameters(String apiD, String funcName) {
		//System.out.println(apiD.split(funcName)[1]);
		System.out.println(apiD.split(funcName)[1].split(">")[0]);
		/*try {
			StringTokenizer st = new StringTokenizer(apiD.toString(), "(");
			String temp = null;
			ArrayList<String> paraList = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				if (st.hasMoreTokens() == false) {
					temp = st.toString();
					System.out.println("in parameter : " + temp);
				}
				st.nextToken();
			}

			StringTokenizer st_para = new StringTokenizer(temp, ",");
			while (st_para.hasMoreTokens()) {
				paraList.add(st_para.nextToken());
			}
			String lastPara = paraList.get(paraList.size() - 1);
			paraList.remove(paraList.size() - 1);
			StringTokenizer checkPara = new StringTokenizer(lastPara, ")");
			String[] revisedPara = lastPara.split(")");

			paraList.add(revisedPara[0]);

			return paraList;
		} catch (Exception exc) {
			exc.printStackTrace();
		}*/
		return apiD.split(funcName)[1].split(">")[0];

	}

	private String getReturnType(String apiD, String funcName) {
		
		//System.out.println(apiD.split(funcName)[0]);
		String[] temp = apiD.split(funcName)[0].split(" ");
		//System.out.println(temp[temp.length -1]);
		/*StringTokenizer st = new StringTokenizer(apiD, funcName.trim());
		System.out.println("funcName trim: " + funcName.trim());
		String tempToken;
		ArrayList<String> TokenList = new ArrayList<String>();
		int idx = 0;

		try 
		{
			while (st.hasMoreTokens()) {
				tempToken = st.nextToken();
				TokenList.add(tempToken);
				System.out.println(tempToken);

				if (tempToken.equals(funcName)) // if find function name
				{
					System.out
							.println("return type: " + TokenList.get(idx - 1));
					return TokenList.get(idx - 1);
					// StringTokenizer st_func = new StringTokenizer(tempToken,
				}
				idx++;
			}
			
		} catch (Exception exc) {

			exc.printStackTrace();
		}
		*/
		return temp[temp.length -1];
	}

	

}
