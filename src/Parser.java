import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;

public class Parser {
	final String inDir;
	final String outDir;
	ArrayList<CompilationUnit> cunits;
	Map<String, Boolean> mapClassOrInterface = new HashMap<String, Boolean>();
	StringBuffer umlString=new StringBuffer();

	Parser(String in, String out) {
		this.inDir = in;
		this.outDir = this.outDir + "/" + out + ".png";
	}

	public void triggerParse() throws ParseException, IOException {
		String astString="";
		//get list of all compilation units in an array list
		cunits = getCunits(inDir);
		/*for ( CompilationUnit cunit : cunits) {
			System.out.println(cunit);
		}*/
		
		//Class or Interface
		for (CompilationUnit cunit : cunits) {
			//System.out.println(cunit);
			List<TypeDeclaration> typesList = cunit.getTypes();
			for (Node node : typesList) {
				//System.out.println(node);
				ClassOrInterfaceDeclaration cOrI = (ClassOrInterfaceDeclaration) node;
				//System.out.println(cOrI.isInterface());
				mapClassOrInterface.put(cOrI.getName(), cOrI.isInterface());
			}
		}
		
		
		//Each compilation unit undergoes parsing now
		for (CompilationUnit cu : cunits)	
			astString = parseCode(cu);
			
		//System.out.println(x);
	}
	

	
	 	private String parseCode(CompilationUnit cu) {

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
		
		List<String> makeFieldPublic = new ArrayList<String>();
		
		List<TypeDeclaration> ltd = cu.getTypes();
		Node node = ltd.get(0);
		ClassOrInterfaceDeclaration corid = (ClassOrInterfaceDeclaration) node;

		if (!corid.isInterface()) 
			 classNm = "[";
        else 
        	 classNm =  "[<<interface>>";
        
		classNm 	+= corid.getName();
		classNmMap   = corid.getName();
		
		
        boolean isThereAnotherParam = false;
        for (BodyDeclaration bodyDecl : ((TypeDeclaration) node).getMembers()) {	
        	ConstructorDeclaration construcDecl = (ConstructorDeclaration) bodyDecl;
        	boolean isPublic = construcDecl.getDeclarationAsString().startsWith("public"), isInterface = corid.isInterface();
        	
        	if ( isPublic && !isInterface) {
        		if (isThereAnotherParam){    //true for the first time
        			
        			functions = functions + ";";
        			functions = functions + "+ ";
        			functions = functions + construcDecl.getName();
        			functions = functions + "(";
        			
                    for (Object childrenNode : construcDecl.getChildrenNodes()) {
                    	
                        if (childrenNode instanceof Parameter) {
                             param = (Parameter) childrenNode;
                             paramCls = param.getType().toString();
                             paramNm = param.getChildrenNodes().get(0).toString();
                             
                            functions = functions+ paramNm + " : " + paramCls;
                            if (mapClassOrInterface.containsKey(paramCls) 			//check for the existence in keys
                            		&& !mapClassOrInterface.get(classNmMap)) {		//check using separately stored class name, shouldn't repeat
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
                   
                    functions += ")";
                    isThereAnotherParam = true;
        		}
        	}	
        }
        
        for (BodyDeclaration bodyDecl : ((TypeDeclaration) node).getMembers()) {
        	 if (bodyDecl instanceof MethodDeclaration) {
        		 MethodDeclaration methodDecl = (MethodDeclaration) bodyDecl;
        		 boolean isPublic = methodDecl.getDeclarationAsString().startsWith("public"), isInterface=corid.isInterface();
        		 if (isPublic && !isInterface) {
        			 //check for getters and setters
        			 if (methodDecl.getName().startsWith("set") || methodDecl.getName().startsWith("get")) {
        				 
        			 }
        		 }
        		 
        	 }
        }
        
			return umlString.toString();
		}
		
			
			
			
			

	//http://stackoverflow.com/questions/32178349/parse-attributes-from-java-files-using-java-parser
	private ArrayList<CompilationUnit> getCunits(String inDir) throws ParseException, IOException {
		ArrayList<CompilationUnit> cunitsArray = new ArrayList<CompilationUnit>();
		File folder = new File(inDir);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".java")) {
				//System.out.println(listOfFiles[i].getName());
				FileInputStream filestream = new FileInputStream(listOfFiles[i]);
				//System.out.println(fis instanceof FileInputStream);
				//System.out.println(cunit instanceof CompilationUnit);
				try {
					cunitsArray.add(JavaParser.parse(filestream));
				} finally {
					filestream.close();
				}
			}		
		}
		return cunitsArray;
	}






}
