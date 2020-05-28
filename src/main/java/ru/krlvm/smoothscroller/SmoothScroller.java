package ru.krlvm.smoothscroller;

import ru.krlvm.smoothscroller.mouse.MouseActionListener;
import ru.krlvm.smoothscroller.mouse.MouseHandler;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SmoothScroller
 * Smooth scrolling effect for all programs
 *
 * @author krlvm
 */
public class SmoothScroller {

    private static final String NAME = "SmoothScroller";
    private static final String VERSION = "0.1";
    private static final String GITHUB_PAGE = "https://github.com/krlvm/SmoothScroller";

    private static Robot robot;

    private static Thread SCROLL_THREAD = null;
    private static boolean SCROLLING = false;

    private static boolean TO_EXISTING_THREAD = false;

    private static int BASE_WHEEL_AM0UNT = 1;
    private static int fadingWheelAmount = -1;

    private static long SCROLL_INTERVAL = 55L;
    private static int INITIAL_SCROLL_ATTEMPTS = 3;
    private static int SCROLL_ATTEMPTS = 0;

    private static int currentDirection = 1; //1=down -1=up
    public static final AtomicInteger PENDING_ATTEMPTS = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println(NAME + " v" + VERSION);
        System.out.println("Smooth scrolling effect for all programs");
        System.out.println(GITHUB_PAGE);
        System.out.println("(c) krlvm, 2020");
        System.out.println();

        if(args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (!arg.startsWith("-")) {
                    continue;
                }
                arg = arg.replaceFirst("-", "").toLowerCase();
                switch (arg) {
                    case "help": {
                        System.out.println("Available arguments:\n" +
                                " -help                             display help                                               \n" +
                                " -to-existing-thread               submit subsequent scrolls to an existing thread     (false)\n" +
                                " -base-wheel-amount [amount]       sets base amount of scroll                          (1)     \n" +
                                " -fading-wheel-amount [amount]     sets fading amount of scroll (ending)               (1)     \n" +
                                " -interval [milliseconds]          sets interval between scroll                        (50)    \n" +
                                " -attempts [attempts]              sets scroll attempts                                (3)     ");
                        System.exit(0);
                        break;
                    }
                    case "to-existing-thread": {
                        TO_EXISTING_THREAD = true;
                        break;
                    }
                    default: {
                        if (args.length < i + 2) {
                            System.out.println("[!] Invalid input for option '" + arg + "'");
                        } else {
                            String value = args[i + 1];
                            int intValue;
                            try {
                                intValue = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {
                                System.out.println("[!] Invalid number value for argument '" + arg + "': '" + value + "'");
                                break;
                            }
                            switch (arg) {
                                case "base-wheel-amount": {
                                    BASE_WHEEL_AM0UNT = intValue;
                                    break;
                                }
                                case "fading-wheel-amount": {
                                    fadingWheelAmount = intValue;
                                    break;
                                }
                                case "interval": {
                                    SCROLL_INTERVAL = intValue;
                                    break;
                                }
                                case "attempts": {
                                    INITIAL_SCROLL_ATTEMPTS = intValue;
                                    break;
                                }
                                default: {
                                    //it is an argument
                                    //Utility.print("[?] Unknown option: '%s'", arg);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        if(fadingWheelAmount == -1) {
            fadingWheelAmount = BASE_WHEEL_AM0UNT / 2;
            if (fadingWheelAmount == 0) {
                fadingWheelAmount = 1;
            }
        }

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            System.out.println("[x] " + NAME + " is incompatible with this system");
            System.out.println("[*] Try launching the program with escalated privileges");
            System.out.println("[i] Submit an issue: " + GITHUB_PAGE + "/issues");
            return;
        }
        //robot.setAutoDelay(0);

        MouseHandler.bind(new MouseActionListener() {
            @Override
            public void mouseClick() {
                suspendScroll();
            }

            @Override
            public void mouseWheel(boolean direction) {
                int newDirection = direction ? -1 : 1;
                if(newDirection != currentDirection) {
                    suspendScroll();
                }
                currentDirection = newDirection;
                scroll();
            }
        });
    }

    private static void scroll() {
        if(PENDING_ATTEMPTS.get() != 0) {
            PENDING_ATTEMPTS.decrementAndGet();
            return;
        }
        if(isScrolling() && TO_EXISTING_THREAD) {
            SCROLL_ATTEMPTS++;
        } else {
            createThread();
        }
    }

    private static void createThread() {
        if(isScrolling()) {
            suspendScroll();
        }
        SCROLL_ATTEMPTS = INITIAL_SCROLL_ATTEMPTS;
        SCROLL_THREAD = new Thread(() -> {
            SCROLLING = true;
            while(!Thread.interrupted() && 0 < SCROLL_ATTEMPTS) {
                PENDING_ATTEMPTS.incrementAndGet();
                robot.mouseWheel(calculateScrollAmount()*currentDirection);
                SCROLL_ATTEMPTS--;
                try {
                    Thread.sleep(SCROLL_INTERVAL);
                } catch (InterruptedException ignored) {}
            }
            SCROLLING = false;
        }, "Scroll Thread");
        SCROLL_THREAD.start();
    }

    private static void suspendScroll() {
        if(isScrolling()) {
            SCROLL_THREAD.interrupt();
            SCROLL_THREAD = null;
        }
    }

    private static boolean isScrolling() {
        return SCROLL_THREAD != null && SCROLL_THREAD.isAlive() && !SCROLL_THREAD.isInterrupted() && SCROLLING;
    }

    private static int calculateScrollAmount() {
        int amount;
        if(SCROLL_ATTEMPTS < INITIAL_SCROLL_ATTEMPTS*0.8) {
            amount = fadingWheelAmount;
        } else {
            amount = BASE_WHEEL_AM0UNT;
        }
        return amount;
    }
}
