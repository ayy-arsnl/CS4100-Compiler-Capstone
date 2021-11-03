//import ADT.SymbolTable;
//import ADT.Lexical;
import java.io.IOException;

import ADT.*;

/**
 *
 * @author abrouill SPRING 2021
 */
public class mainHW4 {

    public static void main(String[] args) throws IOException {
        String fileAndPath = "d:\\LexicalTestFA21.txt";
        System.out.println("Lexical for " + fileAndPath);
        boolean traceOn = true;
        // Create a symbol table to store appropriate3 symbols found
        SymbolTable symbolList;
        symbolList = new SymbolTable(150);
        Lexical myLexer = new Lexical(fileAndPath, symbolList, traceOn);
        Lexical.token currToken;
        currToken = myLexer.GetNextToken();
        while (currToken != null) {
            System.out.println("\t" + currToken.mnemonic + " | \t" + String.format("%04d", currToken.code)
                    + " | \t" + currToken.lexeme);
            currToken = myLexer.GetNextToken();
        }
        symbolList.PrintSymbolTable("/home/alexander/Documents/UCCS/Fall 2021/CS4100/CS4100 Compiler Capstone/outputs/FallsymbolTableLex.txt");
        System.out.println("Done.");
    }

}
