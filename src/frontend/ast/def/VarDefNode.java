package frontend.ast.def;

import frontend.ast.ASTVisitor;
import frontend.ast.SingleVarDefNode;
import frontend.ast.util.position.Position;
import frontend.ast.util.type.ASTType;

import java.util.ArrayList;

public class VarDefNode extends DefinitionNode {
    public ASTType type;
    public ArrayList<SingleVarDefNode> vars = new ArrayList<>();

    public VarDefNode(Position pos, ASTType type){
        super(pos);
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}


