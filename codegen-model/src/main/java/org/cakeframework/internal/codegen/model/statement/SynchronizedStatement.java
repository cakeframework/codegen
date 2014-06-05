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

import org.cakeframework.internal.codegen.model.expression.Expression;
import org.cakeframework.internal.codegen.model.visitor.CodegenVisitor;
import org.cakeframework.internal.codegen.model.visitor.ModifyingCodegenVisitor;

/**
 * This class has been autogenerated
 *
 * @author Kasper Nielsen
 */
public class SynchronizedStatement extends Statement {

    /** The mutex to synchronize on. */
    private Expression mutex;

    /** The statement block. */
    private BlockStatement block;

    public SynchronizedStatement() {}

    public SynchronizedStatement(Expression mutex, BlockStatement block) {
        this.mutex = mutex;
        this.block = block;
    }

    public Expression getMutex() {
        return mutex;
    }

    public BlockStatement getBlock() {
        return block;
    }

    public void setMutex(Expression mutex) {
        this.mutex = mutex;
    }

    public void setBlock(BlockStatement block) {
        this.block = block;
    }

    public void accept(CodegenVisitor visitor) {
        visitor.visit(this);
    }

    public Object accept(ModifyingCodegenVisitor visitor) {
        return visitor.visit(this);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        return other instanceof SynchronizedStatement && equals((SynchronizedStatement) other);
    }

    public boolean equals(SynchronizedStatement other) {
        return super.equals(this) && Objects.equals(mutex, other.mutex) && Objects.equals(block, other.block);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Objects.hash(mutex, block);
    }
}