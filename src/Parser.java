import java.io.*;
import java.util.*;
import java.lang.*;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class Parser {
    final String in;
    final String out;
    public static HashMap<String, Boolean> classOrIntMap;
    public static HashMap<String, String> hierarchyMap;
    public static String astree;
    public static ArrayList<CompilationUnit> cunits;
    
    public static ArrayList<String> makeFieldPublic = new ArrayList<String>();

    Parser(String inP, String outF) {
        astree = "";
        in = inP;
        out = in + "/" + outF + ".png";
        hierarchyMap = new HashMap<String, String>();
        classOrIntMap = new HashMap<String, Boolean>();
        makeFieldPublic = new ArrayList<String>();
    }
    
    public static Map<String, Boolean> getClassOrIntMap(){
    	return classOrIntMap;
    }

    public void start() throws Exception {
    	
		cunits = Helper.getCuArray(in);
        Helper.buildInterfaceClassMap(cunits);
        Helper.buildAst(cunits);
        astree += Helper.parseAdditions();
        astree = Helper.removeDups(astree);
        
        System.out.println(astree);
        Helper.generatePNG(astree, out);
    }


    public static String parser(CompilationUnit cu) {
        String result = "";
        String className = "";
        String classShortName = "";
        String methods = "";
        String fields = "";
        String additions = ",";


        List<TypeDeclaration> ltd = cu.getTypes();
        Node node = ltd.get(0); // assuming no nested classes

        // Get className
        ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) node;
        if (coi.isInterface()) {
            className = "[" + "<<interface>>;";
        } else {
            className = "[";
        }
        className += coi.getName();
        classShortName = coi.getName();
        
        boolean nextParam = false; //just to adjust semicolons
        
        // Parsing Constructors
        methods += (String) ParsingHelper.constructorParser(node, additions,  methods, classShortName, coi, nextParam )[0];
        additions += (String) ParsingHelper.constructorParser(node, additions,  methods, classShortName, coi, nextParam )[1];
        nextParam = (boolean) ParsingHelper.constructorParser(node, additions,  methods, classShortName, coi, nextParam )[2];
        
        //Parsing Methods 
        methods += ParsingHelper.methodParser(node, additions,  methods, classShortName, coi, nextParam )[0];
        additions += ParsingHelper.methodParser(node, additions,  methods, classShortName, coi, nextParam )[1];
       
        //Parsing Fields
        fields=ParsingHelper.fieldParser( node, classShortName, fields);

        // Check extends
        additions+=ParsingHelper.inheritanceChecker( coi,  additions,  classShortName);
        
        // Check  implements
        additions+=ParsingHelper.implementationChecker( coi, additions, classShortName);
        
        // Combine className, methods and fields
        result+=ParsingHelper.combiner( result,  className,  additions,  fields,  methods);
        return result;
    }



}
