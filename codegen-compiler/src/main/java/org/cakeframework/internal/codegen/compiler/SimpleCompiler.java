/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.cakeframework.internal.codegen.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import org.cakeframework.internal.codegen.compiler.compiler.CompileException;
import org.cakeframework.internal.codegen.compiler.compiler.Cookable;
import org.cakeframework.internal.codegen.compiler.compiler.ICookable;
import org.cakeframework.internal.codegen.compiler.compiler.ISimpleCompiler;
import org.cakeframework.internal.codegen.compiler.compiler.Location;
import org.cakeframework.internal.codegen.compiler.util.ClassFile;

/**
 * To set up a {@link SimpleCompiler} object, proceed as described for {@link ISimpleCompiler}. Alternatively, a number
 * of "convenience constructors" exist that execute the described steps instantly.
 */
public class SimpleCompiler extends Cookable implements ISimpleCompiler {
    // private static final boolean DEBUG = false;

    // Set when "cook()"ing.
    private AuxiliaryClassLoader classLoader = null;
    protected boolean debugLines = this.debugSource;

    protected boolean debugSource = Boolean.getBoolean(ICookable.SYSTEM_PROPERTY_SOURCE_DEBUGGING_ENABLE);
    protected boolean debugVars = this.debugSource;

    private IClassLoader iClassLoader = null;

    private Class[] optionalAuxiliaryClasses = null;
    private ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
    private ClassLoader result = null;

    public SimpleCompiler() {}

    /**
     * Equivalent to
     * 
     * <pre>
     * SimpleCompiler sc = new SimpleCompiler();
     * sc.setParentClassLoader(optionalParentClassLoader);
     * sc.cook(scanner);
     * </pre>
     * 
     * @see #SimpleCompiler()
     * @see #setParentClassLoader(ClassLoader)
     * @see Cookable#cook(Reader)
     */
    public SimpleCompiler(Scanner scanner, ClassLoader optionalParentClassLoader) throws IOException, CompileException {
        this.setParentClassLoader(optionalParentClassLoader);
        this.cook(scanner);
    }

    /**
     * Equivalent to
     * 
     * <pre>
     * SimpleCompiler sc = new SimpleCompiler();
     * sc.cook(fileName);
     * </pre>
     * 
     * @see #SimpleCompiler()
     * @see Cookable#cookFile(String)
     */
    public SimpleCompiler(String fileName) throws IOException, CompileException {
        this.cookFile(fileName);
    }

    /**
     * Equivalent to
     * 
     * <pre>
     * SimpleCompiler sc = new SimpleCompiler();
     * sc.cook(optionalFileName, is);
     * </pre>
     * 
     * @see #SimpleCompiler()
     * @see Cookable#cook(String, InputStream)
     */
    public SimpleCompiler(String optionalFileName, InputStream is) throws IOException, CompileException {
        this.cook(optionalFileName, is);
    }

    /**
     * Equivalent to
     * 
     * <pre>
     * SimpleCompiler sc = new SimpleCompiler();
     * sc.cook(optionalFileName, in);
     * </pre>
     * 
     * @see #SimpleCompiler()
     * @see Cookable#cook(String, Reader)
     */
    public SimpleCompiler(String optionalFileName, Reader in) throws IOException, CompileException {
        this.cook(optionalFileName, in);
    }

    /**
     * Throw an {@link IllegalStateException} if this {@link Cookable} is already cooked.
     */
    protected void assertNotCooked() {
        if (this.classLoader != null) {
            throw new IllegalStateException("Already cooked");
        }
    }

    /**
     * Convert an array of {@link Class}es into an array of{@link Java.Type}s.
     */
    protected Java.Type[] classesToTypes(Location location, Class[] classes) {
        Java.Type[] types = new Java.Type[classes.length];
        for (int i = 0; i < classes.length; ++i) {
            types[i] = this.classToType(location, classes[i]);
        }
        return types;
    }

    /**
     * Wrap a reflection {@link Class} in a {@link Java.Type} object.
     */
    protected Java.Type classToType(Location location, final Class optionalClass) {
        if (optionalClass == null) {
            return null;
        }

        this.classLoader.addAuxiliaryClass(optionalClass);

        IClass iClass;
        try {
            iClass = this.iClassLoader.loadIClass(Descriptor.fromClassName(optionalClass.getName()));
        } catch (ClassNotFoundException ex) {
            throw new JaninoRuntimeException("Loading IClass \"" + optionalClass.getName() + "\": " + ex);
        }
        if (iClass == null) {
            throw new JaninoRuntimeException("Cannot load class \"" + optionalClass.getName()
                    + "\" through the given ClassLoader");
        }

        return new Java.SimpleType(location, iClass);
    }

    /**
     * Compile the given compilation unit. (A "compilation unit" is typically the contents of a Java&trade; source
     * file.)
     * 
     * @param compilationUnit
     *            The parsed compilation unit
     * @return The {@link ClassLoader} into which the compiled classes were defined
     * @throws CompileException
     */
    protected final ClassLoader compileToClassLoader(Java.CompilationUnit compilationUnit) throws CompileException {

        // Compile compilation unit to class files.
        ClassFile[] classFiles = new UnitCompiler(compilationUnit, this.iClassLoader).compileUnit(this.debugSource,
                this.debugLines, this.debugVars);

        // Convert the class files to bytes and store them in a Map.
        final Map classes = new HashMap(); // String className => byte[] data
        for (int i = 0; i < classFiles.length; ++i) {
            ClassFile cf = classFiles[i];
            byte[] contents = cf.toByteArray();
            // if (DEBUG) {
            // try {
            // new de.unkrig.jdisasm.Disassembler().disasm(new ByteArrayInputStream(contents));
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            // }
            classes.put(cf.getThisClassName(), contents);
        }

        // Create a ClassLoader that loads the generated classes.
        this.result = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return new ByteArrayClassLoader(classes, // classes
                        SimpleCompiler.this.classLoader // parent
                );
            }
        });
        return this.result;
    }

    /**
     * Cook this compilation unit directly. See {@link Cookable#cook}
     */
    public void cook(Java.CompilationUnit compilationUnit) throws CompileException {
        this.setUpClassLoaders();

        // Compile the classes and load them.
        this.compileToClassLoader(compilationUnit);
    }

    public void cook(Scanner scanner) throws CompileException, IOException {
        this.setUpClassLoaders();

        // Parse the compilation unit.
        Java.CompilationUnit compilationUnit = new Parser(scanner).parseCompilationUnit();

        // Compile the classes and load them.
        this.compileToClassLoader(compilationUnit);
    }

    public final void cook(String optionalFileName, Reader r) throws CompileException, IOException {
        this.cook(new Scanner(optionalFileName, r));
    }

    /**
     * Two {@link SimpleCompiler}s are regarded equal iff
     * <ul>
     * <li>Both are objects of the same class (e.g. both are {@link ScriptEvaluator}s)
     * <li>Both generated functionally equal classes as seen by {@link ByteArrayClassLoader#equals(Object)}
     * </ul>
     */
    public boolean equals(Object o) {
        if (!(o instanceof SimpleCompiler)) {
            return false;
        }
        SimpleCompiler that = (SimpleCompiler) o;
        if (this.getClass() != that.getClass()) {
            return false;
        }
        if (this.result == null || that.result == null) {
            throw new IllegalStateException("Equality can only be checked after cooking");
        }
        return this.result.equals(that.result);
    }

    public ClassLoader getClassLoader() {
        if (this.getClass() != SimpleCompiler.class) {
            throw new IllegalStateException("Must not be called on derived instances");
        }
        if (this.result == null) {
            throw new IllegalStateException("Must only be called after \"cook()\"");
        }
        return this.result;
    }

    public int hashCode() {
        return this.classLoader.hashCode();
    }

    public void setDebuggingInformation(boolean debugSource, boolean debugLines, boolean debugVars) {
        this.debugSource = debugSource;
        this.debugLines = debugLines;
        this.debugVars = debugVars;
    }

    public void setParentClassLoader(ClassLoader optionalParentClassLoader) {
        this.setParentClassLoader(optionalParentClassLoader, null);
    }

    /**
     * Allow references to the classes loaded through this parent class loader (@see
     * {@link #setParentClassLoader(ClassLoader)}), plus the extra <code>auxiliaryClasses</code>.
     * <p>
     * Notice that the <code>auxiliaryClasses</code> must either be loadable through the
     * <code>optionalParentClassLoader</code> (in which case they have no effect), or <b>no class with the same name</b>
     * must be loadable through the <code>optionalParentClassLoader</code>.
     */
    public void setParentClassLoader(ClassLoader optionalParentClassLoader, Class[] auxiliaryClasses) {
        assertNotCooked();
        this.parentClassLoader = optionalParentClassLoader != null ? optionalParentClassLoader : Thread.currentThread()
                .getContextClassLoader();
        this.optionalAuxiliaryClasses = auxiliaryClasses;
    }

    /**
     * Initializes {@link #classLoader} and {@link #iClassLoader} from the configured {@link #parentClassLoader} and
     * {@link #optionalAuxiliaryClasses}. These are needed by {@link #classToType(Location, Class)} and friends which
     * are used when creating the AST.
     */
    protected final void setUpClassLoaders() {
        assertNotCooked();

        // Set up the ClassLoader for the compilation and the loading.
        this.classLoader = (AuxiliaryClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return new AuxiliaryClassLoader(SimpleCompiler.this.parentClassLoader);
            }
        });
        if (this.optionalAuxiliaryClasses != null) {
            for (int i = 0; i < this.optionalAuxiliaryClasses.length; ++i) {
                this.classLoader.addAuxiliaryClass(this.optionalAuxiliaryClasses[i]);
            }
        }

        this.iClassLoader = new ClassLoaderIClassLoader(this.classLoader);
    }

    /**
     * A {@link ClassLoader} that intermixes that classes loaded by its parent with a map of "auxiliary classes".
     */
    private static final class AuxiliaryClassLoader extends ClassLoader {
        private final Map auxiliaryClasses = new HashMap(); // String name => Class

        private AuxiliaryClassLoader(ClassLoader parent) {
            super(parent);
        }

        private void addAuxiliaryClass(Class c) {
            if (this.auxiliaryClasses.containsKey(c.getName())) {
                return;
            }

            // Check whether the auxiliary class is conflicting with this ClassLoader.
            try {
                Class c2 = super.loadClass(c.getName(), false);
                if (c2 != c) {
                    throw new JaninoRuntimeException("Trying to add an auxiliary class \"" + c.getName()
                            + "\" while another class with the same name is already loaded");
                }
            } catch (ClassNotFoundException ex) {
                ;
            }

            this.auxiliaryClasses.put(c.getName(), c);

            {
                Class sc = c.getSuperclass();
                if (sc != null) {
                    this.addAuxiliaryClass(sc);
                }
            }

            {
                Class[] ifs = c.getInterfaces();
                for (int i = 0; i < ifs.length; ++i) {
                    this.addAuxiliaryClass(ifs[i]);
                }
            }
        }

        public boolean equals(Object o) {
            if (!(o instanceof AuxiliaryClassLoader)) {
                return false;
            }
            AuxiliaryClassLoader that = (AuxiliaryClassLoader) o;

            {
                final ClassLoader parentOfThis = this.getParent();
                final ClassLoader parentOfThat = that.getParent();
                if (parentOfThis == null ? parentOfThat != null : !parentOfThis.equals(parentOfThat)) {
                    return false;
                }
            }

            return this.auxiliaryClasses.equals(that.auxiliaryClasses);
        }

        public int hashCode() {
            ClassLoader parent = this.getParent();
            return (parent == null ? 0 : parent.hashCode()) ^ this.auxiliaryClasses.hashCode();
        }

        protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class c = (Class) this.auxiliaryClasses.get(name);
            if (c != null) {
                return c;
            }

            return super.loadClass(name, resolve);
        }
    }
}
