/*    */ package com.kitakeyos.server;
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
/*    */ public class Language
/*    */ {
/* 14 */   private static Language gameString = new Language();
/*    */   
/*    */   public static final String AUTHOR = "Hoàng Hữu Dũng";
/*    */   
/*    */   public static final String CREATE_CHAR_FAIL_1 = "Tên tài khoản chỉ cho phép từ 6 đến 20 ký tự!";
/*    */   public static final String CREATE_CHAR_FAIL_2 = "Tên nhân vật đã tồn tại!";
/*    */   public static final String CREATE_CHAR_FAIL_3 = "Tạo nhân vật thất bại!";
/*    */   public static final String CREATE_CHAR_FAIL_4 = "Tên nhân vật không được chứa ký tự đặc biệt!";
/*    */   public static final String CREATE_CHAR_FAIL_5 = "Bạn đã tạo tối đa số nhân vât!";
/*    */   public static final String LOGIN_FAIL = "Tài khoản mật khẩu không chính xác!";
/*    */   public static final String LOGIN_FAIL_2 = "Tên đăng nhập không được chứa ký tự đặc biệt!";
/*    */   public static final String NOT_ENOUGH_LUONG = "Bạn không đủ lượng!";
/*    */   public static final String NOT_ENOUGH_XU = "Bạn không đủ xu!";
/*    */   public static final String NOT_ENOUGH_XU_AND_YEN = "Bạn không đủ xu và yên!";
/*    */   public static final String NOT_ENOUGH_YEN = "Bạn không đủ yên!";
/*    */   public static final String NOT_ENOUGH_BAG_1 = "Hành trang không đủ chỗ trống.";
/*    */   public static final String NOT_ENOUGH_BAG_2 = "%s hành trang không đủ chỗ trống.";
/*    */   public static final String CRYSTAL_MAX_NUMBER = "Tối đa %d viên đá!";
/*    */   public static final String EQUIPMENT_MAX_LEVEL = "Trang bị đã đạt cấp tối đa!";
/*    */   public static final String CRYSTAL_MAX_LEVEl = "Đá đã đạt cấp tối đa.";
/*    */   
/*    */   public static String getString(String name, Object... data) {
/*    */     try {
/* 37 */       String text = gameString.getClass().getField(name).get(null).toString();
/* 38 */       return String.format(text, data);
/* 39 */     } catch (Exception exception) {
/*    */       
/* 41 */       return "Không tìm thấy nội dung!";
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\server\Language.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */