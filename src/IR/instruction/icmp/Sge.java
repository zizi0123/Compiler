package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.IcmpIns;

public class Sge extends IcmpIns {
    public Sge(Entity l,Entity r,Entity res){
        super(l,r,"icmp sge",res);
    }
}
