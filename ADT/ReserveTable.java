// Alexander Urquhart
// 8 Sept 2021
// CS4100-001
// HW1 - Compiler Foundations

package ADT;
import java.lang.String;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReserveTable {
	private int elementCount;
	private Operation[] table;

    public ReserveTable(int maxSize){
        this.table = new Operation[maxSize];
        elementCount = 0;
    }

    public int Add(String name, int code){
        Operation newOP = new Operation(name, code);
        table[elementCount] = newOP;
        return elementCount++; // returns value of elementCount and then increments
    }

    public int LookupName(String name){
        for(int i = 0; i < elementCount; i++){
            if(table[i].getName().compareToIgnoreCase(name) == 0){
                return table[i].getCode();
            }
        }
        return -1; // if no operation name is found
    }

    public String LookupCode(int code){
        for(int i = 0; i < elementCount; i++){
            if(table[i].getCode() == code){
                return table[i].getName();
            }
        }
        return null; // if no operation name is found
    }

    public void PrintReserveTable(String filename) throws IOException{
        File outputFile = new File(filename);
        FileWriter writer = new FileWriter(outputFile);
        
        String header = "INDEX | OPCODE | NAME \n-----------------------\n";
        try {
        	writer.write(header);
        }
    	catch(IOException e) {
    	}
        
        for(int i = 0; i < elementCount; i++){
        	String name = this.table[i].getName();
        	String code = String.valueOf(this.table[i].getCode());
        	String strOut = "%-7s %-8s %-8s\n";
        	String out = String.format(strOut, i, code, name);
        	try {
            	writer.write(out);
            	
            }
        	catch(IOException e) {
        	}
        }
        writer.close();
    }
    
    class Operation {
    	private String name;
    	private int code;
    	public Operation(String name, int code) {
    		this.name = name;
    		this.code = code;
    	}
    	public int getCode() {
    		return code;
    	}
    	
    	public String getName() {
    		return name;
    	}
    }
}
