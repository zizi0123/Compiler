package util.type;

import grammar.MxParser;

public class TypeName {
    public String typeName;

    public boolean isClass;

    public boolean isVoid;

    public TypeName(MxParser.ReturnTypeContext ctx) {
        if (ctx.Void() == null) {
            this.isVoid = false;
            typeName = ctx.type().typeName().getText();
            this.isClass = ctx.type().typeName().Identifier() != null;
        } else {
            isVoid = true;
        }
    }

    public TypeName(MxParser.TypeContext ctx) {
        this.isVoid = false;
        typeName = ctx.typeName().getText();
        this.isClass = ctx.typeName().Identifier() != null;
    }

    public TypeName(String typeName, boolean isClass, boolean isVoid){
        this.typeName = typeName;
        this.isClass = isClass;
        this.isVoid = isVoid;
    }

    public TypeName(MxParser.TypeNameContext ctx){
        this.isVoid = false;
        typeName = ctx.getText();
        this.isClass = ctx.Identifier() != null;
    }

    public boolean equals(TypeName a){
        return a!=null && isClass == a.isClass && isVoid == a.isVoid && typeName.equals(a.typeName);
    }
}