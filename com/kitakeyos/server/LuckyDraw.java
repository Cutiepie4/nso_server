/*     */ package com.kitakeyos.server;
/*     */ 
/*     */ import com.kitakeyos.db.Connect;
/*     */ import com.kitakeyos.io.Message;
/*     */ import com.kitakeyos.util.NinjaUtil;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LuckyDraw
/*     */   implements Runnable
/*     */ {
/*     */   public String name;
/*     */   
/*     */   public class MemThamGia
/*     */   {
/*     */     int iddb;
/*     */     public int xu;
/*     */     String name;
/*     */   }
/*  29 */   public int totalMoney = 0;
/*  30 */   public int xuWin = 0;
/*  31 */   public int timeCount = 120;
/*  32 */   public String nameWin = "";
/*     */   public int xuThamGia;
/*  34 */   public int[] percen = new int[0];
/*  35 */   public int percenMax = 0;
/*  36 */   public byte type = 0;
/*  37 */   public ArrayList<MemThamGia> mem = new ArrayList<>();
/*     */   
/*     */   public int xuMin;
/*     */   public int xuMax;
/*     */   
/*     */   public LuckyDraw(String name, byte type) {
/*  43 */     this.name = name;
/*  44 */     this.type = type;
/*  45 */     if (type == 0) {
/*  46 */       this.xuMin = 10000;
/*  47 */       this.xuMax = 100000;
/*     */     } else {
/*  49 */       this.xuMin = 1000000;
/*  50 */       this.xuMax = 50000000;
/*     */     } 
/*     */   }
/*     */   public Date timeStart; public static boolean running = true;
/*     */   public void join(Character pl, int numb) throws IOException {
/*  55 */     setPercen();
/*  56 */     if (this.timeCount < 10) {
/*  57 */       pl.user.service.addInfoMe("Đã hết thời gian tham gia vui lòng quay lại vào vòng sau!");
/*     */       return;
/*     */     } 
/*  60 */     if (this.mem.size() >= 30) {
/*  61 */       pl.user.service.addInfoMe("Số người tham gia tối đa là 30!");
/*     */       return;
/*     */     } 
/*  64 */     if (pl.xu < numb) {
/*  65 */       pl.user.service.addInfoMe("Bạn không đủ xu để tham gia!");
/*     */       return;
/*     */     } 
/*  68 */     for (MemThamGia memThamGia : this.mem) {
/*  69 */       if (memThamGia.iddb == pl.id) {
/*  70 */         if (memThamGia.xu + numb > this.xuMax) {
/*  71 */           pl.user.service.addInfoMe("Bạn chỉ có thể đặt thêm tối đa " + NinjaUtil.getCurrency((this.xuMax - memThamGia.xu)));
/*     */           return;
/*     */         } 
/*  74 */         this.totalMoney += numb;
/*  75 */         memThamGia.xu += numb;
/*  76 */         pl.updateXu(pl.xu - numb);
/*  77 */         setPercen();
/*  78 */         pl.user.service.addInfoMe("Bạn đã đặt thêm " + NinjaUtil.getCurrency(numb) + "Xu thành công!");
/*     */         return;
/*     */       } 
/*     */     } 
/*  82 */     if (numb < this.xuMin || numb > this.xuMax) {
/*  83 */       pl.user.service.addInfoMe("Bạn chỉ có thể đặt từ " + NinjaUtil.getCurrency(this.xuMin) + " đến " + NinjaUtil.getCurrency(this.xuMax) + "Xu!");
/*     */       return;
/*     */     } 
/*  86 */     MemThamGia m = new MemThamGia();
/*  87 */     m.iddb = pl.id;
/*  88 */     m.xu = numb;
/*  89 */     this.totalMoney += numb;
/*  90 */     m.name = pl.name;
/*  91 */     this.mem.add(m);
/*  92 */     pl.updateXu(pl.xu - numb);
/*  93 */     setPercen();
/*  94 */     pl.user.service.addInfoMe("Bạn đã tham gia " + NinjaUtil.getCurrency(numb) + " xu thành công!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/* 100 */       while (running) {
/* 101 */         if (this.mem.size() >= 2) {
/* 102 */           this.timeCount--;
/* 103 */           if (this.timeCount <= 0) {
/*     */             try {
/* 105 */               randomCharacterWin();
/* 106 */               result();
/* 107 */               refresh();
/* 108 */             } catch (Exception ex) {
/*     */               continue;
/*     */             } 
/*     */           }
/*     */         } 
/* 113 */         Thread.sleep(1000L);
/*     */       } 
/* 115 */     } catch (InterruptedException ex) {
/* 116 */       Logger.getLogger(LuckyDraw.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void randomCharacterWin() throws SQLException {
/*     */     try {
/* 122 */       int indexWin = NinjaUtil.nextInt(this.percen);
/* 123 */       MemThamGia m = this.mem.get(indexWin);
/* 124 */       int receive = this.totalMoney;
/* 125 */       if (this.mem.size() > 10) {
/* 126 */         receive -= receive / 10;
/*     */       }
/* 128 */       Character pl = Character.characters_name.get(m.name);
/* 129 */       if (pl != null) {
/* 130 */         pl.addXu(receive);
/*     */       } else {
/* 132 */         PreparedStatement stmt = Connect.conn.prepareStatement("UPDATE `player` SET `xu` = `xu` + ? WHERE `id` = ? LIMIT 1;");
/* 133 */         stmt.setInt(1, receive);
/* 134 */         stmt.setInt(2, m.iddb);
/* 135 */         stmt.execute();
/*     */       } 
/* 137 */       this.nameWin = m.name;
/* 138 */       this.xuWin = receive;
/* 139 */       this.xuThamGia = m.xu;
/* 140 */     } catch (IOException ex) {
/* 141 */       Logger.getLogger(LuckyDraw.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNumberMoney() {
/* 146 */     return this.totalMoney;
/*     */   }
/*     */   
/*     */   public String[] getPercen(int iddb) {
/* 150 */     if (this.totalMoney == 0 || this.mem == null) {
/* 151 */       return new String[] { "00", "00" };
/*     */     }
/* 153 */     for (MemThamGia m : this.mem) {
/* 154 */       if (m.iddb == iddb) {
/* 155 */         return String.format("%.2f", new Object[] { Float.valueOf(m.xu * 100.0F / this.totalMoney) }).replace(".", "_").split("_");
/*     */       }
/*     */     } 
/* 158 */     return new String[] { "00", "00" };
/*     */   }
/*     */   
/*     */   public int getMoneyById(int iddb) {
/* 162 */     for (MemThamGia m : this.mem) {
/* 163 */       if (m.iddb == iddb) {
/* 164 */         return m.xu;
/*     */       }
/*     */     } 
/* 167 */     return 0;
/*     */   }
/*     */   
/*     */   public void refresh() {
/* 171 */     this.timeCount = 120;
/* 172 */     this.totalMoney = 0;
/* 173 */     this.mem.clear();
/*     */   }
/*     */   
/*     */   public void result() throws IOException {
/* 177 */     Message mss = new Message(-21);
/* 178 */     DataOutputStream ds = mss.writer();
/* 179 */     ds.writeUTF("Admin");
/* 180 */     ds.writeUTF("Chúc mừng " + this.nameWin.toUpperCase() + " đã chiến thắng " + NinjaUtil.getCurrency(this.xuWin) + " xu trong trò chơi " + this.name + " với " + NinjaUtil.getCurrency(this.xuThamGia) + " xu");
/* 181 */     ds.flush();
/* 182 */     Server.sendToServer(mss);
/*     */   }
/*     */   
/*     */   public void setPercen() {
/* 186 */     this.percen = new int[this.mem.size()];
/* 187 */     int i = 0;
/* 188 */     for (MemThamGia m : this.mem) {
/* 189 */       this.percen[i] = m.xu * 100 / this.totalMoney;
/* 190 */       i++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\LuckyDraw.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */