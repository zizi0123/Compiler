package IR.instruction;

import IR.BasicBlock;
import IR.Entity.Entity;
import IR.type.IRType;
import java.util.ArrayList;

public class PhiIns extends Instruction{
    public Entity result;
    IRType type;

    ArrayList<Entity> values = new ArrayList<>();

    ArrayList<BasicBlock> blocks = new ArrayList<>();

    public PhiIns(Entity result){
        this.result = result;
        type = result.type;
    }

    public void addPair(Entity value,BasicBlock block){
        values.add(value);
        blocks.add(block);
    }

    @Override
    public void Print() {
        System.out.println(result.toString()+" = phi "+type.toString()+" [ "+values.get(0).toString()+", %"+blocks.get(0).label+" ], [ "+values.get(1).toString()+", %"+blocks.get(1).label+" ]");
    }
}
