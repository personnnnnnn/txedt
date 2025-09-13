package org.txedt;

import org.txedt.contexts.Context;
import org.txedt.errors.TxedtThrowable;
import org.txedt.interpreter.CallData;
import org.txedt.interpreter.Interpreter;
import org.txedt.libs.IO;
import org.txedt.libs.Std;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Node;
import org.txedt.parser.Parser;

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
            var join = "";
            File f = new File(file);
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                sb.append(data).append(join);
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

        List<Node> nodes;
        try {
            var backtrace = new Backtrace();
            nodes = Parser.parse(backtrace, file, text);
            var data = new CallData(backtrace, globals);
            for (var stmt : nodes) {
                Interpreter.eval(stmt, data);
            }
        } catch (TxedtThrowable e) {
            System.out.println(e.getOutString());
        }
    }
}
