package backend.assembly.Instruction;

import backend.assembly.ASMVisitor;
import backend.assembly.operand.Imm;
import backend.assembly.operand.Reg;

public class Li extends ASMIns {
    public Reg rd;
    public Imm imm;

    public Li(Reg r, Imm i) {
        rd = r;
        imm = i;
        rd.size = 4;
    }

    public Li(Reg r, int i) {
        rd = r;
        imm = new Imm(i);
        rd.size = 4;
    }

    @Override
    public String toString() {
        return "li  " + rd.toString() + ", " + imm.toString() + '\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
