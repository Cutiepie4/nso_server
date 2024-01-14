package com.kitakeyos.io;

import com.kitakeyos.server.Controller;
import com.kitakeyos.server.Server;
import com.kitakeyos.server.User;
import com.kitakeyos.util.Logger;
import com.kitakeyos.util.NinjaUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Session implements ISession {
	private class Sender implements Runnable {
		private final ArrayList<Message> sendingMessage = new ArrayList<>();

		public void addMessage(Message message) {
			this.sendingMessage.add(message);
		}

		public void run() {
			try {
				while (Session.this.isConnected() && Session.this.dis != null) {
					while (this.sendingMessage != null && this.sendingMessage.size() > 0) {
						Message m = this.sendingMessage.get(0);
						Session.this.doSendMessage(m);
						this.sendingMessage.remove(0);
					}
					Thread.sleep(10L);
				}
			} catch (Exception e) {
				System.out.println("Socket is closed!");
			}
		}
	}

	class MessageCollector implements Runnable {
		public void run() {
			try {
				while (!Session.this.sc.isClosed() && Session.this.dis != null) {
					Message message = readMessage();
					if (message != null) {
						Session.this.controller.onMessage(message);
						message.cleanup();
					}

				}

			} catch (Exception exception) {
			}

			Session.this.closeMessage();
		}

		private Message readMessage() {
			int num = 200;
			try {
				int num2;
				byte b = Session.this.dis.readByte();
				num = b;
				if (Session.this.connected) {
					b = Session.this.readKey(b);
				}

				if (b == -31) {
					num2 = Session.this.dis.readShort();
				} else if (Session.this.connected) {
					byte b6 = Session.this.dis.readByte();
					byte b7 = Session.this.dis.readByte();
					num2 = (Session.this.readKey(b6) & 0xFF) << 8 | Session.this.readKey(b7) & 0xFF;
				} else {
					byte b8 = Session.this.dis.readByte();
					byte b9 = Session.this.dis.readByte();
					num2 = b8 & 0xFF00 | b9 & 0xFF;
				}
				byte[] array = new byte[num2];
				Session.this.dis.read(array, 0, num2);
				if (Session.this.connected) {
					for (int i = 0; i < array.length; i++) {
						array[i] = Session.this.readKey(array[i]);
					}
				}
				return new Message(b, array);
			} catch (Exception exception) {

				return null;
			}
		}
	}

	private byte[] key = ("kitakeyos_" + NinjaUtil.nextInt(1000000)).getBytes();
	public Socket sc;
	public DataInputStream dis;
	public DataOutputStream dos;
	public int id;
	public User user;
	private IMessageHandler controller;
	public boolean connected;
	public boolean login;
	private byte curR;
	private byte curW;
	private final Sender sender;
	private Thread collectorThread;
	protected Thread sendThread;
	protected final Object obj = new Object();
	protected String plastfrom;
	protected String versionARM;
	protected byte clientType;
	public boolean clientOK;
	public byte zoomLevel;
	protected boolean isGPS;
	protected int width;
	protected int height;
	protected boolean isQwert;
	protected boolean isTouch;
	protected byte languageId;
	protected int provider;
	protected String agent;
	protected Object LOCK = new Object();
	private Logger logger = new Logger(getClass());

	public Session(Socket sc, int id) throws IOException {
		this.sc = sc;
		this.id = id;
		this.dis = new DataInputStream(sc.getInputStream());
		this.dos = new DataOutputStream(sc.getOutputStream());
		setHandler((IMessageHandler) new Controller(this));
		this.sendThread = new Thread(this.sender = new Sender());
		this.collectorThread = new Thread(new MessageCollector());
		this.collectorThread.start();
	}

	public void setClientType(Message mss) throws IOException {
		this.clientType = mss.reader().readByte();
		this.zoomLevel = mss.reader().readByte();
		this.isGPS = mss.reader().readBoolean();
		this.width = mss.reader().readInt();
		this.height = mss.reader().readInt();
		this.isQwert = mss.reader().readBoolean();
		this.isTouch = mss.reader().readBoolean();
		this.plastfrom = mss.reader().readUTF();
		mss.reader().readInt();
		mss.reader().readByte();
		this.languageId = mss.reader().readByte();
		this.provider = mss.reader().readInt();
		this.agent = mss.reader().readUTF();
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void setHandler(IMessageHandler messageHandler) {
		this.controller = messageHandler;
	}

	public void sendMessage(Message message) {
		if (this.connected) {
			this.sender.addMessage(message);
			synchronized (this.LOCK) {
				this.LOCK.notify();
			}
		}
	}

	private void doSendMessage(Message m) {
		byte[] data = m.getData();
		try {
			byte value = m.getCommand();
			byte b = value;
			if (this.connected) {
				b = writeKey(value);
			}
			this.dos.writeByte(b);
			int num = data.length;
			if (value == -32) {
				this.dos.writeByte(b);
				int byte2 = writeKey((byte) (num >> 24));
				this.dos.writeByte(byte2);
				int byte3 = writeKey((byte) (num >> 16));
				this.dos.writeByte(byte3);
				int byte4 = writeKey((byte) (num >> 8));
				this.dos.writeByte(byte4);
				int byte5 = writeKey((byte) (num & 0xFF));
				this.dos.writeByte(byte5);
			} else if (this.connected) {
				int byte6 = writeKey((byte) (num >> 8));
				this.dos.writeByte(byte6);
				int byte7 = writeKey((byte) (num & 0xFF));
				this.dos.writeByte(byte7);
			} else {
				byte byte8 = (byte) (num & 0xFF00);
				this.dos.writeByte(byte8);
				byte byte9 = (byte) (num & 0xFF);
				this.dos.writeByte(byte9);
			}
			if (this.connected) {
				for (int i = 0; i < num; i++) {
					data[i] = writeKey(data[i]);
				}
			}
			this.dos.write(data);
			this.dos.flush();
		} catch (Exception ex) {
			this.logger.debug("doSendMessage", ex.toString());
		}
	}

	public byte readKey(byte b) {
		this.curR = (byte) (this.curR + 1);
		byte result = (byte) (this.key[this.curR] & 0xFF ^ b & 0xFF);
		if (this.curR >= this.key.length) {
			this.curR = (byte) (this.curR % (byte) this.key.length);
		}
		return result;
	}

	public byte writeKey(byte b) {
		this.curW = (byte) (this.curW + 1);
		byte result = (byte) (this.key[this.curW] & 0xFF ^ b & 0xFF);
		if (this.curW >= this.key.length) {
			this.curW = (byte) (this.curW % (byte) this.key.length);
		}
		return result;
	}

	public void close() {
		try {
			if (this.user != null) {
				this.user.close();
				this.user = null;
			}
			cleanNetwork();
			Server.removeClient(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cleanNetwork() {
		this.curR = 0;
		this.curW = 0;
		try {
			this.connected = false;
			this.login = false;
			if (this.sc != null) {
				this.sc.close();
				this.sc = null;
			}
			if (this.dos != null) {
				this.dos.close();
				this.dos = null;
			}
			if (this.dis != null) {
				this.dis.close();
				this.dis = null;
			}
			this.sendThread = null;
			this.collectorThread = null;
			synchronized (this.LOCK) {
				this.LOCK.notify();
			}
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		if (this.user != null) {
			return this.user.toString();
		}
		return "Client " + this.id;
	}

	public void sendKey() throws IOException {
		Message ms = new Message(-27);
		DataOutputStream ds = ms.writer();
		ds.writeByte(this.key.length);
		ds.writeByte(this.key[0]);
		for (int i = 1; i < this.key.length; i++) {
			ds.writeByte(this.key[i] ^ this.key[i - 1]);
		}
		ds.flush();
		doSendMessage(ms);
		this.connected = true;
		this.sendThread.start();
	}

	public void login(Message ms) throws IOException {
		if (this.login) {
			return;
		}
		String user = ms.reader().readUTF().trim();
		String pass = ms.reader().readUTF().trim();
		String version = ms.reader().readUTF().trim();
		ms.reader().readUTF();
		ms.reader().readUTF();
		String random = ms.reader().readUTF().trim();
		byte server = ms.reader().readByte();
		this.logger.log(
				String.format("Client id: %d - username: %s - password: %s - version: %s - random: %s - server: %d",
						new Object[] { Integer.valueOf(this.id), user, pass, version, random, Byte.valueOf(server) }));
		this.versionARM = version;
		User us = User.login(this, user, pass, random);
		if (us != null) {
			this.logger.log("Login Success!");
			this.user = us;
			this.login = true;
			this.user.service.sendVersion();
		} else {
			this.login = false;
			this.logger.log("Login Failse!");
		}
	}

	public void closeMessage() {
		if (isConnected()) {
			if (this.controller != null) {
				this.controller.onDisconnected();
			}
			close();
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\io
 * \Session.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */