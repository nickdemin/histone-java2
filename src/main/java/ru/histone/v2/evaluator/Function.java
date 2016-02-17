/**
 * Copyright 2013 MegaFon
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.histone.v2.evaluator;

import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Use this interface when you need to implement your own global function
 *
 * @author alexey.nevinsky
 */
public interface Function {

    /**
     * Return function name
     *
     * @return function name
     */
    String getName();

    /**
     * Method returns {@link CompletableFuture} with result of execution.
     *
     *
     * @param context
     * @param args   arguments from Histone template
     * @return result as one of Histone types
     * @throws FunctionExecutionException if your function stops with error and you need to put details into log,
     *                                    then you should use this exception
     */
    CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException;

    /**
     * Return true, if function must be run in own thread, otherwise - false
     *
     * @return true if async function
     */
    boolean isAsync();

    /**
     * @return
     */
    boolean isClear();
}
