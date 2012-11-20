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
package ru.histone.evaluator.functions.global;

import ru.histone.evaluator.nodes.Node;
import ru.histone.evaluator.nodes.NodeFactory;
import ru.histone.evaluator.nodes.NumberHistoneNode;
import ru.histone.evaluator.nodes.NumberHistoneNode;
import ru.histone.utils.ArrayUtils;

/**
 * Return minimum value from specified arguments<br/>
 * When calculating min value, all arguments are casted to number type
 */
public class Min extends GlobalFunction {
    public Min(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public String getName() {
        return "min";
    }

    @Override
    public Node execute(Node... args) {
        if (ArrayUtils.isEmpty(args)) {
            return getNodeFactory().UNDEFINED;
        }
        NumberHistoneNode result = null;
        for (Node arg : args) {
            if (!arg.isNumber()) {
                continue;
            }
            NumberHistoneNode argNum = (NumberHistoneNode) arg;
            result = (result == null || result.compareTo(argNum) > 0) ? argNum : result;
        }
        return result == null ? getNodeFactory().UNDEFINED : result;
    }
}
