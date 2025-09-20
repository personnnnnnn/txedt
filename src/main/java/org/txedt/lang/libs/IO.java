package org.txedt.libs;

import org.txedt.contexts.Context;
import org.txedt.functions.ExternalFunction;
import org.txedt.functions.signature.FunctionSignature;
import org.txedt.general.TxedtStr;

import java.util.Scanner;

public final class IO {
    public static final Context ctx;
    private IO() { }

    public static final Scanner scanner;

    static {
        scanner = new Scanner(System.in);

        ctx = new Context();

        ctx.put("print", new ExternalFunction(
                "print",
                new FunctionSignature()
                        .arg("msg"),
                (_, args) -> {
                    var msg = args.get("msg");
                    System.out.println(TxedtStr.toString(msg));
                    return null;
                }
        ));

        ctx.put("input", new ExternalFunction(
                "input",
                new FunctionSignature(),
                (_, _) -> scanner.nextLine()
        ));
    }
}
