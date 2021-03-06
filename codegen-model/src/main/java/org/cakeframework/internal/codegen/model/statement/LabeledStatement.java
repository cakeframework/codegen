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
package org.cakeframework.internal.codegen.model.statement;

import java.util.Objects;

import org.cakeframework.internal.codegen.model.visitor.CodegenVisitor;
import org.cakeframework.internal.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
public class LabeledStatement extends Statement {

    /** The label. */
    private String label;

    /** The statement. */
    private Statement statement;

    public LabeledStatement() {}

    public LabeledStatement(String label, Statement statement) {
        this.label = label;
        this.statement = statement;
    }

    public String getLabel() {
        return label;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof LabeledStatement && equals((LabeledStatement) other);
    }

    public boolean equals(LabeledStatement other) {
        return super.equals(this) && Objects.equals(label, other.label) && Objects.equals(statement, other.statement);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(label, statement);
    }
}
