package assembly.operand;

public class VirtualReg extends Reg {
    public int id;

    String name;

    public VirtualReg(int i) {
        id = i;
        name = "v" + id;
    }

    @Override
    public String toString() {
        return name;
    }
}
