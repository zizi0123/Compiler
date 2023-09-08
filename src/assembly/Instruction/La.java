package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.GlobalString;
import assembly.operand.Reg;

public class La extends ASMIns{
    public Reg rd;
    GlobalString string;
    public La(Reg r, GlobalString s){
        rd = r;
        string = s;
        rd.size = 4;
    }

    @Override
    public String toString() {
        return "la  "+rd.toString()+", "+string+'\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }


}
