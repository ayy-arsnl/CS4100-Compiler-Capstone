import ADT.SymbolTable;
import ADT.Lexical;
import ADT.*;

/**
 *
 * @author abrouill SPRING 2021
 */
public class mainP3A {

    public static void main(String[] args) {
        String filePath = "/home/alexander/Documents/UCCS/Fall 2021/CS4100/CS4100 Compiler Capstone/SyntaxTestFA21.txt";
        boolean traceon = true;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        System.out.println("Done.");
    }

}
