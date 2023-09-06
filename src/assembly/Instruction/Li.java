package assembly.Instruction;

import assembly.operand.Imm;
import assembly.operand.Reg;

public class Li extends ASMIns {
    public Reg rd;
    Imm imm;

    public Li(Reg r, Imm i) {
        rd = r;
        imm = i;
    }

    public Li(Reg r, int i) {
        rd = r;
        imm = new Imm(i);
    }

    @Override
    public String toString() {
        return "li  " + rd.toString() + ", " + imm.toString() + '\n';
    }
}
