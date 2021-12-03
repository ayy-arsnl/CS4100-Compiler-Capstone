import ADT.SymbolTable;
import ADT.Lexical;
import ADT.*;

/**
 *
 * @author abrouill SPRING 2021
 */
public class mainP3A {

    public static void main(String[] args) {

        // Laptop: /home/alexander/Documents/UCCS/cs4100/CS4100-Compiler-Capstone/
        // Desktop: /home/alexander/Documents/UCCS/Fall 2021/CS4100/CS4100 Compiler Capstone/
        System.out.println("Alexander Urquhart\nCS4100 Fall 2021");
        String filePath = "/home/alexander/Documents/UCCS/cs4100/CS4100-Compiler-Capstone/SyntaxMinimumTestFA21.txt";
        boolean traceon = true;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        System.out.println("Done.");

        System.out.println("----------Good Syntax Test File----------");
        String goodTestPath = "/home/alexander/Documents/UCCS/cs4100/CS4100-Compiler-Capstone/GoodSyntaxAFA21.txt";
        Syntactic goodTestParser = new Syntactic(goodTestPath, traceon);
        goodTestParser.parse();
        System.out.println("Done.");

        System.out.println("----------Bad Syntax 1 Test File----------");
        String badTest1Path = "/home/alexander/Documents/UCCS/cs4100/CS4100-Compiler-Capstone/BadSyntax-1-AFA21.txt";
        Syntactic badTest1Parser = new Syntactic(badTest1Path, traceon);
        badTest1Parser.parse();
        System.out.println("Done.");

        System.out.println("----------Bad Syntax 2 Test File----------");
        String badTest2Path = "/home/alexander/Documents/UCCS/cs4100/CS4100-Compiler-Capstone/BadSyntax-2-AFA21.txt";
        Syntactic badTest2Parser = new Syntactic(badTest2Path, traceon);
        badTest2Parser.parse();
        System.out.println("Done.");
    }

}
