package org.sonar.samples.java.checks;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.sonar.check.Rule;
import org.sonar.java.model.JavaTree;
import org.sonar.java.model.SyntacticEquivalence;
import org.sonar.java.model.expression.BinaryExpressionTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.*;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@Rule(key = "AvoidMaskedSwitch")
public class AvoidMaskedSwitchCheck extends BaseTreeVisitor implements JavaFileScanner {

  private JavaFileScannerContext context;

  @Override
  public void scanFile(JavaFileScannerContext context) {
    this.context = context;

    scan(context.getTree());
  }

  @Override
  public void visitIfStatement(IfStatementTree tree) {
    ExpressionTree baseCondition = tree.condition();

    StatementTree statement = tree.elseStatement();
    while (!tree.parent().is(Tree.Kind.IF_STATEMENT) && statement != null && statement.is(Tree.Kind.IF_STATEMENT)) {
      IfStatementTree ifStatement = (IfStatementTree) statement;
      ExpressionTree condition = ifStatement.condition();
      if (areConditionsValueNameEquivalent(baseCondition, condition)) {
        context.reportIssue(
            this,
            condition,
            "This branch should be refactored because you are implementing a switch in the sequence of \"if/else if\" statements",
            ImmutableList.of(new JavaFileScannerContext.Location("Original", condition)),
            null
        );
      }
      statement = ifStatement.elseStatement();
    }

    super.visitIfStatement(tree);
  }

  private static boolean areConditionsValueNameEquivalent(@Nullable Tree leftNode, @Nullable Tree rightNode) {
    if(leftNode instanceof BinaryExpressionTreeImpl) {
      return ((BinaryExpressionTreeImpl) leftNode).leftOperand().symbolType().name().equals(((BinaryExpressionTreeImpl) rightNode).leftOperand().symbolType().name());
    } else {
      return false;
    }
  }

}