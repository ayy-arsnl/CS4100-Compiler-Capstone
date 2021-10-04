import ADT.*;
import java.util.Scanner;

public class Interpreter {

    private ReserveTable reserve;

    public Interpreter(){
        this.reserve = new ReserveTable(16);
        initReserve(reserve);
    }

    public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable){
        stable.AddSymbol("n", 'v', 10);
        stable.AddSymbol("i", 'v', 0);
        stable.AddSymbol("product", 'v', 0);
        stable.AddSymbol("1", 'c', 1);
        stable.AddSymbol("$temp ", 'v', 0);

        qtable.AddQuad(5, 3, 0, 2);
        qtable.AddQuad(5, 3, 0, 1);
        qtable.AddQuad(3, 1, 0, 4);
        qtable.AddQuad(11, 4, 0, 7);
        qtable.AddQuad(2, 2, 1, 2);
        qtable.AddQuad(4, 1, 3, 1);
        qtable.AddQuad(8, 0, 0, 2);
        qtable.AddQuad(6, 0, 0, 2);

        return true;
    }

    public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable){
        stable.AddSymbol("n", 'v', 10);
        stable.AddSymbol("i", 'v', 0);
        stable.AddSymbol("sum", 'v', 0);
        stable.AddSymbol("1", 'c', 1);
        stable.AddSymbol("$temp ", 'v', 0);

        qtable.AddQuad(5, 3, 0, 2);
        qtable.AddQuad(5, 3, 0, 1);
        qtable.AddQuad(3, 1, 0, 4);
        qtable.AddQuad(11, 4, 0, 7);
        qtable.AddQuad(4, 2, 1, 2);
        qtable.AddQuad(4, 1, 3, 1);
        qtable.AddQuad(8, 0, 0, 2);
        qtable.AddQuad(6, 0, 0, 2);

        return true;
    }

    public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename){

        int PC = 0;
        int opcode, op1, op2, op3;

        loop: while(PC < Q.getMax()){
            opcode = Q.GetQuad(PC, 0);
            op1 = Q.GetQuad(PC, 1);
            op2 = Q.GetQuad(PC, 2);
            op3 = Q.GetQuad(PC, 3);

            if(TraceOn) System.out.println(makeTraceString(PC, opcode, op1, op2, op3));

            if(opcode >= 0 || opcode <= 15){ // check if opcode is valid
                switch(opcode){
                    case 0: // STOP - Terminate program
                        System.out.println("Execution terminated by program STOP");
                        PC += 1;
                        break loop;

                    case 1: // DIV - op1 / op2, place result into op3 (assume integer divide now)
                        S.UpdateSymbol(op3, 'v', (S.GetInteger(op1) / S.GetInteger(op2)));
                        PC += 1;
                        break;

                    case 2: // MUL - Compute op1 * op2, place result into op3
                        S.UpdateSymbol(op3, 'v', (S.GetInteger(op1) * S.GetInteger(op2)));
                        PC += 1;
                        break;

                    case 3: // SUB - Compute op1 - op2, place result into op3
                        S.UpdateSymbol(op3, 'v', (S.GetInteger(op1) - S.GetInteger(op2)));
                        PC += 1;
                        break;

                    case 4: // ADD - Compute op1 + op2, place result into op3
                        S.UpdateSymbol(op3, 'v', (S.GetInteger(op1) + S.GetInteger(op2)));
                        PC += 1;
                        break;

                    case 5: // MOV - Assign the value in op1 into op3 (op2 is ignored here)
                        S.UpdateSymbol(op3, S.GetKind(op3), S.GetInteger(op1));
                        PC += 1;
                        break;

                    case 6: // PRINT - Write symbol table name and value of op3 to StandardOutput (console)
                        System.out.println("Result:" + S.GetInteger(op3));
                        PC += 1;
                        break;

                    case 7: // READ - Read the next integer from StandardInput (keyboard) as value of op3
                        Scanner input = new Scanner(System.in);
                        int val = input.nextInt();
                        S.UpdateSymbol(op3, S.GetKind(op3), val);
                        PC += 1;
                        break;

                    case 8: // BR - Branch (unconditional); set program counter to op3 (QuadTable index, not S.T.)
                        PC = op3;
                        break;

                    case 9: // BINDR - Branch (unconditional); set program counter to op3 S.T. value contents (indirect)

                    case 10: // BZ - Branch Zero; if op1 = 0, set program counter to op3
                        if(S.GetInteger(op1) == 0){
                            PC = op3;
                            break;
                        }
                        break;

                    case 11: // BP - Branch Positive; if op1 > 0, set program counter to op3
                        if (S.GetInteger(op1) > 0){
                            PC = op3;
                            break;
                        }
                        PC+=1;
                        break;

                    case 12: // BN - Branch Negative; if op1 < 0, set program counter to op3
                        if(S.GetInteger(op1) < 0){
                            PC = op3;
                            break;
                        }
                        break;

                    case 13: // BNZ - Branch Not Zero; if op1 <> 0, set program counter to op3
                        if(S.GetInteger(op1) != 0){
                            PC = op3;
                            break;
                        }
                        break;

                    case 14: // BNP - Branch Not Positive; if op1 <= 0, set program counter to op3
                        if(S.GetInteger(op1) <= 0){
                            PC = op3;
                            break;
                        }
                        break;

                    case 15: // BNN - Branch Not Negative; if op1 >= 0, set program counter to op3
                        if(S.GetInteger(op1) >= 0){
                            PC = op3;
                            break;
                        }
                        break;
                }
            }
        }
    }

    private String makeTraceString(int pc, int opcode, int op1, int op2, int op3){
        String result = "";
        result = "PC = "+String.format("%04d", pc)+": "+(this.reserve.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+
                                ", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
        return result;
    }

    private void initReserve(ReserveTable optable){
        optable.Add("STOP", 0);
        optable.Add("DIV", 1);
        optable.Add("MUL", 2);
        optable.Add("SUB", 3);
        optable.Add("ADD", 4);
        optable.Add("MOV", 5);
        optable.Add("PRINT", 6);
        optable.Add("READ", 7);
        optable.Add("BR", 8);
        optable.Add("BINDR", 9);
        optable.Add("BZ", 10);
        optable.Add("BP", 11);
        optable.Add("BN", 12);
        optable.Add("BNZ", 13);
        optable.Add("BNP", 14);
        optable.Add("BNN", 15);
   }

}