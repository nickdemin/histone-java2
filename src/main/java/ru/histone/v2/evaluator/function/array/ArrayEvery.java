package ru.histone.v2.evaluator.function.array;

import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.function.AbstractFunction;
import ru.histone.v2.evaluator.node.BooleanEvalNode;
import ru.histone.v2.evaluator.node.EvalNode;
import ru.histone.v2.exceptions.FunctionExecutionException;
import ru.histone.v2.utils.Tuple;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author gali.alykoff on 09/02/16.
 */
public class ArrayEvery extends AbstractFunction implements Serializable {
    public static final String NAME = "every";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CompletableFuture<EvalNode> execute(Context context, List<EvalNode> args) throws FunctionExecutionException {
        return ArrayFilter.calcByPredicate(context, args).thenApply(pairs ->
                pairs.stream()
                        .allMatch(Tuple::getRight)
        ).thenApply(BooleanEvalNode::new);
    }
}