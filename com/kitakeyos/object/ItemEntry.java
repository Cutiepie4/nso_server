/*    */ package com.kitakeyos.object;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ItemEntry
/*    */ {
/*    */   public int id;
/*    */   public String name;
/*    */   public String description;
/*    */   public byte type;
/*    */   public byte gender;
/*    */   public short level;
/*    */   public short icon;
/*    */   public short part;
/*    */   public boolean isUpToUp;
/*    */   
/*    */   public boolean isTypeAdorn() {
/* 25 */     return (this.type == 3 || this.type == 5 || this.type == 7 || this.type == 9);
/*    */   }
/*    */   
/*    */   public boolean isTypeClothe() {
/* 29 */     return (this.type == 0 || this.type == 2 || this.type == 4 || this.type == 6 || this.type == 8);
/*    */   }
/*    */   
/*    */   public boolean isTypeWeapon() {
/* 33 */     return (this.type == 1);
/*    */   }
/*    */   
/*    */   public boolean isKunai() {
/* 37 */     return (this.id == 99 || this.id == 100 || this.id == 101 || this.id == 102 || this.id == 103 || this.id == 333 || this.id == 508 || this.id == 634 || this.id == 371);
/*    */   }
/*    */   
/*    */   public boolean isKiem() {
/* 41 */     return (this.id == 194 || this.id == 94 || this.id == 95 || this.id == 96 || this.id == 97 || this.id == 98 || this.id == 369 || this.id == 506 || this.id == 632 || this.id == 369 || this.id == 331);
/*    */   }
/*    */   
/*    */   public boolean isDao() {
/* 45 */     return (this.id == 104 || this.id == 105 || this.id == 106 || this.id == 107 || this.id == 108 || this.id == 373 || this.id == 335 || this.id == 510 || this.id == 636);
/*    */   }
/*    */   
/*    */   public boolean isCung() {
/* 49 */     return (this.id == 109 || this.id == 110 || this.id == 111 || this.id == 112 || this.id == 113 || this.id == 372 || this.id == 334 || this.id == 509 || this.id == 635);
/*    */   }
/*    */   
/*    */   public boolean isTieu() {
/* 53 */     return (this.id == 114 || this.id == 115 || this.id == 116 || this.id == 117 || this.id == 118 || this.id == 370 || this.id == 332 || this.id == 507 || this.id == 633);
/*    */   }
/*    */   
/*    */   public boolean isQuat() {
/* 57 */     return (this.id == 119 || this.id == 120 || this.id == 121 || this.id == 122 || this.id == 123 || this.id == 374 || this.id == 336 || this.id == 511 || this.id == 637);
/*    */   }
/*    */   
/*    */   public boolean isTypeBody() {
/* 61 */     return (this.type >= 0 && this.type <= 15);
/*    */   }
/*    */   
/*    */   public boolean isTypeCrystal() {
/* 65 */     return (this.type == 26);
/*    */   }
/*    */   
/*    */   public boolean isTypeMount() {
/* 69 */     return (29 <= this.type && this.type <= 33);
/*    */   }
/*    */   
/*    */   public boolean isTypeNgocKham() {
/* 73 */     return (this.type == 34);
/*    */   }
/*    */   
/*    */   public int getUpMax() {
/* 77 */     if (this.level >= 1 && this.level < 20) {
/* 78 */       return 4;
/*    */     }
/* 80 */     if (this.level >= 20 && this.level < 40) {
/* 81 */       return 8;
/*    */     }
/* 83 */     if (this.level >= 40 && this.level < 50) {
/* 84 */       return 12;
/*    */     }
/* 86 */     if (this.level >= 50 && this.level < 60) {
/* 87 */       return 14;
/*    */     }
/* 89 */     return 16;
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\object\ItemEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */