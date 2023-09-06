package assembly.Instruction;

import assembly.operand.Reg;

public class Mv extends ASMIns{
    Reg rd;
    Reg rs;
    public Mv(Reg rd, Reg rs){
        this.rd = rd;
        this.rs = rs;
    }

    public Mv(Reg rd, Reg rs,String c){
        this.rd = rd;
        this.rs = rs;
        comment = c;
    }

    @Override
    public String toString() {
        return "mv  "+rd.toString()+", "+rs.toString()+'\n';
    }
}
