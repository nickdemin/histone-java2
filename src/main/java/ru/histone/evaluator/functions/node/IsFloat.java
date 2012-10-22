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
package ru.histone.evaluator.functions.node;

import ru.histone.evaluator.nodes.Node;

/**
 * Return if current target is of float type
 */
public class IsFloat implements NodeFunction<Node> {
    private static final IsNumber IS_NUMBER = new IsNumber();

    @Override
    public String getName() {
        return "isFloat";
    }

    @Override
    public Node execute(Node target, Node... args) {
        return isNumber(target) && target.getAsNumber().getValue().stripTrailingZeros().scale() > 0 ? Node.TRUE : Node.FALSE;
    }

    private boolean isNumber(Node target) {
        return Node.TRUE == IS_NUMBER.execute(target);
    }
}