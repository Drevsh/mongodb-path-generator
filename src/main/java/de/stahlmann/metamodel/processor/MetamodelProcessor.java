package de.stahlmann.metamodel.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import de.stahlmann.metamodel.processor.graph.Edge;
import de.stahlmann.metamodel.processor.graph.Graph;
import de.stahlmann.metamodel.processor.graph.Node;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Generated;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("de.stahlmann.metamodel.processor.Entity")
public class MetamodelProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            // all classes/methods annotated with (de.stahlmann.metamodel.processor.Entity)
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            // skip if nothing found
            if (annotatedElements.isEmpty()) {
                continue;
            }

            Graph graph = generate((Set<Element>) annotatedElements);
            generate(graph);
        }

        return true;
    }

    public boolean isElementAnnotated(Element element, Class<? extends Annotation> annotationClass) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element, annotationClass);
        return annotationMirror != null;
    }

    public AnnotationMirror getAnnotationMirror(Element element, Class<? extends Annotation> annotationClass) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType()
                    .toString()
                    .equals(annotationClass.getName())) {
                return annotationMirror;
            }
        }
        return null;
    }

    private Collection<? extends ExecutableElement> getAnnotatedMethods(TypeElement typeElement,
            Class<? extends Annotation> annotation) {
        List<ExecutableElement> methods = new ArrayList<>();

        // Directly check fields on the class/interface
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) enclosedElement;
                // Check if the method is annotated with the specific annotation
                if (methodElement.getAnnotation(annotation) != null) {
                    methods.add(methodElement);
                }
            }
        }

        // Recursively process methods of interfaces
        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
        for (TypeMirror typeMirrorInterface : interfaces) {
            Element interfaceElement = ((DeclaredType) typeMirrorInterface).asElement();

            if (interfaceElement instanceof TypeElement interfaceTypeElement) {
                Collection<? extends ExecutableElement> superClassMethods = getAnnotatedMethods(interfaceTypeElement,
                        annotation);
                methods.addAll(superClassMethods);
            }
        }

        // Recursively process methods of superclass
        TypeMirror superClassType = typeElement.getSuperclass();
        if (superClassType.getKind() != TypeKind.NONE && superClassType instanceof DeclaredType) {
            Element superClassElement = ((DeclaredType) superClassType).asElement();

            if (superClassElement instanceof TypeElement superClassTypeElement) {
                Collection<? extends ExecutableElement> superClassMethods = getAnnotatedMethods(superClassTypeElement,
                        annotation);
                methods.addAll(superClassMethods);
            }
        }

        return methods;
    }

    public Graph generate(Set<? extends Element> elements) {
        // only handle class/interfaces
        List<TypeElement> typeElements = elements.stream()
                .filter(element -> element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE)
                .map(TypeElement.class::cast)
                .toList();

        Graph graph = new Graph();
        // iterate over all classes/interfaces & create nodes
        for (TypeElement element : typeElements) {
            // create node
            Node node = createNode(element);
            graph.addNode(node);
        }

        // iterate over all classes/interfaces & create edges
        for (TypeElement element : typeElements) {
            // fetch methods
            Collection<? extends ExecutableElement> methods = getAnnotatedMethods(element, Property.class);
            for (ExecutableElement method : methods) {
                Edge edge = createEdge(element, method, graph);
                graph.addEdge(edge);
            }
        }

        return graph;
    }

    private Edge createEdge(TypeElement callingTypeElement, ExecutableElement method, Graph graph) {
        // find source
        PackageElement packageElement = (PackageElement) callingTypeElement.getEnclosingElement();
        String packageName = packageElement.getQualifiedName()
                .toString();
        String className = callingTypeElement.getSimpleName()
                .toString();
        Node source = graph.find(packageName, className);

        // property name
        String methodName = method.getSimpleName()
                .toString();

        // find target
        TypeElement returnType = getReturnType(method);
        if (returnType != null && isElementAnnotated(returnType, Entity.class)) {
            PackageElement packageElementTarget = (PackageElement) returnType.getEnclosingElement();

            String packageNameTarget = packageElementTarget.getQualifiedName()
                    .toString();
            String classNameTarget = returnType.getSimpleName()
                    .toString();
            Node target = graph.find(packageNameTarget, classNameTarget);

            return new Edge(source, target, methodName);
        }

        return new Edge(source, null, methodName);
    }

    private TypeElement getReturnType(ExecutableElement method) {
        TypeMirror returnTypeMirror = method.getReturnType();
        if (returnTypeMirror instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) returnTypeMirror;
            TypeElement returnElement = (TypeElement) declaredType.asElement();

            List<? extends TypeMirror> typeParameters = declaredType.getTypeArguments();
            if (!typeParameters.isEmpty()) {
                TypeMirror typeArgumentMirror = typeParameters.get(0);
                if (typeArgumentMirror instanceof DeclaredType) {
                    DeclaredType typeParameterDeclaredType = (DeclaredType) typeArgumentMirror;
                    TypeElement typeParameterTypeElement = (TypeElement) typeParameterDeclaredType.asElement();

                    // return parameterized type
                    return typeParameterTypeElement;
                }
            }

            // return direct type
            return returnElement;
        }

        // no return type (void?)
        return null;
    }

    private Node createNode(TypeElement typeElement) {
        // extract package/classname
        PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
        String packageName = packageElement.getQualifiedName()
                .toString();
        String className = typeElement.getSimpleName()
                .toString();

        return new Node(packageName, className);
    }

    public void generate(Graph graph) {
        for (Node node : graph.getNodes()) {
            String metaModelClassname = getMetaModelClassname(node.getClassName());
            ClassName currentClass = ClassName.get(node.getPackageName(), metaModelClassname);

            // static root method
            MethodSpec rootMethod = MethodSpec.methodBuilder("root")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(currentClass)
                    .addStatement("return new $T(\"\")", currentClass)
                    .build();

            // constructor
            MethodSpec constructorMethod = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "subgraph")
                    .addStatement("path = subgraph")
                    .build();

            // property methods
            Set<MethodSpec> methodSpecs = new HashSet<>();
            for (Edge e : graph.getEdges(node)) {
                MethodSpec.Builder builder = MethodSpec.methodBuilder(e.getPropertyName())
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("add($S)", e.getPropertyName());

                // complex return type
                if (e.getDestination() != null) {
                    ClassName target = ClassName.get(e.getDestination()
                            .getPackageName(), getMetaModelClassname(e.getDestination()
                            .getClassName()));

                    builder.returns(target)
                            .addStatement("return new $T(path)", target);
                } else {
                    builder.returns(String.class)
                            .addStatement("return getPath()");
                }

                methodSpecs.add(builder.build());
            }

            // create class
            TypeSpec metaModel = TypeSpec.classBuilder(metaModelClassname)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(Path.class)
                    .addMethod(constructorMethod)
                    .addMethod(rootMethod)
                    .addMethods(methodSpecs)
                    .addAnnotation(AnnotationSpec.builder(Generated.class)
                            .addMember("date", "$S", LocalDateTime.now()
                                    .toString())
                            .addMember("value", "$S", MetamodelProcessor.class.getName())
                            .build())
                    .build();

            // create java file
            JavaFile javaFile = JavaFile.builder(node.getPackageName(), metaModel)
                    .build();

            // write to filesystem
            try {
                // Get the file writer for the generated file
                JavaFileObject sourceFile = processingEnv.getFiler()
                        .createSourceFile("%s.%s".formatted(node.getPackageName(), metaModelClassname));
                Writer writer = sourceFile.openWriter();

                // Write the generated code to the file
                javaFile.writeTo(writer);

                // Close the writer
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getMetaModelClassname(String classname) {
        return classname + "_";
    }
}
