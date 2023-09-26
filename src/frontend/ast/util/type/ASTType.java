package frontend.ast.util.type;

import frontend.grammar.MxParser;
import frontend.ast.util.error.SemanticError;
import frontend.ast.util.position.Position;
import frontend.ast.util.scope.Scope;

import java.util.Objects;

public class ASTType {
    public TypeName typeName;

    public boolean isArray;

    public int arrayDim;

    public boolean isVoid;

    public boolean isThis;

    public boolean isNull;

    public boolean isFunction;

    public ASTType(String baseTypeName, Position pos) {
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
            throw new SemanticError("type error: " + baseTypeName + " is not a base type!", pos);
        }
    }

    public ASTType(TypeName typeName, boolean isVoid, boolean isArray, int arrayDim) {
        this.typeName = typeName;
        this.isVoid = isVoid;
        this.isArray = isArray;
        this.arrayDim = arrayDim;
    }

    public ASTType(MxParser.ReturnTypeContext ctx) {
        this.typeName = new TypeName(ctx);
        this.isVoid = typeName.isVoid;
        if (!isVoid && !ctx.type().LBracket().isEmpty()) {
            isArray = true;
            arrayDim = ctx.type().LBracket().size();
        }
    }


    public ASTType(MxParser.TypeContext ctx) {
        this.typeName = new TypeName(ctx);
        if (!ctx.LBracket().isEmpty()) {
            isArray = true;
            arrayDim = ctx.LBracket().size();
        }
    }

    public boolean isReferenceType() {
        return isArray || typeName.isClass || typeName.typeName.equals("string") || isThis;
    }

    public boolean equals(ASTType a) {
        if (a == null || a.typeName == null) return false;
        boolean checkArray = ((!isArray && !a.isArray) || (isArray && a.isArray && (arrayDim == a.arrayDim)));
        boolean val = (isNull == a.isNull && isThis == a.isThis && isVoid == a.isVoid && checkArray);
        return val && typeName.equals(a.typeName);
    }

    //consider this type and class type
    public boolean equals(ASTType a, Scope scope) {
        if (!scope.isInClass) {
            return this.equals(a);
        }
        if (a == null || a.typeName == null) return false;
        boolean checkArray = ((!isArray && !a.isArray) || (isArray && a.isArray && (arrayDim == a.arrayDim)));
        boolean val = (isNull == a.isNull && isVoid == a.isVoid && checkArray);
        if (!val) return false;
        if (!this.isThis && !a.isThis) {
            return typeName.equals(a.typeName);
        }
        if (a.isThis) {
            return this.isThis || (this.typeName.isClass && this.typeName.typeName.equals(scope.classDefNode.className));
        } else {
            return a.typeName.isClass && a.typeName.typeName.equals(scope.classDefNode.className);
        }
    }

    //Allow a reference type and a null type
    public boolean equalsNull(ASTType a, Scope scope){
        if(this.equals(a,scope)){
            return true;
        }else{
            return this.isNull && a.isReferenceType() || a.isNull && this.isReferenceType();
        }
    }


}
