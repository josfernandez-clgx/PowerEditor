//
// Generated by JTB 1.2.2
//

package com.mindbox.pe.server.parser.jtb.rule.visitor;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.*;

/**
 * All void visitors must implement this interface.
 */
public interface Visitor {
   //
   // void Auto class visitors
   //
   public void visit(NodeList n);
   public void visit(NodeListOptional n);
   public void visit(NodeOptional n);
   public void visit(NodeSequence n);
   public void visit(NodeToken n);

   //
   // User-generated visitor methods below
   //

   /**
    * <PRE>
    * f0 -> "if"
    * f1 -> LHS()
    * f2 -> "then"
    * f3 -> RHS()
    * f4 -> &lt;EOF&gt;
    * </PRE>
    */
   public void visit(DeploymentRule n);

   /**
    * <PRE>
    * f0 -> OrExpression()
    * </PRE>
    */
   public void visit(LHS n);

   /**
    * <PRE>
    * f0 -> AndExpression()
    * f1 -> ( ( "||" | "or" ) AndExpression() )*
    * </PRE>
    */
   public void visit(OrExpression n);

   /**
    * <PRE>
    * f0 -> ExistExpression()
    * f1 -> ( ( "and" | "&&" ) ExistExpression() )*
    * </PRE>
    */
   public void visit(AndExpression n);

   /**
    * <PRE>
    * f0 -> ExistExpressionPrefix() "with" ConditionalExpression()
    *       | ConditionalExpression()
    * </PRE>
    */
   public void visit(ExistExpression n);

   /**
    * <PRE>
    * f0 -> ( ExistQualifier() | ExistQuantifier() ) Reference() [ &lt;IDENTIFIER&gt; ] [ ExcludingQualifier() ]
    *       | AllQualifier() Reference()
    * </PRE>
    */
   public void visit(ExistExpressionPrefix n);

   /**
    * <PRE>
    * f0 -> ( "exists" | "any" )
    * </PRE>
    */
   public void visit(ExistQualifier n);

   /**
    * <PRE>
    * f0 -> "all"
    * </PRE>
    */
   public void visit(AllQualifier n);

   /**
    * <PRE>
    * f0 -> "at"
    * f1 -> ( "least" | "most" )
    * f2 -> &lt;INTEGER_LITERAL&gt;
    * </PRE>
    */
   public void visit(ExistQuantifier n);

   /**
    * <PRE>
    * f0 -> "excluding"
    * f1 -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(ExcludingQualifier n);

   /**
    * <PRE>
    * f0 -> AdditiveExpression()
    * f1 -> ( ( ConditionalOperator() | MembershipOperator() ) AdditiveExpression() )*
    * </PRE>
    */
   public void visit(ConditionalExpression n);

   /**
    * <PRE>
    * f0 -> "=="
    *       | "!="
    *       | "&gt;"
    *       | "&lt;"
    *       | "&gt;="
    *       | "&lt;="
    *       | "is"
    * </PRE>
    */
   public void visit(ConditionalOperator n);

   /**
    * <PRE>
    * f0 -> "between"
    *       | "in"
    * </PRE>
    */
   public void visit(MembershipOperator n);

   /**
    * <PRE>
    * f0 -> MultiplicativeExpression()
    * f1 -> ( ( "+" | "-" ) MultiplicativeExpression() )*
    * </PRE>
    */
   public void visit(AdditiveExpression n);

   /**
    * <PRE>
    * f0 -> UnaryExpression()
    * f1 -> ( ( "*" | "/" | "%" ) UnaryExpression() )*
    * </PRE>
    */
   public void visit(MultiplicativeExpression n);

   /**
    * <PRE>
    * f0 -> ( "!" | "~" | "+" | "-" ) UnaryExpression()
    *       | PrimaryExpression()
    * </PRE>
    */
   public void visit(UnaryExpression n);

   /**
    * <PRE>
    * f0 -> LiteralExpression()
    *       | LiteralList()
    *       | Reference()
    *       | "(" OrExpression() ")"
    * </PRE>
    */
   public void visit(PrimaryExpression n);

   /**
    * <PRE>
    * f0 -> &lt;INTEGER_LITERAL&gt;
    *       | &lt;FLOATING_POINT_LITERAL&gt;
    *       | &lt;CHARACTER_LITERAL&gt;
    *       | &lt;STRING_LITERAL&gt;
    *       | BooleanLiteral()
    *       | NullLiteral()
    *       | ColumnLiteral()
    *       | CellValueLiteral()
    *       | ProductIDLiteral()
    * </PRE>
    */
   public void visit(LiteralExpression n);

   /**
    * <PRE>
    * f0 -> "["
    * f1 -> LiteralExpression()
    * f2 -> ( "," LiteralExpression() )*
    * f3 -> "]"
    * </PRE>
    */
   public void visit(LiteralList n);

   /**
    * <PRE>
    * f0 -> &lt;IDENTIFIER&gt;
    * f1 -> ( "." &lt;IDENTIFIER&gt; )*
    * </PRE>
    */
   public void visit(Reference n);

   /**
    * <PRE>
    * f0 -> "true"
    *       | "false"
    * </PRE>
    */
   public void visit(BooleanLiteral n);

   /**
    * <PRE>
    * f0 -> "null"
    * </PRE>
    */
   public void visit(NullLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "column"
    * f2 -> &lt;INTEGER_LITERAL&gt;
    * f3 -> "%"
    * </PRE>
    */
   public void visit(ColumnLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "cellValue"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(CellValueLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "productID"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(ProductIDLiteral n);

   /**
    * <PRE>
    * f0 -> &lt;IDENTIFIER&gt;
    * f1 -> Arguments()
    * </PRE>
    */
   public void visit(RHS n);

   /**
    * <PRE>
    * f0 -> "("
    * f1 -> [ ArgumentList() ]
    * f2 -> ")"
    * </PRE>
    */
   public void visit(Arguments n);

   /**
    * <PRE>
    * f0 -> ArgumentLiteral()
    * f1 -> ( "," ArgumentLiteral() )*
    * </PRE>
    */
   public void visit(ArgumentList n);

   /**
    * <PRE>
    * f0 -> &lt;IDENTIFIER&gt;
    *       | &lt;INTEGER_LITERAL&gt;
    *       | &lt;FLOATING_POINT_LITERAL&gt;
    *       | &lt;CHARACTER_LITERAL&gt;
    *       | &lt;STRING_LITERAL&gt;
    *       | BooleanLiteral()
    *       | NullLiteral()
    *       | ColumnLiteral()
    *       | CellValueLiteral()
    *       | RuleNameLiteral()
    *       | MessageLiteral()
    *       | RowNumberLiteral()
    *       | CategoryIDLiteral()
    *       | CategoryNameLiteral()
    *       | ChannelIDLiteral()
    *       | InvestorIDLiteral()
    *       | ProductIDLiteral()
    *       | LineageIDLiteral()
    *       | ActivationDateLiteral()
    *       | ExpirationDateLiteral()
    *       | ReferenceInArgument()
    *       | ListCreationArguments()
    * </PRE>
    */
   public void visit(ArgumentLiteral n);

   /**
    * <PRE>
    * f0 -> "|"
    * f1 -> &lt;IDENTIFIER&gt;
    * f2 -> ( "." &lt;IDENTIFIER&gt; )*
    * f3 -> "|"
    * </PRE>
    */
   public void visit(ReferenceInArgument n);

   /**
    * <PRE>
    * f0 -> "$create"
    * f1 -> ( "," ArgumentLiteral() )*
    * f2 -> ","
    * f3 -> "create$"
    * </PRE>
    */
   public void visit(ListCreationArguments n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "message"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(MessageLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "ruleName"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(RuleNameLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "rowNumber"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(RowNumberLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "categoryID"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(CategoryIDLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "categoryName"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(CategoryNameLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "channelID"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(ChannelIDLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "investorID"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(InvestorIDLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "lineageID"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(LineageIDLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "activationDate"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(ActivationDateLiteral n);

   /**
    * <PRE>
    * f0 -> "%"
    * f1 -> "expirationDate"
    * f2 -> "%"
    * </PRE>
    */
   public void visit(ExpirationDateLiteral n);

}

