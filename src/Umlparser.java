public class Umlparser {

    public static void main(String[] args) throws Exception {
    	
    	Parser pe = new Parser("/Users/akshaymishra/Desktop/test/uml-parser-test-1", "myDiag");
    	pe.start();
    	/*
        if (args[0].equals("class")) {
            ParseEngine pe = new ParseEngine(args[1], args[2]);
            pe.start();
        } else if (args[0].equals(("seq"))) {
            ParseSeqEngine pse = new ParseSeqEngine(args[1], args[2], args[3], args[4]);
            pse.start();
        } else {
            System.out.println("Invalid keyword " + args[0]);
        }
    */
    }
}

