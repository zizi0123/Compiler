package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.IcmpIns;

public class Eq extends IcmpIns {
    public Eq(Entity l,Entity r,Entity res){
        super(l,r,"icmp eq",res);
    }
}
