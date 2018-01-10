import auth.FireBaseAuthModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Dean on 2014/6/28.
 */
public class MainServer {
    private int port;
    public MainServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                System.out.println("WARNING: Bad argument passed to MAIN. Assigning default port.");
            }
        }
        new MainServer(port).run();
    }

    public void run() throws Exception {
        final SslContext sslCtx;
        System.out.println("Running Websockets insecure");
        sslCtx = null;
        System.out.println(" on port " + this.port);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Injector authInjector = Guice.createInjector(new FireBaseAuthModule());
        final ServerAuthHandler serverAuthHandler = authInjector.getInstance(ServerAuthHandler.class);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("authHandler", serverAuthHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}