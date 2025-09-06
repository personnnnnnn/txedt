package org.txedt;

import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.std.controlFlow.*;
import org.txedt.interpreter.std.general.AstMacro;
import org.txedt.interpreter.std.general.ListFn;
import org.txedt.interpreter.std.general.PrintFn;
import org.txedt.interpreter.std.ops.*;
import org.txedt.interpreter.std.vars.LetMacro;
import org.txedt.interpreter.std.vars.LetStarMacro;
import org.txedt.interpreter.std.vars.SetMacro;
import org.txedt.parser.nodes.Node;
import org.txedt.parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String file = "./lisp/main.txl";
        StringBuilder text = new StringBuilder();

        try {
            File f = new File(file);
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                text.append(data);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + file + " not found!");
            return;
        }

        Parser parser = new Parser(file, text.toString());
        try {
            Node node = parser.nextNode();
            System.out.println("> " + node);
            if (node == null) {
                throw new Error("unexpected eof");
            }
            var ctx = new Context();

            ctx.put("print", PrintFn.func);
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
            ctx.put("!=", NeqSign.func);
            ctx.put("set", SetMacro.macro);
            ctx.put("loop", LoopMacro.macro);
            ctx.put("and", AndFn.func);
            ctx.put("or", OrFn.func);
            ctx.put("not", NotFn.func);
            ctx.put("is", IsFn.func);
            ctx.put("isnt", IsntFn.func);

            System.out.println("> " + Interpreter.eval(node, ctx));
        } catch (TxedtThrowable e) {
            System.out.println(e.getMessage());
        }

    }
}
