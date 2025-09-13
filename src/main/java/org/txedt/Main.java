package org.txedt;

import org.txedt.errors.TxedtError;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Node;
import org.txedt.parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
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

        System.out.println(text);
        Node.Lst nodes;
        try {
            var backtrace = new Backtrace();
            nodes = new Node.Lst(backtrace, Parser.parse(backtrace, file, text));
        } catch (TxedtError e) {
            System.out.println(e.getOutString());
            return;
        }
        System.out.println("Ok: " + nodes);
    }
}
