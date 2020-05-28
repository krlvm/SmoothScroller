package ru.krlvm.smoothscroller.mouse;

import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

/**
 * Underlying library:
 *
 * system-hook
 * https://github.com/kristian/system-hook
 * MIT License
 *
 *
 * Only Windows supported at the moment due to the library restriction
 * TODO: Find another library to support Linux distributions
 */
public class MouseHandler {

    public static void bind(final MouseActionListener listener) {
        GlobalMouseHook mouseHook = new GlobalMouseHook();
        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mousePressed(GlobalMouseEvent event)  {
                listener.mouseClick();
            }
            @Override
            public void mouseWheel(GlobalMouseEvent event) {
                listener.mouseWheel(event.getDelta() > 0);
            }
        });
    }
}
