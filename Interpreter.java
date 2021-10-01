import ADT.*;

public class Interpreter {

    public Interpreter(){
        ReserveTable reserve = new ReserveTable(16);
        initReserve(reserve);
    }

    public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable){
        return true;
    }

    public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable){
        return true;
    }

    public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename){

        int PC = 0;
        int opcode, op1, op2, op3;

        while(PC < Q.getMax()){
            opcode = Q.GetQuad(PC, 0);
            switch(opcode){
                case 0:

                case 1:

                case 2:

                case 3:
                
                case 4:

                case 5:

                case 6:

                case 7:

                case 8:

                case 9:

                case 10:

                case 11:

                case 12:

                case 13:

                case 14:

                case 15:

                case 16:
            }
        }

    }

    private void initReserve(ReserveTable optable){
        optable.Add("STOP", 0);
        optable.Add("DIV", 1);
        optable.Add("MUL", 2);
        optable.Add("SUB", 3);
        optable.Add("ADD", 4);
        optable.Add("MOV", 5);
        optable.Add("STI", 6);
        optable.Add("LDI", 7);
        optable.Add("BNZ", 8);
        optable.Add("BNP", 9);
        optable.Add("BNN", 10);
        optable.Add("BZ", 11);
        optable.Add("BP", 12);
        optable.Add("BN", 13);
        optable.Add("BR", 14);
        optable.Add("BINDR", 15);
        optable.Add("PRINT", 16);
   }

}
