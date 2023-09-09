package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.Reg;

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
}
