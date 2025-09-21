package org.txedt.windowing;

import javax.swing.*;

public final class WFrame extends JFrame {
    public final Window window;
    public final WPanel panel;

    public WFrame(Window window) {
        this.window = window;
        panel = new WPanel(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Txedt window!");
        setSize(400, 400);
        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void dispose() {
        window.disposed = true;
        window.frame = null;
        super.dispose();
    }
}
