// Alexander Urquhart
// 22 Sept 2021
// CS4100-001
// HW2 - Compiler Foundations

package ADT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Class definition for Quad Table
public class QuadTable {
    private int maxSize;
    private int[][] table;
    private int currentIndex;

    // Constructor for Quadtable type
    public QuadTable(int maxSize){
        this.maxSize = maxSize;
        this.table = new int[this.maxSize][4];
        this.currentIndex = 0;
    }

    // Returns next quad table entry index
    public int NextQuad(){
        return this.currentIndex;
    }

    // TODO: Add index checker for adding new quad
    // Adds new entry to quad table and increases the current available index
    public void AddQuad(int opcode, int op1, int op2, int op3){
        this.table[this.currentIndex][0] = opcode;
        this.table[this.currentIndex][1] = op1;
        this.table[this.currentIndex][2] = op2;
        this.table[this.currentIndex][3] = op3;
        this.currentIndex++;
    }
    
    // Returns quad table entry at index/column location
    public int GetQuad(int index, int column){
        return this.table[index][column];
    }

    // Updates quad table entry at specific index with new data
    public void UpdateQuad(int index, int opcode, int op1, int op2, int op3){
        this.table[index][0] = opcode;
        this.table[index][1] = op1;
        this.table[index][2] = op2;
        this.table[index][3] = op3;
    }

    public void setQuadOp3(int index, int newOp3){
        this.table[index][3] = newOp3;
    }

    public int getMax(){
        return this.maxSize;
    }

    // Prints header and each quad table entry to specified file
    public void PrintQuadTable(String filename) throws IOException {
            File outputFile = new File(filename);
            FileWriter writer = new FileWriter(outputFile);
            
            String header = "INDEX | OPCODE | OP1 | OP2 | OP3 \n-------------------------------------------\n";
            try {
                writer.write(header);
            }
            catch(IOException e) {
                System.out.println("ERROR: Failed to print to file");
            }
            
            // Prints out each entry in the quad table to specified file
            for(int i = 0; i < currentIndex; i++){
                String opcode = String.valueOf(GetQuad(i, 0));
                String op1 = String.valueOf(GetQuad(i, 1));
                String op2 = String.valueOf(GetQuad(i, 2));
                String op3 = String.valueOf(GetQuad(i, 3));

                String strOut = "%-7s %-8s %-5s %-5s %-5s\n";
                String out = String.format(strOut, i, opcode, op1, op2, op3);
                try {
                    writer.write(out);            
                }
                catch(IOException e) {
                    System.out.println("ERROR: Failed to print to file");
                }
            }
            writer.close();
    }
}