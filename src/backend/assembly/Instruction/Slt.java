package backend.assembly.Instruction;

import backend.assembly.ASMVisitor;
import backend.assembly.operand.Imm;
import backend.assembly.operand.Reg;

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
}
