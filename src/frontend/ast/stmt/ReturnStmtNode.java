package frontend.ast.stmt;

import frontend.ast.ASTVisitor;
import frontend.ast.expr.ExprNode;
import frontend.ast.util.position.Position;
import frontend.ast.util.type.ASTType;

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
