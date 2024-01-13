/*     */ package com.kitakeyos.server;
/*     */ 
/*     */ import com.kitakeyos.io.IMessageHandler;
/*     */ import com.kitakeyos.io.Message;
/*     */ import com.kitakeyos.io.Session;
/*     */ import com.kitakeyos.util.Logger;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Controller
/*     */   implements IMessageHandler
/*     */ {
/*     */   private final Session client;
/*  16 */   private Logger logger = new Logger(getClass());
/*     */   
/*     */   public Controller(Session client) {
/*  19 */     this.client = client;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onMessage(Message mss) {
/*  24 */     if (mss != null) {
/*     */       try {
/*  26 */         switch (mss.getCommand()) {
/*     */           
/*     */           case -27:
/*  29 */             this.client.sendKey();
/*     */             return;
/*     */           
/*     */           case -28:
/*  33 */             messageNotMap(mss);
/*     */             return;
/*     */           
/*     */           case -29:
/*  37 */             messageNotLogin(mss);
/*     */             return;
/*     */           
/*     */           case -30:
/*  41 */             messageSubCommand(mss);
/*     */             return;
/*     */           
/*     */           case -23:
/*  45 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  46 */               this.client.user.selectedCharacter.chatPublic(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -22:
/*  51 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  52 */               this.client.user.selectedCharacter.chatPrivate(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -21:
/*  57 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  58 */               this.client.user.selectedCharacter.chatGlobal(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -17:
/*  63 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  64 */               this.client.user.selectedCharacter.changeMap();
/*     */             }
/*     */             return;
/*     */           
/*     */           case -12:
/*  69 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  70 */               this.client.user.selectedCharacter.throwItem(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -14:
/*  75 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  76 */               this.client.user.selectedCharacter.pickItem(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -9:
/*  81 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  82 */               this.client.user.selectedCharacter.returnTownFromDead(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -10:
/*  87 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  88 */               this.client.user.selectedCharacter.wakeUpFromDead(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 1:
/*  93 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/*  94 */               this.client.user.selectedCharacter.move(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 25:
/*  99 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 100 */               this.client.user.selectedCharacter.requestCharacterInfo(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 68:
/* 105 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 106 */               this.client.user.selectedCharacter.addCuuSat(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 11:
/* 111 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 112 */               this.client.user.selectedCharacter.useItem(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 12:
/* 117 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 118 */               this.client.user.selectedCharacter.useItemChangeMap(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 13:
/* 123 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 124 */               this.client.user.selectedCharacter.buyItem(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 14:
/* 129 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 130 */               this.client.user.selectedCharacter.sellItem(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 15:
/* 135 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 136 */               this.client.user.selectedCharacter.itemBodyToBag(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 16:
/* 141 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 142 */               this.client.user.selectedCharacter.itemBoxToBag(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 17:
/* 147 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 148 */               this.client.user.selectedCharacter.itemBagToBox(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 21:
/* 153 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 154 */               this.client.user.selectedCharacter.upgradeItem(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 22:
/* 159 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 160 */               this.client.user.selectedCharacter.splitItem(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 19:
/* 165 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 166 */               this.client.user.selectedCharacter.upPearl(mss, true);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 20:
/* 171 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 172 */               this.client.user.selectedCharacter.upPearl(mss, false);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 28:
/* 177 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 178 */               this.client.user.selectedCharacter.changeZone(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 36:
/* 183 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 184 */               this.client.user.service.openUIZone();
/*     */             }
/*     */             return;
/*     */           
/*     */           case 40:
/* 189 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 190 */               this.client.user.selectedCharacter.menuId(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 41:
/* 195 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 196 */               this.client.user.selectedCharacter.selectSkill(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 42:
/* 201 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 202 */               this.client.user.selectedCharacter.requestItemInfo(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 43:
/* 207 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 208 */               this.client.user.selectedCharacter.tradeInvite(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 44:
/* 213 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 214 */               this.client.user.selectedCharacter.acceptInviteTrade(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 45:
/* 219 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 220 */               this.client.user.selectedCharacter.tradeItemLock(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 46:
/* 225 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 226 */               this.client.user.selectedCharacter.tradeAccept();
/*     */             }
/*     */             return;
/*     */           
/*     */           case 47:
/* 231 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 232 */               this.client.user.selectedCharacter.menu(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 57:
/* 237 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 238 */               this.client.user.selectedCharacter.tradeClose();
/*     */             }
/*     */             return;
/*     */           
/*     */           case 59:
/* 243 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 244 */               this.client.user.selectedCharacter.addFriend(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 60:
/* 249 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 250 */               this.client.user.selectedCharacter.attackMonster(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 61:
/* 255 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 256 */               this.client.user.selectedCharacter.attackCharacter(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 73:
/* 261 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 262 */               this.client.user.selectedCharacter.attackAllType(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 92:
/* 267 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 268 */               this.client.user.selectedCharacter.input(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 93:
/* 273 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 274 */               this.client.user.selectedCharacter.viewInfo(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 94:
/* 279 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 280 */               this.client.user.selectedCharacter.requestItemCharacter(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case 108:
/* 285 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 286 */               this.client.user.selectedCharacter.itemMountToBag(mss);
/*     */             }
/*     */             return;
/*     */         } 
/*     */         
/* 291 */         this.logger.log("CMD: " + mss.getCommand());
/*     */       
/*     */       }
/* 294 */       catch (IOException e) {
/* 295 */         this.logger.debug("onMessage", e.getMessage());
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void messageNotLogin(Message mss) {
/* 301 */     if (mss != null) {
/*     */       try {
/* 303 */         byte command = mss.reader().readByte();
/* 304 */         switch (command) {
/*     */           case -127:
/* 306 */             if (this.client.user == null) {
/* 307 */               this.client.login(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -125:
/* 312 */             if (this.client.user == null) {
/* 313 */               this.client.setClientType(mss);
/*     */             }
/*     */             return;
/*     */         } 
/*     */         
/* 318 */         this.logger.log(String.format("Client %d: messageNotLogin: %d", new Object[] { Integer.valueOf(this.client.id), Byte.valueOf(command) }));
/*     */       
/*     */       }
/* 321 */       catch (Exception e) {
/* 322 */         this.logger.debug("messageNotLogin", e.getMessage());
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void messageNotMap(Message mss) {
/* 328 */     if (mss != null) {
/*     */       try {
/* 330 */         byte command = mss.reader().readByte();
/* 331 */         switch (command) {
/*     */           case -126:
/* 333 */             if (this.client.user != null && this.client.user.selectedCharacter == null) {
/* 334 */               this.client.user.selectToChar(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -125:
/* 339 */             if (this.client.user != null && this.client.user.selectedCharacter == null) {
/* 340 */               this.client.user.createCharacter(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -122:
/* 345 */             this.client.user.service.sendData();
/*     */             return;
/*     */           
/*     */           case -121:
/* 349 */             this.client.user.service.sendMap();
/*     */             return;
/*     */           
/*     */           case -120:
/* 353 */             this.client.user.service.sendSkill();
/*     */             return;
/*     */           
/*     */           case -119:
/* 357 */             this.client.user.service.sendItem();
/*     */             return;
/*     */           
/*     */           case -115:
/* 361 */             if (this.client.user != null) {
/* 362 */               this.client.user.service.requestIcon(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -109:
/* 367 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 368 */               this.client.user.service.requestMapTemplate(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -108:
/* 373 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 374 */               this.client.user.service.requestMobTemplate(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -101:
/* 379 */             if (this.client.user != null) {
/* 380 */               this.client.clientOK = true;
/* 381 */               this.client.user.service.selectChar();
/*     */             } 
/*     */             return;
/*     */           
/*     */           case -88:
/* 386 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 387 */               this.client.user.selectedCharacter.convertUpgrade(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -85:
/* 392 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 393 */               this.client.user.selectedCharacter.inputNumberSplit(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -72:
/* 398 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 399 */               this.client.user.selectedCharacter.selectCard(mss);
/*     */             }
/*     */             return;
/*     */         } 
/*     */         
/* 404 */         this.logger.log(String.format("Client %d: messageNotMap: %d", new Object[] { Integer.valueOf(this.client.id), Byte.valueOf(command) }));
/*     */       
/*     */       }
/* 407 */       catch (Exception e) {
/* 408 */         e.printStackTrace();
/* 409 */         this.logger.error("messageNotMap", e.getMessage());
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void messageSubCommand(Message mss) {
/* 415 */     if (mss != null) {
/*     */       try {
/* 417 */         byte command = mss.reader().readByte();
/* 418 */         switch (command) {
/*     */           
/*     */           case -65:
/* 421 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 422 */               this.client.user.selectedCharacter.loadSkillShortcut(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -67:
/* 427 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 428 */               this.client.user.selectedCharacter.saveRms(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -93:
/* 433 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 434 */               this.client.user.selectedCharacter.changePk(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -83:
/* 439 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 440 */               this.client.user.selectedCharacter.removeFriend(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -84:
/* 445 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 446 */               this.client.user.selectedCharacter.requestEnemies();
/*     */             }
/*     */             return;
/*     */           
/*     */           case -85:
/* 451 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 452 */               this.client.user.service.requestFriend();
/*     */             }
/*     */             return;
/*     */           
/*     */           case -109:
/* 457 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 458 */               this.client.user.selectedCharacter.upPotential(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -108:
/* 463 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 464 */               this.client.user.selectedCharacter.upSkill(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -107:
/* 469 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 470 */               this.client.user.selectedCharacter.bagSort();
/* 471 */               this.client.user.service.bagSort();
/*     */             } 
/*     */             return;
/*     */           
/*     */           case -106:
/* 476 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 477 */               this.client.user.selectedCharacter.boxSort();
/* 478 */               this.client.user.service.boxSort();
/*     */             } 
/*     */             return;
/*     */           
/*     */           case -105:
/* 483 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 484 */               this.client.user.selectedCharacter.boxCoinIn(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -104:
/* 489 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 490 */               this.client.user.selectedCharacter.boxCoinOut(mss);
/*     */             }
/*     */             return;
/*     */           
/*     */           case -103:
/* 495 */             if (this.client.user != null && this.client.user.selectedCharacter != null) {
/* 496 */               this.client.user.selectedCharacter.requestItem(mss);
/*     */             }
/*     */             return;
/*     */         } 
/*     */         
/* 501 */         this.logger.log(String.format("Client %d: messageSubCommand: %d", new Object[] { Integer.valueOf(this.client.id), Byte.valueOf(command) }));
/*     */       
/*     */       }
/* 504 */       catch (Exception e) {
/* 505 */         this.logger.debug("messageSubCommand", e.getMessage());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void onConnectionFail() {
/* 512 */     this.logger.log(String.format("Client %d: Kết nối thất bại!", new Object[] { Integer.valueOf(this.client.id) }));
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisconnected() {
/* 517 */     this.logger.log(String.format("Client %d: Mất kết nối!", new Object[] { Integer.valueOf(this.client.id) }));
/*     */   }
/*     */ 
/*     */   
/*     */   public void onConnectOK() {
/* 522 */     this.logger.log(String.format("Client %d: Kết nối thành công!", new Object[] { Integer.valueOf(this.client.id) }));
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Controller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */