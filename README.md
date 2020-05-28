# SmoothScroller: Smooth scrolling effect for all programs
SmoothScroller captures mouse scroll events across all the system and makes your scrolling smoother.

Very early preview version, only Windows systems are supported at the moment

## How to install
You can get the binary from the [Releases](https://github.com/krlvm/SmoothScroller/releases) tab.

[Java 8](https://www.java.com/en/download/win10.jsp) required to run.

## How to start
Execute following command:

`$ java -jar SmoothScroller.jar`

To start without console window:

`$ javaw -jar SmoothScroller.jar`

### Configuring
You can get ideal smooth scroll by tuning a few parameters through CLI:

```
$ java -jar SmoothScroller.jar -help

Available arguments:
 -help                             display help
 -to-existing-thread               submit subsequent scrolls to an existing thread     (false)
 -base-wheel-amount [amount]       sets base amount of scroll                          (1)
 -fading-wheel-amount [amount]     sets fading amount of scroll (ending)               (1)
 -interval [milliseconds]          sets interval between scroll                        (50)
 -attempts [attempts]              sets scroll attempts                                (3)
```

## Dependencies
* [system-hook](https://github.com/kristian/system-hook) - mouse events capture library