package com.dforensic.plugin.manal.model;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import soot.jimple.infoflow.InfoflowResults.SinkInfo;
import soot.jimple.infoflow.InfoflowResults.SourceInfo;

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
	
	private List<WeakReference<ApiDescriptor>> mDependencyList = null;
	private WeakReference<SinkInfo> mRootSink = null;
	private WeakReference<SourceInfo> mRootSource = null;

	public ApiDescriptor() {
		
	}
	
	public ApiDescriptor(SinkInfo sinkInfo) {
		mRootSink = new WeakReference<SinkInfo>(sinkInfo);
	}
	
	public ApiDescriptor(SourceInfo sourceInfo) {
		mRootSource = new WeakReference<SourceInfo>(sourceInfo);
	}
	
	
	public void addDependency(ApiDescriptor method) {
		if (mDependencyList == null) {
			mDependencyList = new ArrayList<WeakReference<ApiDescriptor>>();
		}
		mDependencyList.add(new WeakReference<ApiDescriptor>(method));
	}		
	
	public List<WeakReference<ApiDescriptor>> getDependencyList() {
		return mDependencyList;
	}
		
	public boolean isSink() {
		if (mRootSink != null) {
			return true;
		}
		return false;
	}
	
	public SinkInfo getSinkInfo() {
		if (mRootSink != null) {
			return mRootSink.get();
		}
		return null;
	}
	
	public SourceInfo getSourceInfo() {
		if (mRootSource != null) {
			return mRootSource.get();
		}
		return null;
	}
	
	public boolean isSource() {
		if (mRootSource != null) {
			return true;
		}
		return false;
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
		
		return sb.toString();
	}

}
