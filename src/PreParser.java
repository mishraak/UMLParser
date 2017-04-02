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
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;

public class PreParser {
	final String inDir;
	final String outDir;
	ArrayList<CompilationUnit> cunits;
	Map<String, Boolean> mapClassOrInterface = new HashMap<String, Boolean>();
	StringBuffer umlString = new StringBuffer();

	PreParser(String in, String out) {
		this.inDir = in;
		this.outDir = this.outDir + "/" + out + ".png";
	}
	
	public static Map<String, Boolean> getMapClassOrInterface() {
		return mapClassOrInterface;
		
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
			// System.out.println(cunit);
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
			astString = Parser.parseCode(cu);

		// System.out.println(astString);
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
				} finally {
					filestream.close();
				}
			}
		}
		return cunitsArray;
	}

}
