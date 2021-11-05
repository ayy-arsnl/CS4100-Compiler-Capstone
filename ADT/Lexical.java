/*
 The following code is provided by the instructor for the completion of PHASE 2 
of the compiler project for CS4100.

STUDENTS ARE TO PROVIDE THE FOLLOWING CODE FOR THE COMPLETION OF THE ASSIGNMENT:

1) Initialize the 2 reserve tables, which are fields in the Lexical class,
    named reserveWords and mnemonics.  Create the following functions,
    whose calls are in the lexical constructor:
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

2) 


 */
package ADT;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Currency;
/**
 *
 * @author abrouill
 */
import java.io.*;

public class Lexical {

    private File file;                        //File to be read for input
    private FileReader filereader;            //Reader, Java reqd
    private BufferedReader bufferedreader;    //Buffered, Java reqd
    private String line;                      //Current line of input from file   
    private int linePos;                      //Current character position
                                              //  in the current line
    private SymbolTable saveSymbols;          //SymbolTable used in Lexical
                                              //  sent as parameter to construct
    private boolean EOF;                      //End Of File indicator
    private boolean echo;                     //true means echo each input line
    private boolean printToken;               //true to print found tokens here
    private int lineCount;                    //line #in file, for echo-ing
    private boolean needLine;                 //track when to read a new line
    //Tables to hold the reserve words and the mnemonics for token codes
    private ReserveTable reserveWords = new ReserveTable(50); //a few more than # reserves
    private ReserveTable mnemonics = new ReserveTable(50); //a few more than # reserves
     
    //constructor
    public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
        saveSymbols = symbols;  //map the initialized parameter to the local ST
        echo = echoOn;          //store echo status
        lineCount = 0;          //start the line number count
        line = "";              //line starts empty
        needLine = true;        //need to read a line
        printToken = false;     //default OFF, do not print tokesn here
                                //  within GetNextToken; call setPrintToken to
                                //  change it publicly.
        linePos = -1;           //no chars read yet
        //call initializations of tables
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

        //set up the file access, get first character, line retrieved 1st time
        try {
            file = new File(filename);    //creates a new file instance  
            filereader = new FileReader(file);   //reads the file  
            bufferedreader = new BufferedReader(filereader);  //creates a buffering character input stream  
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    }

    // token class is declared here, no accessors needed
    public class token {
        public String lexeme;
        public int code;
        public String mnemonic;

        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }

    private token dummyGet() {
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        result.code = 0;
        result.mnemonic = "DUMY";
        return result;
        
    }
        
    
    public final int _GRTR = 38;
    public final int _LESS = 39;
    public final int _GREQ = 40;
    public final int _LEEQ = 41;
    public final int _EQLS = 42;
    public final int _NEQL = 43;
    
 /* @@@ These are nice for syntax to call later 
    // given a mnemonic, find its token code value
    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
    // given a mnemonic, return its reserve word
    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }
*/
    // Public access to the current End Of File status
    public boolean EOF() {
        return EOF;
    }
    // DEBUG enabler, turns on token printing inside of GetNextToken
    public void setPrintToken(boolean on) {
        printToken = on;
    }

    
/* @@@ */    
    private void initReserveWords(ReserveTable reserveWords) {
        reserveWords.Add("GOTO", 0);
        reserveWords.Add("INTEGER", 1);
        reserveWords.Add("TO", 2);
        reserveWords.Add("DO", 3);
        reserveWords.Add("IF", 4);
        reserveWords.Add("THEN", 5);
        reserveWords.Add("ELSE", 6);
        reserveWords.Add("FOR", 7);
        reserveWords.Add("OF", 8);
        reserveWords.Add("WRITELN", 9);
        reserveWords.Add("READLN", 10);
        reserveWords.Add("BEGIN", 11);
        reserveWords.Add("END", 12);
        reserveWords.Add("VAR", 13);
        reserveWords.Add("WHILE", 14);
        reserveWords.Add("MODULE", 15);
        reserveWords.Add("LABEL", 16);
        reserveWords.Add("REPEAT", 17);
        reserveWords.Add("UNTIL", 18);
        reserveWords.Add("PROCEDURE", 19);
        reserveWords.Add("DOWNTO", 20);
        reserveWords.Add("FUNCTION", 21);
        reserveWords.Add("RETURN", 22);
        reserveWords.Add("FLOAT", 23);
        reserveWords.Add("STRING", 24);
        reserveWords.Add("ARRAY", 25);

        //1 and 2-char
        reserveWords.Add("/", 30);
        reserveWords.Add("*", 31);
        reserveWords.Add("+", 32);
        reserveWords.Add("-", 33);
        reserveWords.Add("(", 34);
        reserveWords.Add(")", 35);
        reserveWords.Add(";", 36);
        reserveWords.Add(":=", 37);
        reserveWords.Add(">", 38);
        reserveWords.Add("<", 39);
        reserveWords.Add(">=", 40);
        reserveWords.Add("<=", 41);
        reserveWords.Add("=", 42);
        reserveWords.Add("<>", 43);
        reserveWords.Add(",", 44);
        reserveWords.Add("[", 45);
        reserveWords.Add("]", 46);
        reserveWords.Add(":", 47);
        reserveWords.Add(".", 48);
//     reserveWords.Add("", );

    }
/* @@@ */
    private void initMnemonics(ReserveTable mnemonics) {
        mnemonics.Add("GOTO", 0);
        mnemonics.Add("INTG", 1);
        mnemonics.Add("_TO_", 2);
        mnemonics.Add("_DO_", 3);
        mnemonics.Add("_IF_", 4);
        mnemonics.Add("THEN", 5);
        mnemonics.Add("ELSE", 6);
        mnemonics.Add("FOR_", 7);
        mnemonics.Add("_OF_", 8);
        mnemonics.Add("WRTN", 9);
        mnemonics.Add("READ", 10);
        mnemonics.Add("BGIN", 11);
        mnemonics.Add("END_", 12);
        mnemonics.Add("VAR_", 13);
        mnemonics.Add("WHLE", 14);
        mnemonics.Add("MODL", 15);
        mnemonics.Add("LABL", 16);
        mnemonics.Add("REPT", 17);
        mnemonics.Add("UNTL", 18);
        mnemonics.Add("PROC", 19);
        mnemonics.Add("DOWN", 20);
        mnemonics.Add("FUNC", 21);
        mnemonics.Add("RETN", 22);
        mnemonics.Add("FLOT", 23);
        mnemonics.Add("STRG", 24);
        mnemonics.Add("ARRY", 25);

        //1 and 2-char
        mnemonics.Add("DIVD", 30);
        mnemonics.Add("MULT", 31);
        mnemonics.Add("PLUS", 32);
        mnemonics.Add("MINS", 33);
        mnemonics.Add("LPAR", 34);
        mnemonics.Add("RPAR", 35);
        mnemonics.Add("SEMI", 36);
        mnemonics.Add("ASGN", 37);
        mnemonics.Add("GRTR", 38);
        mnemonics.Add("LESS", 39);
        mnemonics.Add("GREQ", 40);
        mnemonics.Add("LEEQ", 41);
        mnemonics.Add("EQLS", 42);
        mnemonics.Add("NEQL", 43);
        mnemonics.Add("COMA", 44);
        mnemonics.Add("LBRK", 45);
        mnemonics.Add("RBRK", 46);
        mnemonics.Add("COLN", 47);
        mnemonics.Add("PERD", 48);
        mnemonics.Add("IDNT", 50);
        mnemonics.Add("ICNS", 51);
        mnemonics.Add("FCNS", 52);
        mnemonics.Add("SCNS", 53);
        mnemonics.Add("UNKN", 99);

    }

    private final int UNKNOWN_CHAR = 99;

    // Character category for alphabetic chars
    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }
    // Character category for 0..9 
    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }
    // Category for any whitespace to be skipped over
    private boolean isWhitespace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }

    
    // Returns the VALUE of the next character without removing it from the
    //    input line.  Useful for checking 2-character tokens that start with
    //    a 1-character token.
    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' '; //at end of line, so nothing
        } else // 
        {
            if ((linePos + 1) < line.length()) { //have a char to peek
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }

    // Called by GetNextChar when the cahracters in the current line are used up.
    private void GetNextLine() {
        try {
            line = bufferedreader.readLine();
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {    // The readLine returns null at EOF, set flag
            EOF = true;
        }
        linePos = -1;      // reset vars for new line if we have one
        needLine = false;  // we have one, no need
        //the line is ready for the next call to get a character
    }

    public char GetNextChar() {
        char result;
        if (needLine) //ran out last time we got a char, so get a new line
        {
            GetNextLine();
        }
        //try to get char from line buff
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            if ((linePos < line.length() - 1)) { //have a character available
                linePos++;
                result = line.charAt(linePos);
            } else { //need a new line, but want to return eoln on this call first
                result = '\n';
                needLine = true; //will read a new line on next GetNextChar call
            }
        }
        return result;
    }

    final char commstart1 = '{';
    final char commend1 = '}';
    final char commstart2 = '(';
    final char commstartend = '*';
    final char commend2 = ')';

    public char skipComment(char curr) {
        if (curr == commstart1) {
            curr = GetNextChar();
            while ((curr != commend1) && (!EOF)) {
                curr = GetNextChar();
            }
            if (EOF) {
                System.out.println("Comment not terminated before End Of File");
            } else {
                curr = GetNextChar();
            }
        } else {
            if ((curr == commstart2) && (PeekNextChar() == commstartend)) {
                curr = GetNextChar(); // get the second
                curr = GetNextChar(); // into comment or end of comment
//            while ((curr != commstartend) && (PeekNextChar() != commend2) &&(!EOF)) {
                while ((!((curr == commstartend) && (PeekNextChar() == commend2))) && (!EOF)) {
//                if (lineCount >=4) {
                    //              System.out.println("In Comment, curr, peek: "+curr+", "+PeekNextChar());}
                    curr = GetNextChar();
                }
                if (EOF) {
                    System.out.println("Comment not terminated before End Of File");
                } else {
                    curr = GetNextChar();          //must move past close
                    curr = GetNextChar();          //must get following
                }
            }

        }
        return (curr);
    }

    public char skipWhiteSpace() {

        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }

    private boolean isPrefix(char ch) {
        return ((ch == ':') || (ch == '<') || (ch == '>'));
    }

    private boolean isStringStart(char ch) {
        return ch == '"';
    }

    private final int IDENT_ID = 50;
    private final int INTEGER_ID = 51;
    private final int FLOAT_ID = 52;
    private final int STRING_ID = 53;
//global char
    char currCh;

    private token getIdent() {
        //      int lookup = IDENT;
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        while (isLetter(currCh)||(isDigit(currCh)||(currCh == '$')||(currCh=='_'))) {
            result.lexeme = result.lexeme + currCh; //extend lexeme
            currCh = GetNextChar();
        }
        // end of token, lookup or IDENT      
                
        result.code = reserveWords.LookupName(result.lexeme);
        if (result.code == -1) // ReserveTable.notFound
            result.code = IDENT_ID;

        result.mnemonic = mnemonics.LookupCode(result.code);
        return result;
        //return dummyGet();
    }

    private token getNumber() {
        /* a number is:   <digit>+[.<digit>*[E[+|-]<digit>+]] */
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        while (isDigit(currCh) || currCh == '.' || currCh == 'e' || currCh == 'E' || currCh == '+' || currCh == '-') {
            
            result.lexeme = result.lexeme + currCh; //extend lexeme
            currCh = GetNextChar();
        }
        // end of token, lookup or IDENT      
                
        result.code = 51;

        result.mnemonic = mnemonics.LookupCode(result.code);
        return result;
        //return dummyGet();
        // return dummyGet();
    }

    private token getString() {
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        while (currCh != '"' && currCh != '\n' && currCh != '\r') {
            result.lexeme = result.lexeme + currCh; //extend lexeme
            currCh = GetNextChar();
        }
        // end of token, lookup or IDENT      
        result.lexeme += currCh;  
        currCh = GetNextChar();      
        result.code = 53;

        result.mnemonic = mnemonics.LookupCode(result.code);
        return result;
        // return dummyGet();
    }

    private token getOneTwoChar() {
        return dummyGet();
    }

    // Checks to see if a string contains a valid DOUBLE 
    public boolean doubleOK(String stin) {
        boolean result;
        Double x;
        try {
            x = Double.parseDouble(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    // Checks the input string for a valid INTEGER
    public boolean integerOK(String stin) {
        boolean result;
        int x;
        try {
            x = Integer.parseInt(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    public token checkTruncate(token result) {
        // truncate if needed
        int ival = 0;
        double dval = 0.0;
        int len = result.lexeme.length();
        String lexemetrunc = result.lexeme;

        switch (result.code) {
            case IDENT_ID:
                if (len > 20) {
                    lexemetrunc = result.lexeme.substring(0, 20);
                    System.out.println("Identifier length > 20, truncated " + result.lexeme
                            + " to " + lexemetrunc);
                }
                saveSymbols.AddSymbol(lexemetrunc, 'v', 0);
                break;
            case INTEGER_ID:
                if (len > 6) {
                    lexemetrunc = result.lexeme.substring(0, 6);
                    System.out.println("Integer length > 6, truncated " + result.lexeme
                            + " to " + lexemetrunc);
                    ival = 0;
                } else //no trun, but is it ok
                {
                    if (integerOK(result.lexeme)) {
                        ival = Integer.valueOf(lexemetrunc);
                    } else {
                        System.out.println("Invalid integer value");
                    }
                }
                saveSymbols.AddSymbol(lexemetrunc, 'c', ival);
                break;
            case FLOAT_ID:
                if (len > 12) {
                    lexemetrunc = result.lexeme.substring(0, 12);
                    System.out.println("Float length > 12, truncated " + result.lexeme
                            + " to " + lexemetrunc);
                    dval = 0;
                } else //no trun, but is it ok
                {
                    if (doubleOK(result.lexeme)) {
                        dval = Double.valueOf(lexemetrunc);
                    } else {
                        System.out.println("Invalid float value");
                    }
                }
                saveSymbols.AddSymbol(lexemetrunc, 'c', dval);
                break;

            case STRING_ID:
                saveSymbols.AddSymbol(result.lexeme, 'c', result.lexeme);
                break;
            default:
                break; //don't add                           
        }

        return result;
    }
    

    public token GetNextToken() {
        token result = new token();

        currCh = skipWhiteSpace();
        if (isLetter(currCh)) { //is ident
            result = getIdent();
        } else if (isDigit(currCh)) { //is numeric
            result = getNumber();
        } else if (isStringStart(currCh)) { //string literal
            result = getString();
        } else //default char checks
        {
            result = getOneTwoChar();
        }

        if ((result.lexeme.equals("")) || (EOF)) {
            result = null;
        }
//set the mnemonic
        if (result != null) {
// THIS LINE REMOVED-- PUT BACK IN TO USE LOOKUP            result.mnemonic = mnemonics.LookupCode(result.code);
            result = checkTruncate(result);
            if (printToken) { // THIS IS TURNED OFF BY DEFAULT
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }
        return result;

    }

   
}
