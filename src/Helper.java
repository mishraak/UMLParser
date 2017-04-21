import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;


public class Helper {
	
	public static String removeDups(String inString){
        String[] codeLines = inString.split(",");
        String[] uniqueCodeLines = new LinkedHashSet<String>(
                Arrays.asList(codeLines)).toArray(new String[0]);
        String result = String.join(",", uniqueCodeLines);
        return result;
	} 
	
	public static ArrayList<CompilationUnit> getCuArray(String in) throws Exception {
        File folder = new File(in);
        ArrayList<CompilationUnit> cunits = new ArrayList<CompilationUnit>();
        for (final File f : folder.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".java")) {
                FileInputStream ins = new FileInputStream(f);
                CompilationUnit cu;
                try {
                    cu = JavaParser.parse(ins);
                    cunits.add(cu);
                } finally {
                    ins.close();
                }
            }
        }
        return cunits;
    }
	
	public static void buildInterfaceClassMap(ArrayList<CompilationUnit> cunits) {
        for (CompilationUnit cu : cunits) {
            List<TypeDeclaration> cl = cu.getTypes();
            for (Node node : cl) {
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) node;
                Parser.getClassOrIntMap().put(coi.getName(), coi.isInterface()); // false is class, true is interface
                                     
            }
        }
	}
	
	public static void buildAst(ArrayList<CompilationUnit> cunits){
        for (CompilationUnit cu : cunits) { 
        	Parser.astree += Parser.parser(cu); 
        }
	}
	
    public static String changeBrackets(String foo) {
        foo = foo.replace("[", "(");
        foo = foo.replace("]", ")");
        foo = foo.replace("<", "(");
        foo = foo.replace(">", ")");
        return foo;
    }

    public static String aToSymScope(String stringScope) {
        switch (stringScope) {
        case "private":
            return "-";
        case "public":
            return "+";
        default:
            return "";
        }
    }
	
    public static String parseAdditions() {
        String result = "";
        Set<String> keys = Parser.hierarchyMap.keySet(); // get all keys
        for (String i : keys) {
            String[] classes = i.split("-");
            if (Parser.classOrIntMap.get(classes[0]))
                result += "[<<interface>>;" + classes[0] + "]";
            else
                result += "[" + classes[0] + "]";
            result += Parser.hierarchyMap.get(i); // Add connection
            if (Parser.classOrIntMap.get(classes[1]))
                result += "[<<interface>>;" + classes[1] + "]";
            else
                result += "[" + classes[1] + "]";
            result += ",";
        }
        return result;
    }
	

    public static Boolean generatePNG(String grammar, String outPath) {

        try {
            String webLink = "https://yuml.me/diagram/plain/class/draw/" + grammar + ".png";
            URL url = new URL(webLink);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json"); 
            OutputStream outputStream=null;

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException(
                        "Failed : HTTP error code : " + conn.getResponseCode());
            }
            outputStream = new FileOutputStream(new File(outPath));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = conn.getInputStream().read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
}
