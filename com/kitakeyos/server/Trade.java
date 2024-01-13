/*     */ package com.kitakeyos.server;
/*     */ 
/*     */ import com.kitakeyos.io.Message;
/*     */ import com.kitakeyos.option.ItemOption;
/*     */ import com.kitakeyos.util.NinjaUtil;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class Trade
/*     */   extends Thread
/*     */   implements Runnable {
/*  15 */   public int timeCount = 600;
/*  16 */   public Trader trader_1 = new Trader();
/*  17 */   public Trader trader_2 = new Trader();
/*     */   public boolean isFinish = false;
/*     */   
/*     */   public void showMenu() throws IOException {
/*  21 */     Message ms = new Message(37);
/*  22 */     DataOutputStream ds = ms.writer();
/*  23 */     ds.writeUTF(this.trader_2.character.name);
/*  24 */     ds.flush();
/*  25 */     this.trader_1.character.user.client.sendMessage(ms);
/*  26 */     ms = new Message(37);
/*  27 */     ds = ms.writer();
/*  28 */     ds.writeUTF(this.trader_1.character.name);
/*  29 */     ds.flush();
/*  30 */     this.trader_2.character.user.client.sendMessage(ms);
/*     */   }
/*     */   
/*     */   public void closeMenu() throws IOException {
/*  34 */     Message ms = new Message(57);
/*  35 */     this.trader_1.character.user.client.sendMessage(ms);
/*  36 */     this.trader_2.character.user.client.sendMessage(ms);
/*     */   }
/*     */   
/*     */   public void run() {
/*     */     try {
/*  41 */       showMenu();
/*  42 */       while (this.timeCount > 0 && 
/*  43 */         !this.isFinish) {
/*     */ 
/*     */         
/*  46 */         if (this.trader_1.character == null || this.trader_2.character == null) {
/*     */           break;
/*     */         }
/*  49 */         if (this.trader_1.accept && this.trader_2.accept) {
/*  50 */           closeMenu();
/*  51 */           this.isFinish = true;
/*  52 */           if (this.trader_2.coinTradeOrder > 0) {
/*  53 */             this.trader_1.character.addXu(this.trader_2.coinTradeOrder);
/*  54 */             this.trader_2.character.updateXu(this.trader_2.character.xu - this.trader_2.coinTradeOrder);
/*     */           } 
/*  56 */           if (this.trader_1.coinTradeOrder > 0) {
/*  57 */             this.trader_2.character.addXu(this.trader_1.coinTradeOrder);
/*  58 */             this.trader_1.character.updateXu(this.trader_1.character.xu - this.trader_1.coinTradeOrder);
/*     */           } 
/*  60 */           int num = this.trader_1.character.getSlotNull();
/*  61 */           if (this.trader_2.itemTradeOrder.size() > num) {
/*  62 */             this.trader_1.character.startOKDlg(Language.getString("NOT_ENOUGH_BAG_1", new Object[0]));
/*  63 */             this.trader_2.character.startOKDlg(Language.getString("NOT_ENOUGH_BAG_2", new Object[] { this.trader_1.character.name }));
/*     */             break;
/*     */           } 
/*  66 */           num = this.trader_2.character.getSlotNull();
/*  67 */           if (this.trader_1.itemTradeOrder.size() > num) {
/*  68 */             this.trader_2.character.startOKDlg(Language.getString("NOT_ENOUGH_BAG_1", new Object[0]));
/*  69 */             this.trader_1.character.startOKDlg(Language.getString("NOT_ENOUGH_BAG_2", new Object[] { this.trader_2.character.name }));
/*     */             break;
/*     */           } 
/*  72 */           int numberItem = this.trader_2.itemTradeOrder.size();
/*  73 */           if (numberItem > 0) {
/*  74 */             for (Character.Item item : this.trader_2.itemTradeOrder) {
/*  75 */               int index = item.index;
/*  76 */               int quantity = item.quantity;
/*  77 */               this.trader_1.character.addItemToBag(item);
/*  78 */               this.trader_2.character.removeItem(index, quantity, true);
/*     */             } 
/*     */           }
/*  81 */           numberItem = this.trader_1.itemTradeOrder.size();
/*  82 */           if (numberItem > 0) {
/*  83 */             for (Character.Item item : this.trader_1.itemTradeOrder) {
/*  84 */               int index = item.index;
/*  85 */               int quantity = item.quantity;
/*  86 */               this.trader_2.character.addItemToBag(item);
/*  87 */               this.trader_1.character.removeItem(index, quantity, true);
/*     */             } 
/*     */           }
/*  90 */           this.trader_1.character.user.service.tradeAccept();
/*  91 */           this.trader_2.character.user.service.tradeAccept();
/*     */           return;
/*     */         } 
/*  94 */         this.timeCount--;
/*  95 */         Thread.sleep(1000L);
/*     */       } 
/*  97 */       closeMenu();
/*  98 */     } catch (IOException ex) {
/*  99 */       Logger.getLogger(Trade.class.getName()).log(Level.SEVERE, (String)null, ex);
/* 100 */     } catch (InterruptedException ex) {
/* 101 */       Logger.getLogger(Trade.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void tradeItemLock(Trader trader) throws IOException {
/* 106 */     Message ms = new Message(45);
/* 107 */     DataOutputStream ds = ms.writer();
/* 108 */     ds.writeInt(trader.coinTradeOrder);
/* 109 */     ds.writeByte(trader.itemTradeOrder.size());
/* 110 */     for (Character.Item item : trader.itemTradeOrder) {
/* 111 */       ds.writeShort(item.id);
/* 112 */       if (item.entry.isTypeBody() || item.entry.isTypeNgocKham()) {
/* 113 */         ds.writeByte(item.upgrade);
/*     */       }
/* 115 */       ds.writeBoolean((item.expire != -1L));
/* 116 */       ds.writeShort(item.quantity);
/*     */     } 
/* 118 */     ds.flush();
/* 119 */     ((trader == this.trader_1) ? this.trader_2 : this.trader_1).character.user.client.sendMessage(ms);
/*     */   }
/*     */   
/*     */   public void viewItemInfo(Character _char, byte type, byte index) throws IOException {
/* 123 */     Trader trader = (_char == this.trader_1.character) ? this.trader_2 : this.trader_1;
/* 124 */     Character.Item item = trader.itemTradeOrder.get(index);
/* 125 */     Message mss = new Message(42);
/* 126 */     DataOutputStream ds = mss.writer();
/* 127 */     ds.writeByte(type);
/* 128 */     ds.writeByte(index);
/* 129 */     ds.writeLong(item.expire);
/* 130 */     ds.writeInt(item.yen);
/* 131 */     if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
/* 132 */       ds.writeByte(item.sys);
/* 133 */       for (ItemOption ability : item.options) {
/* 134 */         ds.writeByte(ability.optionTemplate.id);
/* 135 */         ds.writeInt(ability.param);
/*     */       } 
/* 137 */     } else if (item.id == 233 || item.id == 234 || item.id == 235) {
/* 138 */       byte[] ab = NinjaUtil.getFile("Data/Img/Item/" + _char.user.client.zoomLevel + "/Small" + item.entry.icon + ".png");
/* 139 */       ds.writeInt(ab.length);
/* 140 */       ds.write(ab);
/*     */     } 
/* 142 */     ds.flush();
/* 143 */     _char.sendMessage(mss);
/* 144 */     mss.cleanup();
/*     */   }
/*     */   
/*     */   public class Trader {
/*     */     public Character character;
/*     */     public int coinTradeOrder;
/*     */     public ArrayList<Character.Item> itemTradeOrder;
/*     */     public boolean accept;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Trade.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */