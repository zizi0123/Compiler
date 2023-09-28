package assembly.Instruction;

import assembly.ASMBlock;
import assembly.ASMVisitor;
import assembly.operand.Reg;

import java.util.HashSet;

public class Branch extends ASMIns {
    public Reg rs1;
    public Reg rs2;
    public ASMBlock jumpTo;
    String operator;

    public Branch(Reg r1, Reg r2, ASMBlock j, String o) {
        rs1 = r1;
        rs2 = r2;
        jumpTo = j;
        operator = o;
    }

    @Override
    public String toString() {
        return operator + "  " + rs1.toString() + ", " + rs2.toString() + ", " + jumpTo.name + "\n";
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HashSet<Reg> getUse() {
        HashSet<Reg> result = new HashSet<>();
        result.add(rs1);
        result.add(rs2);
        return result;
    }

    @Override
    public Reg getDef() {
        return null;
    }
}
