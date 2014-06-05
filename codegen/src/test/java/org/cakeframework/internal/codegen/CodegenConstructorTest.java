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
package org.cakeframework.internal.codegen;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import org.junit.Test;

/**
 * 
 * @author Kasper Nielsen
 */
@SuppressWarnings({ "rawtypes" })
public class CodegenConstructorTest extends AbstractCodegenTest {

    @Test
    public void defaultConstructor() throws Exception {
        Class<Object> call = c.newClass("public class Test").compile();
        assertEquals(1, call.getDeclaredConstructors().length);
        call.newInstance();// does not fail
    }

    @Test
    public void singleConstructor() throws Exception {
        CodegenClass clz1 = c.newClass("public class Test implements Callable");
        clz1.addImport(Callable.class);
        clz1.addField("String value;");
        clz1.newMethod("public (String value)").add("this.value = value;");
        clz1.newMethod("public Object call()").add("return value;");

        Class<Callable> call = clz1.compile();
        assertEquals(1, call.getDeclaredConstructors().length);
        assertEquals("abc", call.getConstructor(String.class).newInstance("abc").call());
        assertEquals("cba", call.getConstructor(String.class).newInstance("cba").call());
    }

}
