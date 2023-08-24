package IR;

import IR.literal.Literal;
import IR.literal.StringLiteral;
import IR.type.IRClassType;
import IR.variable.GlobalVar;
import IR.variable.LocalVar;

import java.util.ArrayList;
import java.util.HashMap;

import static IR.type.IRTypes.irVoidType;

public class Program {
    HashMap<String, IRClassType> classes = new HashMap<>();

    HashMap<String, IRFunction> functions = new HashMap<>();

    HashMap<String, GlobalVar> globalVars = new HashMap<>();

    ArrayList<StringLiteral> stringLiterals = new ArrayList<>();

    public IRFunction initFunction;

    public IRFunction mainFunction;

    public IRClassType getClassType(String className) {
        return classes.get(className);
    }

    public HashMap<String, Integer> buildInCallTime = new HashMap<>();


    public Program() {
        initFunction = new IRFunction("@global_init", false, irVoidType); //todo 将initFunction插入所有function中
        buildInCallTime.put("@print", 0);
        buildInCallTime.put("@println", 0);
        buildInCallTime.put("@printInt", 0);
        buildInCallTime.put("@printlnInt", 0);
        buildInCallTime.put("@getString", 0);
        buildInCallTime.put("@getInt", 0);
        buildInCallTime.put("@toString", 0);
        buildInCallTime.put("@array_size", 0);
        buildInCallTime.put("@string_length", 0);
        buildInCallTime.put("@string_substring", 0);
        buildInCallTime.put("@string_parseInt", 0);
        buildInCallTime.put("@string_ord", 0);
        buildInCallTime.put("@string_add", 0);
        buildInCallTime.put("@string_eq", 0);
        buildInCallTime.put("@string_ne", 0);
        buildInCallTime.put("@string_gt", 0);
        buildInCallTime.put("@string_lt", 0);
        buildInCallTime.put("@string_le", 0);
        buildInCallTime.put("@string_ge", 0);

    }

    int getBuildInCallTime(String name) {
        if (buildInCallTime.containsKey(name)) {
            int time = buildInCallTime.get(name);
            buildInCallTime.put(name, time + 1);
            return time;
        }
        return -1;
    }

}
