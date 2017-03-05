import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class Parser {
	final String inDir;
	final String outDir;
	ArrayList<CompilationUnit> cunits;
	HashMap<String, String> mapClassOrInterface;
	String umlString;
	
	
    Parser(String in, String out) {
        this.inDir = in;
        this.outDir = this.outDir + "/" + out + ".png";
    }

	public void triggerParse() throws ParseException, IOException {
		//get list of all compilation units in an array list
		cunits = getCunits(inDir);
		/*for ( CompilationUnit cunit : cunits) {
			System.out.println(cunit);
		}*/
		
        for (CompilationUnit cunit : cunits) {
            List<TypeDeclaration> typesList = cunit.getTypes();
            for (Node node : typesList) {
                ClassOrInterfaceDeclaration cOrI = (ClassOrInterfaceDeclaration) node;
                mapClassOrInterface.put(cOrI.getName(), cOrI.isInterface() ? "INTERFACE" : "CLASS" ); 
            }
        }
        
        for (CompilationUnit cu : cunits)
        	umlString += parseCode(cu);
        
		
		
	}
	
	private String parseCode(CompilationUnit cu) {
		
		String fresult = "", classNm = "", classShrtNm = "", method = "", field = "", addition = ",";
		ArrayList<String> makeFieldPublic = new ArrayList<String>();
        List<TypeDeclaration> ltd = cu.getTypes();
		
		return "";
		
	}
	
	//http://stackoverflow.com/questions/32178349/parse-attributes-from-java-files-using-java-parser
	private ArrayList<CompilationUnit> getCunits(String inDir) throws ParseException, IOException {
		ArrayList<CompilationUnit> cunitsArray = new ArrayList<CompilationUnit>();
		File folder = new File(inDir);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".java")) {
				//System.out.println(listOfFiles[i].getName());
				FileInputStream fis = new FileInputStream(listOfFiles[i]);
				//System.out.println(fis instanceof FileInputStream);
				CompilationUnit cunit;
				//System.out.println(cunit instanceof CompilationUnit);
                try {
                    cunit = JavaParser.parse(fis);
                    cunitsArray.add(cunit);
                } finally {
                	fis.close();
                }
			}		
		}
		return cunitsArray;
	}
	
	
	
	
	
	
}
