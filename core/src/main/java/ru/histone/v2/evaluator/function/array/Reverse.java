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

package ru.histone.v2.evaluator.function.array;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.Converter;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.utils.ParserUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexey Nevinsky
 */
public class Reverse extends AbstractFunction {
    public Reverse(Converter converter) {
        super(converter);
    }

    @Override
    public String getName() {
        return "reverse";
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        Map<String, Object> v = getValue(args, 0);
        List<Map.Entry<String, Object>> list = new ArrayList<>(v.entrySet());
        Collections.reverse(list);

        Map<String, Object> res = new LinkedHashMap<>();
        int i = 0;
        for (Map.Entry<String, Object> entry : list) {
            if (ParserUtils.isNumber(entry.getKey())) {
                res.put(i++ + "", entry.getValue());
            } else {
                res.put(entry.getKey(), entry.getValue());
            }
        }
        return converter.getValue(res);
    }
}
