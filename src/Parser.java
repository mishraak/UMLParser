import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class Parser {
	String inDir;
	String outDir;
	ArrayList<CompilationUnit> cunits;
	static Map<String, String> mapOfClass = new HashMap<String, String>();
	static Map<String, Boolean> mapClassOrInterface = new HashMap<String, Boolean>();
	static String umlString ;
	
	public Parser(String in, String out){
		inDir=in;
		outDir=out;
	}
	
	public static String parseCode(CompilationUnit cu) {
		String fresult 		= new String(),
			   extras		= new String(),	
			   classNm 		= new String(),
			   functions 	= new String(), 
			   field 		= new String(), 
			   addition 	= new String(","),
			   classNmMap   = new String(),
			   paramCls     = new String(),
			   paramNm	    = new String();
		
		Parameter param;
		List<String> publicFields = new ArrayList<String>();
		
		List<TypeDeclaration> ltd = cu.getTypes();
		Node node = ltd.get(0);
		ClassOrInterfaceDeclaration corid = (ClassOrInterfaceDeclaration) node;

		if (!corid.isInterface()) 
			 classNm = "[";
        else 
        	 classNm =  "[<<interface>>";

		classNm 	 = classNm + corid.getName();
		classNmMap   = corid.getName();
		
        boolean isThereAnotherParam = false;			//just to adjust for semicolons
        for (BodyDeclaration bodyDecl : ((TypeDeclaration) node).getMembers()) {
          if (bodyDecl instanceof ConstructorDeclaration) {
        	ConstructorDeclaration construcDecl = (ConstructorDeclaration) bodyDecl;
        	boolean isPublic = construcDecl.getDeclarationAsString().startsWith("public"), 
        			isInterface = corid.isInterface();
        	
        	if ( isPublic && !isInterface) {
        		if (isThereAnotherParam){   
        			functions = functions + ";";
	    			functions = functions + "+ ";
	    			functions = functions + construcDecl.getName();
	    			functions = functions + "(";
        			
                for (Object childrenNode : construcDecl.getChildrenNodes()) {
                    if (childrenNode instanceof Parameter) {
                         param = (Parameter) childrenNode;
                         paramCls = param.getType().toString();
                         paramNm = param.getChildrenNodes().get(0).toString();
                         //System.out.println(paramNm);
                        functions = functions + paramNm + " : " + paramCls;
                        if (mapClassOrInterface.containsKey(paramCls) 			//check for the existence in keys
                        		&& !(mapClassOrInterface.get(classNmMap))) {		//check using separately stored class name, shouldn't repeat
                        	extras = extras + "["; 
                        	extras = extras + classNmMap;
                        	extras = extras + "] uses -.->";
                        	
                            if (mapClassOrInterface.get(paramCls)) {
                            	extras = extras + "[<<interface>>;"; 
                            	extras = extras + paramCls;
                            	extras = extras+ "]";
                            }
                            else {
                            	extras = extras + "["; 
                            	extras = extras + paramCls; 
                            	extras = extras + "]";
                            }
                        }
                        extras = extras + ",";
                    }
                }           
	            functions = functions + ")";
	            //System.out.println(functions);
	            isThereAnotherParam = true;
        		}
        	}
          }
        }

        for (BodyDeclaration bodyDecl : ((TypeDeclaration) node).getMembers()) {
        	 if (bodyDecl instanceof MethodDeclaration) {
        		 MethodDeclaration methodDecl = (MethodDeclaration) bodyDecl;
        		 boolean isPublic = methodDecl.getDeclarationAsString().startsWith("public"), isInterface=corid.isInterface();
        		 if (isPublic && !isInterface) {
        			 //check for getters and setters
        			 boolean isGetter=methodDecl.getName().startsWith("get");
        			 boolean isSetter=methodDecl.getName().startsWith("set");
        			 if ( isGetter || isSetter) {
        				 publicFields.add(methodDecl.getName().substring(3)); //3 for the attribute
        				 //System.out.println(methodDecl.getName().substring(3));
        			 }
        			 else {
        				 if (isThereAnotherParam){   
        	        			functions = functions + ";"; 
        	        			functions = functions + "+ "; 
        	        			functions = functions + methodDecl.getName();
        	        			functions = functions + "(";
        	        			
        	        			for (Object childrenNode : methodDecl.getChildrenNodes()) {
                                    if (childrenNode instanceof Parameter) {
                                        Parameter paramCast = (Parameter) childrenNode;
                                        String paramClass = paramCast.getType().toString();
                                        String paramName = paramCast.getChildrenNodes().get(0).toString();
                                        
                                        functions = functions +  paramName + " : " + paramClass;
                                        if (mapClassOrInterface.containsKey(paramClass)	//check for existence in map
                                                && !mapClassOrInterface.get(classNmMap)) {	//check if it is interface
                                        	extras = extras + "["; 
                                        	extras = extras + classNmMap;
                                        	extras = extras + "] uses -.->";
                                            if (mapClassOrInterface.get(paramClass)){
                                            	extras = extras + "[<<interface>>;";
                                            	extras = extras + paramClass; 
                                            	extras = extras + "]";
                                            }
                                            else{
                                            	extras = extras + "[" ; 
                                            	extras = extras + paramClass; 
                                            	extras = extras + "]";
                                            }
                                        }
                                        extras = extras+ ",";
                                    } else {
                                        String methodBody[] = childrenNode.toString().split(" ");
                                        for (String mb : methodBody) {
                                            if (mapClassOrInterface.containsKey(mb)
                                                    && !mapClassOrInterface.get(classNmMap)) {
                                            	extras = extras + "[" + classNmMap
                                                        + "] uses -.->";
                                                if (mapClassOrInterface.get(mb))
                                                	extras += "[<<interface>>;" + mb
                                                            + "]";
                                                else
                                                	extras += "[" + mb + "]";
                                                	extras += ",";
                                            }
                                        }
                                    }
        	        			}
        				   }
        			  }
        		 }
        	 }
        	 
        	 //System.out.println(extras);
		}
        
        

        // Parsing Fields
        boolean nextField = false;
        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) { 
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = ((FieldDeclaration) bd);
                String fieldScope = aToSymScope(
                        bd.toStringWithoutComments().substring(0,
                                bd.toStringWithoutComments().indexOf(" ")));
                String fieldClass = changeBrackets(fd.getType().toString());
                String fieldName = fd.getChildrenNodes().get(1).toString();
                if (fieldName.contains("="))
                    fieldName = fd.getChildrenNodes().get(1).toString()
                            .substring(0, fd.getChildrenNodes().get(1)
                                    .toString().indexOf("=") - 1);
                // Change scope of getter, setters
                if (fieldScope.equals("-")
                        && publicFields.contains(fieldName.toLowerCase())) {
                    fieldScope = "+";
                }
                String getDepen = "";
                boolean getDepenMultiple = false;
                if (fieldClass.contains("(")) {
                    getDepen = fieldClass.substring(fieldClass.indexOf("(") + 1,
                            fieldClass.indexOf(")"));
                    getDepenMultiple = true;
                } else if (mapClassOrInterface.containsKey(fieldClass)) {
                    getDepen = fieldClass;
                }
                if (getDepen.length() > 0 && mapClassOrInterface.containsKey(getDepen)) {
                    String connection = "-";

                    if (mapOfClass
                            .containsKey(getDepen + "-" + classNmMap)) {
                        connection = mapOfClass
                                .get(getDepen + "-" + classNmMap);
                        if (getDepenMultiple)
                            connection = "*" + connection;
                        mapOfClass.put(getDepen + "-" + classNmMap,
                                connection);
                    } else {
                        if (getDepenMultiple)
                            connection += "*";
                        mapOfClass.put(classNmMap + "-" + getDepen,
                                connection);
                    }
                }
                if (fieldScope == "+" || fieldScope == "-") {
                    if (nextField)
                        field += "; ";
                    field += fieldScope + " " + fieldName + " : " + fieldClass;
                    nextField = true;
                }
            }

        }
        
     // Check extends, implements
        if (corid.getExtends() != null) {
        	addition += "[" + classNmMap + "] " + "-^ " + corid.getExtends();
        	addition += ",";
        }
        if (corid.getImplements() != null) {
            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) corid
                    .getImplements();
            for (ClassOrInterfaceType intface : interfaceList) {
            	addition += "[" + classNmMap + "] " + "-.-^ " + "["
                        + "<<interface>>;" + intface + "]";
            	
        
        return fresult;
	}
	
	private static String changeBrackets(String foo) {
        foo = foo.replace("[", "(");
        foo = foo.replace("]", ")");
        foo = foo.replace("<", "(");
        foo = foo.replace(">", ")");
        return foo;
    }

    private static String aToSymScope(String stringScope) {
        switch (stringScope) {
        case "private":
            return "-";
        case "public":
            return "+";
        default:
            return "";
        }
    }
	
	
	
	public void triggerParse() throws ParseException, IOException {
		String astString = "";
		// get list of all compilation units in an array list
		cunits = getCunits(inDir);
		/*
		 * for ( CompilationUnit cunit : cunits) { System.out.println(cunit); }
		*/
		
		// Class or Interface
		for (CompilationUnit cunit : cunits) {
			 //System.out.println(cunit);
			List<TypeDeclaration> typesList = cunit.getTypes();
			for (Node node : typesList) {
				// System.out.println(node);
				ClassOrInterfaceDeclaration cOrI = (ClassOrInterfaceDeclaration) node;
				// System.out.println(cOrI.isInterface());
				mapClassOrInterface.put(cOrI.getName(), cOrI.isInterface());
			}
		}

		// Each compilation unit undergoes parsing now
		for (CompilationUnit cu : cunits)
			umlString += Parser.parseCode(cu);

		 System.out.println(umlString);
	}
			
	// http://stackoverflow.com/questions/32178349/parse-attributes-from-java-files-using-java-parser
	private ArrayList<CompilationUnit> getCunits(String inDir) throws ParseException, IOException {
		ArrayList<CompilationUnit> cunitsArray = new ArrayList<CompilationUnit>();
		File folder = new File(inDir);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".java")) {
				// System.out.println(listOfFiles[i].getName());
				FileInputStream filestream = new FileInputStream(listOfFiles[i]);
				
				// System.out.println(fis instanceof FileInputStream);
				// System.out.println(cunit instanceof CompilationUnit);
				
				try {
					cunitsArray.add(JavaParser.parse(filestream));
				} catch (com.github.javaparser.ParseException e) {
					e.printStackTrace();
				}
				finally {
					filestream.close();
				}
			}
		}
		return cunitsArray;
	}
	
}
