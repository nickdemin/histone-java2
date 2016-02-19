package ru.histone.v2.evaluator.function.array;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.function.string.StringHtmlEntities;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.evaluator.node.MapEvalNode;
import ru.histone.v2.evaluator.node.NullEvalNode;
import ru.histone.v2.evaluator.node.StringEvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.rtti.HistoneType;
import ru.histone.v2.utils.AsyncUtils;
import ru.histone.v2.utils.Tuple;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author gali.alykoff on 19/02/16.
 */
public class ArrayHtmlEntities extends AbstractFunction {
    public static final String NAME = "htmlentities";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        final EvalNode node = args.get(0);
        return htmlEntities(context, node);
    }

    private static CompletableFuture<EvalNode> htmlEntities(Context context, EvalNode node) {
        final HistoneType type = node.getType();
        if (type == HistoneType.T_STRING) {
            return StringHtmlEntities.htmlEntities(node);
        }
        final MapEvalNode mapNode = (MapEvalNode) node;
        final Set<Map.Entry<String, EvalNode>> keyValues = mapNode.getValue().entrySet();
        final List<CompletableFuture<Tuple<String, EvalNode>>> accFutures = new LinkedList<>();
        for (Map.Entry<String, EvalNode> entry : keyValues) {
            final String key = entry.getKey();
            final EvalNode value = entry.getValue();
            final boolean isCheckingValuesInArray = value.getType() == HistoneType.T_ARRAY
                    || value.getType() == HistoneType.T_STRING;
            if (isCheckingValuesInArray) {
                accFutures.add(htmlEntities(context, value).thenApply(newValue ->
                        new Tuple<>(key, newValue)
                ));
            } else {
                accFutures.add(CompletableFuture.completedFuture(
                        new Tuple<>(key, value))
                );
            }
        }
        return AsyncUtils.sequence(accFutures).thenApply(acc -> {
            Map<String, EvalNode> result = new LinkedHashMap<>();
            for (Tuple<String, EvalNode> tuple : acc) {
                result.put(tuple.getLeft(), tuple.getRight());
            }
            return new MapEvalNode(result);
        });
    }
}