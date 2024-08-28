/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.junit.Assert;

import static javassist.Modifier.ABSTRACT;
import static javassist.Modifier.PRIVATE;
import static javassist.Modifier.PROTECTED;
import static javassist.Modifier.PUBLIC;
import static javassist.Modifier.STATIC;
import static javassist.Modifier.SYNCHRONIZED;
import static javassist.Modifier.VOLATILE;

/**
 * @author ealeerm
 * @since 06/2012
 */
public abstract class MockUtilities {

    public static String OLD_METHOD_POSTFIX = "$impl";

    private MockUtilities() {
    }

    /**
     * Usage example:
     * <p/>
     * <b><code>
     * renameAndReplaceMethod("com.extjs.gxt.ui.client.GXT", "init");
     * </code></b>
     *
     * @param clazzStr   a fully-qualified class name where replace method
     *                   by empty implementation
     * @param methodName the method name
     */
    public static void renameAndReplaceMethod(String clazzStr, String methodName) {
        renameAndReplaceMethod(clazzStr, null, methodName);
    }

    /**
     * Usage example:
     * <p/>
     * <b><code>
     * renameAndReplaceMethod("com.google.gwt.core.client.impl.Impl", "java.lang.String", "getModuleBaseURL");
     * </code></b>
     *
     * @param clazzStr       a fully-qualified class name where replace method
     *                       by empty implementation
     * @param returnClazzStr a fully-qualified name of returned class or null if void
     * @param methodName     the method name
     */
    public static void renameAndReplaceMethod(String clazzStr, String returnClazzStr, String methodName) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass clazz = classPool.get(clazzStr);

            CtClass returnClazz;
            if (returnClazzStr != null) {
                returnClazz = classPool.get(returnClazzStr);
            } else {
                returnClazz = CtClass.voidType;
            }

            renameAndReplaceMethod(clazz, returnClazz, methodName);
        } catch (NotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }

    /**
     * @param clazz       a class where replace method
     *                    by empty implementation
     * @param returnClazz returned class or null if void
     * @param methodName  the method name
     *
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    public static void renameAndReplaceMethod(CtClass clazz, CtClass returnClazz, String methodName) {
        try {
            CtMethod oldMethod = clazz.getDeclaredMethod(methodName);

            // rename old method to synthetic name
            String dummyMethodName = methodName + OLD_METHOD_POSTFIX;
            oldMethod.setName(dummyMethodName);

            // create a new dummy method
            // cannot simply copy as the the method can be native
            int modifiers = oldMethod.getModifiers() & (PUBLIC | PROTECTED | PRIVATE |
                    ABSTRACT | STATIC | VOLATILE | SYNCHRONIZED);

            CtClass[] parameters = oldMethod.getParameterTypes();
            CtClass[] exceptions = oldMethod.getExceptionTypes();

            CtMethod newMethod = CtNewMethod.make(modifiers, returnClazz, methodName, parameters, exceptions, null, clazz);
            clazz.addMethod(newMethod);

            // Loads the classes to the class loader
            clazz.toClass();
            clazz.detach();
        } catch (NotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.toString() + " [clazz=" + clazz + " returnClazz=" + returnClazz + " methodName=" +
                    methodName + "]");
        } catch (CannotCompileException e) {
            e.printStackTrace();
            Assert.fail(e.toString() + " [clazz=" + clazz + " returnClazz=" + returnClazz + " methodName=" +
                    methodName + "]");
        }
    }
}
