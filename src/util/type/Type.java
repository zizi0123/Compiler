package util.type;

import grammar.MxParser;
import util.error.SemanticError;

import java.util.Objects;

public class Type {
    public TypeName typeName;

    public boolean isArray;

    public int arrayDim;

    public boolean isVoid;

    public boolean isThis;

    public boolean isNull;

    public boolean isFunction;

    public Type(String baseTypeName) {
        if (Objects.equals(baseTypeName, "void")) {
            typeName = new TypeName("void", false, true);
            isVoid = true;
        } else if (Objects.equals(baseTypeName, "int")) {
            typeName = new TypeName("int", false, false);
        } else if (Objects.equals(baseTypeName, "string")) {
            typeName = new TypeName("string", false, false);
        } else if (Objects.equals(baseTypeName, "bool")) {
            typeName = new TypeName("bool", false, false);
        } else if (Objects.equals(baseTypeName, "this")) {
            typeName = new TypeName("this", false, false);
            isThis = true;
        } else if (Objects.equals(baseTypeName, "null")) {
            typeName = new TypeName("null", false, false);
            isNull = true;
        } else if (baseTypeName.equals("function")) {
            typeName = new TypeName("function", false, false);
            isFunction = true;
        } else {
            throw new SemanticError("type error: " + baseTypeName + " is not a base type!");
        }
    }

    public Type(TypeName typeName, boolean isVoid, boolean isArray, int arrayDim) {
        this.typeName = typeName;
        this.isVoid = isVoid;
        this.isArray = isArray;
        this.arrayDim = arrayDim;
    }

    public Type(MxParser.ReturnTypeContext ctx) {
        this.typeName = new TypeName(ctx);
        this.isVoid = typeName.isVoid;
        if (ctx.type().LBracket().isEmpty()) {
        } else {
            isArray = true;
            arrayDim = ctx.type().LBracket().size();
        }
    }


    public Type(MxParser.TypeContext ctx) {
        this.typeName = new TypeName(ctx);
        if (ctx.LBracket().isEmpty()) {
        } else {
            isArray = true;
            arrayDim = ctx.LBracket().size();
        }
    }

    public boolean isNonReferenceType() {
        return !isArray && !typeName.isClass;
    }

    public boolean equals(Type a) {
        if (a == null || a.typeName == null) return false;
        boolean checkArray = ((!isArray && !a.isArray) || (isArray && a.isArray && (arrayDim == a.arrayDim)));
        boolean val = (isNull == a.isNull && isThis == a.isThis && isVoid == a.isVoid && checkArray);
        return val && typeName.equals(a.typeName);
    }

}
