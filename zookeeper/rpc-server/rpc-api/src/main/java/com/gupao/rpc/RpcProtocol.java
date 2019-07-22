package com.gupao.rpc;

import java.io.Serializable;
import java.util.Arrays;

public class RpcProtocol implements Serializable {

    /**
     * 方法的参数
     */
    private Object[] parameters;

    /**
     * 方法名字
     */
    private String methodName;

    /**
     * 类名
     */
    private String className;

    /**
     * 版本号
     */
    private String version;

    private Class<?>[] paramTypes;

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "parameters=" + Arrays.toString(parameters) +
                ", methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                ", version='" + version + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
