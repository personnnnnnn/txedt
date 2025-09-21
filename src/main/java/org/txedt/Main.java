package org.txedt;

import org.txedt.lang.contexts.Context;
import org.txedt.lang.errors.TxedtThrowable;
import org.txedt.lang.interpreter.CallData;
import org.txedt.lang.interpreter.Interpreter;
import org.txedt.lang.libs.IO;
import org.txedt.lang.libs.Std;
import org.txedt.lang.parser.Backtrace;
import org.txedt.lang.parser.Node;
import org.txedt.lang.parser.Parser;
import org.txedt.windowing.Windowing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        program();
    }

    public static void program() {
        String file = "./lisp/main.txel";
        String text;

        try {
            var sb = new StringBuilder();
            File f = new File(file);
            Scanner scanner = new Scanner(f);
            var join = "";
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine().replace("\r", "");
                sb.append(join).append(data);
                join = "\n";
            }
            scanner.close();
            text = sb.toString();
        } catch (FileNotFoundException e) {
            System.out.println("File " + file + " not found!");
            return;
        }

        var globals = new Context()
                .parent(IO.ctx)
                .parent(Std.ctx);
        globals.put("std", Std.ctx);
        globals.put("io", IO.ctx);
        globals.put("windowing", Windowing.ctx);

        List<Node> nodes;
        try {
            var backtrace = new Backtrace("<global>", null);
            nodes = Parser.parse(backtrace, file, text);
            for (var stmt : nodes) {
                var data = new CallData(Backtrace.sameWith(backtrace, stmt.bounds), globals);
                Interpreter.eval(stmt, data);
            }
        } catch (TxedtThrowable e) {
            System.out.println(e.getOutString());
        }
    }
}
