package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.GlobalString;
import assembly.operand.Reg;

import java.util.HashSet;

public class La extends ASMIns {
    public Reg rd;
    GlobalString gbstring;

    public La(Reg r, GlobalString s) {
        rd = r;
        gbstring = s;
        rd.size = 4;
    }

    @Override
    public String toString() {
        return "la  " + rd.toString() + ", " + gbstring + '\n';
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
