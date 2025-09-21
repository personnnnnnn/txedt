package org.txedt.windowing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.lang.errors.TxedtThrowable;

import java.awt.*;

public final class Window {
    //                                      XXYY
    public static final int TEXT_TOP    = 0b0010;
    public static final int TEXT_BOTTOM = 0b0001;
    public static final int TEXT_CENTER = 0b0011;
    public static final int TEXT_LEFT   = 0b1000;
    public static final int TEXT_RIGHT  = 0b0100;
    public static final int TEXT_MIDDLE = 0b1100;

    public static boolean alignmentFlagValid(int alignment) {
        return (alignment & 0b1111) == alignment && alignment >> 2 != 0 || (alignment & 0b11) != 0;
    }

    public boolean disposed = false;
    public WFrame frame;

    public @Nullable Graphics2D g2d = null;
    @NotNull DrawAction action = () -> {};

    public void draw(DrawAction action) throws TxedtThrowable {
        if (disposed) {
            throw new RuntimeException("cannot draw to closed window");
        }
        this.action = action;
        frame.panel.repaint();
        if (frame.panel != null && frame.panel.error != null) {
            throw frame.panel.error;
        }
    }

    public Window() {
        frame = new WFrame(this);
    }

    public int width() {
        return frame.getWidth();
    }

    public int centerX() {
        return width() / 2;
    }

    public int height() {
        return frame.getHeight();
    }

    public int centerY() {
        return height() / 2;
    }

    public void resize(int width, int height) {
        frame.setSize(width, height);
    }

    public String title() {
        return frame.getTitle();
    }

    public void title(String title) {
        frame.setTitle(title);
    }

    public void setCtx(@NotNull Graphics2D g2d) {
        this.g2d = g2d;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public void rect(int x, int y, int width, int height, Color color) {
        if (g2d == null) {
            throw new RuntimeException("tried to draw a rectangle outside of a DrawAction");
        }
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
    }

    public int textHeight(Font font) {
        if (g2d == null) {
            throw new RuntimeException("tried to get text height outside of a DrawAction");
        }
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        return fm.getAscent();
    }

    public int textWidth(Font font, String text) {
        if (g2d == null) {
            throw new RuntimeException("tried to get text width outside of a DrawAction");
        }
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        return fm.stringWidth(text);
    }

    public void text(String text, Font font, int alignment, int x, int y, Color color) {
        if (g2d == null) {
            throw new RuntimeException("tried to draw text outside of a DrawAction");
        }
        if (!alignmentFlagValid(alignment)) {
            throw new RuntimeException("invalid alignment flag");
        }

        g2d.setColor(color);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        int alignedX = switch (alignment & 0b1100) {
            case TEXT_LEFT -> x;
            case TEXT_RIGHT -> x - fm.stringWidth(text);
            default -> x - fm.stringWidth(text) / 2;
        };

        int alignedY = switch (alignment & 0b0011) {
            case TEXT_BOTTOM -> y;
            case TEXT_TOP -> y + fm.getAscent();
            default -> y + fm.getAscent() / 2;
        };

        g2d.drawString(text, alignedX, alignedY);
    }

    public void dispose() {
        if (disposed) {
            throw new RuntimeException("window already closed");
        }
        frame.dispose();
    }
}
