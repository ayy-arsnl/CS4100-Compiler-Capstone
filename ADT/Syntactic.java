package ADT;

import java.io.IOException;

import javax.print.event.PrintJobAttributeEvent;

public class Syntactic {

    private String filein;              //The full file path to input file
    private SymbolTable symbolList;     //Symbol table storing ident/const
    private Interpreter interp;
    private QuadTable quads;
    private Lexical lex;                //Lexical analyzer 
    private Lexical.token token;        //Next Token retrieved 
    private boolean traceon;            //Controls tracing mode 
    private int level = 0;              //Controls indent for trace mode
    private boolean anyErrors;          //Set TRUE if an error happens 

    private final int symbolSize = 250; // originally 250
    private final int quadSize = 1500;
    //private int genSymbolCount;
    private final char genSymbolChar = '@';
    private int genSymbolCount;

    private int Minus1Index;
    private int Plus1Index;

   

    public Syntactic(String filename, boolean traceOn) {
        filein = filename;
        traceon = traceOn;
        symbolList = new SymbolTable(symbolSize);
        genSymbolCount = 0;
        Minus1Index = symbolList.AddSymbol("-1", 'C', -1);
        Plus1Index = symbolList.AddSymbol("1", 'C', 1);

        quads = new QuadTable(quadSize);
        interp = new Interpreter();

        lex = new Lexical(filein, symbolList, true);
        lex.setPrintToken(traceOn);
        anyErrors = false;        
    }

    public QuadTable getQuadTable(){
        return quads;
    }
    public SymbolTable getSymbolTable(){
        return symbolList;
    }

    // genSymbolChar is defined as a '@', and genSymbolCount is a SymbolTable integer initialized to 0 in the constructor
    // Creates a new symbol with identifiable unique name, not legal variable 
    public int GenSymbol() {
        String name = genSymbolChar + Integer.toString(genSymbolCount);
        genSymbolCount++;
        return symbolList.AddSymbol(name, 'V', 0);
    }

//The interface to the syntax analyzer, initiates parsing
// Uses variable RECUR to get return values throughout the non-terminal methods    
    public void parse() throws IOException {
        // make filename pattern for symbol table and quad table output later
        String filenameBase = filein.substring(0, filein.length() - 4);
        System.out.println(filenameBase);
        int recur = 0;
        // prime the pump, get first token
        token = lex.GetNextToken();
        // call PROGRAM
        recur = Program();
        // done, so add the final STOP quad
        quads.AddQuad(0, 0, 0, 0);

        System.out.println("-------FINISHED PARSING\nOUTPUTING FILES...");
        //print ST, QUAD before execute
        symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
        quads.PrintQuadTable(filenameBase + "QUADS.txt");
        //interpret
        if (!anyErrors) {
            System.out.println("--------STARTING INTERPRETER-------");
            interp.InterpretQuads(quads, symbolList, false, filenameBase + "TRACE.txt");
        } else {
            System.out.println("Errors, unable to run program.");
        }
        symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");
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
                if(token.code == lex.codeFor("BEGIN")){
                    recur = Block();
                    if(token.code == lex.codeFor("SEMIC")){
                        // do nothing really
                    }
                    else{
                        error(lex.reserveFor("SEMIC"), token.lexeme);    
                    }
                }
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

    private int handleWriteln() {
        int recur = 0;
        int toprint = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleWriteln", true);
        
        //got here from a WRITELN token, move past it...
        token = lex.GetNextToken();
        //look for ( stringconst, ident, simpleexp )
        if (token.code == lex.codeFor("LPAR_")) {
            //move on
            token = lex.GetNextToken();
            if ((token.code == lex.codeFor("SCNST"))) { //|| (token.code == lex.codeFor("IDNT"))) {
                // save index for string literal or identifier
                toprint = symbolList.LookupSymbol(token.lexeme);
                //move on
                token = lex.GetNextToken();
            } else {
                toprint = SimpleExpression();
            }
            quads.AddQuad(16/*interp.opcodeFor("PRINT")*/, toprint, 0, 0);
            //now need right ")"
            if (token.code == lex.codeFor("RPAR_")) {
                //move on
                token = lex.GetNextToken();
            } else {
            error(lex.reserveFor("RPAR_"), token.lexeme);
            }
        } 
        else {
            error(lex.reserveFor("LPAR_"), token.lexeme);
        }
        // end lpar group
        trace("handleWriteln", false);
        return recur;
    }

    /*
    PSEUDOCODE FOR SIMPLE EXPRESSION
    int simpleexpression{
        int left, right, signval, temp, opcode;
        signval = sign; //returns -1 if neg, otherwise 1
        left = term;
        if (signval == -1) //Add a negation quad to flip sign
            Quads.addQuad(MULOPCODE,left,Minus1Index,left);
        //This is adding terms together as they are found, adding Quads 
        while (plusorminus(tokenCode){ //is + or -
            if (tokenCode==PLUS) opcode = ADDOPCODE;
            else opcode = SUBOPCODE;
            GNT; //move ahead
            right = term; // index of term result
            temp = GenSymbol; //index of new temp variable
            Quads.addQuad(opcode,left,right,temp);
            left = temp; //new leftmost term is last result
        }
        return(left);
    }
    */
    // <simple expression> -> [<sign>]  <term>  {<addop>  <term>}*
    private int SimpleExpression() {
        int recur = 0;
        int left, right, signval, temp, opcode;
        if (anyErrors) {
            return -1;
        }

        trace("SimpleExpression", true);

        signval = Sign(); //returns -1 if neg, otherwise 1
        left = Term();
        if (signval == -1){ //Add a negation quad to flip sign
            quads.AddQuad(2/*MULTI*/, left, Minus1Index, left);
        }
      //This is adding terms together as they are found, adding Quads 
        while (token.code == lex.codeFor("PLUS_") || token.code == lex.codeFor("MINUS")){ //is + or -
            if (token.code == lex.codeFor("PLUS_")){
                opcode = 4 /*ADD*/;
            }
            else { 
                opcode = 3 /*SUB*/;
            }
            token = lex.GetNextToken(); //move ahead
            right = Term(); // index of term result
            temp = GenSymbol(); //index of new temp variable
            temp -=1 ;
            quads.AddQuad(opcode, left, right, temp);
            left = temp; //new leftmost term is last result
        }
        //return(left);
        recur = left;

        /* // [<sign>]
        // if (token.code == lex.codeFor("PLUS_") || token.code == lex.codeFor("MINUS")) {
        //     recur = Sign();
        //     token = lex.GetNextToken();
        // }
        
        // // <term>
        // recur = Term();
            
        // //{<addop>  <term>}*
        // while(token.code == lex.codeFor("PLUS_") || token.code == lex.codeFor("MINUS")){
        //     recur = AddOp();
        //     token = lex.GetNextToken();
        //     recur = Term();
        // }
        */

        trace("SimpleExpression", false);
        return recur;
    }

/*
PSEUDO CODE FOR STATEMENT
int statement() { int left, right;
    if (tokenCode==IDENT_){ //ident, 50, is present; call <variable>
        left = variable; //returns variable ST index, advances
        if (tokenCode==ASGN_){ //assignment op found 
            GNT; //move ahead to start of expression
            right = simpleexpression; //get result index
            Quads.AddQuad(MovCode,right,0,left);
        }
        else{
            errorexpect(tokenCode,ASGN_); //display error
        }
    }
    else{
        ... //do other <statement> possibilities checks
    }
}
*/
//Non Terminal
//<statement>-> <variable> $COLON-EQUALS <simple expression>  
    private int Statement() {
        int recur = 0;
        int saveTop;
        int branchQuad;
        int patchElse;
        if (anyErrors) {
            return -1;
        }

        int left, right;

        trace("Statement", true);

        if (token.code == lex.codeFor("IDENT")) {  //must be an ASSUGNMENT
            left = Variable(); //returns variable ST index, advances
            if(token.code == lex.codeFor("ASIGN")){
                token = lex.GetNextToken();
                right = SimpleExpression();
                quads.AddQuad(5/*MOV*/, right, 0, left);
                //recur = handleAssignment();
            }
        }
        else if(token.code == lex.codeFor("IF___")){//must be an ASSUGNMENT
            // this would handle the rest of the IF statment IN PART B
            token = lex.GetNextToken(); // move past ‘if’
            branchQuad = relExpression();//tells where branchTarget to be set 
                                        // to jump around TRUE part

            if(token.code == lex.codeFor("THEN_")){ //all ok, continue
                token = lex.GetNextToken(); // move past ‘then’
                recur = Statement(); //all if body quads are genned
                if (token.code == lex.codeFor("ELSE_")){ //have to jump around to ??
                    token = lex.GetNextToken(); // move past ELSE
                    patchElse = quads.NextQuad(); //save backfill quad to jump around
                                                 // ELSE body, target is unknown now
                    quads.AddQuad(8 /*BR*/, 0, 0, 0); //backfill the FALSE IF branch jump 
                    quads.setQuadOp3(branchQuad, quads.NextQuad()); //conditional jump 
                    recur = Statement(); // gen ELSE body quads
                              // fill in end of ELSE part
                    quads.setQuadOp3(patchElse, quads.NextQuad());
                }
                else { //no ELSE encountered, fix IF branch
                    quads.setQuadOp3(branchQuad, quads.NextQuad());
                }
            }//if the THEN was found
            else{// error, no THEN
                error("THEN", token.lexeme);
            }
        }// end of IF statement stuff
        else if(token.code == lex.codeFor("WHILE")){
            token = lex.GetNextToken();
            saveTop = quads.NextQuad();
            branchQuad = relExpression();
            if(token.code == lex.codeFor("DO___")){
                token = lex.GetNextToken();
                recur = Block();
                quads.AddQuad(8 /*BR*/, 0, 0, saveTop);
                quads.setQuadOp3(branchQuad, quads.NextQuad());
            }
            else{
                error("DO", token.lexeme);
            }

        }
        else if(token.code == lex.codeFor("WRTLN")){
            recur = handleWriteln();
        }
        else {
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

    /*
    PSEUDOCODE FOR VARIABLE
    int variable(){ 
        int result= -1;
        result = identifier; //identifier call returns ST index.
                            //Does not call GNT, so token
                            // can be inspected here!
        if (token.tokenCode==IDENT_){ //Do type check if needed- not label,
                                    // and not program name or string?
                                    // TYPECHECK for integer or float
                                    //  would go here if needed
            GNT; //move ahead
        }
        else{
            //here, it was not an ident as expected, so throw error
        }
        return(result); //result is the SymbolTable index of the ident.
    }
    */
    // <variable> -> <identifier> 

    private int Variable() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Variable", true);

        recur = Identifier();
        if (token.code == lex.codeFor("IDENT")) {
            //return the location of this variable for Quad use
            //recur = symbolList.LookupSymbol(token.lexeme);
            // bookkeeping and move on
            token = lex.GetNextToken();
        } else {
            error("Variable", token.lexeme);
        }

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

        // $PLUS
        if(token.code == lex.codeFor("PLUS_")){
            recur = 4;
        }
        // $MINUS
        else if(token.code == lex.codeFor("MINUS")){
            recur = 5;
        }
        else {
            error("AddOp", token.lexeme);
        }

        trace("AddOp", false);
        return recur;
    }
    
    /*
    PSEUDOCODE FOR SIGN
    int sign(){
        int result=1; //only move ahead if + or - found; optional sign
        if (tokenCode==MINUS){
            result = -1;
            GNT;
        }
        else if (tokenCode==PLUS)
            GNT;
        return(result);
    }
    */
    // <sign> -> $PLUS | $MINUS
    private int Sign(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Sign", true);

        if(token.code == lex.codeFor("MINUS")){
            recur = -1;
        }
        else {
            recur = 1;
        }

        trace("Sign", false);
        return recur;
    } 

    // <term> -> <factor> {<mulop> <factor> }*
    private int Term(){
        int left, right, temp, opcode;
        if (anyErrors) {
            return -1;
        }

        trace("Term", true);

        // <factor> 
        left = Factor();
        //token = lex.GetNextToken();

        // { <mulop> <factor> }*
        while(token.code == lex.codeFor("MULTI") || token.code == lex.codeFor("DIVID")){
            if(token.code == lex.codeFor("MULTI")){
                opcode = 2;//MulOp();
            }
            else{
                opcode = 1;
            }
            
            token = lex.GetNextToken();
            right = Factor();
            temp = GenSymbol();
            temp -= 1;
            quads.AddQuad(opcode, left, right, temp);
            left = temp;
        }

        trace("Term", false);
        return left;
    }

    // <mulop> -> $MULTIPLY | $DIVIDE
    private int MulOp(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("MulOp", true);

        // $MULTIPLY
        if(token.code == lex.codeFor("MULTI")){
            recur = 2; // Multi OP
        }
        // $DIVIDE
        else if(token.code == lex.codeFor("DIVID")){
            recur = 1; // Divide OP;
        }
        else {
            error("MulOp", token.lexeme);
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
        if(token.code == lex.codeFor("ICNST") || token.code == lex.codeFor("FCNST")){
            recur = UnsignedConstant();
            //token = lex.GetNextToken();
        }
        else if(token.code == lex.codeFor("IDENT")){
            recur = Variable();
            //token = lex.GetNextToken();
        }
        // $LPAR
        else if(token.code == lex.codeFor("LPAR_")){
            token = lex.GetNextToken();

            // <simple expression>
            recur = SimpleExpression();
            //token = lex.GetNextToken();
            // $RPAR
            if(token.code == lex.codeFor("RPAR_")){
                token = lex.GetNextToken();
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
    private int UnsignedConstant() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("UnsignedConstant", true);

        // only accepting a number
        recur = UnsignedNumber();

        trace("UnsignedConstant", false);
        return recur;
    }

    // <unsigned number>-> $INTEGER
    private int UnsignedNumber() {
        int recur = 0;
        if (anyErrors) {
        return -1;
        }
        trace("UnsignedNumber", true);
        
        // float or int or ERROR
        // unsigned constant starts with integer or float number
        if ((token.code == lex.codeFor("ICNST") || (token.code == lex.codeFor("FCNST")))) {
            // return the s.t. index
            recur = symbolList.LookupSymbol(token.lexeme);
            token = lex.GetNextToken();
        } else {
            error("Integer or Floating Point Number", token.lexeme);
        }

        trace("UnsignedNumber", false);
        return recur;
    }

    // <identifier> -> $IDENTIFIER
    private int Identifier(){
        int symbolLocation = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Identifier", true);

        symbolLocation = symbolList.LookupSymbol(token.lexeme);

        trace("Identifier", false);
        return symbolLocation;
    }

    // <stringconst> -> $STRINGTYPE
    private int StringConstant(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Identifier", true);
        recur = 53; // Token code 53
        trace("Identifier", false);
        return recur;
    }

    // <relop> -> $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
    private int RelOp(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }
        trace("Relop", true);

        if (token.code == lex.codeFor("EQLS_")) { // 
            // return the location of this variable for Quad use
            recur = lex.codeFor("EQLS_");
            // bookkeeping and move on
            token = lex.GetNextToken();
        }
        else if(token.code == lex.codeFor("LESS_")){
            recur = lex.codeFor("EQLS_");
            // bookkeeping and move on
            token = lex.GetNextToken();
        }
        else if(token.code == lex.codeFor("GRTR_")){
            recur = lex.codeFor("GRTR_");
            // bookkeeping and move on
            token = lex.GetNextToken();
        } 
        else if(token.code == lex.codeFor("NTEQL")){
            recur = lex.codeFor("NTEQL");
            // bookkeeping and move on
            token = lex.GetNextToken();
        } 
        else if(token.code == lex.codeFor("LESEQ")){
            recur = lex.codeFor("LESEQ");
            // bookkeeping and move on
            token = lex.GetNextToken();
        } 
        else if(token.code == lex.codeFor("GRTEQ")){
            recur = lex.codeFor("GRTEQ");
            // bookkeeping and move on
            token = lex.GetNextToken();
        } 
         
        else {
            error("Relop", token.lexeme);
        }

        trace("Relop", false);
        return recur;
    }
    
    // <relexpression> -> <simple expression> <relop> <simple expression>
    private int relExpression() {
        int left, right, saveRelop, result, temp;
        left = SimpleExpression(); // get the left operand, our ‘A’
        saveRelop = RelOp(); // returns tokenCode of rel operator
        right = SimpleExpression(); // right operand, our ‘B’
        temp = GenSymbol(); // Create temp var in symbol table
        temp -= 1;
        quads.AddQuad(3, left, right, temp); // compare
        result = quads.NextQuad(); // Save Q index where branch will be
        quads.AddQuad(relopToOpcode(saveRelop), temp, 0, 0); // target set later
        return result;
    }

    // converst relationals into opcodes based on their false branch
    private int relopToOpcode(int relop) {
        int opcode = 0;
        switch (relop) {
            case 42: // EQLS_
                opcode = 13; // BNZ;
                break;
            case 43: // NTEQL
                opcode = 10; // BZ
                break;
            case 32: // LESS_
                opcode = 15; // BNN
                break;
            case 38: // GRTR_
                opcode = 14; // BNP
                break;
            case 41: // LESEQ
                opcode = 11; // BP
                break;
            case 40: // GRTEQ
                opcode = 12; // BN
                break;           
            default: //
                opcode = 0;
        }
        return opcode;
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
}