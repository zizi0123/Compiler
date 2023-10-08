package assembly.Instruction;

import assembly.ASMBlock;
import assembly.ASMVisitor;
import assembly.operand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> getUse() {
        return new HashSet<>();
    }

    @Override
    public HashSet<Reg> getDef() {
        return new HashSet<>();
    }

    @Override
    public void replace(Reg olde, Reg newe) {
    }
}
