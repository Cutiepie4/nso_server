/*     */ package com.kitakeyos.io;
/*     */ 
/*     */ import com.kitakeyos.server.Controller;
/*     */ import com.kitakeyos.server.Server;
/*     */ import com.kitakeyos.server.User;
/*     */ import com.kitakeyos.util.Logger;
/*     */ import com.kitakeyos.util.NinjaUtil;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Session
/*     */   implements ISession
/*     */ {
/*     */   private class Sender
/*     */     implements Runnable
/*     */   {
/*  26 */     private final ArrayList<Message> sendingMessage = new ArrayList<>();
/*     */ 
/*     */     
/*     */     public void addMessage(Message message) {
/*  30 */       this.sendingMessage.add(message);
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/*  36 */         while (Session.this.isConnected() && Session.this.dis != null) {
/*  37 */           while (this.sendingMessage != null && this.sendingMessage.size() > 0) {
/*  38 */             Message m = this.sendingMessage.get(0);
/*  39 */             Session.this.doSendMessage(m);
/*  40 */             this.sendingMessage.remove(0);
/*     */           } 
/*  42 */           Thread.sleep(10L);
/*     */         } 
/*  44 */       } catch (Exception e) {
/*  45 */         System.out.println("Socket is closed!");
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   class MessageCollector
/*     */     implements Runnable
/*     */   {
/*     */     public void run() {
/*     */       try {
/*  55 */         while (!Session.this.sc.isClosed() && Session.this.dis != null) {
/*  56 */           Message message = readMessage();
/*  57 */           if (message != null) {
/*  58 */             Session.this.controller.onMessage(message);
/*  59 */             message.cleanup();
/*     */           }
/*     */         
/*     */         }
/*     */       
/*  64 */       } catch (Exception exception) {}
/*     */       
/*  66 */       Session.this.closeMessage();
/*     */     }
/*     */     
/*     */     private Message readMessage() {
/*  70 */       int num = 200; try {
/*     */         int num2;
/*  72 */         byte b = Session.this.dis.readByte();
/*  73 */         num = b;
/*  74 */         if (Session.this.connected) {
/*  75 */           b = Session.this.readKey(b);
/*     */         }
/*     */         
/*  78 */         if (b == -31) {
/*  79 */           num2 = Session.this.dis.readShort();
/*  80 */         } else if (Session.this.connected) {
/*  81 */           byte b6 = Session.this.dis.readByte();
/*  82 */           byte b7 = Session.this.dis.readByte();
/*  83 */           num2 = (Session.this.readKey(b6) & 0xFF) << 8 | Session.this.readKey(b7) & 0xFF;
/*     */         } else {
/*  85 */           byte b8 = Session.this.dis.readByte();
/*  86 */           byte b9 = Session.this.dis.readByte();
/*  87 */           num2 = b8 & 0xFF00 | b9 & 0xFF;
/*     */         } 
/*  89 */         byte[] array = new byte[num2];
/*  90 */         Session.this.dis.read(array, 0, num2);
/*  91 */         if (Session.this.connected) {
/*  92 */           for (int i = 0; i < array.length; i++) {
/*  93 */             array[i] = Session.this.readKey(array[i]);
/*     */           }
/*     */         }
/*  96 */         return new Message(b, array);
/*  97 */       } catch (Exception exception) {
/*     */         
/*  99 */         return null;
/*     */       } 
/*     */     } }
/*     */   
/* 103 */   private byte[] key = ("kitakeyos_" + NinjaUtil.nextInt(1000000)).getBytes(); public Socket sc;
/*     */   public DataInputStream dis;
/*     */   public DataOutputStream dos;
/*     */   public int id;
/*     */   public User user;
/*     */   private IMessageHandler controller;
/*     */   public boolean connected;
/*     */   public boolean login;
/*     */   private byte curR;
/*     */   private byte curW;
/*     */   private final Sender sender;
/*     */   private Thread collectorThread;
/*     */   protected Thread sendThread;
/* 116 */   protected final Object obj = new Object();
/*     */   protected String plastfrom;
/*     */   protected String versionARM;
/*     */   protected byte clientType;
/*     */   public boolean clientOK;
/*     */   public byte zoomLevel;
/*     */   protected boolean isGPS;
/*     */   protected int width;
/*     */   protected int height;
/*     */   protected boolean isQwert;
/*     */   protected boolean isTouch;
/*     */   protected byte languageId;
/*     */   protected int provider;
/*     */   protected String agent;
/* 130 */   protected Object LOCK = new Object();
/* 131 */   private Logger logger = new Logger(getClass());
/*     */   
/*     */   public Session(Socket sc, int id) throws IOException {
/* 134 */     this.sc = sc;
/* 135 */     this.id = id;
/* 136 */     this.dis = new DataInputStream(sc.getInputStream());
/* 137 */     this.dos = new DataOutputStream(sc.getOutputStream());
/* 138 */     setHandler((IMessageHandler)new Controller(this));
/* 139 */     this.sendThread = new Thread(this.sender = new Sender());
/* 140 */     this.collectorThread = new Thread(new MessageCollector());
/* 141 */     this.collectorThread.start();
/*     */   }
/*     */   
/*     */   public void setClientType(Message mss) throws IOException {
/* 145 */     this.clientType = mss.reader().readByte();
/* 146 */     this.zoomLevel = mss.reader().readByte();
/* 147 */     this.isGPS = mss.reader().readBoolean();
/* 148 */     this.width = mss.reader().readInt();
/* 149 */     this.height = mss.reader().readInt();
/* 150 */     this.isQwert = mss.reader().readBoolean();
/* 151 */     this.isTouch = mss.reader().readBoolean();
/* 152 */     this.plastfrom = mss.reader().readUTF();
/* 153 */     mss.reader().readInt();
/* 154 */     mss.reader().readByte();
/* 155 */     this.languageId = mss.reader().readByte();
/* 156 */     this.provider = mss.reader().readInt();
/* 157 */     this.agent = mss.reader().readUTF();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConnected() {
/* 162 */     return this.connected;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHandler(IMessageHandler messageHandler) {
/* 167 */     this.controller = messageHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(Message message) {
/* 172 */     if (this.connected) {
/* 173 */       this.sender.addMessage(message);
/* 174 */       synchronized (this.LOCK) {
/* 175 */         this.LOCK.notify();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doSendMessage(Message m) {
/* 181 */     byte[] data = m.getData();
/*     */     try {
/* 183 */       byte value = m.getCommand();
/* 184 */       byte b = value;
/* 185 */       if (this.connected) {
/* 186 */         b = writeKey(value);
/*     */       }
/* 188 */       this.dos.writeByte(b);
/* 189 */       int num = data.length;
/* 190 */       if (value == -32) {
/* 191 */         this.dos.writeByte(b);
/* 192 */         int byte2 = writeKey((byte)(num >> 24));
/* 193 */         this.dos.writeByte(byte2);
/* 194 */         int byte3 = writeKey((byte)(num >> 16));
/* 195 */         this.dos.writeByte(byte3);
/* 196 */         int byte4 = writeKey((byte)(num >> 8));
/* 197 */         this.dos.writeByte(byte4);
/* 198 */         int byte5 = writeKey((byte)(num & 0xFF));
/* 199 */         this.dos.writeByte(byte5);
/* 200 */       } else if (this.connected) {
/* 201 */         int byte6 = writeKey((byte)(num >> 8));
/* 202 */         this.dos.writeByte(byte6);
/* 203 */         int byte7 = writeKey((byte)(num & 0xFF));
/* 204 */         this.dos.writeByte(byte7);
/*     */       } else {
/* 206 */         byte byte8 = (byte)(num & 0xFF00);
/* 207 */         this.dos.writeByte(byte8);
/* 208 */         byte byte9 = (byte)(num & 0xFF);
/* 209 */         this.dos.writeByte(byte9);
/*     */       } 
/* 211 */       if (this.connected) {
/* 212 */         for (int i = 0; i < num; i++) {
/* 213 */           data[i] = writeKey(data[i]);
/*     */         }
/*     */       }
/* 216 */       this.dos.write(data);
/* 217 */       this.dos.flush();
/* 218 */     } catch (Exception ex) {
/* 219 */       this.logger.debug("doSendMessage", ex.toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   public byte readKey(byte b) {
/* 224 */     this.curR = (byte)(this.curR + 1); byte result = (byte)(this.key[this.curR] & 0xFF ^ b & 0xFF);
/* 225 */     if (this.curR >= this.key.length) {
/* 226 */       this.curR = (byte)(this.curR % (byte)this.key.length);
/*     */     }
/* 228 */     return result;
/*     */   }
/*     */   
/*     */   public byte writeKey(byte b) {
/* 232 */     this.curW = (byte)(this.curW + 1); byte result = (byte)(this.key[this.curW] & 0xFF ^ b & 0xFF);
/* 233 */     if (this.curW >= this.key.length) {
/* 234 */       this.curW = (byte)(this.curW % (byte)this.key.length);
/*     */     }
/* 236 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 242 */       if (this.user != null) {
/* 243 */         this.user.close();
/* 244 */         this.user = null;
/*     */       } 
/* 246 */       cleanNetwork();
/* 247 */       Server.removeClient(this);
/* 248 */     } catch (Exception e) {
/* 249 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void cleanNetwork() {
/* 254 */     this.curR = 0;
/* 255 */     this.curW = 0;
/*     */     try {
/* 257 */       this.connected = false;
/* 258 */       this.login = false;
/* 259 */       if (this.sc != null) {
/* 260 */         this.sc.close();
/* 261 */         this.sc = null;
/*     */       } 
/* 263 */       if (this.dos != null) {
/* 264 */         this.dos.close();
/* 265 */         this.dos = null;
/*     */       } 
/* 267 */       if (this.dis != null) {
/* 268 */         this.dis.close();
/* 269 */         this.dis = null;
/*     */       } 
/* 271 */       this.sendThread = null;
/* 272 */       this.collectorThread = null;
/* 273 */       synchronized (this.LOCK) {
/* 274 */         this.LOCK.notify();
/*     */       } 
/* 276 */       System.gc();
/* 277 */     } catch (Exception e) {
/* 278 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 284 */     if (this.user != null) {
/* 285 */       return this.user.toString();
/*     */     }
/* 287 */     return "Client " + this.id;
/*     */   }
/*     */   
/*     */   public void sendKey() throws IOException {
/* 291 */     Message ms = new Message(-27);
/* 292 */     DataOutputStream ds = ms.writer();
/* 293 */     ds.writeByte(this.key.length);
/* 294 */     ds.writeByte(this.key[0]);
/* 295 */     for (int i = 1; i < this.key.length; i++) {
/* 296 */       ds.writeByte(this.key[i] ^ this.key[i - 1]);
/*     */     }
/* 298 */     ds.flush();
/* 299 */     doSendMessage(ms);
/* 300 */     this.connected = true;
/* 301 */     this.sendThread.start();
/*     */   }
/*     */   
/*     */   public void login(Message ms) throws IOException {
/* 305 */     if (this.login) {
/*     */       return;
/*     */     }
/* 308 */     String user = ms.reader().readUTF().trim();
/* 309 */     String pass = ms.reader().readUTF().trim();
/* 310 */     String version = ms.reader().readUTF().trim();
/* 311 */     ms.reader().readUTF();
/* 312 */     ms.reader().readUTF();
/* 313 */     String random = ms.reader().readUTF().trim();
/* 314 */     byte server = ms.reader().readByte();
/* 315 */     this.logger.log(String.format("Client id: %d - username: %s - password: %s - version: %s - random: %s - server: %d", new Object[] { Integer.valueOf(this.id), user, pass, version, random, Byte.valueOf(server) }));
/* 316 */     this.versionARM = version;
/* 317 */     User us = User.login(this, user, pass, random);
/* 318 */     if (us != null) {
/* 319 */       this.logger.log("Login Success!");
/* 320 */       this.user = us;
/* 321 */       this.login = true;
/* 322 */       this.user.service.sendVersion();
/*     */     } else {
/* 324 */       this.login = false;
/* 325 */       this.logger.log("Login Failse!");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void closeMessage() {
/* 330 */     if (isConnected()) {
/* 331 */       if (this.controller != null) {
/* 332 */         this.controller.onDisconnected();
/*     */       }
/* 334 */       close();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\io\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */