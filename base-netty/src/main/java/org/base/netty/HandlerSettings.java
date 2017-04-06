package org.base.netty;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

/**
 * Allows autoFlowControl to be turned on and off from interop testing and flow control windows to
 * be accessed.
 */
@VisibleForTesting
public final class HandlerSettings {

  private static volatile boolean enabled;

  private static boolean autoFlowControlOn;
  // These will be the most recently created handlers created using NettyClientTransport and
  // NettyServerTransport
  private static AbstractNettyHttp2Handler clientHandler;
  private static AbstractNettyHttp2Handler serverHandler;

  static void setAutoWindow(AbstractNettyHttp2Handler handler) {
    if (!enabled) {
      return;
    }
    synchronized (HandlerSettings.class) {
      handler.setAutoTuneFlowControl(autoFlowControlOn);
      if (handler instanceof NettyHttp2ClientHandler) {
        clientHandler = handler;
      } else if (handler instanceof AbstractNettyHttp2Handler) {
        serverHandler = handler;
      } else {
        throw new RuntimeException("Expecting NettyClientHandler or NettyServerHandler");
      }
    }
  }

  public static void enable(boolean enable) {
    enabled = enable;
  }

  public static synchronized void autoWindowOn(boolean autoFlowControl) {
    autoFlowControlOn = autoFlowControl;
  }

  public static synchronized int getLatestClientWindow() {
    return getLatestWindow(clientHandler);
  }

  public static synchronized int getLatestServerWindow() {
    return getLatestWindow(serverHandler);
  }

  private static synchronized int getLatestWindow(AbstractNettyHttp2Handler handler) {
    Preconditions.checkNotNull(handler);
    return handler.decoder().flowController()
        .initialWindowSize(handler.connection().connectionStream());
  }
}
