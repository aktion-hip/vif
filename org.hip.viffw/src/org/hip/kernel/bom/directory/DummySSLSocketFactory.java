package org.hip.kernel.bom.directory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.hip.kernel.exc.DefaultExceptionHandler;

/**
 * DummySSLSocketFactory
 *
 * @author Luthiger
 * Created on 23.07.2007
 * copied from http://www.javaworld.com/javatips/javatip115/javatip115.zip
 */
public class DummySSLSocketFactory extends SSLSocketFactory {
  private SSLSocketFactory factory;

	public DummySSLSocketFactory() {
		try {
			SSLContext lSslContext = SSLContext.getInstance("TLS");
			lSslContext.init(null,
					new TrustManager[] { new DummyTrustManager() },
					new java.security.SecureRandom());
			factory = (SSLSocketFactory) lSslContext.getSocketFactory();

		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
	}

	public static SocketFactory getDefault() {
		return new DummySSLSocketFactory();
	}

	public Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
		return factory.createSocket(socket, s, i, flag);
	}

	public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr1, int j) throws IOException {
		return factory.createSocket(inaddr, i, inaddr1, j);
	}

	public Socket createSocket(InetAddress inaddr, int i) throws IOException {
		return factory.createSocket(inaddr, i);
	}

	public Socket createSocket(String s, int i, InetAddress inaddr, int j) throws IOException {
		return factory.createSocket(s, i, inaddr, j);
	}

	public Socket createSocket(String s, int i) throws IOException {
		return factory.createSocket(s, i);
	}

	public String[] getDefaultCipherSuites() {
		return factory.getSupportedCipherSuites();
	}

	public String[] getSupportedCipherSuites() {
		return factory.getSupportedCipherSuites();
	}
  
}
