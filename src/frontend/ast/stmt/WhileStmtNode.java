package frontend.ast.stmt;

import frontend.ast.ASTVisitor;
import frontend.ast.expr.ExprNode;
import frontend.ast.util.position.Position;

public class WhileStmtNode extends StmtNode {
    public ExprNode condition;
    public StmtNode stmts;

    public WhileStmtNode(Position pos){
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
