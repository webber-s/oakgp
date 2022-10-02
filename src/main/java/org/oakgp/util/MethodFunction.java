package org.oakgp.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

// TODO rename, repackage
// TODO test public/protected default classes and methods
// TODO test static methods
// TODO test inner classes
public final class MethodFunction {
   private static final JavaSourceCompiler COMPILER = new JavaSourceCompiler();
   private static final String PACKAGE = "org.oakgp.generated_at_runtime";

   private MethodFunction() {
   }

   public static Function createFunction(Class<?> target, String methodName) {
      Method method = null;
      boolean duplicates = false;
      for (Method m : target.getMethods()) {
         if (m.getName().equals(methodName)) {
            duplicates = method != null;
            method = m;

            if (method.getParameterCount() == 0) {
               duplicates = false;
               break;
            }
         }
      }

      if (method == null) {
         throw new RuntimeException(target.getName() + " " + methodName);
      }
      if (duplicates) {
         throw new RuntimeException();
      }

      return createFunction(method);
   }

   public static Function createFunction(Method method) {
      Type[] argumentTypes = getMethodArguments(method);
      Type returnType = Types.type(method.getReturnType());
      Signature signature = Signature.createSignature(returnType, argumentTypes);
      return generate(method, signature);
   }

   private static Type[] getMethodArguments(Method method) {
      return isStatic(method) ? getStaticMethodArguments(method) : getInstanceMethodArguments(method);
   }

   private static Type[] getStaticMethodArguments(Method method) {
      Parameter[] methodParameters = method.getParameters();
      Type[] argumentTypes = new Type[methodParameters.length];
      for (int i = 0; i < methodParameters.length; i++) {
         argumentTypes[i] = Types.type(methodParameters[i].getType());
      }
      return argumentTypes;
   }

   private static Type[] getInstanceMethodArguments(Method method) {
      Parameter[] methodParameters = method.getParameters();
      Type[] argumentTypes = new Type[methodParameters.length + 1];
      argumentTypes[0] = Types.type(method.getDeclaringClass());
      for (int i = 0; i < methodParameters.length; i++) {
         argumentTypes[i + 1] = Types.type(methodParameters[i].getType());
      }
      return argumentTypes;
   }

   private static Function generate(Method method, Signature signature) {
      String className = method.getName();
      String sourceCode = generateSourceCode(className, method);
      Class<?> functionClass = COMPILER.compileClass(PACKAGE + "." + className, sourceCode);
      try {
         return (Function) functionClass.getConstructor(Signature.class).newInstance(signature);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
            | SecurityException e) {
         throw new RuntimeException(e);
      }
   }

   private static String generateSourceCode(String className, Method method) {
      StringBuilder sb = new StringBuilder();
      sb.append("package ");
      sb.append(PACKAGE);
      sb.append(";\n");
      sb.append("import org.oakgp.*;\n");
      sb.append("import org.oakgp.function.*;\n");
      sb.append("import org.oakgp.node.*;\n");
      sb.append("public final class ");
      sb.append(className);
      sb.append(" implements Function{\n");
      sb.append("private final Signature s;\n");

      // constructor
      sb.append("public ");
      sb.append(className);
      sb.append("(Signature s){this.s=s;}\n");

      // getSignature
      sb.append("public Signature getSignature(){return s;}\n");

      // getDisplayName
      sb.append("public String getDisplayName(){return \"" + method.getName() + "\";}\n");

      // evaluate
      sb.append("public ");
      Class<?> returnType = PrimitiveMapper.mapPrimitive(method.getReturnType());
      boolean isVoidReturnType = returnType == Void.class;
      sb.append(returnType.getName());
      sb.append(" evaluate(ChildNodes a, Assignments v, AutomaticallyDefinedFunctions f){\n");
      if (!isVoidReturnType) {
         sb.append("return ");
      }
      // TODO throw exception if classes or methods are not public
      int offset = 0;
      if (isStatic(method)) {
         sb.append(method.getDeclaringClass().getName());
         sb.append('.');
         offset = 0;
      } else {
         sb.append("((");
         sb.append(method.getDeclaringClass().getName());
         sb.append(")a.getNode(0).evaluate(v,f)).");
         offset = 1;
      }
      sb.append(method.getName());
      sb.append('(');
      Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i++) {
         if (i != 0) {
            sb.append(',');
         }
         sb.append('(');
         sb.append(parameters[i].getType().getName());
         sb.append(")a.getNode(");
         sb.append(i + offset);
         sb.append(").evaluate(v,f)");
      }
      sb.append(");");
      if (isVoidReturnType) {
         sb.append("return null;");
      }
      sb.append("}}");

      return sb.toString();
   }

   private static boolean isStatic(Method method) {
      return (method.getModifiers() & Modifier.STATIC) != 0;
   }
}