import java.util.ArrayList;
import com.github.javaparser.ast.CompilationUnit;

public class Parser {
	final String inDir;
	final String outDir;
	ArrayList<CompilationUnit> cuArray;
	
    Parser(String in, String out) {
        this.inDir = in;
        this.outDir = this.outDir + "/" + out + ".png";
    }

	public void parse() {
		
	}
}
