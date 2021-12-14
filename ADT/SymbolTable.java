// Alexander Urquhart
// 22 Sept 2021
// CS4100-001
// HW2 - Compiler Foundations

package ADT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SymbolTable {
    private int maxSize;
    private SymbolTableEntry[] symbolTable;
    private int currentIndex;

    // Constructor for SymbolTable
    public SymbolTable(int maxSize){
        this.maxSize = maxSize;
        this.symbolTable = new SymbolTableEntry[this.maxSize];
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

    // Adds new symbol table entry with int value
    public int AddSymbol(String symbol, char kind, int value){
        SymbolTableEntry newEntry = new SymbolTableEntry(symbol, kind, value);
        this.symbolTable[currentIndex] = newEntry;
        this.currentIndex++;
        return this.currentIndex;
    }

    // Adds new symbol table entry with float value
    public int AddSymbol(String symbol, char kind, double value){
        SymbolTableEntry newEntry = new SymbolTableEntry(symbol, kind, value);
        this.symbolTable[currentIndex] = newEntry;
        this.currentIndex++;
        return this.currentIndex;
    }

    // Adds new symbol table entry with string value
    public int AddSymbol(String symbol, char kind, String value){
        SymbolTableEntry newEntry = new SymbolTableEntry(symbol, kind, value);
        this.symbolTable[currentIndex] = newEntry;
        this.currentIndex++;
        return this.currentIndex;
    }

    // Iterates through the symbol table checking for specified symbol and returns its
    // index or returns -1 if it isn't in the table
    public int LookupSymbol(String symbol){
        for(int i = 0; i < currentIndex; i++){
            if(this.symbolTable[i].getSymbol().compareToIgnoreCase(symbol) == 0){
                return i;
            }
        }
        return -1;
    }

    public String GetSymbol(int index){
        return this.symbolTable[index].symbol;
    }

    public char GetKind(int index){
        return this.symbolTable[index].kind;
    }

    public char GetDataType(int index){
        return this.symbolTable[index].data_type;
    }

    public int GetInteger(int index){
        return this.symbolTable[index].value_i;
    }
    
    public String GetString(int index){
        return this.symbolTable[index].value_s; 
    }

    public double GetFloat(int index){
        return this.symbolTable[index].value_f;
    }
    
    // Updates symbol table entry with new data with int value
    public void UpdateSymbol(int index, char kind, int value) {
        this.symbolTable[index].kind = kind;
        this.symbolTable[index].value_i = value;
    }

    // Updates symbol table entry with new data with float value
    public void UpdateSymbol(int index, char kind, double value) {
        this.symbolTable[index].kind = kind;
        this.symbolTable[index].value_f = value;
    }

    // Updates symbol table entry with new data with string value
    public void UpdateSymbol(int index, char kind, String value) {
        this.symbolTable[index].kind = kind;
        this.symbolTable[index].value_s = value;
    }

    // Prints header and each entry in symbol table to specified file
    public void PrintSymbolTable(String filename) throws IOException {
        File outputFile = new File(filename);
        FileWriter writer = new FileWriter(outputFile);

        String header = "INDEX | SYMBOL | KIND | DTYPE | VALUE \n-----------------------\n";
        try {
            writer.write(header);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to print to file");
        }

        for (int i = 0; i < currentIndex; i++) {
            String smbl = this.symbolTable[i].symbol;
            String knd = String.valueOf(this.symbolTable[i].kind);
            String data_type = String.valueOf(GetDataType(i));
            String val;

            if (data_type.compareTo("I") == 0) {
                val = String.valueOf(this.symbolTable[i].value_i);
            } else if (data_type.compareTo("F") == 0) {
                val = String.valueOf(this.symbolTable[i].value_f);
            } else {
                val = this.symbolTable[i].value_s;
            }

            //String strOut = "%-7s %-8s %-8s %-8s %-8s\n";
            String strOut = "%s | %s | %s | %s | %s\n";
            String out = String.format(strOut, i, smbl, knd, data_type, val);
            try {
                writer.write(out);
            } catch (IOException e) {
                System.out.println("ERROR: Failed to print to file");
            }
        }
        writer.close();
    }

    // Subclass for table entries
    public class SymbolTableEntry {
        private String symbol;
        private char kind; // Can be = 'L', 'V', 'C'
        private char data_type; // Can be = 'I', 'F', 'S'
        private int value_i; // integer version for value
        private double value_f; // float version for value
        private String value_s; // string version for value

        // Constructor for entries with int values
        public SymbolTableEntry(String symbol, char kind, int value) {
            this.symbol = symbol;
            this.kind = kind;
            this.value_i = value;
            this.data_type = 'I';
        }

        // Constructor for entries with float values
        public SymbolTableEntry(String symbol, char kind, double value) {
            this.symbol = symbol;
            this.kind = kind;
            this.value_f = value;
            this.data_type = 'F';
        }

        // Constructor for entries with string values
        public SymbolTableEntry(String symbol, char kind, String value) {
            this.symbol = symbol;
            this.kind = kind;
            this.value_s = value;
            this.data_type = 'S';
        }

        public String getSymbol() {
            return this.symbol;
        }

        public char getKind() {
            return this.kind;
        }
    }
}