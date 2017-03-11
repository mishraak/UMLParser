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
import com.github.javaparser.ast.body.TypeDeclaration;

public class Parser {
	final String inDir;
	final String outDir;
	ArrayList<CompilationUnit> cunits;
	Map<String, String> mapClassOrInterface = new HashMap<String, String>();
	StringBuffer umlString=new StringBuffer();

	Parser(String in, String out) {
		this.inDir = in;
		this.outDir = this.outDir + "/" + out + ".png";
	}

	public void triggerParse() throws ParseException, IOException {
		//get list of all compilation units in an array list
		this.cunits = getCunits(inDir);
		/*for ( CompilationUnit cunit : cunits) {
			System.out.println(cunit);
		}*/
		
		//Class or Interface
		for (CompilationUnit cunit : cunits) {
			umlString.append("[ ");
			//System.out.println(cunit);
			List<TypeDeclaration> typesList = cunit.getTypes();
			for (Node node : typesList) {
				//System.out.println(node);
				ClassOrInterfaceDeclaration cOrI = (ClassOrInterfaceDeclaration) node;
				//System.out.println(cOrI.isInterface());
				mapClassOrInterface.put(cOrI.getName(), (cOrI.isInterface() ? "INTERFACE" : "CLASS") ); 
			}
		}
		
		//Each compilation unit undergoes parsing now
		for (CompilationUnit cu : cunits)	
			umlString.append(parseCode(cu));	
	}
	
	private String parseCode(CompilationUnit cu) {
        ArrayList classNames = getClasses(cu);
	}
	
	private static ArrayList<String> getClasses(CompilationUnit cu)
    {
        ArrayList classList = new ArrayList();
        for (TypeDeclaration typeDec : cu.getTypes()) {
            if(typeDec instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration) typeDec).isInterface())
            {
                classList.add(typeDec.getName());
            }
        }
        return classList;
    }

	/*
	 	private String parseCode(CompilationUnit cu) {

		StringBuffer fresult 	= new StringBuffer(), 
					 classNm 	= new StringBuffer(),
					 method 	= new StringBuffer(), 
					 field 		= new StringBuffer(), 
					 addition 	= new StringBuffer(",");
		
		String classNmMap="";
		
		List<String> makeFieldPublic = new ArrayList<String>();
		
		List<TypeDeclaration> ltd = cu.getTypes();
		Node node = ltd.get(0);
		ClassOrInterfaceDeclaration corid = (ClassOrInterfaceDeclaration) node;
		if (corid.isInterface())
			umlString.append("<<INTERFACE>>");
		else 
			umlString.append("CLASS");
		
			classNm.append(corid.getName());
			classNmMap=corid.getName();
			return umlString.toString() + " ]";
		}
	 */
		
			
			
			
			

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
