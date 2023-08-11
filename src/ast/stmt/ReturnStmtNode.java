package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import ast.expr.TernaryExprNode;
import util.position.Position;
import util.type.Type;

public class ReturnStmtNode extends StmtNode{
    public ExprNode expr;
    public ReturnStmtNode(Position pos){
        super(pos);
        expr = null;
    }

    public Type returnType;

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
