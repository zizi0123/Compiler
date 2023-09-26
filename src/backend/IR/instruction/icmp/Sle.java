package backend.IR.instruction.icmp;

import backend.IR.Entity.Entity;
import backend.IR.instruction.IcmpIns;

public class Sle extends IcmpIns {
    public Sle(Entity l,Entity r,Entity res){
        super(l,r,"icmp sle",res);
    }
}
