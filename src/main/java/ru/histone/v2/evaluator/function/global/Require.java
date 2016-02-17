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
package ru.histone.v2.evaluator.function.global;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.Evaluator;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.evaluator.node.RequireEvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.parser.Parser;
import ru.histone.v2.parser.node.ExpAstNode;
import ru.histone.v2.rtti.HistoneType;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Function loads histone template from specified file and evaluating it.
 *
 * @author alexey.nevinsky
 */
public class Require extends AbstractFunction {
    @Override
    public String getName() {
        return "require";
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        checkMinArgsLength(args, 1);
        checkMaxArgsLength(args, 1);
        checkTypes(args.get(0), 0, Collections.singletonList(HistoneType.T_STRING), Collections.singletonList(String.class));

        String url = getValue(args, 0);

        String template = getTemplate(url);
        //todo get parser and evaluator from context
        Parser p = new Parser();
        ExpAstNode root = p.process(template, context.getBaseUri());
        Evaluator evaluator = new Evaluator();

        Context macroCtx = context.clone();

        CompletableFuture<EvalNode> nodeFuture = evaluator.evaluateNode(root, macroCtx); // we evaluated template and add all macros and variables to context

        EvalNode rNode = nodeFuture.join();
        if (rNode.getType() == HistoneType.T_REQUIRE) {
            return nodeFuture;
        } else if (rNode.isReturn()) {
            return CompletableFuture.completedFuture(new RequireEvalNode(rNode));
        } else {
            return CompletableFuture.completedFuture(new RequireEvalNode(macroCtx));
        }
    }

    private String getTemplate(String url) {
        try {
            URL resourceUrl = Require.class.getResource(url);
            if (resourceUrl == null) {
                throw new FileNotFoundException(url);
            }
            Stream<String> stringStream = Files.lines(Paths.get(resourceUrl.toURI()));
            return stringStream.collect(Collectors.joining());
        } catch (Exception e) {
            throw new FunctionExecutionException(e);
        }
    }
}
