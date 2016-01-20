package ru.histone.v2.parser;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import ru.histone.HistoneException;
import ru.histone.tokenizer.BaseTokens;
import ru.histone.tokenizer.Token;
import ru.histone.tokenizer.Tokens;
import ru.histone.v2.exceptions.UnknownAstTypeException;
import ru.histone.v2.parser.node.*;
import ru.histone.v2.parser.tokenizer.ExpressionList;
import ru.histone.v2.parser.tokenizer.Tokenizer;
import ru.histone.v2.parser.tokenizer.TokenizerResult;
import ru.histone.v2.parser.tokenizer.TokenizerWrapper;
import ru.histone.v2.utils.ParserUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static ru.histone.v2.parser.node.AstType.AST_REF;

/**
 * Created by alexey.nevinsky on 24.12.2015.
 */
public class Parser {


    private static final Pattern regexpFlagsPattern = Pattern.compile("^(?:([gim])(?!.*\\1))*$");

    public AstNode process(String template, String baseURI) throws HistoneException {
        Tokenizer tokenizer = new Tokenizer(template, baseURI, ExpressionList.VALUES);
        TokenizerWrapper wrapper = new TokenizerWrapper(tokenizer);
        AstNode result = getNodeList(wrapper);
        if (!wrapper.next(BaseTokens.T_EOF.getId()).isFound())
            UnexpectedToken(wrapper, "EOF");
        final Optimizer optimizer = new Optimizer();
        result = optimizer.mergeStrings(result);
//        markReferences(result);
        return result;
    }

    // TODO
    private void markReferences(ExpAstNode astNode) throws HistoneException {
        switch (astNode.getType()) {
            case AST_REF:
                break;
            case AST_VAR:
                break;
            case AST_IF:
                break;
            case AST_FOR:
                break;
            case AST_MACRO:
                break;
            case AST_NODES:
                break;
            default:
                break;
        }
    }

    private ExpAstNode getNodeList(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode result = new ExpAstNode(AstType.AST_NODELIST);
        AstNode node;
        wrapper = new TokenizerWrapper(wrapper);
        for (; ; ) {
            node = getStatement(wrapper);
            if (node.getType() == AstType.AST_T_BREAK) {
                break;
            } else if (node.getType() != AstType.AST_T_NOP) {
                //todo check this
                if (node.getType() != AstType.AST_T_ARRAY) {
                    result.add(node);
                } else {
                    result.add(node);
                }
            }
        }
        return result;
    }

    private AstNode getStatement(TokenizerWrapper wrapper) throws ParserException {
        if (next(wrapper, Tokens.T_BLOCK_START)) {
            return getTemplateStatement(wrapper);
        }
        if (next(wrapper, Tokens.T_LITERAL_START)) {
            return getLiteralStatement(wrapper);
        }
        if (next(wrapper, Tokens.T_CMT_START)) {
            return getCommentStatement(wrapper);
        }
        if (!wrapper.test(BaseTokens.T_EOF.getId()).isFound()) {
            return new StringAstNode(wrapper.next().first().getValue());
        }
        return new ExpAstNode(AstType.AST_T_BREAK);
    }

    private ExpAstNode getCommentStatement(TokenizerWrapper wrapper) {
        throw new NotImplementedException();
    }

    private AstNode getTemplateStatement(TokenizerWrapper wrapper) throws ParserException {
        wrapper = new TokenizerWrapper(wrapper, Collections.singletonList(Tokens.T_SPACES.getId()));
        AstNode result;
        if (next(wrapper, Tokens.T_IF)) {
            result = getIfStatement(wrapper);
        } else if (next(wrapper, Tokens.T_FOR)) {
            result = getForStatement(wrapper);
        } else if (next(wrapper, Tokens.T_VAR)) {
            result = getVarStatement(wrapper);
        } else if (next(wrapper, Tokens.T_MACRO)) {
            result = getMacroExpression(wrapper);
        } else if (next(wrapper, Tokens.T_RETURN)) {
            result = getReturnStatement(wrapper);
        } else if (next(wrapper, Tokens.T_SUPRESS)) {
            result = getSupressStatement(wrapper);
        } else if (next(wrapper, Tokens.T_LISTEN)) {
            result = getListenStatement(wrapper, AstType.AST_LISTEN);
        } else if (next(wrapper, Tokens.T_TRIGGER)) {
            result = getListenStatement(wrapper, AstType.AST_TRIGGER);
        } else if (test(wrapper, Tokens.T_SLASH, Tokens.T_STATEMENT, Tokens.T_BLOCK_END)) {
            result = new ExpAstNode(AstType.AST_T_BREAK);
        } else if (test(wrapper, Tokens.T_STATEMENT)) {
            result = new ExpAstNode(AstType.AST_T_BREAK);
        } else {
            result = getExpressionStatement(wrapper);
        }
        return result;
    }

    private AstNode getExpressionStatement(TokenizerWrapper wrapper) throws ParserException {
        if (next(wrapper, Tokens.T_BLOCK_END)) {
            return new ExpAstNode(AstType.AST_T_NOP);
        }
        AstNode expression = getExpression(wrapper);
        if (!next(wrapper, Tokens.T_BLOCK_END)) {
            UnexpectedToken(wrapper, "}}");
        }
        return expression;
    }

    private ExpAstNode getListenStatement(TokenizerWrapper wrapper, AstType astListen) {
        throw new NotImplementedException();
    }

    private ExpAstNode getSupressStatement(TokenizerWrapper wrapper) {
        throw new NotImplementedException();
    }

    private ExpAstNode getReturnStatement(TokenizerWrapper wrapper) {
        throw new NotImplementedException();
    }

    private ExpAstNode getVarStatement(TokenizerWrapper wrapper) throws ParserException {
//        TokenizerResult name;
//        ExpAstNode result;
//        if (!test(wrapper, Tokens.T_ID, Tokens.T_EQ)) {
//            name = wrapper.next(Tokens.T_ID.getId());
//            if (!name.isFound()) {
//                UnexpectedToken(wrapper, "IDENTIFIER");
//            }
//            if (!next(wrapper, Tokens.T_BLOCK_END)) {
//                UnexpectedToken(wrapper, "}}");
//            }
//            result = new ExpAstNode(AstType.AST_VAR);
//            result.add(getNodesStatement(wrapper, false));
//            result.setValue(name.firstValue());
//            if (!next(wrapper, Tokens.T_SLASH, Tokens.T_VAR)) {
//                UnexpectedToken(wrapper, "{{/var}}");
//            }
//        } else {
//            result = new ExpAstNode(AstType.AST_ARRAY);
//            do {
//                name = wrapper.next(Tokens.T_ID.getId());
//                if (!name.isFound()) {
//                    UnexpectedToken(wrapper, "IDENTIFIER");
//                }
//                if (!next(wrapper, Tokens.T_EQ)) {
//                    UnexpectedToken(wrapper, "=");
//                }
//                ExpAstNode varNode = new ExpAstNode(AstType.AST_VAR).add(getExpression(wrapper));
//                varNode.setValue(name.firstValue());
//                result.add(varNode);
//                if (!next(wrapper, Tokens.T_COMMA)) {
//                    break;
//                }
//            } while (!wrapper.test(BaseTokens.T_EOF.getId()).isFound());
//        }
//        if (!next(wrapper, Tokens.T_BLOCK_END)) {
//            UnexpectedToken(wrapper, "}}");
//        }
//        return result;
        throw new NotImplementedException();
    }

    private ExpAstNode getNodesStatement(TokenizerWrapper wrapper, boolean nested) throws ParserException {
        ExpAstNode res = new ExpAstNode(AstType.AST_NODES);
        wrapper = new TokenizerWrapper(wrapper);
        AstNode node;
        for (; ; ) {
            if (nested && test(wrapper, Tokens.T_BLOCK_END)) {
                break;
            }
            node = getStatement(wrapper);
            if (node.getType() == AstType.AST_T_BREAK) {
                break;
            }
            if (node.getType() != AstType.AST_T_NOP) {
                if (node.getType() != AstType.AST_T_ARRAY) {
                    res.add(node);
                } else {
                    throw new NotImplementedException();
                    //                else Array.prototype.push.apply(result, node.slice(1));
                }
            }
        }
        if (nested && next(wrapper, Tokens.T_BLOCK_END)) {
            UnexpectedToken(wrapper, "}}");
        }
        return res;

    }

    private ExpAstNode getForStatement(TokenizerWrapper wrapper) throws ParserException {
        final ExpAstNode node = new ExpAstNode(AstType.AST_FOR);
        final TokenizerResult exp1 = wrapper.next(Tokens.T_ID.getId());
        if (exp1.isFound()) {
            if (next(wrapper, Tokens.T_COLON)) {
                node.add(new StringAstNode(exp1.firstValue()));
                final TokenizerResult exp2 = wrapper.next(Tokens.T_ID.getId());
                if (exp2.isFound()) {
                    node.add(new StringAstNode(exp2.firstValue()));
                } else {
                    UnexpectedToken(wrapper, "IDENTIFIER");
                }
            } else {
                node.add(new StringAstNode(null))
                        .add(new StringAstNode(exp1.firstValue()));
            }
        } else {
            node.add(new StringAstNode(null))
                    .add(new StringAstNode(null));
        }

        if (!next(wrapper, Tokens.T_IN)) {
            UnexpectedToken(wrapper, "in");
        }

        do {
            final AstNode node2 = getExpression(wrapper);
            if (!next(wrapper, Tokens.T_BLOCK_END)) {
                UnexpectedToken(wrapper, "}}");
            }
            node.add(getNodeList(wrapper), node2);
        } while (next(wrapper, Tokens.T_ELSEIF));

        if (next(wrapper, Tokens.T_ELSE)) {
            if (!next(wrapper, Tokens.T_BLOCK_END)) {
                UnexpectedToken(wrapper, "}}");
            }
            node.add(getNodeList(wrapper));
        }

        if (!next(wrapper, Tokens.T_SLASH, Tokens.T_FOR, Tokens.T_BLOCK_END)) {
            UnexpectedToken(wrapper, "{{/for}}");
        }
        return node;
    }

    private ExpAstNode getIfStatement(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode node = new ExpAstNode(AstType.AST_IF);
        do {
            AstNode condition = getExpression(wrapper);
            if (!next(wrapper, Tokens.T_BLOCK_END)) {
                UnexpectedToken(wrapper, "}}");
            }
            node.add(getNodesStatement(wrapper, false), condition);
        } while (next(wrapper, Tokens.T_ELSEIF));

        if (next(wrapper, Tokens.T_ELSE)) {
            if (!next(wrapper, Tokens.T_BLOCK_END)) {
                UnexpectedToken(wrapper, "}}");
            }
            node.add(getNodesStatement(wrapper, false));
        }
        if (!next(wrapper, Tokens.T_SLASH, Tokens.T_IF, Tokens.T_BLOCK_END)) {
            UnexpectedToken(wrapper, "{{/if}}");
        }

        return node;
    }

    private AstNode getExpression(TokenizerWrapper wrapper) throws ParserException {
        if (test(wrapper, Tokens.T_ARROW) ||
                test(wrapper, Tokens.T_ID, Tokens.T_ARROW) ||
                test(wrapper, Tokens.T_LPAREN, Tokens.T_RPAREN) ||
                test(wrapper, Tokens.T_LPAREN, Tokens.T_ID, Tokens.T_COMMA) ||
                test(wrapper, Tokens.T_LPAREN, Tokens.T_ID, Tokens.T_RPAREN, Tokens.T_ARROW)) {
            return getMacroExpression(wrapper);
        }
        return getTernaryExpression(wrapper);
    }

    private ExpAstNode getMacroExpression(TokenizerWrapper wrapper) throws ParserException {
//        ExpAstNode result = new ExpAstNode(AstType.AST_NODELIST);
//
//        TokenizerResult name = wrapper.next(Tokens.T_ID.getId());
//        if (name.isFound()) {
//            result.add(new ExpAstNode(AstType.AST_NOP).setValue(name.first().getValue()));
//        } else if (next(wrapper, Tokens.T_LPAREN)) {
//            if (!test(wrapper, Tokens.T_RPAREN)) {
//                do {
//                    name = wrapper.next(Tokens.T_ID.getId());
//                    if (name.isFound()) {
//                        result.add(new ExpAstNode(AstType.AST_NOP).setValue(name.first().getValue()));
//                    } else {
//                        UnexpectedToken(wrapper, "IDENTIFIER");
//                    }
//                } while (next(wrapper, Tokens.T_COMMA));
//            }
//            if (!next(wrapper, Tokens.T_RPAREN)) {
//                UnexpectedToken(wrapper, ")");
//            }
//        }
//        if (!next(wrapper, Tokens.T_ARROW)) {
//            UnexpectedToken(wrapper, "=>");
//        }
//        if (result.getNodes().size() > 0) {
//            //todo
////            result.unshift(result.length);
//        }

//        return createMacroNode(wrapper).add(result);
        throw new NotImplementedException();
    }

    private ExpAstNode createMacroNode(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode returnNode = new ExpAstNode(AstType.AST_RETURN).add(getExpression(wrapper));
        ExpAstNode nodeList = new ExpAstNode(AstType.AST_NODELIST).add(returnNode);
        ExpAstNode node = new ExpAstNode(AstType.AST_MACRO).add(nodeList);
        return node;
    }

    private ExpAstNode getTernaryExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getLogicalOrExpression(wrapper);
        while (next(wrapper, Tokens.T_QUERY)) {
            ExpAstNode node = new ExpAstNode(AstType.AST_TERNARY);
            res = node.add(res, getExpression(wrapper));
            if (next(wrapper, Tokens.T_COLON)) {
                res.add(getExpression(wrapper));
            }
        }
        return res;
    }

    private ExpAstNode getLogicalOrExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getLogicalAndExpression(wrapper);
        while (next(wrapper, Tokens.T_OR)) {
            ExpAstNode node = new ExpAstNode(AstType.AST_OR);
            res = node.add(res);
            res.add(getLogicalAndExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getLogicalAndExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getBitwiseOrExpression(wrapper);
        while (next(wrapper, Tokens.T_AND)) {
            ExpAstNode node = new ExpAstNode(AstType.AST_AND);
            res = node.add(res);
            res.add(getBitwiseOrExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getBitwiseOrExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getBitwiseXorExpression(wrapper);
        while (next(wrapper, Tokens.T_BOR)) {
            ExpAstNode node = new ExpAstNode(AstType.AST_BOR);
            res = node.add(res);
            res.add(getBitwiseXorExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getBitwiseXorExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getBitwiseAndExpression(wrapper);
        while (next(wrapper, Tokens.T_BXOR)) {
            ExpAstNode node = new ExpAstNode(AstType.AST_BXOR);
            res = node.add(res);
            res.add(getBitwiseAndExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getBitwiseAndExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getEqualityExpression(wrapper);
        while (next(wrapper, Tokens.T_BAND)) {
            ExpAstNode node = new ExpAstNode(AstType.AST_BAND);
            res = node.add(res);
            res.add(getEqualityExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getEqualityExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getRelationalExpression(wrapper);
        while (test(wrapper, Tokens.T_EQ) || test(wrapper, Tokens.T_NEQ)) {
            ExpAstNode node;
            if (next(wrapper, Tokens.T_EQ)) {
                node = new ExpAstNode(AstType.AST_EQ);
            } else {
                next(wrapper, Tokens.T_NEQ);// we needed to read next token from buffer for right work
                node = new ExpAstNode(AstType.AST_NEQ);
            }
            res = node.add(res);
            res.add(getRelationalExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getRelationalExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getAdditiveExpression(wrapper);
        while (test(wrapper, Tokens.T_LE)
                || test(wrapper, Tokens.T_GE)
                || test(wrapper, Tokens.T_LT)
                || test(wrapper, Tokens.T_GT)
                ) {
            ExpAstNode node;
            if (next(wrapper, Tokens.T_LE)) {
                node = new ExpAstNode(AstType.AST_LE);
            } else if (next(wrapper, Tokens.T_GE)) {
                node = new ExpAstNode(AstType.AST_GE);
            } else if (next(wrapper, Tokens.T_LT)) {
                node = new ExpAstNode(AstType.AST_LT);
            } else {
                next(wrapper, Tokens.T_LT);// we needed to read next token from buffer for right work
                node = new ExpAstNode(AstType.AST_GT);
            }
            res = node.add(res);
            res.add(getAdditiveExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getAdditiveExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getMultiplicativeExpression(wrapper);
        while (test(wrapper, Tokens.T_PLUS) || test(wrapper, Tokens.T_MINUS)) {
            ExpAstNode node;
            if (next(wrapper, Tokens.T_PLUS)) {
                node = new ExpAstNode(AstType.AST_ADD);
            } else {
                next(wrapper, Tokens.T_MINUS);// we needed to read next token from buffer for right work
                node = new ExpAstNode(AstType.AST_SUB);
            }
            res = node.add(res);
            res.add(getMultiplicativeExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getMultiplicativeExpression(TokenizerWrapper wrapper) throws ParserException {
        ExpAstNode res = getUnaryExpression(wrapper);
        while (test(wrapper, Tokens.T_STAR) || test(wrapper, Tokens.T_SLASH) || test(wrapper, Tokens.T_MOD)) {
            ExpAstNode node;
            if (next(wrapper, Tokens.T_STAR)) {
                node = new ExpAstNode(AstType.AST_MUL);
            } else if (next(wrapper, Tokens.T_SLASH)) {
                node = new ExpAstNode(AstType.AST_DIV);
            } else {
                next(wrapper, Tokens.T_MOD);// we needed to read next token from buffer for right work
                node = new ExpAstNode(AstType.AST_MOD);
            }
            res = node.add(res);
            res.add(getUnaryExpression(wrapper));
        }
        return res;
    }

    private ExpAstNode getUnaryExpression(TokenizerWrapper wrapper) throws ParserException {
        if (next(wrapper, Tokens.T_NOT)) {
            ExpAstNode node = getUnaryExpression(wrapper);
            return new ExpAstNode(AstType.AST_NOT).add(node);
        } else if (next(wrapper, Tokens.T_MINUS)) {
            ExpAstNode node = getUnaryExpression(wrapper);
            return new ExpAstNode(AstType.AST_USUB).add(node);
        } else {
            return getMemberExpression(wrapper);
        }
    }

    private ExpAstNode getMemberExpression(TokenizerWrapper wrapper) throws ParserException {
//        ExpAstNode res = getPrimaryExpression(wrapper);
//
//        while (true) {
//            if (next(wrapper, Tokens.T_DOT)) {
//                res = new ExpAstNode(AstType.AST_PROP, res);
//                if (wrapper.test(Tokens.T_PROP.getId()) != null) {
//                    UnexpectedToken(wrapper, "IDENTIFIER");
//                }
//                res.setValue(wrapper.next().first().getValue());
//            } else if (next(wrapper, Tokens.T_METHOD)) {
//                res = new ExpAstNode(AstType.AST_METHOD, res);
//                if (wrapper.test(Tokens.T_PROP.getId()) != null) {
//                    UnexpectedToken(wrapper, "IDENTIFIER");
//                }
//                res.setValue(wrapper.next().first().getValue());
//            } else if (next(wrapper, Tokens.T_LBRACKET)) {
//                res = new ExpAstNode(AstType.AST_PROP, res);
//                res.add(getExpression(wrapper));
//                if (wrapper.test(Tokens.T_RBRACKET.getId()) != null) {
//                    UnexpectedToken(wrapper, "]");
//                }
//            } else if (next(wrapper, Tokens.T_LPAREN)) {
//                res = new ExpAstNode(AstType.AST_CALL, res);
//                if (wrapper.next(Tokens.T_RPAREN.getId()) != null) {
//                    continue;
//                }
//                do {
//                    res.add(getExpression(wrapper));
//                } while (next(wrapper, Tokens.T_COMMA));
//                if (wrapper.test(Tokens.T_RPAREN.getId()) != null) {
//                    UnexpectedToken(wrapper, "]");
//                }
//            } else {
//                return res;
//            }
//        }
        throw new NotImplementedException();
    }

    private AstNode getPrimaryExpression(TokenizerWrapper wrapper) throws ParserException {
        if (next(wrapper, Tokens.T_NULL)) {
            return new StringAstNode(null);
        } else if (next(wrapper, Tokens.T_TRUE)) {
            return new BooleanAstNode(true);
        } else if (next(wrapper, Tokens.T_FALSE)) {
            return new BooleanAstNode(false);
        } else if (next(wrapper, Tokens.T_SLASH)) {
            return getRegexpLiteral(wrapper);
        } else if (next(wrapper, Tokens.T_LITERAL_START)) {
            return getLiteralStatement(wrapper);
        } else if (test(wrapper, Tokens.T_SQUOTE)) {
            return getStringLiteral(wrapper);
        } else if (test(wrapper, Tokens.T_DQUOTE)) {
            return getStringLiteral(wrapper);
        } else if (next(wrapper, Tokens.T_LBRACKET)) {
            return getArrayExpression(wrapper);
        } else if (next(wrapper, Tokens.T_BLOCK_START)) {
            return getNodesStatement(wrapper, true);
        } else if (next(wrapper, Tokens.T_THIS)) {
            return new ExpAstNode(AstType.AST_THIS);
        } else if (next(wrapper, Tokens.T_GLOBAL)) {
            return new ExpAstNode(AstType.AST_GLOBAL);
        } else if (test(wrapper, Tokens.T_INT)) {
            return new LongAstNode(Integer.parseInt(wrapper.next().first().getValue(), 10));
        } else if (test(wrapper, Tokens.T_BIN)) {
            return new LongAstNode(Integer.parseInt(wrapper.next().first().getValue().substring(2), 2));
        } else if (test(wrapper, Tokens.T_HEX)) {
            return new LongAstNode(Integer.parseInt(wrapper.next().first().getValue().substring(2), 16));
        } else if (test(wrapper, Tokens.T_FLOAT)) {
            return new DoubleAstNode(Double.parseDouble(wrapper.next().first().getValue()));
        } else if (test(wrapper, Tokens.T_REF)) {
            return new ExpAstNode(AST_REF)
                    .add(new StringAstNode(wrapper.next().first().getValue()));
        } else if (next(wrapper, Tokens.T_LPAREN)) {
            return getParenthesizedExpression(wrapper);
        } else {
            UnexpectedToken(wrapper, "EXPRESSION");
        }
        //todo remove this thrown
        throw new NotImplementedException();
    }

    private ExpAstNode getArrayExpression(TokenizerWrapper wrapper) throws ParserException {
        int counter = 0;

        Map<String, Object> values = new HashMap<>();

//        Token key, value;

        ExpAstNode result = new ExpAstNode(AstType.AST_ARRAY);
//        String key;
//        ExpAstNode value;
        AstNode key;
        AstNode value;
        Map<String, AstNode> map = new LinkedHashMap<>();


        do {
            while (next(wrapper, Tokens.T_COMMA)) ;
            if (next(wrapper, Tokens.T_RBRACKET)) {
                return result;
            }
            TokenizerResult tokenRes = wrapper.next(Tokens.T_PROP.getId(), Tokens.T_COLON.getId());
            if (tokenRes.isFound()) {
                map.put(tokenRes.firstValue(), getExpression(wrapper));
            } else {
                key = getExpression(wrapper);
                if (key.hasValue()) {
                    Object val = ((ValueNode) key).getValue();
                    if ((ParserUtils.isString(val) || ParserUtils.isNumber(val)) && next(wrapper, Tokens.T_COLON)) {
                        value = getExpression(wrapper);
                        Object mapKey = val;
                        if (ParserUtils.isString(val) && ParserUtils.isInt((String) val)) {
                            mapKey = Integer.valueOf((String) val); //todo check this
                        }
                        if (ParserUtils.isDouble((String) val)) {
                            Double d = Double.parseDouble((String) val);
                            if (d.intValue() < counter) {
                                mapKey = d.intValue() + "";
                            } else {
                                counter = d.intValue();
                                mapKey = (counter++) + "";
                            }
                        }
                        map.put(mapKey + "", value);
                    } else {
                        map.put((counter++) + "", key);
                    }
                }
            }


//            if (t != null) {
//                key = t.getValue();
//                value = getExpression(wrapper);
//            } else if ((Utils_isString(key = Expression(ctx)) || Utils_isNumber(key)) && ctx.next(Tokens.T_COLON)) {
//                value = getExpression(wrapper);
//                if (Utils_isString(key) && validInteger.test(key)) {
//                    key = parseInt(key, 10);
//                }
//                if (Utils_isNumber(key)) {
//                    key = Math.floor(key);
//                    if (key < counter) {
//                        key = String(key);
//                    } else {
//                        counter = key, key = String(counter++);
//                    }
//                }
//            } else {
//                value = key;
//                key = String(counter++);
//            }
//            if (values.hasOwnProperty(key)) {
//                result[values[key]] = value;
//            } else {
//                values[key] = result.length;
//                result.push(value, key);
//            }
        } while (next(wrapper, Tokens.T_COMMA));

        if (!next(wrapper, Tokens.T_RBRACKET)) {
            UnexpectedToken(wrapper, "]");
        }
        for (Map.Entry<String, AstNode> entry : map.entrySet()) {
            result.add(new StringAstNode(entry.getKey()))
                    .add(entry.getValue());
        }

        return result;
    }

    private boolean isStringOrNumber(AstNode node) {
        if (node instanceof ValueNode) {
            Object value = ((ValueNode) node).getValue();
            if (value instanceof String
                    || value instanceof Double
                    || value instanceof Long) {
                return true;
            }
        }
        return false;
    }

    private StringAstNode getStringLiteral(TokenizerWrapper wrapper) throws ParserException {
        String start = wrapper.next().first().getValue();
        TokenizerResult fragment;
        final StringBuilder builder = new StringBuilder();
        while ((fragment = wrapper.next()).isFound()) {
            if (fragment.first().getTypes().contains(BaseTokens.T_EOF.getId())) {
                SyntaxError(wrapper, "unterminated string literal");
            }
            if (StringUtils.equals(fragment.first().getValue(), start)) {
                break;
            } else if (StringUtils.equals(fragment.first().getValue(), "\\")) {
                builder.append("\\")
                    .append(wrapper.next().first().getValue());
            } else {
                builder.append(fragment.first().getValue());
            }
        }
        return new StringAstNode(builder.toString()).escaped();
    }

    private StringAstNode getLiteralStatement(TokenizerWrapper wrapper) throws ParserException {
        wrapper = new TokenizerWrapper(wrapper);
        final StringBuilder builder = new StringBuilder("");
        while (wrapper.test(BaseTokens.T_EOF.getId(), Tokens.T_LITERAL_END.getId()) == null) {
            builder.append(wrapper.next().first().getValue());
        }
        if (!next(wrapper, Tokens.T_LITERAL_END)) {
            UnexpectedToken(wrapper, "%}}");
        }
        return new StringAstNode(builder.toString());
    }

    private AstNode getParenthesizedExpression(TokenizerWrapper wrapper) throws ParserException {
        AstNode node = getExpression(wrapper);
        if (!next(wrapper, Tokens.T_RPAREN)) {
            UnexpectedToken(wrapper, ")");
        }
        return node;
    }

    private ExpAstNode getRegexpLiteral(TokenizerWrapper wrapper) throws ParserException {
        final StringBuilder result = new StringBuilder("");
        boolean inCharSet = false;

        for (; ; ) {
            if (wrapper.test(BaseTokens.T_EOF.getId()).isFound()) {
                break;
            }
            if (test(wrapper, Tokens.T_EOL)) {
                break;
            }
            if (!inCharSet && test(wrapper, Tokens.T_SLASH)) {
                break;
            }
            if (next(wrapper, Tokens.T_BACKSLASH)) {
                result.append("\\");
            } else if (test(wrapper, Tokens.T_LBRACKET)) {
                inCharSet = true;
            } else if (test(wrapper, Tokens.T_RBRACKET)) {
                inCharSet = false;
            }
            result.append(wrapper.next().first().getValue());
        }

        if (!next(wrapper, Tokens.T_SLASH)) {
            SyntaxError(wrapper, "unterminated regexp literal");
        }

        try {
            Pattern.compile(result.toString());// TODO why result is ignored
        } catch (Exception e) {
            SyntaxError(wrapper, e.getMessage());
        }

        int flagNum = 0;
        Token flagToken = wrapper.next(Tokens.T_PROP.getId()).first();
        if (flagToken != null) {
            String flagStr = flagToken.getValue();

            if (!regexpFlagsPattern.matcher(flagStr).find()) {
                SyntaxError(wrapper, "invalid flags supplied to regular expression '" + flagStr + "'");
            }

            if (flagStr.contains("g")) {
                flagNum |= AstRegexType.RE_GLOBAL.getId();
            }
            if (flagStr.contains("m")) {
                flagNum |= AstRegexType.RE_MULTILINE.getId();
            }
            if (flagStr.contains("i")) {
                flagNum |= AstRegexType.RE_IGNORECASE.getId();
            }
        }

        return new ExpAstNode(AstType.AST_REGEXP)
                .add(new StringAstNode(result.toString()))
                .add(new LongAstNode(flagNum));
    }

    private boolean next(TokenizerWrapper wrapper, Tokens... tokens) {
        return wrapper.next(toIntArr(tokens)).isFound();
    }

    private boolean test(TokenizerWrapper wrapper, Tokens... tokens) {
        return wrapper.test(toIntArr(tokens)).isFound();
    }

    private Integer[] toIntArr(Tokens... tokens) {
        Integer[] ids = new Integer[tokens.length];
        for (Integer i = 0; i < tokens.length; i++) {
            ids[i] = tokens[i].getId();
        }
        return ids;
    }

    private void UnexpectedToken(TokenizerWrapper wrapper, String expected) throws ParserException {
        Token token = wrapper.next().first();
        int line = wrapper.getLineNumber(token.getIndex());
        String value = token.getTypes().contains(BaseTokens.T_EOF.getId()) ? "EOF" : token.getValue();
        String message = "unexpected '" + value + "', expected '" + expected + "'";
        throw new ParserException(message, wrapper.getBaseURI(), line);
    }

    private void SyntaxError(TokenizerWrapper wrapper, String s) throws ParserException {
        Token token = wrapper.next().first();
        int line = wrapper.getLineNumber(token.getIndex());
        throw new ParserException(s, wrapper.getBaseURI(), line);
    }
}
