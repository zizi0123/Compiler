package assembly.Instruction;

import assembly.operand.GlobalString;
import assembly.operand.Reg;

public class La extends ASMIns{
    public Reg rd;
    GlobalString string;
    public La(Reg r, GlobalString s){
        rd = r;
        string = s;
    }

    @Override
    public String toString() {
        return "la  "+rd.toString()+", "+string+'\n';
    }
}
