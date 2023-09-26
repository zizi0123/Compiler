package backend.IR.instruction.icmp;

import backend.IR.Entity.Entity;
import backend.IR.instruction.IcmpIns;

public class Eq extends IcmpIns {
    public Eq(Entity l,Entity r,Entity res){
        super(l,r,"icmp eq",res);
    }
}
