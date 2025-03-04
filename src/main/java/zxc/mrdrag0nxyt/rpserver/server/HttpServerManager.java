package zxc.mrdrag0nxyt.rpserver.server;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.scheduler.BukkitRunnable;

import zxc.mrdrag0nxyt.rpserver.CachedResourcePack;
import zxc.mrdrag0nxyt.rpserver.RPServer;
import zxc.mrdrag0nxyt.rpserver.config.Config;

public class HttpServerManager {

    private final RPServer plugin;
    private final CachedResourcePack resourcepack;
    private final Config config;

    private HttpServer httpServer;
    private BukkitRunnable runnable;

    public HttpServerManager(RPServer plugin, CachedResourcePack resourcepack, Config config) {
        this.plugin = plugin;
        this.resourcepack = resourcepack;
        this.config = config;
    }


    public synchronized void startServer() {
        if (httpServer != null && httpServer.isAlive()) return;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                try {
                	int port = config.getPort();
                    httpServer = new HttpServer(port, resourcepack);
                    httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                    plugin.getLogger().info("HTTP server started at http://0.0.0.0:" + port + ". Use '/rpserver link' to get link");
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
    	
        private final CachedResourcePack resourcepack;
    	private String hash = null;
    	private Response response = null;

        public HttpServer(int port, CachedResourcePack resourcepack) {
            super(port);
            this.resourcepack = resourcepack;
            updateResponse();
        }
        
        private void updateResponse() {
        	String hash = resourcepack.getHash();
        	if(hash == this.hash) {
        		return;
        	}
        	final Response response;
        	if(resourcepack.getLength() == 0) {
        		plugin.getLogger().severe("File not found!");
        		response = newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not found!");
        	} else {
        		response = newFixedLengthResponse(Response.Status.OK, "application/zip", resourcepack.getStream(), resourcepack.getLength());
            	response.addHeader("Content-Disposition", "attachment; filename=\"" + resourcepack.getFile().getName() + "\"");
        	}
        	this.hash = hash;
        	this.response = response;
        }

        @Override
        public Response serve(IHTTPSession session) {
        	updateResponse();
        	return response;
        }
    }

}
