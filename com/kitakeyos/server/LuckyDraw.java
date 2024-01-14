package com.kitakeyos.server;

import com.kitakeyos.db.Connect;
import com.kitakeyos.io.Message;
import com.kitakeyos.util.NinjaUtil;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LuckyDraw implements Runnable {
	public String name;

	public class MemThamGia {
		int iddb;
		public int xu;
		String name;
	}

	public int totalMoney = 0;
	public int xuWin = 0;
	public int timeCount = 120;
	public String nameWin = "";
	public int xuThamGia;
	public int[] percen = new int[0];
	public int percenMax = 0;
	public byte type = 0;
	public ArrayList<MemThamGia> mem = new ArrayList<>();

	public int xuMin;
	public int xuMax;

	public LuckyDraw(String name, byte type) {
		this.name = name;
		this.type = type;
		if (type == 0) {
			this.xuMin = 10000;
			this.xuMax = 100000;
		} else {
			this.xuMin = 1000000;
			this.xuMax = 50000000;
		}
	}

	public Date timeStart;
	public static boolean running = true;

	public void join(Character pl, int numb) throws IOException {
		setPercen();
		if (this.timeCount < 10) {
			pl.user.service.addInfoMe("Đã hết thời gian tham gia vui lòng quay lại vào vòng sau!");
			return;
		}
		if (this.mem.size() >= 30) {
			pl.user.service.addInfoMe("Số người tham gia tối đa là 30!");
			return;
		}
		if (pl.xu < numb) {
			pl.user.service.addInfoMe("Bạn không đủ xu để tham gia!");
			return;
		}
		for (MemThamGia memThamGia : this.mem) {
			if (memThamGia.iddb == pl.id) {
				if (memThamGia.xu + numb > this.xuMax) {
					pl.user.service.addInfoMe(
							"Bạn chỉ có thể đặt thêm tối đa " + NinjaUtil.getCurrency((this.xuMax - memThamGia.xu)));
					return;
				}
				this.totalMoney += numb;
				memThamGia.xu += numb;
				pl.updateXu(pl.xu - numb);
				setPercen();
				pl.user.service.addInfoMe("Bạn đã đặt thêm " + NinjaUtil.getCurrency(numb) + "Xu thành công!");
				return;
			}
		}
		if (numb < this.xuMin || numb > this.xuMax) {
			pl.user.service.addInfoMe("Bạn chỉ có thể đặt từ " + NinjaUtil.getCurrency(this.xuMin) + " đến "
					+ NinjaUtil.getCurrency(this.xuMax) + "Xu!");
			return;
		}
		MemThamGia m = new MemThamGia();
		m.iddb = pl.id;
		m.xu = numb;
		this.totalMoney += numb;
		m.name = pl.name;
		this.mem.add(m);
		pl.updateXu(pl.xu - numb);
		setPercen();
		pl.user.service.addInfoMe("Bạn đã tham gia " + NinjaUtil.getCurrency(numb) + " xu thành công!");
	}

	public void run() {
		try {
			while (running) {
				if (this.mem.size() >= 2) {
					this.timeCount--;
					if (this.timeCount <= 0) {
						try {
							randomCharacterWin();
							result();
							refresh();
						} catch (Exception ex) {
							continue;
						}
					}
				}
				Thread.sleep(1000L);
			}
		} catch (InterruptedException ex) {
			Logger.getLogger(LuckyDraw.class.getName()).log(Level.SEVERE, (String) null, ex);
		}
	}

	public void randomCharacterWin() throws SQLException {
		try {
			int indexWin = NinjaUtil.nextInt(this.percen);
			MemThamGia m = this.mem.get(indexWin);
			int receive = this.totalMoney;
			if (this.mem.size() > 10) {
				receive -= receive / 10;
			}
			Character pl = Character.characters_name.get(m.name);
			if (pl != null) {
				pl.addXu(receive);
			} else {
				PreparedStatement stmt = Connect.conn
						.prepareStatement("UPDATE `player` SET `xu` = `xu` + ? WHERE `id` = ? LIMIT 1;");
				stmt.setInt(1, receive);
				stmt.setInt(2, m.iddb);
				stmt.execute();
			}
			this.nameWin = m.name;
			this.xuWin = receive;
			this.xuThamGia = m.xu;
		} catch (IOException ex) {
			Logger.getLogger(LuckyDraw.class.getName()).log(Level.SEVERE, (String) null, ex);
		}
	}

	public int getNumberMoney() {
		return this.totalMoney;
	}

	public String[] getPercen(int iddb) {
		if (this.totalMoney == 0 || this.mem == null) {
			return new String[] { "00", "00" };
		}
		for (MemThamGia m : this.mem) {
			if (m.iddb == iddb) {
				return String.format("%.2f", new Object[] { Float.valueOf(m.xu * 100.0F / this.totalMoney) })
						.replace(".", "_").split("_");
			}
		}
		return new String[] { "00", "00" };
	}

	public int getMoneyById(int iddb) {
		for (MemThamGia m : this.mem) {
			if (m.iddb == iddb) {
				return m.xu;
			}
		}
		return 0;
	}

	public void refresh() {
		this.timeCount = 120;
		this.totalMoney = 0;
		this.mem.clear();
	}

	public void result() throws IOException {
		Message mss = new Message(-21);
		DataOutputStream ds = mss.writer();
		ds.writeUTF("Admin");
		ds.writeUTF("Chúc mừng " + this.nameWin.toUpperCase() + " đã chiến thắng " + NinjaUtil.getCurrency(this.xuWin)
				+ " xu trong trò chơi " + this.name + " với " + NinjaUtil.getCurrency(this.xuThamGia) + " xu");
		ds.flush();
		Server.sendToServer(mss);
	}

	public void setPercen() {
		this.percen = new int[this.mem.size()];
		int i = 0;
		for (MemThamGia m : this.mem) {
			this.percen[i] = m.xu * 100 / this.totalMoney;
			i++;
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\LuckyDraw.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */