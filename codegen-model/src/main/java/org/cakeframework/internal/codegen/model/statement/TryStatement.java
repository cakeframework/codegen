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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.cakeframework.internal.codegen.model.visitor.CodegenVisitor;
import org.cakeframework.internal.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
public class TryStatement extends Statement {

    /** The condition. */
    private BlockStatement tryBlock;

    /** The else statement. */
    private List<CatchClause> catchClauses = new ArrayList<>(2);

    /** The then statement. */
    private BlockStatement finallyBlock;

    public TryStatement() {}

    public TryStatement(BlockStatement tryBlock, List<CatchClause> catchClauses, BlockStatement finallyBlock) {
        this.tryBlock = tryBlock;
        this.catchClauses = catchClauses;
        this.finallyBlock = finallyBlock;
    }

    public BlockStatement getTryBlock() {
        return tryBlock;
    }

    public List<CatchClause> getCatchClauses() {
        return catchClauses;
    }

    public BlockStatement getFinallyBlock() {
        return finallyBlock;
    }

    public void setTryBlock(BlockStatement tryBlock) {
        this.tryBlock = tryBlock;
    }

    public void setCatchClauses(List<CatchClause> catchClauses) {
        this.catchClauses = catchClauses;
    }

    public void setFinallyBlock(BlockStatement finallyBlock) {
        this.finallyBlock = finallyBlock;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof TryStatement && equals((TryStatement) other);
    }

    public boolean equals(TryStatement other) {
        return super.equals(this) && Objects.equals(tryBlock, other.tryBlock) && Objects.equals(catchClauses, other.catchClauses) && Objects.equals(finallyBlock, other.finallyBlock);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(tryBlock, catchClauses, finallyBlock);
    }
}
