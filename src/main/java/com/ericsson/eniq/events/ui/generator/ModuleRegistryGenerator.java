package com.ericsson.eniq.events.ui.generator;

import com.ericsson.eniq.events.common.client.module.IModule;
import com.ericsson.eniq.events.ui.client.main.IEniqEventsModuleRegistry;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate a registry of EniqEvents modules. Eniq Events Modules are found based on the inherited GWT modules from this module root.
 * @author ecarsea
 * @since 2012
 */
public class ModuleRegistryGenerator extends Generator {

    private static final String IMPL_TYPE_NAME = "EniqEventsModuleRegistryImpl";

    private static final String IMPL_PACKAGE_NAME = IEniqEventsModuleRegistry.class.getPackage().getName();

    @Override
    public String generate(final TreeLogger logger, final GeneratorContext context, final String typeName)
            throws UnableToCompleteException {
        final TypeOracle typeOracle = context.getTypeOracle();
        JClassType moduleIf = null;
        try {
            moduleIf = typeOracle.getType(IModule.class.getName());
        } catch (final NotFoundException e) {
            e.printStackTrace();
            throw new UnableToCompleteException();
        }

        final ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(IMPL_PACKAGE_NAME,
                IMPL_TYPE_NAME);
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, IMPL_PACKAGE_NAME, IMPL_TYPE_NAME);

        // print writer if null, source code has ALREADY been generated, 
        if (printWriter == null) {
            return IMPL_PACKAGE_NAME + "." + IMPL_TYPE_NAME;
        }

        /** Required Imports **/
        composerFactory.addImport(Map.class.getCanonicalName());
        composerFactory.addImport(HashMap.class.getCanonicalName());
        composerFactory.addImport(IModule.class.getCanonicalName());

        composerFactory.addImplementedInterface("IEniqEventsModuleRegistry");

        final SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);

        int index = 0;
        sourceWriter
                .println("private final Map<String, IModule> moduleMap = new HashMap<String, IModule>();");

        /** Write the class body **/
        sourceWriter.println("public EniqEventsModuleRegistryImpl() {");
        if (moduleIf != null) {
            for (final JClassType module : moduleIf.getSubtypes()) {
                final String moduleVar = "module" + index++;
                sourceWriter.indentln("IModule " + moduleVar + "= new " + module.getQualifiedSourceName()
                        + "();");
                sourceWriter.indentln("moduleMap.put(" + moduleVar + ".getModuleId()," + moduleVar + ");");
            }
        } else {
            throw new UnableToCompleteException();
        }
        sourceWriter.println("}");
        sourceWriter.println("public IModule getModule(final String moduleId) {");
        sourceWriter.indentln(" return moduleMap.get(moduleId);");
        sourceWriter.println("}");

        sourceWriter.println("public boolean containsModule(final String moduleId) {");
        sourceWriter.indentln(" return moduleMap.containsKey(moduleId);");
        sourceWriter.println("}");

        sourceWriter.commit(logger);
        return IMPL_PACKAGE_NAME + "." + IMPL_TYPE_NAME;
    }
}
