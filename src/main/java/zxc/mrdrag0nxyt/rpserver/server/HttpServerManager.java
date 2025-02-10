package zxc.mrdrag0nxyt.rpserver.server;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.scheduler.BukkitRunnable;
import zxc.mrdrag0nxyt.rpserver.RPServer;
import zxc.mrdrag0nxyt.rpserver.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HttpServerManager {

    private final RPServer plugin;
    private final Config config;

    private File resourcePackFile;

    private HttpServer httpServer;
    private BukkitRunnable runnable;

    public HttpServerManager(RPServer plugin, Config config) {
        this.plugin = plugin;
        this.config = config;

        resourcePackFile = new File(plugin.getDataFolder(), config.getResourcePackFileName());
    }


    public synchronized void startServer() {
        if (httpServer != null && httpServer.isAlive()) return;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                try {
                    httpServer = new HttpServer(config.getPort());
                    httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                    plugin.getLogger().info("HTTP server started at http://0.0.0.0:" + config.getPort() + ". Use '/rpserver link' to get link");

                } catch (Exception e) {
                    plugin.getLogger().severe(e.getMessage());
                }

            }
        };
        runnable.runTaskAsynchronously(plugin);
    }

    public synchronized void stopServer() {
        if (httpServer == null) return;

        try {
            httpServer.stop();
            plugin.getLogger().info("HTTP server stopped.");

        } catch (Exception e) {
            plugin.getLogger().severe(e.getMessage());
        }

        if (runnable != null) runnable.cancel();
    }

    public void reloadServer() {
        stopServer();
        startServer();
    }

    private class HttpServer extends NanoHTTPD {

        public HttpServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {

            try {
                Response response = newChunkedResponse(Response.Status.OK, "application/zip", new FileInputStream(resourcePackFile));
                response.addHeader("Content-Disposition", "attachment; filename=\"" + resourcePackFile.getName() + "\"");
                return response;

            } catch (FileNotFoundException fileNotFoundException) {
                plugin.getLogger().severe(fileNotFoundException.getMessage());
                return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not found!");
            }
        }
    }

}
