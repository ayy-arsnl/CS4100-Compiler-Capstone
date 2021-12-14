//import ADT.SymbolTable;
//import ADT.Lexical;
import java.io.IOException;

import ADT.*;

/**
 *
 * @author abrouill FALL 2021
 */
public class mainCODEGEN {

    public static void main(String[] args) throws IOException{
        String currentDir = System.getProperty("user.dir");
        String filePath = currentDir + "/CodeGenInputs/CodeGenFULL-FA21.txt";
        //String filePath = "d:\\BadSyntax-1-ASP21.txt";
        System.out.println("Parsing "+filePath);
        boolean traceon = false; //true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        
        System.out.println("Done.");
    }

}
