package org.txedt.lang.libs;

import org.txedt.lang.contexts.Context;
import org.txedt.lang.functions.ExternalFunction;
import org.txedt.lang.functions.signature.FunctionSignature;
import org.txedt.lang.general.TxedtStr;

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
