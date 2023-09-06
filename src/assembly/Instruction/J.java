package assembly.Instruction;

import assembly.Block;

public class J extends ASMIns{
    public Block jumpTo;

    public J(Block b){
        jumpTo = b;
    }

    @Override
    public String toString() {
        return "j  "+jumpTo.name+'\n';
    }
}
