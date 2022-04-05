package com.chemanman.rxbus.compile;

import com.chemanman.rxbus.annotation.InjectMethodBind;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by huilin on 2017/7/14.
 */
@AutoService(Processor.class)
public class BindEventProcesser extends AbstractProcessor {
    /**
     * 文件辅助类
     */
    private Filer filer;
    /**
     * 元素辅助类
     */
    private Elements elements;
    /**
     * 存储注解类的映射
     */
    private HashMap<String, AnnotatedClass> annotatedClassHashMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        annotatedClassHashMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.clear();
        processEventMethod(roundEnv);
        for (AnnotatedClass annotatedClass : annotatedClassHashMap.values()) {
            try {
                annotatedClass.generateFile().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 解析注解
     *
     * @param roundEnv
     */
    private void processEventMethod(RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(InjectMethodBind.class);
        for (Element element : elementsAnnotatedWith) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            annotatedClass.addBindMethod(new BindEventMethod(element));
        }
    }

    /**
     * 指定使用的java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 指定要被注解处理器处理的注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(InjectMethodBind.class.getCanonicalName());
        return set;
    }

    private AnnotatedClass getAnnotatedClass(Element element) {
        TypeElement className = (TypeElement) element.getEnclosingElement();
        String fullName = className.getQualifiedName().toString();
        AnnotatedClass annotatedClass = annotatedClassHashMap.get(fullName);
        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClass(className, elements);
            annotatedClassHashMap.put(fullName, annotatedClass);
        }
        return annotatedClass;
    }

}
