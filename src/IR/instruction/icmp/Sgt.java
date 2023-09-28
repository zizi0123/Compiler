package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.IcmpIns;

public class Sgt extends IcmpIns {
    public Sgt(Entity l,Entity r,Entity res){
        super(l,r,"icmp sgt",res);
    }
}
