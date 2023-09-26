package backend.IR.instruction.icmp;

import backend.IR.Entity.Entity;
import backend.IR.instruction.IcmpIns;

public class Sgt extends IcmpIns {
    public Sgt(Entity l,Entity r,Entity res){
        super(l,r,"icmp sgt",res);
    }
}
