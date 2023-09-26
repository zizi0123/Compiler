package backend.assembly.operand;

public class PhysicalReg extends Reg{
    public int id;
    public String name;

    public PhysicalReg(int i){
        id = i;
    }

    @Override
    public String toString() {
        return name;
    }
}
