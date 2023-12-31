package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.Imm;
import assembly.operand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> getUse() {
        return new HashSet<>();
    }

    @Override
    public HashSet<Reg> getDef() {
        HashSet<Reg> result = new HashSet<>();
        result.add(rd);
        return result;
    }

    @Override
    public void replace(Reg olde, Reg newe) {
        rd = (rd == olde) ? newe : rd;
    }


}
