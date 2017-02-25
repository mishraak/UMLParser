import java.io.IOException;

import com.github.javaparser.ParseException;

public class Driver {
	public static void main(String[] args) throws ParseException, IOException {
		Parser parser = new Parser("/Users/akshaymishra/Desktop/input", "outDiagram");
		parser.triggerParse();
	}
}
