/**
 *    Copyright 2012 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.histone.acceptance.helpers;

import java.math.BigDecimal;

import ru.histone.evaluator.functions.node.NodeFunction;
import ru.histone.evaluator.functions.node.NodeFunctionExecutionException;
import ru.histone.evaluator.nodes.*;
import ru.histone.evaluator.nodes.BooleanHistoneNode;
import ru.histone.evaluator.nodes.StringHistoneNode;

/**
 *
 *
 */
public class MockNodeFunction extends NodeFunction {
    private String name;
    private String data;
    private String resultType;

    public MockNodeFunction(NodeFactory nodeFactory, String name, String resultType, String data) {
        super(nodeFactory);
        this.name = name;
        this.resultType = resultType;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node execute(Node target, Node... args) throws NodeFunctionExecutionException {
        if (data.contains(":exception:")) {
            throw new RuntimeException("Function exception");
        }

        Node node = null;
        if ("string".equals(resultType.toLowerCase())) {
            if (data.contains(":target:")) {
                data = data.replace(":target:", "(" + target.toString() + ")");
            }
            if (data.contains(":args:")) {
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                boolean addSeparator = false;
                for (Object arg : args) {
                    if (addSeparator) {
                        sb.append('-');
                    }
                    sb.append(arg.toString());
                    addSeparator = true;
                }
                sb.append(']');

                data = data.replace(":args:", sb.toString());
            }

            node = getNodeFactory().string(data);
        } else if ("number".equals(resultType.toLowerCase())) {
            node = getNodeFactory().number(new BigDecimal(data));
        } else if ("boolean".equals(resultType.toLowerCase())) {
            node = "true".equalsIgnoreCase(data) ? getNodeFactory().TRUE : getNodeFactory().FALSE;
        } else {
            throw new RuntimeException();
        }

        return node;
    }


}
