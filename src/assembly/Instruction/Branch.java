package assembly.Instruction;

import IR.instruction.BranchIns;
import assembly.Block;
import assembly.operand.Reg;

public class Branch extends ASMIns{
    Reg rs1;
    Reg rs2;
    Block jumpTo;
    String operator;

    public Branch(Reg r1,Reg r2,Block j,String o){
        rs1 = r1;
        rs2 = r2;
        jumpTo = j;
        operator = o;
    }

    @Override
    public String toString() {
        return operator+"  "+rs1.toString()+", "+rs2.toString()+", "+jumpTo.name+"\n";
    }
}
