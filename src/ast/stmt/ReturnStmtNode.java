package ast.stmt;

import ast.ASTVisitor;
import ast.expr.ExprNode;
import ast.util.position.Position;
import ast.util.type.ASTType;

public class ReturnStmtNode extends StmtNode{
    public ExprNode expr;
    public ReturnStmtNode(Position pos){
        super(pos);
        expr = null;
    }

    public ASTType returnType;

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
