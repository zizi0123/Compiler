package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Srem extends ArithmeticIns {
    public Srem(Entity l,Entity r,Entity res){
        super(l,r,"srem",res);
    }
}
