package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Srem extends ArithmeticIns {
    public Srem(Entity l,Entity r,Entity res){
        super(l,r,"srem",res);
    }
}
