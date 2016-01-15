package ru.histone.v2;

import org.junit.Assert;
import ru.histone.HistoneException;
import ru.histone.v2.evaluator.Context;
import ru.histone.v2.evaluator.Evaluator;
import ru.histone.v2.parser.Parser;
import ru.histone.v2.parser.node.AstNode;

/**
 * Created by inv3r on 14/01/16.
 */
public class BaseTest {
    protected void doTest(String input, String expectedRes) throws HistoneException {
        Parser parser = new Parser();
        AstNode ifNode = parser.process(input, "");
        Context context = new Context();
        Evaluator evaluator = new Evaluator();
        String result = evaluator.process("", ifNode, context);
        Assert.assertEquals(expectedRes, result);
    }

}