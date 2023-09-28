package ast.def;

import ast.ASTVisitor;
import ast.SingleVarDefNode;
import ast.util.position.Position;
import ast.util.type.ASTType;

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


