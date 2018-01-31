package com.chemanman.rxbus.compile;

import com.chemanman.rxbus.annotation.InjectMethodBind;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

/**
 * Created by huilin on 2017/7/14.
 */

public class BindEventMethod {

    private ExecutableElement executableElement;
    private int type;

    public BindEventMethod(Element element) {
        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException("not method");
        }
        executableElement = (ExecutableElement) element;
        if(getParameters().size() != 1){
            throw new IllegalArgumentException("parameter not equal to 1");
        }
        InjectMethodBind methodBind = executableElement.getAnnotation(InjectMethodBind.class);
        type = methodBind.type();
    }

    public int getType() {
        return type;
    }

    public Name getMethodName() {
        return executableElement.getSimpleName();
    }
    public List<? extends VariableElement> getParameters(){
        return executableElement.getParameters();
    }
}
