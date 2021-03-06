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

package ru.histone.v2.parser.node;

/**
 * @author Alexey Nevinsky
 */
public enum AstType {
    AST_NOP(0),
    AST_ARRAY(1),
    AST_REGEXP(2),
    AST_THIS(3),
    AST_GLOBAL(4),
    AST_NOT(5),
    AST_AND(6),
    AST_OR(7),
    AST_TERNARY(8),
    AST_ADD(9),
    AST_SUB(10),
    AST_MUL(11),
    AST_DIV(12),
    AST_MOD(13),
    AST_USUB(14),
    AST_LT(15),
    AST_GT(16),
    AST_LE(17),
    AST_GE(18),
    AST_EQ(19),
    AST_NEQ(20),
    AST_REF(21),
    AST_CALL(22),
    AST_VAR(23),
    AST_IF(24),
    AST_FOR(25),
    AST_MACRO(26),
    // {{if ...}} {{return 10}} {{/if}} - throws from the template
    // {{for ...}} {{return 10}} {{/for}} - throws from the template
    // {{macro ...}} {{return 10}} {{/macro}} - throws from the macro
    // {{[foo: {{ bla bla {{return 10}} }} ] }} - throws from the block of code
    AST_RETURN(27),
    AST_NODES(28),
    AST_NODELIST(29),

    AST_BOR(30),
    AST_BXOR(31),
    AST_BAND(32),

    AST_SUPPRESS(33),
    AST_BLS(34),
    AST_BRS(35),
    AST_BNOT(36),

    AST_BREAK(37),
    AST_CONTINUE(38),
    AST_WHILE(39),

    AST_T_NOP(-1),
    AST_T_BREAK(-2),
    AST_T_ARRAY(-3),
    AST_VALUE_NODE(Integer.MIN_VALUE);

    private final int id;

    AstType(int id) {
        this.id = id;
    }

    public static AstType fromId(int id) {
        for (AstType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("wrong AstType id '" + id + "'");
    }

    public int getId() {
        return id;
    }
}
