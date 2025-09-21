package org.txedt.windowing;

import org.jetbrains.annotations.NotNull;
import org.txedt.lang.contexts.Context;
import org.txedt.lang.errors.TxedtError;
import org.txedt.lang.functions.ExternalFunction;
import org.txedt.lang.functions.signature.FunctionSignature;
import org.txedt.lang.general.TxedtBool;
import org.txedt.lang.interpreter.CallData;
import org.txedt.lang.interpreter.Interpreter;
import org.txedt.lang.macros.ExternalMacro;
import org.txedt.lang.parser.Backtrace;
import org.txedt.lang.properties.PropertyValue;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class Windowing {
    private Windowing() { }

    public final static Context ctx;

    public static @NotNull String leftPad(char c, @NotNull String s, int len) {
        if (s.length() >= len) {
            return s;
        }
        return (c + "").repeat(len - s.length()) + s;
    }

    static {
        ctx = new Context();
        ctx.put("normal", Font.PLAIN);
        ctx.put("italic", Font.ITALIC);
        ctx.put("bold", Font.BOLD);
        ctx.put("bold-italic", Font.BOLD | Font.ITALIC);

        ctx.put("top", Window.TEXT_TOP);
        ctx.put("bottom", Window.TEXT_BOTTOM);
        ctx.put("left", Window.TEXT_LEFT);
        ctx.put("right", Window.TEXT_RIGHT);
        ctx.put("center", Window.TEXT_CENTER);
        ctx.put("middle", Window.TEXT_MIDDLE);

        ctx.put("create", new ExternalFunction(
                "create",
                new FunctionSignature(),
                (backtrace, args) -> new Window()
        ));

        ctx.put("color", new ExternalFunction(
                "color",
                new FunctionSignature()
                        .arg("r")
                        .arg("g")
                        .arg("b")
                        .opt("a"),
                (backtrace, args) -> {
                    if (!(args.get("r") instanceof Integer r)) {
                        throw new TxedtError(backtrace, "'r' must be an int");
                    }
                    if (!(0 <= r && r <= 255)) {
                        throw new TxedtError(backtrace, "'r' is outside the range 0-255 (" + r + ")");
                    }
                    if (!(args.get("g") instanceof Integer g)) {
                        throw new TxedtError(backtrace, "'g' must be an int");
                    }
                    if (!(0 <= g && g <= 255)) {
                        throw new TxedtError(backtrace, "'g' is outside the range 0-255 (" + g + ")");
                    }
                    if (!(args.get("b") instanceof Integer b)) {
                        throw new TxedtError(backtrace, "'b' must be an int");
                    }
                    if (!(0 <= b && b <= 255)) {
                        throw new TxedtError(backtrace, "'b' is outside the range 0-255 (" + b + ")");
                    }
                    if (!(args.getOrDefault("a", 255) instanceof Integer a)) {
                        throw new TxedtError(backtrace, "'a' must be an int");
                    }
                    if (!(0 <= a && a <= 255)) {
                        throw new TxedtError(backtrace, "'a' is outside the range 0-255 (" + a + ")");
                    }
                    return new Color(r, g, b, a);
                }
        ));

        ctx.put("closed", new ExternalFunction(
                "closed",
                new FunctionSignature().arg("window"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    return TxedtBool.to(window.disposed);
                }
        ));

        ctx.put("title", new PropertyValue(
                "title",
                new ExternalFunction(
                        "title",
                        new FunctionSignature().arg("window"),
                        (backtrace, args) -> {
                            if (!(args.get("window") instanceof Window window)) {
                                throw new TxedtError(backtrace, "'window' must be a window");
                            }
                            return window.title();
                        }
                ),
                new ExternalFunction(
                        "title",
                        new FunctionSignature().arg("window").arg("title"),
                        (backtrace, args) -> {
                            if (!(args.get("window") instanceof Window window)) {
                                throw new TxedtError(backtrace, "'window' must be a window");
                            }
                            if (!(args.get("title") instanceof String title)) {
                                throw new TxedtError(backtrace, "'title' must be a string");
                            }
                            window.title(title);
                            return window.title();
                        }
                )
        ));

        ctx.put("resize", new ExternalFunction(
                "resize",
                new FunctionSignature().arg("window").arg("width").arg("height"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    if (!(args.get("width") instanceof Integer width)) {
                        throw new TxedtError(backtrace, "'width' must be an int");
                    }
                    if (!(args.get("height") instanceof Integer height)) {
                        throw new TxedtError(backtrace, "'height' must be an int");
                    }
                    window.resize(width, height);
                    return null;
                }
        ));

        ctx.put("width", new ExternalFunction(
                "width",
                new FunctionSignature().arg("window"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    return window.width();
                }
        ));

        ctx.put("height", new ExternalFunction(
                "height",
                new FunctionSignature().arg("window"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    return window.height();
                }
        ));

        ctx.put("center-x", new ExternalFunction(
                "center-x",
                new FunctionSignature().arg("window"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    return window.centerX();
                }
        ));

        ctx.put("close", new ExternalFunction(
                "close",
                new FunctionSignature().arg("window"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    if (window.disposed) {
                        throw new TxedtError(backtrace, "window was already closed");
                    }
                    window.dispose();
                    return null;
                }
        ));

        ctx.put("ensure-closed", new ExternalFunction(
                "ensure-closed",
                new FunctionSignature().arg("window"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    if (!window.disposed) {
                        window.dispose();
                    }
                    return null;
                }
        ));

        ctx.put("center-y", new ExternalFunction(
                "center-y",
                new FunctionSignature().arg("window"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    return window.centerY();
                }
        ));

        ctx.put("rect", new ExternalFunction(
                "rect",
                new FunctionSignature().arg("window").arg("x").arg("y").arg("width").arg("height").arg("color"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    if (!(args.get("x") instanceof Integer x)) {
                        throw new TxedtError(backtrace, "'x' must be an int");
                    }
                    if (!(args.get("y") instanceof Integer y)) {
                        throw new TxedtError(backtrace, "'y' must be an int");
                    }
                    if (!(args.get("width") instanceof Integer width)) {
                        throw new TxedtError(backtrace, "'width' must be an int");
                    }
                    if (!(args.get("height") instanceof Integer height)) {
                        throw new TxedtError(backtrace, "'height' must be an int");
                    }
                    if (!(args.get("color") instanceof Color color)) {
                        throw new TxedtError(backtrace, "'color' must be a color");
                    }
                    if (window.disposed) {
                        throw new TxedtError(backtrace, "can't draw to closed window");
                    }
                    if (window.g2d == null) {
                        throw new TxedtError(backtrace, "can't draw to window outside of it's respective 'windowing:draw-to' block");
                    }
                    window.rect(x, y, width, height, color);
                    return null;
                }
        ));

        ctx.put("text", new ExternalFunction(
                "text",
                new FunctionSignature().arg("window").arg("font").arg("alignment").arg("text").arg("x").arg("y").arg("color"),
                (backtrace, args) -> {
                    if (!(args.get("window") instanceof Window window)) {
                        throw new TxedtError(backtrace, "'window' must be a window");
                    }
                    if (!(args.get("font") instanceof Font font)) {
                        throw new TxedtError(backtrace, "'font' must be a font");
                    }
                    if (!(args.get("alignment") instanceof Integer alignment)) {
                        throw new TxedtError(backtrace, "'alignment' must be an int");
                    }
                    if (!(args.get("text") instanceof String text)) {
                        throw new TxedtError(backtrace, "'text' must be a string");
                    }
                    if (!(args.get("x") instanceof Integer x)) {
                        throw new TxedtError(backtrace, "'x' must be an int");
                    }
                    if (!(args.get("y") instanceof Integer y)) {
                        throw new TxedtError(backtrace, "'y' must be an int");
                    }
                    if (!(args.get("color") instanceof Color color)) {
                        throw new TxedtError(backtrace, "'color' must be a color");
                    }
                    if (window.disposed) {
                        throw new TxedtError(backtrace, "can't draw to closed window");
                    }
                    if (window.g2d == null) {
                        throw new TxedtError(backtrace, "can't draw to window outside of it's respective 'windowing:draw-to' block");
                    }
                    if (!Window.alignmentFlagValid(alignment)) {
                        throw new TxedtError(backtrace, "invalid alignment flag: 0x" + leftPad('0', Integer.toBinaryString(alignment), 4));
                    }
                    window.text(text, font, alignment, x, y, color);
                    return null;
                }
        ));

        ctx.put("draw-to", new ExternalMacro(
                "draw-to",
                (callData, args) -> {
                    if (args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "expected window and body");
                    }
                    var windowNode = args.children.getFirst();
                    var windowValue = Interpreter.evalWith(windowNode, callData);
                    if (!(windowValue instanceof Window window)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), windowNode.bounds), "expected window");
                    }
                    var rest = args.sublist(1);
                    AtomicReference<Object> result = new AtomicReference<>();
                    window.draw(() ->
                            result.set(Interpreter.exec(rest.children, new CallData(Backtrace.sameWith(callData.backtrace(), rest.bounds), callData.context()))));
                    return result;
                }
        ));

        ctx.put("clock", new ExternalFunction(
                "clock",
                new FunctionSignature(),
                (_, _) -> new Clock()
        ));

        ctx.put("ensure-framerate", new ExternalFunction(
                "ensure-framerate",
                new FunctionSignature().arg("clock").arg("target-fps"),
                (backtrace, args) -> {
                    if (!(args.get("clock") instanceof Clock clock)) {
                        throw new TxedtError(backtrace, "'clock' must be a clock");
                    }
                    if (!(args.get("target-fps") instanceof Number targetFpsNum)) {
                        throw new TxedtError(backtrace, "'target-fps' must be a number");
                    }
                    var targetFps = targetFpsNum.doubleValue();
                    clock.frameRate(targetFps);
                    return null;
                }
        ));

        ctx.put("font", new ExternalFunction(
                "font",
                new FunctionSignature().arg("name").arg("style").arg("size"),
                (backtrace, args) -> {
                    if (!(Objects.requireNonNullElse(args.get("name"), "Default") instanceof String fontName)) {
                        throw new TxedtError(backtrace, "'name' must be a string or nil");
                    }
                    if (!(args.get("style") instanceof Integer style)) {
                        throw new TxedtError(backtrace, "'style' must be an int");
                    }
                    if (!(args.get("size") instanceof Integer size)) {
                        throw new TxedtError(backtrace, "'size' must be an int");
                    }
                    // intellij doesn't trust 'style' to be PLAIN, BOLD or ITALIC
                    // is this better?
                    return new Font(fontName, style == Font.PLAIN ? Font.PLAIN : style == Font.BOLD ? Font.BOLD : style == Font.ITALIC ? Font.ITALIC : Font.BOLD | Font.ITALIC, size);
                }
        ));

        ctx.put("derive-font", new ExternalFunction(
                "derive-font",
                new FunctionSignature().arg("src").arg("style").arg("size"),
                (backtrace, args) -> {
                    if (!(args.get("src") instanceof Font src)) {
                        throw new TxedtError(backtrace, "'src' must be a font");
                    }
                    if (!(args.get("style") instanceof Integer style)) {
                        throw new TxedtError(backtrace, "'style' must be an int");
                    }
                    if (!(args.get("size") instanceof Integer size)) {
                        throw new TxedtError(backtrace, "'size' must be an int");
                    }
                    // intellij doesn't trust 'style' to be PLAIN, BOLD or ITALIC
                    // is this better?
                    return src.deriveFont(style == Font.PLAIN ? Font.PLAIN : style == Font.BOLD ? Font.BOLD : style == Font.ITALIC ? Font.ITALIC : Font.BOLD | Font.ITALIC, size);
                }
        ));

        ctx.put("derive-font-style", new ExternalFunction(
                "derive-font-style",
                new FunctionSignature().arg("src").arg("style"),
                (backtrace, args) -> {
                    if (!(Objects.requireNonNullElse(args.get("name"), "Default") instanceof String fontName)) {
                        throw new TxedtError(backtrace, "'name' must be a string or nil");
                    }
                    if (!(args.get("src") instanceof Font src)) {
                        throw new TxedtError(backtrace, "'src' must be a font");
                    }
                    if (!(args.get("style") instanceof Integer style)) {
                        throw new TxedtError(backtrace, "'style' must be an int");
                    }
                    // intellij doesn't trust 'style' to be PLAIN, BOLD or ITALIC
                    // is this better?
                    return src.deriveFont(style == Font.PLAIN ? Font.PLAIN : style == Font.BOLD ? Font.BOLD : style == Font.ITALIC ? Font.ITALIC : Font.BOLD | Font.ITALIC);
                }
        ));

        ctx.put("derive-font-size", new ExternalFunction(
                "derive-font-size",
                new FunctionSignature().arg("src").arg("size"),
                (backtrace, args) -> {
                    if (!(Objects.requireNonNullElse(args.get("name"), "Default") instanceof String fontName)) {
                        throw new TxedtError(backtrace, "'name' must be a string or nil");
                    }
                    if (!(args.get("src") instanceof Font src)) {
                        throw new TxedtError(backtrace, "'src' must be a font");
                    }
                    if (!(args.get("size") instanceof Number size)) {
                        throw new TxedtError(backtrace, "'size' must be a number int");
                    }
                    return src.deriveFont(size.floatValue());
                }
        ));
    }
}
