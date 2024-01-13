/*     */ package com.kitakeyos.object;
/*     */ 
/*     */ import com.kitakeyos.template.NpcTemplate;
/*     */ import com.kitakeyos.util.Logger;
/*     */ import com.kitakeyos.util.NinjaUtil;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TileMap
/*     */ {
/*  22 */   public static int T_EMPTY = 0;
/*  23 */   public static int T_TOP = 2;
/*  24 */   public static int T_LEFT = 4;
/*  25 */   public static int T_RIGHT = 8;
/*  26 */   public static int T_TREE = 16;
/*  27 */   public static int T_WATERFALL = 32;
/*  28 */   public static int T_WATERFLOW = 64;
/*  29 */   public static int T_TOPFALL = 128;
/*  30 */   public static int T_OUTSIDE = 256;
/*  31 */   public static int T_DOWN1PIXEL = 512;
/*  32 */   public static int T_BRIDGE = 1024;
/*  33 */   public static int T_UNDERWATER = 2048;
/*  34 */   public static int T_SOLIDGROUND = 4096;
/*  35 */   public static int T_BOTTOM = 8192;
/*  36 */   public static int T_DIE = 16384;
/*  37 */   public static int T_HEBI = 32768;
/*  38 */   public static int T_BANG = 65536;
/*  39 */   public static int T_JUM8 = 131072;
/*  40 */   public static int T_NT0 = 262144;
/*  41 */   public static int T_NT1 = 524288;
/*     */   
/*  43 */   private Logger logger = new Logger(getClass());
/*     */   
/*     */   public int id;
/*     */   
/*     */   public String name;
/*     */   
/*     */   public byte type;
/*     */   
/*     */   public byte tileId;
/*     */   
/*     */   public byte bgId;
/*     */   public ArrayList<NpcTemplate> npcs;
/*     */   public ArrayList<Waypoint> waypoints;
/*     */   
/*     */   public int tileAt(int x, int y) {
/*     */     int result;
/*     */     try {
/*  60 */       result = this.maps[y * this.tmw + x];
/*  61 */     } catch (Exception ex) {
/*  62 */       result = 1000;
/*     */     } 
/*  64 */     return result;
/*     */   }
/*     */   public ArrayList<MonsterCoordinate> monsterCoordinates; public short tmw; public short tmh; public char[] maps; public int[] types; public short pxh; public short pxw;
/*     */   public boolean tileTypeAt(int px, int py, int t) {
/*     */     boolean result;
/*     */     try {
/*  70 */       result = ((this.types[py / 24 * this.tmw + px / 24] & t) == t);
/*  71 */     } catch (Exception ex) {
/*  72 */       result = false;
/*     */     } 
/*  74 */     return result;
/*     */   }
/*     */   
/*     */   public int tileTypeAt(int x, int y) {
/*     */     int result;
/*     */     try {
/*  80 */       result = this.types[y * this.tmw + x];
/*  81 */     } catch (Exception ex) {
/*  82 */       result = 1000;
/*  83 */       ex.printStackTrace();
/*     */     } 
/*  85 */     return result;
/*     */   }
/*     */   
/*     */   public int tileTypeAtPixel(int px, int py) {
/*     */     int result;
/*     */     try {
/*  91 */       result = this.types[py / 24 * this.tmw + px / 24];
/*  92 */     } catch (Exception ex) {
/*  93 */       result = 1000;
/*     */     } 
/*  95 */     return result;
/*     */   }
/*     */   
/*     */   public int tileXofPixel(int px) {
/*  99 */     return px / 24 * 24;
/*     */   }
/*     */   
/*     */   public int tileYofPixel(int py) {
/* 103 */     return py / 24 * 24;
/*     */   }
/*     */   
/*     */   public void loadMap() {
/* 107 */     this.types = new int[this.tmw * this.tmh];
/* 108 */     this.pxh = (short)(this.tmh * 24);
/* 109 */     this.pxw = (short)(this.tmw * 24);
/*     */     try {
/* 111 */       int length = this.tmh * this.tmw;
/* 112 */       for (int i = 0; i < length; i++) {
/*     */ 
/*     */ 
/*     */         
/* 116 */         if (this.tileId == 4) {
/* 117 */           if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003' || this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\t' || this.maps[i] == '\n' || this.maps[i] == 'O' || this.maps[i] == 'P' || this.maps[i] == '\r' || this.maps[i] == '\016' || this.maps[i] == '+' || this.maps[i] == ',' || this.maps[i] == '-' || this.maps[i] == '2') {
/* 118 */             this.types[i] = this.types[i] | T_TOP;
/*     */           }
/* 120 */           if (this.maps[i] == '\t' || this.maps[i] == '\013') {
/* 121 */             this.types[i] = this.types[i] | T_LEFT;
/*     */           }
/* 123 */           if (this.maps[i] == '\n' || this.maps[i] == '\f') {
/* 124 */             this.types[i] = this.types[i] | T_RIGHT;
/*     */           }
/* 126 */           if (this.maps[i] == '\r' || this.maps[i] == '\016') {
/* 127 */             this.types[i] = this.types[i] | T_BRIDGE;
/*     */           }
/* 129 */           if (this.maps[i] == 'L' || this.maps[i] == 'M') {
/* 130 */             this.types[i] = this.types[i] | T_WATERFLOW;
/*     */           }
/* 132 */         } else if (this.tileId == 1) {
/* 133 */           if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003' || this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\007' || this.maps[i] == '$' || this.maps[i] == '%' || this.maps[i] == '6' || this.maps[i] == '[' || this.maps[i] == '\\' || this.maps[i] == ']' || this.maps[i] == '^' || this.maps[i] == 'I' || this.maps[i] == 'J' || this.maps[i] == 'a' || this.maps[i] == 'b' || this.maps[i] == 't' || this.maps[i] == 'u' || this.maps[i] == 'v' || this.maps[i] == 'x' || this.maps[i] == '=') {
/* 134 */             this.types[i] = this.types[i] | T_TOP;
/*     */           }
/* 136 */           if (this.maps[i] == '\002' || this.maps[i] == '\003' || this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\024' || this.maps[i] == '\025' || this.maps[i] == '\026' || this.maps[i] == '\027' || this.maps[i] == '$' || this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == '\'' || this.maps[i] == '=') {
/* 137 */             this.types[i] = this.types[i] | T_SOLIDGROUND;
/*     */           }
/* 139 */           if (this.maps[i] == '\b' || this.maps[i] == '\t' || this.maps[i] == '\n' || this.maps[i] == '\f' || this.maps[i] == '\r' || this.maps[i] == '\016' || this.maps[i] == '\036') {
/* 140 */             this.types[i] = this.types[i] | T_TREE;
/*     */           }
/* 142 */           if (this.maps[i] == '\021') {
/* 143 */             this.types[i] = this.types[i] | T_WATERFALL;
/*     */           }
/* 145 */           if (this.maps[i] == '\022') {
/* 146 */             this.types[i] = this.types[i] | T_TOPFALL;
/*     */           }
/* 148 */           if (this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == '=') {
/* 149 */             this.types[i] = this.types[i] | T_LEFT;
/*     */           }
/* 151 */           if (this.maps[i] == '$' || this.maps[i] == '\'' || this.maps[i] == '=') {
/* 152 */             this.types[i] = this.types[i] | T_RIGHT;
/*     */           }
/* 154 */           if (this.maps[i] == '\023') {
/* 155 */             this.types[i] = this.types[i] | T_WATERFLOW;
/* 156 */             if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
/* 157 */               this.types[i] = this.types[i] | T_SOLIDGROUND;
/*     */             }
/*     */           } 
/* 160 */           if (this.maps[i] == '#') {
/* 161 */             this.types[i] = this.types[i] | T_UNDERWATER;
/*     */           }
/* 163 */           if (this.maps[i] == '\007') {
/* 164 */             this.types[i] = this.types[i] | T_BRIDGE;
/*     */           }
/* 166 */           if (this.maps[i] == ' ' || this.maps[i] == '!' || this.maps[i] == '"') {
/* 167 */             this.types[i] = this.types[i] | T_OUTSIDE;
/*     */           }
/* 169 */         } else if (this.tileId == 2) {
/* 170 */           if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003' || this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\007' || this.maps[i] == '$' || this.maps[i] == '%' || this.maps[i] == '6' || this.maps[i] == '=' || this.maps[i] == 'I' || this.maps[i] == 'L' || this.maps[i] == 'M' || this.maps[i] == 'N' || this.maps[i] == 'O' || this.maps[i] == 'R' || this.maps[i] == 'S' || this.maps[i] == 'b' || this.maps[i] == 'c' || this.maps[i] == 'd' || this.maps[i] == 'f' || this.maps[i] == 'g' || this.maps[i] == 'l' || this.maps[i] == 'm' || this.maps[i] == 'n' || this.maps[i] == 'p' || this.maps[i] == 'q' || this.maps[i] == 't' || this.maps[i] == 'u' || this.maps[i] == '}' || this.maps[i] == '~' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '') {
/* 171 */             this.types[i] = this.types[i] | T_TOP;
/*     */           }
/* 173 */           if (this.maps[i] == '\001' || this.maps[i] == '\003' || this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\024' || this.maps[i] == '\025' || this.maps[i] == '\026' || this.maps[i] == '\027' || this.maps[i] == '$' || this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == '\'' || this.maps[i] == '7' || this.maps[i] == 'm' || this.maps[i] == 'o' || this.maps[i] == 'p' || this.maps[i] == 'q' || this.maps[i] == 'r' || this.maps[i] == 's' || this.maps[i] == 't' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '') {
/* 174 */             this.types[i] = this.types[i] | T_SOLIDGROUND;
/*     */           }
/* 176 */           if (this.maps[i] == '\b' || this.maps[i] == '\t' || this.maps[i] == '\n' || this.maps[i] == '\f' || this.maps[i] == '\r' || this.maps[i] == '\016' || this.maps[i] == '\036' || this.maps[i] == '') {
/* 177 */             this.types[i] = this.types[i] | T_TREE;
/*     */           }
/* 179 */           if (this.maps[i] == '\021') {
/* 180 */             this.types[i] = this.types[i] | T_WATERFALL;
/*     */           }
/* 182 */           if (this.maps[i] == '\022') {
/* 183 */             this.types[i] = this.types[i] | T_TOPFALL;
/*     */           }
/* 185 */           if (this.maps[i] == '=' || this.maps[i] == '%' || this.maps[i] == '&' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '') {
/* 186 */             this.types[i] = this.types[i] | T_LEFT;
/*     */           }
/* 188 */           if (this.maps[i] == '=' || this.maps[i] == '$' || this.maps[i] == '\'' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '') {
/* 189 */             this.types[i] = this.types[i] | T_RIGHT;
/*     */           }
/* 191 */           if (this.maps[i] == '\023') {
/* 192 */             this.types[i] = this.types[i] | T_WATERFLOW;
/* 193 */             if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
/* 194 */               this.types[i] = this.types[i] | T_SOLIDGROUND;
/*     */             }
/*     */           } 
/* 197 */           if (this.maps[i] == '') {
/* 198 */             this.types[i] = this.types[i] | T_WATERFLOW;
/* 199 */             if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
/* 200 */               this.types[i] = this.types[i] | T_SOLIDGROUND;
/*     */             }
/*     */           } 
/* 203 */           if (this.maps[i] == '#') {
/* 204 */             this.types[i] = this.types[i] | T_UNDERWATER;
/*     */           }
/* 206 */           if (this.maps[i] == '\007') {
/* 207 */             this.types[i] = this.types[i] | T_BRIDGE;
/*     */           }
/* 209 */           if (this.maps[i] == ' ' || this.maps[i] == '!' || this.maps[i] == '"') {
/* 210 */             this.types[i] = this.types[i] | T_OUTSIDE;
/*     */           }
/* 212 */           if (this.maps[i] == '=' || this.maps[i] == '') {
/* 213 */             this.types[i] = this.types[i] | T_BOTTOM;
/*     */           }
/* 215 */         } else if (this.tileId == 3) {
/* 216 */           if (this.maps[i] == '\001' || this.maps[i] == '\002' || this.maps[i] == '\003' || this.maps[i] == '\004' || this.maps[i] == '\005' || this.maps[i] == '\006' || this.maps[i] == '\007' || this.maps[i] == '\013' || this.maps[i] == '\016' || this.maps[i] == '\021' || this.maps[i] == '+' || this.maps[i] == '3' || this.maps[i] == '?' || this.maps[i] == 'A' || this.maps[i] == 'C' || this.maps[i] == 'D' || this.maps[i] == 'G' || this.maps[i] == 'H' || this.maps[i] == 'S' || this.maps[i] == 'T' || this.maps[i] == 'U' || this.maps[i] == 'W' || this.maps[i] == '[' || this.maps[i] == '^' || this.maps[i] == 'a' || this.maps[i] == 'b' || this.maps[i] == 'j' || this.maps[i] == 'k' || this.maps[i] == 'o' || this.maps[i] == 'q' || this.maps[i] == 'u' || this.maps[i] == 'v' || this.maps[i] == 'w' || this.maps[i] == '}' || this.maps[i] == '~' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '' || this.maps[i] == '') {
/* 217 */             this.types[i] = this.types[i] | T_TOP;
/*     */           }
/* 219 */           if (this.maps[i] == '|' || this.maps[i] == 't' || this.maps[i] == '{' || this.maps[i] == ',' || this.maps[i] == '\f' || this.maps[i] == '\017' || this.maps[i] == '\020' || this.maps[i] == '-' || this.maps[i] == '\n' || this.maps[i] == '\t') {
/* 220 */             this.types[i] = this.types[i] | T_SOLIDGROUND;
/*     */           }
/* 222 */           if (this.maps[i] == '\027') {
/* 223 */             this.types[i] = this.types[i] | T_WATERFALL;
/*     */           }
/* 225 */           if (this.maps[i] == '\030') {
/* 226 */             this.types[i] = this.types[i] | T_TOPFALL;
/*     */           }
/* 228 */           if (this.maps[i] == '\006' || this.maps[i] == '\017' || this.maps[i] == '3' || this.maps[i] == '_' || this.maps[i] == 'a' || this.maps[i] == 'j' || this.maps[i] == 'o' || this.maps[i] == '{' || this.maps[i] == '}' || this.maps[i] == '' || this.maps[i] == '') {
/* 229 */             this.types[i] = this.types[i] | T_LEFT;
/*     */           }
/* 231 */           if (this.maps[i] == '\007' || this.maps[i] == '\020' || this.maps[i] == '3' || this.maps[i] == '`' || this.maps[i] == 'b' || this.maps[i] == 'k' || this.maps[i] == 'o' || this.maps[i] == '|' || this.maps[i] == '~' || this.maps[i] == '' || this.maps[i] == '') {
/* 232 */             this.types[i] = this.types[i] | T_RIGHT;
/*     */           }
/* 234 */           if (this.maps[i] == '\031') {
/* 235 */             this.types[i] = this.types[i] | T_WATERFLOW;
/* 236 */             if ((this.types[i - this.tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
/* 237 */               this.types[i] = this.types[i] | T_SOLIDGROUND;
/*     */             }
/*     */           } 
/* 240 */           if (this.maps[i] == '"') {
/* 241 */             this.types[i] = this.types[i] | T_UNDERWATER;
/*     */           }
/* 243 */           if (this.maps[i] == '\021') {
/* 244 */             this.types[i] = this.types[i] | T_BRIDGE;
/*     */           }
/* 246 */           if (this.maps[i] == '!' || this.maps[i] == 'g' || this.maps[i] == 'h' || this.maps[i] == 'i' || this.maps[i] == '\032' || this.maps[i] == '!') {
/* 247 */             this.types[i] = this.types[i] | T_OUTSIDE;
/*     */           }
/* 249 */           if (this.maps[i] == '3' || this.maps[i] == 'o' || this.maps[i] == 'D') {
/* 250 */             this.types[i] = this.types[i] | T_BOTTOM;
/*     */           }
/* 252 */           if (this.maps[i] == 'R' || this.maps[i] == 'n' || this.maps[i] == '') {
/* 253 */             this.types[i] = this.types[i] | T_DIE;
/*     */           }
/* 255 */           if (this.maps[i] == 'q') {
/* 256 */             this.types[i] = this.types[i] | T_BANG;
/*     */           }
/* 258 */           if (this.maps[i] == '') {
/* 259 */             this.types[i] = 0x8000 | this.types[i];
/*     */           }
/* 261 */           if (this.maps[i] == '(' || this.maps[i] == ')') {
/* 262 */             this.types[i] = this.types[i] | T_JUM8;
/*     */           }
/* 264 */           if (this.maps[i] == 'n') {
/* 265 */             this.types[i] = this.types[i] | T_NT0;
/*     */           }
/* 267 */           if (this.maps[i] == '') {
/* 268 */             this.types[i] = this.types[i] | T_NT1;
/*     */           }
/*     */         } 
/*     */       } 
/* 272 */     } catch (Exception ex) {
/* 273 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void loadMapFromResource() {
/*     */     try {
/* 279 */       if (this.id == 0 || this.id == 56 || (this.id > 72 && this.id < 125) || (this.id > 125 && this.id < 133) || (this.id > 133 && this.id < 139) || this.id > 148) {
/*     */         return;
/*     */       }
/* 282 */       byte[] ab = NinjaUtil.getFile("Data/Map/" + this.id);
/* 283 */       this.logger.debug("LoadMapFromResource", "mapId: " + this.id + " size: " + ab.length);
/* 284 */       DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ab));
/* 285 */       this.tmw = (short)dis.read();
/* 286 */       this.tmh = (short)dis.read();
/* 287 */       this.maps = new char[dis.available()];
/* 288 */       int size = this.tmw * this.tmh;
/* 289 */       for (int i = 0; i < size; i++) {
/* 290 */         this.maps[i] = (char)dis.readByte();
/*     */       }
/* 292 */       loadMap();
/* 293 */     } catch (IOException ex) {
/* 294 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static class MonsterCoordinate
/*     */   {
/*     */     public short templateId;
/*     */     public short x;
/*     */     public short y;
/*     */     
/*     */     public MonsterCoordinate(short templateId, short x, short y) {
/* 306 */       this.templateId = templateId;
/* 307 */       this.x = x;
/* 308 */       this.y = y;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\object\TileMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */