package com.github.andrew0030.pandora_core.config.factory_manager.modmenu.class_loader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ModMenuStubGenerator {
    private static final String API_NAME                   = "com/terraformersmc/modmenu/api/ModMenuApi";
    private static final String CONFIG_SCREEN_FACTORY_NAME = "com/terraformersmc/modmenu/api/ConfigScreenFactory";

    /**
     * Stub for {@code ModMenuApi}, it is used when modmenu is absent.<br/>
     * All methods return safe defaults to satisfy the verifier.
     *
     * @return ASM bytecode for the stubbed {@code ModMenuApi} interface
     */
    public static byte[] generateModMenuApiStub() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return "java/lang/Object";
            }
        };
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT, API_NAME, null, "java/lang/Object", null);

        // Default (instance) methods:
        // They return null/void defaults to satisfy callers when ModMenu isn't loaded
        addDefaultMethod(cw, "getModConfigScreenFactory", "()Ljava/lang/Object;", Opcodes.ACONST_NULL, Opcodes.ARETURN);     // () -> Object (erased ConfigScreenFactory<?>)
        addDefaultMethod(cw, "getProvidedConfigScreenFactories", "()Ljava/util/Map;", Opcodes.ACONST_NULL, Opcodes.ARETURN); // () -> Map<?, ?>
        addDefaultMethod(cw, "attachModpackBadges", "(Ljava/util/function/Consumer;)V", Opcodes.RETURN);                     // (Consumer) -> void (no-op)
        // Static Methods
        // They return null defaults, included for completeness
        addStaticMethod(cw, "createModsScreen", "(Lnet/minecraft/client/gui/screens/Screen;)Lnet/minecraft/client/gui/screens/Screen;"); // (Screen) -> Screen
        addStaticMethod(cw, "createModsButtonText", "()Lnet/minecraft/network/chat/Component;");                                         // () -> Component

        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * Stub for {@code ConfigScreenFactory}, it is used to invoke the "create" method.<br/>
     * Enables reflection on mod lambdas without the real API present.
     *
     * @return ASM bytecode for the stubbed {@code ConfigScreenFactory} interface
     */
    public static byte[] generateConfigScreenFactoryStub() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return "java/lang/Object";
            }
        };
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT, CONFIG_SCREEN_FACTORY_NAME, null, "java/lang/Object", null);

        // The functional interface method: create(Screen)
        addAbstractMethod(cw, "create", "(Lnet/minecraft/client/gui/screens/Screen;)Lnet/minecraft/client/gui/screens/Screen;"); // (Screen) -> Screen

        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * Adds a default method to an interface being built with ASM.
     *
     * @param cw           The {@link ClassWriter} building the class/interface
     * @param name         The method name
     * @param desc         The method descriptor in JVM format
     * @param instructions Varargs of ASM {@code Opcodes} to emit as the method body
     */
    private static void addDefaultMethod(ClassWriter cw, String name, String desc, int... instructions) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, name, desc, null, null);
        mv.visitCode();
        for (int instr : instructions)
            mv.visitInsn(instr);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /**
     * Adds a static method to a class or interface being built with ASM.
     *
     * @param cw   The {@link ClassWriter} building the class/interface
     * @param name The method name
     * @param desc The method descriptor in JVM format
     */
    private static void addStaticMethod(ClassWriter cw, String name, String desc) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, name, desc, null, null);
        mv.visitCode();
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /**
     * Adds an abstract method declaration to an interface being built with ASM.
     *
     * @param cw   The {@link ClassWriter} building the interface
     * @param name The method name
     * @param desc The method descriptor in JVM format
     */
    private static void addAbstractMethod(ClassWriter cw, String name, String desc) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT, name, desc, null, null);
        mv.visitEnd();
    }
}