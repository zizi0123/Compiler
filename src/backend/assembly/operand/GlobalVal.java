package backend.assembly.operand;

public class GlobalVal extends Val {
    String name;
    Val initVal;
    public int size;

    public GlobalVal(String name, Val initVal, int size) {
        this.name = name;
        this.initVal = initVal;
        this.size = size;
    }

    @Override
    public String toString() {
        return name;
    }

    public void print() {
        System.out.print("\t.section data\n" +
                "\t.globl " + name + "\n" +
                name + ":\n" +
                "\t.word\t" + initVal.toString() + "\n" +
                "\n");
    }
}
