import java.io.File;
import java.util.ArrayList;
import com.github.javaparser.ast.CompilationUnit;

public class Parser {
	final String inDir;
	final String outDir;
	ArrayList<CompilationUnit> cunits;
	
    Parser(String in, String out) {
        this.inDir = in;
        this.outDir = this.outDir + "/" + out + ".png";
    }

	public void triggerParse() {
		//get list of all compilation units in an array list
		cunits = getCunits(inDir);
	}
	
	//http://stackoverflow.com/questions/32178349/parse-attributes-from-java-files-using-java-parser
	private ArrayList<CompilationUnit> getCunits(String inDir) {
		ArrayList<CompilationUnit> cunitsArray = new ArrayList<CompilationUnit>();
		File folder = new File(inDir);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        System.out.println("File " + listOfFiles[i].getName());
	      } else if (listOfFiles[i].isDirectory()) {
	        System.out.println("Directory " + listOfFiles[i].getName());
	      }
		}
		return cunitsArray;
	}
}
