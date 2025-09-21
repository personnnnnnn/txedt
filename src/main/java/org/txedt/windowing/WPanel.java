package org.txedt.windowing;

import org.jetbrains.annotations.Nullable;
import org.txedt.lang.errors.TxedtThrowable;

import javax.swing.*;
import java.awt.*;

public final class WPanel extends JPanel {
    public final WFrame frame;
    public @Nullable TxedtThrowable error = null;

    public WPanel(WFrame frame) {
        this.frame = frame;
        setDoubleBuffered(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        frame.window.setCtx((Graphics2D) g);
        try {
            frame.window.action.complete();
            error = null;
        } catch (TxedtThrowable e) {
            error = e;
        } finally {
            frame.window.g2d = null;
            frame.window.action = () -> {};
        }
    }
}
