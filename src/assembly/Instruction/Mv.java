package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.Reg;

import java.util.HashSet;

public class Mv extends ASMIns{
    public Reg rd;
    public Reg rs;
    public Mv(Reg rd, Reg rs){
        this.rd = rd;
        this.rs = rs;
        rd.size = rs.size;
    }

    @Override
    public String toString() {
        return "mv  "+rd.toString()+", "+rs.toString()+'\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HashSet<Reg> getUse() {
        HashSet<Reg> result = new HashSet<>();
        result.add(rs);
        return result;
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
        rs = (rs == olde) ? newe : rs;
    }
}
