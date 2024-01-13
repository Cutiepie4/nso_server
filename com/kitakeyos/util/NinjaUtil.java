/*     */ package com.kitakeyos.util;
/*     */ 
/*     */ import com.kitakeyos.server.Server;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.Random;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NinjaUtil
/*     */ {
/*  20 */   public static Random rand = new Random();
/*  21 */   public static NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi"));
/*     */   
/*     */   public static int nextInt(int max) {
/*  24 */     return rand.nextInt(max);
/*     */   }
/*     */   
/*     */   public static String getCurrency(long number) {
/*  28 */     return numberFormat.format(number);
/*     */   }
/*     */   
/*     */   public static int nextInt(int[] percen) {
/*  32 */     int next = nextInt(100); int i;
/*  33 */     for (i = 0; i < percen.length; i++) {
/*  34 */       if (next < percen[i]) {
/*  35 */         return i;
/*     */       }
/*  37 */       next -= percen[i];
/*     */     } 
/*  39 */     return i;
/*     */   }
/*     */   
/*     */   public static short[] getXY(int map) {
/*  43 */     short[] xy = new short[2];
/*  44 */     if (map == 22) {
/*  45 */       xy[0] = 228;
/*  46 */       xy[1] = 192;
/*  47 */     } else if (map == 10) {
/*  48 */       xy[0] = 109;
/*  49 */       xy[1] = 264;
/*  50 */     } else if (map == 17) {
/*  51 */       xy[0] = 1480;
/*  52 */       xy[1] = 264;
/*  53 */     } else if (map == 32) {
/*  54 */       xy[0] = 2502;
/*  55 */       xy[1] = 384;
/*  56 */     } else if (map == 38) {
/*  57 */       xy[0] = 397;
/*  58 */       xy[1] = 336;
/*  59 */     } else if (map == 43) {
/*  60 */       xy[0] = 2529;
/*  61 */       xy[1] = 240;
/*  62 */     } else if (map == 48) {
/*  63 */       xy[0] = 131;
/*  64 */       xy[1] = 432;
/*  65 */     } else if (map == 1) {
/*  66 */       xy[0] = 193;
/*  67 */       xy[1] = 384;
/*  68 */     } else if (map == 27) {
/*  69 */       xy[0] = 647;
/*  70 */       xy[1] = 408;
/*  71 */     } else if (map == 72) {
/*  72 */       xy[0] = 1611;
/*  73 */       xy[1] = 672;
/*     */     } else {
/*  75 */       xy[0] = 0;
/*  76 */       xy[1] = 0;
/*     */     } 
/*  78 */     return xy;
/*     */   }
/*     */   
/*     */   public static int nextInt(int min, int max) {
/*  82 */     if (min >= max) {
/*  83 */       return max;
/*     */     }
/*  85 */     return min + rand.nextInt(max - min);
/*     */   }
/*     */   
/*     */   public static void setOption(int[][] option, int up1, int up2) {
/*  89 */     int num = up2 - up1;
/*  90 */     for (int i = 0; i < option.length; i++) {
/*  91 */       if (option[i][0] == 6 || option[i][0] == 7) {
/*  92 */         option[i][1] = option[i][1] + 15 * num;
/*  93 */       } else if (option[i][0] == 8 || option[i][0] == 9 || option[i][0] == 19) {
/*  94 */         option[i][1] = option[i][1] + 10 * num;
/*  95 */       } else if (option[i][0] == 10 || option[i][0] == 11 || option[i][0] == 12 || option[i][0] == 13 || option[i][0] == 14 || option[i][0] == 15 || option[i][0] == 17 || option[i][0] == 18 || option[i][0] == 20) {
/*  96 */         option[i][1] = option[i][1] + 5 * num;
/*  97 */       } else if (option[i][0] == 21 || option[i][0] == 22 || option[i][0] == 23 || option[i][0] == 24 || option[i][0] == 25 || option[i][0] == 26) {
/*  98 */         option[i][1] = option[i][1] + 150 * num;
/*  99 */       } else if (option[i][0] == 16) {
/* 100 */         option[i][1] = option[i][1] + 3 * num;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static int[][] getOptionShop(int[][] option) {
/* 106 */     int[][] result = new int[option.length][2];
/* 107 */     for (int i = 0; i < option.length; i++) {
/* 108 */       if (option[i][0] == 0 || option[i][0] == 1 || option[i][0] == 21 || option[i][0] == 22 || option[i][0] == 23 || option[i][0] == 24 || option[i][0] == 25 || option[i][0] == 26) {
/* 109 */         result[i][1] = option[i][1] - 49;
/* 110 */       } else if (option[i][0] == 6 || option[i][0] == 7 || option[i][0] == 8 || option[i][0] == 9 || option[i][0] == 19) {
/* 111 */         result[i][1] = option[i][1] - 9;
/* 112 */       } else if (option[i][0] == 2 || option[i][0] == 3 || option[i][0] == 4 || option[i][0] == 5 || option[i][0] == 10 || option[i][0] == 11 || option[i][0] == 12 || option[i][0] == 13 || option[i][0] == 14 || option[i][0] == 15 || option[i][0] == 17 || option[i][0] == 18) {
/* 113 */         result[i][1] = option[i][1] - 4;
/* 114 */       } else if (option[i][0] == 16) {
/* 115 */         result[i][1] = option[i][1] - 2;
/*     */       } else {
/* 117 */         result[i][1] = option[i][1];
/*     */       } 
/*     */     } 
/* 120 */     return result;
/*     */   }
/*     */   
/*     */   public static String getColor(String color) {
/* 124 */     if (color.equals("tahoma_7_white")) {
/* 125 */       return "\nc0";
/*     */     }
/* 127 */     if (color.equals("tahoma_7b_yellow")) {
/* 128 */       return "\nc1";
/*     */     }
/* 130 */     if (color.equals("tahoma_7b_white")) {
/* 131 */       return "\nc2";
/*     */     }
/* 133 */     if (color.equals("tahoma_7_yellow")) {
/* 134 */       return "\nc3";
/*     */     }
/* 136 */     if (color.equals("tahoma_7b_red")) {
/* 137 */       return "\nc4";
/*     */     }
/* 139 */     if (color.equals("tahoma_7_red")) {
/* 140 */       return "\nc5";
/*     */     }
/* 142 */     if (color.equals("tahoma_7_grey")) {
/* 143 */       return "\nc6";
/*     */     }
/* 145 */     if (color.equals("tahoma_7b_blue")) {
/* 146 */       return "\nc7";
/*     */     }
/* 148 */     if (color.equals("tahoma_7_blue")) {
/* 149 */       return "\nc8";
/*     */     }
/* 151 */     if (color.equals("tahoma_7_green")) {
/* 152 */       return "\nc9";
/*     */     }
/* 154 */     return "\nc0";
/*     */   }
/*     */   
/*     */   public static byte[] getFile(String url) {
/*     */     try {
/* 159 */       FileInputStream fis = new FileInputStream(url);
/* 160 */       byte[] ab = new byte[fis.available()];
/* 161 */       fis.read(ab, 0, ab.length);
/* 162 */       fis.close();
/* 163 */       return ab;
/* 164 */     } catch (IOException ex) {
/* 165 */       Logger.getLogger(NinjaUtil.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */       
/* 167 */       return null;
/*     */     } 
/*     */   }
/*     */   public static void saveFile(String url, byte[] ab) {
/*     */     try {
/* 172 */       File f = new File(url);
/* 173 */       if (f.exists()) {
/* 174 */         f.delete();
/*     */       }
/* 176 */       f.createNewFile();
/* 177 */       FileOutputStream fos = new FileOutputStream(url);
/* 178 */       fos.write(ab);
/* 179 */       fos.flush();
/* 180 */       fos.close();
/* 181 */     } catch (IOException e) {
/* 182 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static int getRange(int x1, int x2) {
/* 187 */     return (int)Math.sqrt(Math.pow((x2 - x1), 2.0D));
/*     */   }
/*     */   
/*     */   public static int getLevel(long num) {
/* 191 */     for (int i = 0; i < Server.exps.length; i++) {
/* 192 */       if (num < Server.exps[i]) {
/* 193 */         return i;
/*     */       }
/* 195 */       num -= Server.exps[i];
/*     */     } 
/* 197 */     return 1;
/*     */   }
/*     */   
/*     */   public static long getExp(int level) {
/* 201 */     long exp = 0L;
/* 202 */     for (int i = 0; i < level; i++) {
/* 203 */       exp += Server.exps[i];
/*     */     }
/* 205 */     return exp;
/*     */   }
/*     */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyo\\util\NinjaUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */