package assembly.operand;

public class GlobalString extends Val {
    String value;

    public String name;

    public GlobalString(String val, String name) {
        value = val.replace("\\","\\\\").replace("\n", "\\n").replace("\"","\\\"");
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void print() {
        System.out.print(".section rodata\n" +
                name + ":\n" +
                "\t.asciz \""+value+"\"\n" +
                "\n");
    }
}
