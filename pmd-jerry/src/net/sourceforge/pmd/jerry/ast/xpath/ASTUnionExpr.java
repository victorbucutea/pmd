/* Generated By:JJTree: Do not edit this line. ASTUnionExpr.java */

package net.sourceforge.pmd.jerry.ast.xpath;

import net.sourceforge.pmd.jerry.ast.xpath.custom.OperatorNode;

public class ASTUnionExpr extends OperatorNode {
  public ASTUnionExpr(int id) {
    super(id);
  }

  public ASTUnionExpr(XPath2Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(XPath2ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}