package org.txedt;

import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.std.controlFlow.*;
import org.txedt.interpreter.std.general.*;
import org.txedt.interpreter.std.ops.*;
import org.txedt.interpreter.std.vars.*;
import org.txedt.parser.Parser;
import org.txedt.parser.nodes.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String file = "./lisp/main.txel";
        StringBuilder text = new StringBuilder();

        try {
            File f = new File(file);
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                text.append(data).append('\n');
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + file + " not found!");
            return;
        }

        var ctx = new Context();

        ctx.put("print", PrintFn.func);
        ctx.put("input", InputFn.func);
        ctx.put("given", GivenMacro.macro);
        ctx.put("fn", FnMacro.macro);
        ctx.put("var", VarMacro.macro);
        ctx.put("cat", CatFn.func);
        ctx.put("min", MinFn.func);
        ctx.put("max", MaxFn.func);
        ctx.put("prog", ProgMacro.macro);
        ctx.put("progn", PrognMacro.macro);
        ctx.put("ast", AstMacro.macro);
        ctx.put("list", ListFn.func);
        ctx.put("let", LetMacro.macro);
        ctx.put("let*", LetStarMacro.macro);
        ctx.put("return", ReturnFn.func);
        ctx.put("return-from", ReturnFromMacro.macro);
        ctx.put("block", BlockMacro.macro);
        ctx.put("if", IfMacro.macro);
        ctx.put("when", WhenMacro.macro);
        ctx.put("unless", UnlessMacro.macro);
        ctx.put("+", AddFn.func);
        ctx.put("-", SubFn.func);
        ctx.put("*", MulFn.func);
        ctx.put("/", DivFn.func);
        ctx.put("1+", Plus1Fn.func);
        ctx.put("1-", Minus1Fn.func);
        ctx.put("%", ModFn.func);
        ctx.put("<", LtFn.func);
        ctx.put("<=", LteFn.func);
        ctx.put(">", GtFn.func);
        ctx.put(">=", GteFn.func);
        ctx.put("=", EqFn.func);
        ctx.put("!=", NeqFn.func);
        ctx.put("set", SetMacro.macro);
        ctx.put("loop", LoopMacro.macro);
        ctx.put("and", AndFn.func);
        ctx.put("or", OrFn.func);
        ctx.put("not", NotFn.func);
        ctx.put("is", IsFn.func);
        ctx.put("isnt", IsntFn.func);

        Parser parser = new Parser(file, text.toString());
        try {
            for (Node node = parser.nextNode(); node != null; node = parser.nextNode()) {
                Interpreter.eval(node, ctx);
            }
        } catch (TxedtThrowable e) {
            System.out.println(e.getMessage());
        }
    }
}
