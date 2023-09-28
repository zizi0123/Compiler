package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.IcmpIns;

public class Slt extends IcmpIns {
    public Slt(Entity l,Entity r,Entity res){
        super(l,r,"icmp slt",res);
    }
}
