import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ParsingHelper {
	
	public static Object[] constructorParser(Node node, String additions, String methods, String classShortName, ClassOrInterfaceDeclaration coi, Boolean nextParam){
        
        for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {	
            // Get Methods
            if (bd instanceof ConstructorDeclaration) {
                ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
                if (cd.getDeclarationAsString().startsWith("public")
                        && !coi.isInterface()) {
                    if (nextParam)
                        methods += ";";
                    methods += "+ " + cd.getName() + "(";
                    for (Object gcn : cd.getChildrenNodes()) {
                        if (gcn instanceof Parameter) {
                            Parameter paramCast = (Parameter) gcn;
                            String paramClass = paramCast.getType().toString();
                            String paramName = paramCast.getChildrenNodes()
                                    .get(0).toString();
                            methods += paramName + " : " + paramClass;
                            if (Parser.classOrIntMap.containsKey(paramClass)
                                    && !Parser.classOrIntMap.get(classShortName)) {
                                additions += "[" + classShortName
                                        + "] uses -.->";
                                if (Parser.classOrIntMap.get(paramClass))
                                    additions += "[<<interface>>;" + paramClass
                                            + "]";
                                else
                                    additions += "[" + paramClass + "]";
                            }
                            additions += ",";
                        }
                    }
                    methods += ")";
                    nextParam = true;
                }
            }
        }
        
        return new Object[]{methods, additions, nextParam};   
	}
	
	
	public static String[] methodParser(Node node, String additions, String methods, String classShortName, ClassOrInterfaceDeclaration coi, Boolean nextParam){
	     
	 for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {
         if (bd instanceof MethodDeclaration) {
             MethodDeclaration md = ((MethodDeclaration) bd);
             // Get only public methods
             if (md.getDeclarationAsString().startsWith("public")
                     && !coi.isInterface()) {
                 // Identify Setters and Getters
                 if (md.getName().startsWith("set")
                         || md.getName().startsWith("get")) {
                     String varName = md.getName().substring(3);
                     Parser.makeFieldPublic.add(varName.toLowerCase());
                 } else {
                     if (nextParam)
                         methods += ";";
                     methods += "+ " + md.getName() + "(";
                     for (Object gcn : md.getChildrenNodes()) {
                         if (gcn instanceof Parameter) {
                             Parameter paramCast = (Parameter) gcn;
                             String paramClass = paramCast.getType()
                                     .toString();
                             String paramName = paramCast.getChildrenNodes()
                                     .get(0).toString();
                             methods += paramName + " : " + paramClass;
                             if (Parser.classOrIntMap.containsKey(paramClass)
                                     && !Parser.classOrIntMap.get(classShortName)) {
                                 additions += "[" + classShortName
                                         + "] uses -.->";
                                 if (Parser.classOrIntMap.get(paramClass))
                                     additions += "[<<interface>>;"
                                             + paramClass + "]";
                                 else
                                     additions += "[" + paramClass + "]";
                             }
                             additions += ",";
                         } else {
                             String methodBody[] = gcn.toString().split(" ");
                             for (String foo : methodBody) {
                                 if (Parser.classOrIntMap.containsKey(foo)
                                         && !Parser.classOrIntMap.get(classShortName)) {
                                     additions += "[" + classShortName
                                             + "] uses -.->";
                                     if (Parser.classOrIntMap.get(foo))
                                         additions += "[<<interface>>;" + foo
                                                 + "]";
                                     else
                                         additions += "[" + foo + "]";
                                     additions += ",";
                                 }
                             }
                         }
                     }
                     methods += ") : " + md.getType();
                     nextParam = true;
                 }
             }
         }
     }
	 return new String[]{methods, additions};  
	}
	public static String fieldParser(Node node, String classShortName, String fields){
		
	    // Parsing Fields
	    boolean nextField = false;
	    for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) { 
	        if (bd instanceof FieldDeclaration) {
	            FieldDeclaration fd = ((FieldDeclaration) bd);
	            String fieldScope = Helper.aToSymScope(
	                    bd.toStringWithoutComments().substring(0, bd.toStringWithoutComments().indexOf(" "))); 
	            String fieldClass = Helper.changeBrackets(fd.getType().toString());
	            String fieldName = fd.getChildrenNodes().get(1).toString();
	            if (fieldName.contains("="))
	                fieldName = fd.getChildrenNodes().get(1).toString()
	                        .substring(0, fd.getChildrenNodes().get(1)
	                                .toString().indexOf("=") - 1);
	            // Change scope of getter, setters
	            if (fieldScope.equals("-")
	                    && Parser.makeFieldPublic.contains(fieldName.toLowerCase())) {
	                fieldScope = "+";
	            }
	            String getDepen = "";
	            boolean getDepenMultiple = false;
	            if (fieldClass.contains("(")) {
	                getDepen = fieldClass.substring(fieldClass.indexOf("(") + 1,
	                        fieldClass.indexOf(")"));
	                getDepenMultiple = true;
	            } else if (Parser.classOrIntMap.containsKey(fieldClass)) {
	                getDepen = fieldClass;
	            }
	            if (getDepen.length() > 0 && Parser.classOrIntMap.containsKey(getDepen)) {
	                String connection = "-";
	
	                if (Parser.hierarchyMap.containsKey(getDepen + "-" + classShortName)) {
	                    connection = Parser.hierarchyMap.get(getDepen + "-" + classShortName);
	                    if (getDepenMultiple)
	                        connection = "*" + connection;
	                    Parser.hierarchyMap.put(getDepen + "-" + classShortName, connection);
	                } else {
	                    if (getDepenMultiple)
	                        connection += "*";
	                    Parser.hierarchyMap.put(classShortName + "-" + getDepen, connection);
	                }
	            }
	            if (fieldScope == "+" || fieldScope == "-") {
	                if (nextField)
	                    fields += "; ";
	                fields += fieldScope + " " + fieldName + " : " + fieldClass;
	                nextField = true;
	            }
	        }
	
	    }
	    return fields;  
	}
	
	public static String inheritanceChecker(ClassOrInterfaceDeclaration coi, String additions, String classShortName){
	    if (coi.getExtends() != null) {
	        additions += "[" + classShortName + "] " + "-^ " + coi.getExtends();
	        additions += ",";
	    }
	    return additions;
	}
	
	public static String implementationChecker(ClassOrInterfaceDeclaration coi, String additions, String classShortName){
		
	    if (coi.getImplements() != null) {
	        List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) coi.getImplements();
	        for (ClassOrInterfaceType intface : interfaceList) {
	            additions += "[" + classShortName + "] " + "-.-^ " + "["
	                    + "<<interface>>;" + intface + "]";
	            additions += ",";
	        }
	    }
	    return additions;
	}
	
	public static String combiner(String result, String className, String additions, String fields, String methods){
	    result += className;
	    if (!fields.isEmpty()) {
	        result += "|" + Helper.changeBrackets(fields);
	    }
	    if (!methods.isEmpty()) {
	        result += "|" + Helper.changeBrackets(methods);
	    }
	    result += "]";
	    result += additions;
	    
	    return result;
	}
	
}
