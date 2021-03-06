/*
 * Copyright (c) 2016 MegaFon
 *
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

package ru.histone.v2.evaluator.node;

import ru.histone.v2.rtti.HistoneType;

/**
 * @author Alexey Nevinsky
 */
public class BooleanEvalNode extends EvalNode<Boolean> {

    public static final BooleanEvalNode TRUE = new BooleanEvalNode(Boolean.TRUE);
    public static final BooleanEvalNode FALSE = new BooleanEvalNode(Boolean.FALSE);

    public BooleanEvalNode(Boolean res) {
        super(res);
    }

    @Override
    public HistoneType getType() {
        return HistoneType.T_BOOLEAN;
    }
}
