package backend.IR.instruction.icmp;

import backend.IR.Entity.Entity;
import backend.IR.instruction.IcmpIns;

public class Sge extends IcmpIns {
    public Sge(Entity l,Entity r,Entity res){
        super(l,r,"icmp sge",res);
    }
}
