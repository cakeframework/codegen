/*
 * Copyright (c) 2008 Kasper Nielsen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* 
 * This class was automatically generated by cake.bootstrap.codegen.model.GenerateModel 
 * Available in the https://github.com/cakeframework/cake-developers/ project 
 */
package org.cakeframework.internal.codegen.model.expression;

import java.util.Objects;

import org.cakeframework.internal.codegen.model.visitor.CodegenVisitor;
import org.cakeframework.internal.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
public class BinaryExpression extends Expression {

    /** The left side of the expression. */
    private Expression left;

    /** The operator. */
    private BiOperator operator;

    /** The right side of the expression. */
    private Expression right;

    public BinaryExpression() {}

    public BinaryExpression(Expression left, BiOperator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public BiOperator getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public void setOperator(BiOperator operator) {
        this.operator = operator;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof BinaryExpression && equals((BinaryExpression) other);
    }

    public boolean equals(BinaryExpression other) {
        return super.equals(this) && Objects.equals(left, other.left) && Objects.equals(operator, other.operator) && Objects.equals(right, other.right);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(left, operator, right);
    }

    /** The operator for the expression. */
    public enum BiOperator {
        PLUS("+"), 
        MINUS("-"), 
        MULTIPLY("*"), 
        DIVIDE("/"), 
        MODULUS("%"), 
        EQUALS("=="), 
        NOT_EQUALS("!="), 
        LESS_THEN("<"), 
        GREATER_THEN(">"), 
        LESS_THEN_OR_EQUALS("<="), 
        GREATER_THEN_OR_EQUALS(">="), 
        AND("&&"), 
        NOT("!"), 
        OR("||"), 
        BINARY_AND("&"), 
        BINARY_NOT("~"), 
        BINARY_OR("|"), 
        BINARY_XOR("^"), 
        LEFT_SHIFT("<<"), 
        RIGHT_SHIFT(">>"), 
        RIGHT_UNSIGNED_SHIFT(">>>");
        private final String symbol;
        private BiOperator(String symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return symbol;
        }
    }
}