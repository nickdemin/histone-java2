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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.Converter;
import ru.histone.v2.evaluator.Evaluator;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.evaluator.resource.HistoneResourceLoader;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.parser.Parser;
import ru.histone.v2.rtti.HistoneType;
import ru.histone.v2.utils.IOUtils;
import ru.histone.v2.utils.RttiUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Alexey Nevinsky
 */
public class LoadText extends AbstractFunction {

    protected ObjectMapper mapper = new ObjectMapper();

    public LoadText(Executor executor, HistoneResourceLoader loader, Evaluator evaluator, Parser parser,
                    Converter converter) {
        super(executor, loader, evaluator, parser, converter);
    }

    @Override
    public String getName() {
        return "loadText";
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        return doExecute(context, clearGlobal(args));
    }

    private CompletableFuture<EvalNode> doExecute(Context context, List<EvalNode> args) {
        if (args.size() < 1 || args.get(0).getType() != HistoneType.T_STRING) {
            return converter.getValue(null);
        }

        final String path = getValue(args, 0);

        EvalNode requestMap = null;
        if (args.size() > 1) {
            requestMap = args.get(1);
        }
        Map<String, Object> params = getParamsMap(context, requestMap);
        return loadResource(context, path, params);
    }

    protected CompletableFuture<EvalNode> loadResource(Context context, String path, Map<String, Object> params) {
        return resourceLoader.load(context, path, context.getBaseUri(), params)
                             .exceptionally(ex -> {
                                 logger.error("Error", ex);
                                 return null;
                             })
                             .thenApply(resource -> {
                                 if (resource == null) {
                                     return converter.createEvalNode(null);
                                 }

                                 String content = IOUtils.readStringFromResource(resource, path);
                                 return converter.createEvalNode(content);
                             });
    }

    protected Map<String, Object> getParamsMap(Context context, EvalNode requestMap) {
        if (requestMap == null) {
            return Collections.emptyMap();
        }

        String json = (String) RttiUtils.callToJSON(context, requestMap).join().getValue();
        Object obj = fromJSON(json);
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return Collections.emptyMap();
    }

    protected EvalNode convertToJson(EvalNode res) {
        String str = (String) res.getValue();
        Object json;
        if (StringUtils.isEmpty(str)) {
            return converter.createEvalNode(null);
        } else if (StringUtils.isNotEmpty(str)) {
            json = fromJSON(str);
        } else {
            json = new LinkedHashMap<String, EvalNode>();
        }

        return converter.constructFromObject(json);
    }

    protected Object fromJSON(String json) {
        try {
            return mapper.readValue(json, Object.class);
        } catch (IOException e) {
            throw new FunctionExecutionException(e.getMessage(), e);
        }
    }
}