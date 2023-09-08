package assembly.Instruction;

import assembly.ASMBlock;
import assembly.ASMVisitor;

public class J extends ASMIns{
    public ASMBlock jumpTo;

    public J(ASMBlock b){
        jumpTo = b;
    }

    @Override
    public String toString() {
        return "j  "+jumpTo.name+'\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
