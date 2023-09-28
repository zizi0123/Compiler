package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.Imm;
import assembly.operand.Reg;

import java.util.HashSet;

public class Slt extends ASMIns {
    boolean unsigned;

    public Reg rd;
    public Reg rs1;
    public Reg rs2;
    public Imm imm;

    public Slt(Reg rd, Reg rs1, Reg rs2, boolean u) {
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
        unsigned = u;
    }

    public Slt(Reg rd, Reg rs1, Imm i, boolean u) {
        this.rd = rd;
        this.rs1 = rs1;
        imm = i;
        unsigned = u;
    }

    @Override
    public String toString() {
        String result = "slt";
        if (imm != null) result += "i";
        if (unsigned) result += "u";
        result+="  ";
        result += rd.toString() + ", " + rs1.toString() + ", ";
        if (imm != null) {
            result += imm.toString();
        } else {
            result += rs2.toString();
        }
        return result + '\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }


    @Override
    public HashSet<Reg> getUse() {
        HashSet<Reg> result = new HashSet<>();
        result.add(rs1);
        if(rs2!=null){
            result.add(rs2);
        }
        return result;
    }

    @Override
    public Reg getDef() {
        return rd;
    }

}
