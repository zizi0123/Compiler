package backend.IR.instruction.icmp;

import backend.IR.Entity.Entity;
import backend.IR.instruction.IcmpIns;

public class Slt extends IcmpIns {
    public Slt(Entity l,Entity r,Entity res){
        super(l,r,"icmp slt",res);
    }
}
