package IR;

import IR.Entity.literal.StringLiteral;
import IR.Entity.literal.voidLiteral;
import IR.instruction.ReturnIns;
import IR.type.IRClassType;
import IR.Entity.variable.GlobalVar;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static IR.type.IRTypes.irVoidType;

public class IRProgram {
    HashMap<String, IRClassType> classes = new HashMap<>();

    public HashMap<String, IRFunction> functions = new HashMap<>();

    public HashMap<String, GlobalVar> globalVars = new HashMap<>();

    public ArrayList<StringLiteral> stringLiterals = new ArrayList<>();

    public IRFunction initFunction;

    public IRClassType getClassType(String className) {
        return classes.get("%class." + className);
    }

    public HashMap<String, Integer> buildInCallTime = new HashMap<>();


    public IRProgram() {
        initFunction = new IRFunction("@global_init", false, irVoidType);
        initFunction.entryBlock.exitInstruction = new ReturnIns(new voidLiteral());
        buildInCallTime.put("@print", 0);
        buildInCallTime.put("@println", 0);
        buildInCallTime.put("@printInt", 0);
        buildInCallTime.put("@printlnInt", 0);
        buildInCallTime.put("@getString", 0);
        buildInCallTime.put("@getInt", 0);
        buildInCallTime.put("@toString", 0);
        buildInCallTime.put("@array.size", 0);
        buildInCallTime.put("@string.length", 0);
        buildInCallTime.put("@string.substring", 0);
        buildInCallTime.put("@string.parseInt", 0);
        buildInCallTime.put("@string.ord", 0);
        buildInCallTime.put("@string.add", 0);
        buildInCallTime.put("@string.eq", 0);
        buildInCallTime.put("@string.ne", 0);
        buildInCallTime.put("@string.gt", 0);
        buildInCallTime.put("@string.lt", 0);
        buildInCallTime.put("@string.le", 0);
        buildInCallTime.put("@string.ge", 0);

    }

    int getBuildInCallTime(String name) {
        if (buildInCallTime.containsKey(name)) {
            int time = buildInCallTime.get(name);
            buildInCallTime.put(name, time + 1);
            return time;
        }
        return -1;
    }

    public void Print(PrintWriter pw) {
        for (var irclass : classes.values()) {
            pw.println(irclass.toDefineString());
        }
        for (var globalVar : globalVars.values()) {
            pw.println(globalVar.name+" = global "+globalVar.type.toString()+" "+globalVar.initVal.toString());
        }
        for (var string : stringLiterals) {
            pw.println(string.name + " = private unnamed_addr constant [" + (string.val.length() + 1) + " x i8] c\"" + string.toIrVal() + "\\00\"");
        }
        String buildIn =
                "declare void @print(ptr %str)\n" +
                        "declare void @println(ptr %str)\n" +
                        "declare void @printInt(i32 %n)\n" +
                        "declare void @printlnInt(i32 %n)\n" +
                        "declare ptr @getString()\n" +
                        "declare i32 @getInt()\n" +
                        "declare ptr @toString(i32 %n)\n" +
                        "declare i32 @array.size(ptr %array)\n" +
                        "declare i32 @string.length(ptr %str)\n" +
                        "declare ptr @string.substring(ptr %str, i32 %left, i32 %right)\n" +
                        "declare i32 @string.parseInt(ptr %str)\n" +
                        "declare i32 @string.ord(ptr %str, i32 %pos)\n" +
                        "declare ptr @string.add(ptr %lhs, ptr %rhs)\n" +
                        "declare i1 @string.eq(ptr %lhs, ptr %rhs)\n" +
                        "declare i1 @string.ne(ptr %lhs, ptr %rhs)\n" +
                        "declare i1 @string.lt(ptr %lhs, ptr %rhs)\n" +
                        "declare i1 @string.gt(ptr %lhs, ptr %rhs)\n" +
                        "declare i1 @string.ge(ptr %lhs, ptr %rhs)\n" +
                        "declare i1 @string.le(ptr %lhs, ptr %rhs)declare ptr @malloc(i32 %size)\n" +
                        "declare ptr @_newPtrArray(i32 %size)\n" +
                        "declare ptr @_newIntArray(i32 %size)\n" +
                        "declare ptr @_newBoolArray(i32 %size)\n";
        pw.println(buildIn);
        for (var function : functions.values()) function.Print(pw);
        pw.close();
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
