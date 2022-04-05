package com.chemanman.rxbus.compile;

import com.chemanman.rxbus.compile.common.Common;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by huilin on 2017/7/14.
 */

public class AnnotatedClass {
    private ArrayList<BindEventMethod> bindEventMethods;
    // 注解所在类
    private TypeElement typeElement;
    private Elements elements;

    public AnnotatedClass(TypeElement typeElement, Elements elements) {
        this.typeElement = typeElement;
        this.elements = elements;
        bindEventMethods = new ArrayList<>();
    }

    public void addBindMethod(BindEventMethod bindEventMethod) {
        bindEventMethods.add(bindEventMethod);
    }

    public String getPackName() {
        return elements.getPackageOf(typeElement).getQualifiedName().toString();
    }

    public JavaFile generateFile() {
        //生成类名
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "_BindInject")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Common.RXBUS_INJECT_PACK_NAME, "Inject"), TypeName.get(typeElement.asType())));
        //声明inject方法
        MethodSpec.Builder injectBuilder = MethodSpec.methodBuilder("inject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(typeElement.asType()), "host", Modifier.FINAL);
        //声明unInject方法
        MethodSpec.Builder unInjectBuilder = MethodSpec.methodBuilder("unInject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(typeElement.asType()), "host", Modifier.FINAL);
//        FieldSpec.Builder host = FieldSpec.builder(TypeName.get(typeElement.asType()),
//                "m"+typeElement.getSimpleName().toString(), Modifier.PUBLIC);
//        classBuilder.addField(host.build());
//        injectBuilder.addStatement("m$N = host",typeElement.getSimpleName().toString());
        //解析注解方法
        for (BindEventMethod bindEventMethod : bindEventMethods) {
            //声明匿名内部类OnEventListener
            TypeSpec typeSpec = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ClassName.get(Common.RXBUS_PACK_NAME, "RxBus", "OnEventListener"))
                    .addMethod(MethodSpec.methodBuilder("onEvent")
                                    .addAnnotation(Override.class)
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.VOID)
                                    .addParameter(TypeName.OBJECT, "object", Modifier.FINAL)
                                    .addStatement("$T o = ($T)object", TypeName.get(bindEventMethod.getParameters().get(0).asType()),
                                            TypeName.get(bindEventMethod.getParameters().get(0).asType()))
                                    .addStatement("host.$N($L)", /**typeElement.getSimpleName().toString(),**/bindEventMethod.getMethodName(), "o").build()
//                                    .addStatement("m$N.$N($L)", typeElement.getSimpleName().toString(), bindEventMethod.getMethodName(), "o").build()
                    ).build();
            //声明类成员变量（匿名内部类）
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(ClassName.get(Common.RXBUS_PACK_NAME, "RxBus", "OnEventListener"),
                    bindEventMethod.getMethodName().toString() + "_bind", Modifier.PUBLIC);
            classBuilder.addField(fieldBuilder.build());
            //inject方法添加代码（成员变量赋值）
            injectBuilder.addStatement(bindEventMethod.getMethodName().toString() + "_bind = $L", typeSpec);
            //inject方法添加代码（RxBus注册）
            injectBuilder.addStatement(" RxBus.getDefault().register($L,$L,$T.class)", bindEventMethod.getMethodName().toString() + "_bind",
                    bindEventMethod.getType(), TypeName.get(bindEventMethod.getParameters().get(0).asType()));
            //UnInject方法添加（RxBus解绑）
            unInjectBuilder.addStatement("RxBus.getDefault().unregister($L)", bindEventMethod.getMethodName().toString() + "_bind");
        }
        //类增加inject方法
        classBuilder.addMethod(injectBuilder.build());
        //类增加unInject方法
        classBuilder.addMethod(unInjectBuilder.build());
        return JavaFile.builder(getPackName(), classBuilder.build()).build();
    }
}
