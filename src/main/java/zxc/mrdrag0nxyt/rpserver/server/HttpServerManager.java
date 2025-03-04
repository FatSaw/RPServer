package zxc.mrdrag0nxyt.rpserver.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import zxc.mrdrag0nxyt.rpserver.CachedResourcePack;

public class HttpServerManager extends Thread {

    private final Logger logger;
    private final CachedResourcePack resourcepack;
    
    private int port;
    
    private byte[] header;
    private byte[] data;
    
    private ServerSocket server;
    private volatile boolean run;

    public HttpServerManager(Logger logger, CachedResourcePack resourcepack, int port) {
        this.logger = logger;
        this.resourcepack = resourcepack;
        restartServer(port);
    }
    
    @Override
    public void start() {
    	this.run = true;
    	super.start();
    }
    
    public void end() {
    	this.run = false;
    	if(server == null) {
    		return;
    	}
    	try {
			server.close();
		} catch (IOException e) {
		}
    }
    
    @Override
    public void run() {
    	while(run) {
    		try {
    			server = new ServerSocket();
    			server.setSoTimeout(5000);
    			server.bind(new InetSocketAddress(port), 0);
    			logger.info("HTTP server started at http://0.0.0.0:" + port + ". Use '/rpserver link' to get link");
    		} catch (IOException|SecurityException|IllegalArgumentException e) {
    			e.printStackTrace();
    			return;
    		}
    		try {
				sleep(1000);
			} catch (InterruptedException e) {
			}
			while (!server.isClosed()) {
				try {
					byte[] header = this.header, data = this.data;
					Socket socket = server.accept();
					socket.shutdownInput();
					new ResponseSender(socket.getOutputStream(), header, data).start();
				} catch (IOException e) {
				}
			}
    	}
    }

    public void restartServer(int port) {
    	this.port = port;
    	
    	byte[] pack = resourcepack.getPack();
    	if(pack.length == 0) {
    		this.data = "File not found!".getBytes(StandardCharsets.US_ASCII);
    		StringBuilder sb = new StringBuilder();
        	sb.append("HTTP/1.1 200 OK");
        	sb.append("\r\n");
        	sb.append("Content-Type: text/plain");
        	sb.append("\r\n");
        	sb.append("Connection: close");
        	sb.append("\r\n");
        	sb.append("Content-Length: ");
        	sb.append(data.length);
        	sb.append("\r\n");
        	sb.append("\r\n");
        	this.header = sb.toString().getBytes(StandardCharsets.US_ASCII);
    	} else {
    		this.data = pack;
    		StringBuilder sb = new StringBuilder();
        	sb.append("HTTP/1.1 200 OK");
        	sb.append("\r\n");
        	sb.append("Content-Type: application/zip");
        	sb.append("\r\n");
        	sb.append("Connection: close");
        	sb.append("\r\n");
        	sb.append("Content-Disposition: attachment; filename=\"");
        	sb.append(resourcepack.getFile().getName());
        	sb.append("\"");
        	sb.append("\r\n");
        	sb.append("Content-Length: ");
        	sb.append(pack.length);
        	sb.append("\r\n");
        	sb.append("\r\n");
        	this.header = sb.toString().getBytes(StandardCharsets.UTF_8);
    	}
    	
    	if(server == null) {
    		return;
    	}
    	try {
			server.close();
		} catch (IOException e) {
		}
    }
    
    private final static class ResponseSender extends Thread {
    	
    	private final OutputStream os;
    	private final byte[] response, data;
    	
    	protected ResponseSender(OutputStream os, byte[] header, byte[] data) {
    		this.os = os;
    		this.response = header;
    		this.data = data;
    	}
    	
    	@Override
        public void run() {
    		try {
				os.write(this.response);
				os.write(this.data);
				os.close();
			} catch (IOException e1) {
				try {
					os.close();
				} catch (IOException e2) {
				}
			}
    	}
    }

}
