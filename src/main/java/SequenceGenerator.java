import java.io.*;
import java.util.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

import net.sourceforge.plantuml.SourceStringReader;

public class SequenceGenerator {	
		 //ATTRIBUTES
	     String pumlCode;
	     String in;
	     String out;
		 String method;
	     String className;
	     static HashMap<String, String> methodMapClass;
	     static ArrayList<CompilationUnit> cunits;
	     static HashMap<String, ArrayList<MethodCallExpr>> methodCalls;

	     
	    //GETTERS AND SETTERS 
	    public String getPumlCode() {
			return pumlCode;
		}

		public void setPumlCode(String pumlCode) {
			this.pumlCode = pumlCode;
		}

		public String getInPath() {
			return in;
		}

		public void setInPath(String inPath) {
			this.in = inPath;
		}

		public String getOutPath() {
			return out;
		}

		public void setOutPath(String outPath) {
			this.out = outPath;
		}

		public String getInFuncName() {
			return method;
		}

		public void setInFuncName(String inFuncName) {
			this.method = inFuncName;
		}

		public String getInClassName() {
			return className;
		}

		public void setInClassName(String inClassName) {
			this.className = inClassName;
		}

		public static HashMap<String, String> getMapMethodClass() {
			return methodMapClass;
		}

		public static void setMapMethodClass(HashMap<String, String> mapMethodClass) {
			SequenceGenerator.methodMapClass = mapMethodClass;
		}

		public static ArrayList<CompilationUnit> getCuArray() {
			return cunits;
		}

		public static void setCuArray(ArrayList<CompilationUnit> cuArray) {
			SequenceGenerator.cunits = cuArray;
		}

		public static HashMap<String, ArrayList<MethodCallExpr>> getMapMethodCalls() {
			return methodCalls;
		}

		public static void setMapMethodCalls(HashMap<String, ArrayList<MethodCallExpr>> mapMethodCalls) {
			SequenceGenerator.methodCalls = mapMethodCalls;
		}
	     
	    SequenceGenerator(String in, String className, String function,String out) {
	        this.in = in;
	        this.out = in + "/" + out + ".png";
	        this.className = className;
	        this.method = function;
	        methodMapClass = new HashMap<String, String>();
	        methodCalls = new HashMap<String, ArrayList<MethodCallExpr>>();
	        pumlCode = "@startuml\n";
	    }

	    public void start() throws Exception {
	        cunits = UtilityHelper.getCuArray(in);
	        for (CompilationUnit cu : cunits) {
	            String className = "";
	            List<TypeDeclaration> td = cu.getTypes();
	            for (Node n : td) {
	                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
	                className = coi.getName();
	                for (BodyDeclaration bd : ((TypeDeclaration) coi)
	                        .getMembers()) {
	                    if (bd instanceof MethodDeclaration) {
	                        MethodDeclaration md = (MethodDeclaration) bd;
	                        ArrayList<MethodCallExpr> mcea = new ArrayList<MethodCallExpr>();
	                        for (Object bs : md.getChildrenNodes()) {
	                            if (bs instanceof BlockStmt) {
	                                for (Object es : ((Node) bs)
	                                        .getChildrenNodes()) {
	                                    if (es instanceof ExpressionStmt) {
	                                        if (((ExpressionStmt) (es)).getExpression() instanceof MethodCallExpr) {
	                                            mcea.add( (MethodCallExpr) (((ExpressionStmt) (es)).getExpression()));
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                        methodCalls.put(md.getName(), mcea);
	                        methodMapClass.put(md.getName(), className);
	                    }
	                }
	            }
	        }
	        pumlCode += "actor user #red\n";
	        pumlCode += "user" + " -> " + className + " : " + method + "\n";
	        pumlCode += "activate " + methodMapClass.get(method) + "\n";
	        parse(method);
	        pumlCode += "@enduml";
	        generateDiagram(pumlCode);
	        System.out.println("Plant UML Code:\n" + pumlCode);
	    }

	    private void parse(String callerFunc) {
	        for (MethodCallExpr mce : methodCalls.get(callerFunc)) {
	            String callerClass = methodMapClass.get(callerFunc);
	            String calleeFunc = mce.getName();
	            String calleeClass = methodMapClass.get(calleeFunc);
	            if (methodMapClass.containsKey(calleeFunc)) {
	                pumlCode += callerClass + " -> " + calleeClass + " : "
	                        + mce.toStringWithoutComments() + "\n";
	                pumlCode += "activate " + calleeClass + "\n";
	                parse(calleeFunc);
	                pumlCode += calleeClass + " -->> " + callerClass + "\n";
	                pumlCode += "deactivate " + calleeClass + "\n";
	            }
	        }
	    }
	    
	    private String generateDiagram(String source) throws IOException {

	        OutputStream png = new FileOutputStream(out);
	        SourceStringReader reader = new SourceStringReader(source);
	        String desc = reader.generateImage(png);
	        return desc;

	    }	
	    
	    /*
	     * import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;

public class Test {
    public static void main(String[] k) throws Exception {
    	    	
    	//String[] args= { "seq", "/Users/akshaymishra/Desktop/tests/uml-sequence-test.zip", "Main", "main", "out"};
    	String[] args= { "class", "/Users/akshaymishra/Desktop/tests/uml-parser-test-4.zip", "out"};
    	String extPath=null,inPath=null;    	
        boolean zipped=false;     		  
        
      	if (FilenameUtils.getExtension(args[1]).equals("zip")){
      		zipped=true;
      		extPath = args[1].substring(0, args[1].lastIndexOf("/"));
      		try {
      	        ZipFile zipFile = new ZipFile(args[1]);
      	        zipFile.extractAll(extPath);
      	    } catch (ZipException e) {
      	        e.printStackTrace();
      	    }
      		
      		inPath = extPath + args[1].substring(args[1].lastIndexOf("/"), args[1].lastIndexOf("."));	
      	}      	
      	
      	if (zipped) {
  	        if (args[0].equals("class")) {
  	            ClassGenerator cp = new ClassGenerator(inPath, args[2]);
  	            cp.start();
  	        } 
  	        else if (args[0].equals("seq")) {
  	        	SequenceGenerator sg = new SequenceGenerator(args[1].substring(0, args[1].indexOf(".")) ,args[2],args[3],args[4]);
  	        	sg.start();
  	        } else{
  	            System.out.println("Invalid keyword " + args[0]);
  	        }
      	}
      	else {
      		if (args[0].equals("class")) {
  	            ClassGenerator cp = new ClassGenerator(args[1], args[2]);
  	            cp.start();
  	        } 
  	        else if (args[0].equals("seq")) {
  	        	SequenceGenerator sg = new SequenceGenerator(args[1] ,args[2],args[3],args[4]);
  	        	sg.start();
  	        } else{
  	            System.out.println("Invalid keyword " + args[0]);
  	        }

      	}
      	
	
    }
}


	     * 
	     * */
	    
	    
}
