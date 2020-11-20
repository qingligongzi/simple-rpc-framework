/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.liyue2008.rpc.client;

import com.github.liyue2008.rpc.transport.Transport;
import com.itranswarp.compiler.JavaStringCompiler;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author LiYue
 * Date: 2019/9/27
 */
public class DynamicStubFactory implements StubFactory{
    private final static String STUB_SOURCE_TEMPLATE =
            "package com.github.liyue2008.rpc.client.stubs;\n" +
            "import com.github.liyue2008.rpc.serialize.SerializeSupport;\n" +
            "\n" +
            "public class %s extends AbstractStub implements %s {\n" +
            "    @Override\n" +
            "    public String %s(String arg) {\n" +
            "        return SerializeSupport.parse(\n" +
            "                invokeRemote(\n" +
            "                        new RpcRequest(\n" +
            "                                \"%s\",\n" +
            "                                \"%s\",\n" +
            "                                SerializeSupport.serialize(arg)\n" +
            "                        )\n" +
            "                )\n" +
            "        );\n" +
            "    }\n" +
            "}";

    private static final String STUB_CLASS_TEMPLATE =
            "package com.github.liyue2008.rpc.client.stubs;\n" +
            "import com.github.liyue2008.rpc.serialize.SerializeSupport;\n" +
            "\n" +
            "public class %s extends AbstractStub implements %s {\n" +
            "%s\n" +
            "}";

    public static final String STUB_METHOD_TEMPLATE =
            "    @Override\n" +
            "    public %s %s(%s) {\n" +
            "        Class[] parameterTypeClassList = {%s};\n" +
            "        Object[] parameterTypeObjectList = {%s};\n" +
            "        return SerializeSupport.parse(\n" +
            "                invokeRemote(\n" +
            "                        new RpcRequest(\n" +
            "                                \"%s\",\n" +
            "                                \"%s\",\n" +
            "                                SerializeSupport.serialize(parameterTypeClassList),\n" +
            "                                SerializeSupport.serialize(parameterTypeObjectList)\n" +
            "                        )\n" +
            "                )\n" +
            "        );\n" +
            "    }\n";

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        try {
            // 填充模板
            String stubSimpleName = serviceClass.getSimpleName() + "Stub";
            String classFullName = serviceClass.getName();
            String stubFullName = "com.github.liyue2008.rpc.client.stubs." + stubSimpleName;
            // String methodName = serviceClass.getMethods()[0].getName();

            StringBuilder methodStr = new StringBuilder();
            Method[] methods = serviceClass.getMethods();
            for (Method method : methods) {
                String methodReturnName = method.getReturnType().getName();
                String methodName = method.getName();
                StringBuilder parameterStr = new StringBuilder();
                StringBuilder parameterTypeClassStr = new StringBuilder();
                StringBuilder parameterTypeObjectStr = new StringBuilder();

                Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    String parameterTypeName = parameterType.getName();
                    parameterStr.append(parameterTypeName + " arg" + i + ",");
                    parameterTypeClassStr.append("arg" + i + ".getClass()" + ",");
                    parameterTypeObjectStr.append("arg" + i + ",");
                }

                parameterStr.deleteCharAt(parameterStr.length() - 1);
                parameterTypeClassStr.deleteCharAt(parameterTypeClassStr.length() - 1);
                parameterTypeObjectStr.deleteCharAt(parameterTypeObjectStr.length() - 1);

                String methodSource = String.format(STUB_METHOD_TEMPLATE, methodReturnName, methodName, parameterStr.toString(),
                        parameterTypeClassStr.toString(), parameterTypeObjectStr.toString(),classFullName, methodName);
                methodStr.append(methodSource);
            }

            String source = String.format(STUB_CLASS_TEMPLATE, stubSimpleName, classFullName, methodStr.toString());
            System.out.println("---------\n" + source + "----------\n");
            // 编译源代码
            JavaStringCompiler compiler = new JavaStringCompiler();
            Map<String, byte[]> results = compiler.compile(stubSimpleName + ".java", source);
            // 加载编译好的类
            Class<?> clazz = compiler.loadClass(stubFullName, results);

            // 把Transport赋值给桩
            ServiceStub stubInstance = (ServiceStub) clazz.newInstance();
            stubInstance.setTransport(transport);
            // 返回这个桩
            return (T) stubInstance;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
