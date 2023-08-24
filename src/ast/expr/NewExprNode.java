package ast.expr;

import ast.ASTVisitor;
import ast.util.position.Position;
import ast.util.type.TypeName;

import java.util.ArrayList;

public class NewExprNode extends ExprNode{
    public boolean isArray;
    public TypeName typeName;
    public String irClassName;
    public int arrDim;
    public ArrayList<ExprNode> exprs = new ArrayList<>();

    public NewExprNode(Position pos){
        super(pos);
    }

    @Override
    public boolean isLeftValue() {
        return false;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
