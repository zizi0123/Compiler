package backend.assembly.operand;

public class StackOffset extends Imm {
    public StackVal stackVal;

    public StackOffset(StackVal s) {
        super(s.offset);
        stackVal = s;
    }

    @Override
    public String toString() {
        return Integer.toString(stackVal.offset);
    }
}
