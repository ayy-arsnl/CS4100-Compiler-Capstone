/*
SAMPLE syntactic CODE FOR FALL 21 Compiler Class.
See TEMPLATE at end of this file for the framework to be used for
ALL non-terminal methods created.

Two methods shown below are added to LEXICAL, where reserve and mnemonic
tables are accessible:
 
public int codeFor(String mnemonic){
    return mnemonics.LookupName(mnemonic);
}
public String reserveFor(String mnemonic){
    return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
}

public void setPrintToken(boolean on){
    printToken = on;
}

Add 2 lines which prints each token found by GetNextToken:
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }

 */
package ADT;

/**
 *
 * @author abrouill
 */
public class Syntactic {

    private String filein;              //The full file path to input file
    private SymbolTable symbolList;     //Symbol table storing ident/const
    private Lexical lex;                //Lexical analyzer 
    private Lexical.token token;        //Next Token retrieved 
    // private ReserveTable reserveWords = lex.getReserves(); //a few more than # reserves
    // private ReserveTable mnemonics = lex.getMnemonics(); //a few more than # reserves
    private boolean traceon;            //Controls tracing mode 
    private int level = 0;              //Controls indent for trace mode
    private boolean anyErrors;          //Set TRUE if an error happens 

    private final int symbolSize = 250;

    public Syntactic(String filename, boolean traceOn) {
        filein = filename;
        traceon = traceOn;
        symbolList = new SymbolTable(symbolSize);
        lex = new Lexical(filein, symbolList, true);
        lex.setPrintToken(traceOn);
        anyErrors = false;
    }

//The interface to the syntax analyzer, initiates parsing
// Uses variable RECUR to get return values throughout the non-terminal methods    
    public void parse() {
        int recur = 0;
// prime the pump
        token = lex.GetNextToken();
// call PROGRAM
        recur = Program();
    }

//Non Terminal     
    private int ProgIdentifier() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        // This non-term is used to uniquely mark the program identifier
        if (token.code == lex.codeFor("IDENT")) {
            // Because this is the progIdentifier, it will get a 'p' type to prevent re-use as a var
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'p', 0);
            //move on
            token = lex.GetNextToken();
        }
        return recur;
    }

//Non Terminals
    private int Program() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Program", true);
        if (token.code == lex.codeFor("MODUL")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("SEMIC")) {
                token = lex.GetNextToken();
                recur = Block();
                if (token.code == lex.codeFor("PERD_")) {
                    if (!anyErrors) {
                        System.out.println("Success.");
                    } else {
                        System.out.println("Compilation failed.");
                    }
                } else {
                    error(lex.reserveFor("PERD_"), token.lexeme);
                }
            } else {
                error(lex.reserveFor("SEMIC"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("MODUL"), token.lexeme);
        }
        trace("Program", false);
        return recur;
    }

//Non Terminal    
    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Block", true);

        if (token.code == lex.codeFor("BEGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("SEMIC")) && (!lex.EOF()) && (!anyErrors)) {
                token = lex.GetNextToken();
                recur = Statement();
            }
            if (token.code == lex.codeFor("END__")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("END__"), token.lexeme);
            }

        } else {
            error(lex.reserveFor("BEGIN"), token.lexeme);
        }

        trace("Block", false);
        return recur;
    }

//Not a NT, but used to shorten Statement code body   
    //<variable> $COLON-EQUALS <simple expression>
    private int handleAssignment() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);
        //have ident already in order to get to here, handle as Variable
        recur = Variable();  //Variable moves ahead, next token ready

        if (token.code == lex.codeFor("ASIGN")) {
            token = lex.GetNextToken();
            recur = SimpleExpression();
        } else {
            error(lex.reserveFor("ASIGN"), token.lexeme);
        }

        trace("handleAssignment", false);
        return recur;
    }

// NT This is dummied in to only work for an identifier.  MUST BE 
//  COMPLETED TO IMPLEMENT CFG <simple expression>
    // private int SimpleExpression() {
    //     int recur = 0;
    //     if (anyErrors) {
    //         return -1;
    //     }

    //     trace("SimpleExpression", true);
    //     if (token.code == lex.codeFor("IDENT")) {
    //         token = lex.GetNextToken();
    //     }
    //     trace("SimpleExpression", false);
    //     return recur;
    // }
    
    // <simple expression> -> [<sign>]  <term>  {<addop>  <term>}*
    private int SimpleExpression() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("SimpleExpression", true);

        // [<sign>]
        if (token.code == lex.codeFor("PLUS_") || token.code == lex.codeFor("MINUS")) {
            recur = Sign();
        }
        
        // <term>
        recur = Term();
            
        //{<addop>  <term>}*
        while(token.code == lex.codeFor("PLUS_") || token.code == lex.codeFor("MINUS")){
            recur = AddOp();
            token = lex.GetNextToken();
            recur = Term();
        }

        trace("SimpleExpression", false);
        return recur;
    }

//Non Terminal
//<statement>-> <variable> $COLON-EQUALS <simple expression>  
    private int Statement() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Statement", true);

        if (token.code == lex.codeFor("IDENT")) {  //must be an ASSUGNMENT
            recur = handleAssignment();
        } else {
            if (token.code == lex.codeFor("IF___")) {  //must be an ASSUGNMENT
                // this would handle the rest of the IF statment IN PART B
            } else // if/elses should look for the other possible statement starts...  
            //  but not until PART B
            {
                error("Statement start", token.lexeme);
            }
        }

        trace("Statement", false);
        return recur;
    }

    // <variable> -> <identifier> 
    private int Variable(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Variable", true);

        if(token.code == lex.codeFor("IDENT")){
            recur = Identifier();
        } else {
            error("variable start", token.lexeme);
        }

        token = lex.GetNextToken();

        trace("Variable", false);
        return recur;
    }

    // <addop> -> $PLUS | $MINUS
    private int AddOp(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("AddOp", true);

        if(token.code == lex.codeFor("PLUS_")){
            // terminal stuff
        }
        else if(token.code == lex.codeFor("MINUS")){
            // terminal stuff
        }
        else {
            error("AddOp", token.lexeme);
        }

        trace("AddOp", false);

        return recur;
    }
    
    // <sign> -> $PLUS | $MINUS
    private int Sign(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Sign", true);
        if(token.code == lex.codeFor("PLUS_")){
            // terminal stuff
        }
        else if(token.code == lex.codeFor("MINUS")){
            // terminal stuff
        }
        else {
            error("Sign", token.lexeme);
        }
        trace("Sign", false);
        return recur;
    } 

    // <term> -> <factor> {<mulop> <factor> }*
    private int Term(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Term", true);

        // <term> -> <factor> {<mulop> <factor> }*
        recur = Factor();
        token = lex.GetNextToken();
        while(token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")){
                recur = MulOp();
                token = lex.GetNextToken();
        }

        trace("Term", false);
        return recur;
    }

    // <mulop> -> $MULTIPLY | $DIVIDE
    private int MulOp(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("MulOp", true);

        if(token.code == lex.codeFor("MULTI")){
            // terminal stuff
        }
        else if(token.code == lex.codeFor("DIVID")){
            // terminal stuff
        }
        else {
            error("AddOp", token.lexeme);
        }

        trace("MulOp", false);
        
        return recur;
    } 

    /* <factor> -> <unsigned constant> |
                   <variable> |
                   $LPAR <simple expression> $RPAR
    */ 
    private int Factor(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Factor", true);
        if(token.code == lex.codeFor("ICNST")){
            recur = UConst();
        }
        else if(token.code == lex.codeFor("IDENT")){
            recur = Identifier();
        }
        else if(token.code == lex.codeFor("LPAR_")){
            token = lex.GetNextToken();
            recur = SimpleExpression();
            token = lex.GetNextToken();
            if(token.code == lex.codeFor("RPAR_")){
                // token = lex.GetNextToken();
            }
            else{
                error("RPAR", token.lexeme);
            }
        }
        else{
            error("Factor start", token.lexeme);
        }
        trace("Factor", false);
        return recur;
    } 

    // <unsigned constant> -> <unsigned number>
    private int UConst(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("UConst", true);
        if(token.code == lex.codeFor("ICNST")){
            recur = UNumber();
        }
        else if(token.code == lex.codeFor("FCNST")){
            recur = UNumber();
        }
        else{
            error("Uconst", token.lexeme);
        }
        trace("UConst", false);
        return recur;
    } 

    // <unsigned number>-> $FLOAT | $INTEGER
    private int UNumber(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("UNumber", true);

        if(token.code == lex.codeFor("FCNST")){

        }
        else if(token.code == lex.codeFor("ICNST")) {

        }
        else{
            error("UNumber", token.lexeme);
        }

        trace("UNumber", false);
        return recur;
    } 

    // <identifier> -> $IDENTIFIER
    private int Identifier(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Identifier", true);
    // unique non-terminal stuff
        trace("Identifier", false);
        return recur;
    }

/**
 * *************************************************
*/
    /*     UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
// error provides a simple way to print an error statement to standard output
//     and avoid reduncancy
    private void error(String wanted, String got) {
        anyErrors = true;
        System.out.println("ERROR: Expected " + wanted + " but found " + got);
    }

// trace simply RETURNs if traceon is false; otherwise, it prints an
    // ENTERING or EXITING message using the proc string
    private void trace(String proc, boolean enter) {
        String tabs = "";

        if (!traceon) {
            return;
        }

        if (enter) {
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("--> Entering " + proc);
            level++;
        } else {
            if (level > 0) {
                level--;
            }
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("<-- Exiting " + proc);
        }
    }

// repeatChar returns a string containing x repetitions of string s; 
//    nice for making a varying indent format
    private String repeatChar(String s, int x) {
        int i;
        String result = "";
        for (i = 1; i <= x; i++) {
            result = result + s;
        }
        return result;
    }
 

/*  Template for all the non-terminal method bodies
private int exampleNonTerminal(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("NameOfThisMethod", true);
// unique non-terminal stuff
        trace("NameOfThisMethod", false);
        return recur;

}  
    
    */    
}
