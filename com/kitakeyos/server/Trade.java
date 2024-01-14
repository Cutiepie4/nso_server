package com.kitakeyos.server;

import com.kitakeyos.io.Message;
import com.kitakeyos.option.ItemOption;
import com.kitakeyos.util.NinjaUtil;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Trade extends Thread implements Runnable {
	public int timeCount = 600;
	public Trader trader_1 = new Trader();
	public Trader trader_2 = new Trader();
	public boolean isFinish = false;

	public void showMenu() throws IOException {
		Message ms = new Message(37);
		DataOutputStream ds = ms.writer();
		ds.writeUTF(this.trader_2.character.name);
		ds.flush();
		this.trader_1.character.user.client.sendMessage(ms);
		ms = new Message(37);
		ds = ms.writer();
		ds.writeUTF(this.trader_1.character.name);
		ds.flush();
		this.trader_2.character.user.client.sendMessage(ms);
	}

	public void closeMenu() throws IOException {
		Message ms = new Message(57);
		this.trader_1.character.user.client.sendMessage(ms);
		this.trader_2.character.user.client.sendMessage(ms);
	}

	public void run() {
		try {
			showMenu();
			while (this.timeCount > 0 && !this.isFinish) {

				if (this.trader_1.character == null || this.trader_2.character == null) {
					break;
				}
				if (this.trader_1.accept && this.trader_2.accept) {
					closeMenu();
					this.isFinish = true;
					if (this.trader_2.coinTradeOrder > 0) {
						this.trader_1.character.addXu(this.trader_2.coinTradeOrder);
						this.trader_2.character.updateXu(this.trader_2.character.xu - this.trader_2.coinTradeOrder);
					}
					if (this.trader_1.coinTradeOrder > 0) {
						this.trader_2.character.addXu(this.trader_1.coinTradeOrder);
						this.trader_1.character.updateXu(this.trader_1.character.xu - this.trader_1.coinTradeOrder);
					}
					int num = this.trader_1.character.getSlotNull();
					if (this.trader_2.itemTradeOrder.size() > num) {
						this.trader_1.character.startOKDlg(Language.getString("NOT_ENOUGH_BAG_1", new Object[0]));
						this.trader_2.character.startOKDlg(
								Language.getString("NOT_ENOUGH_BAG_2", new Object[] { this.trader_1.character.name }));
						break;
					}
					num = this.trader_2.character.getSlotNull();
					if (this.trader_1.itemTradeOrder.size() > num) {
						this.trader_2.character.startOKDlg(Language.getString("NOT_ENOUGH_BAG_1", new Object[0]));
						this.trader_1.character.startOKDlg(
								Language.getString("NOT_ENOUGH_BAG_2", new Object[] { this.trader_2.character.name }));
						break;
					}
					int numberItem = this.trader_2.itemTradeOrder.size();
					if (numberItem > 0) {
						for (Character.Item item : this.trader_2.itemTradeOrder) {
							int index = item.index;
							int quantity = item.quantity;
							this.trader_1.character.addItemToBag(item);
							this.trader_2.character.removeItem(index, quantity, true);
						}
					}
					numberItem = this.trader_1.itemTradeOrder.size();
					if (numberItem > 0) {
						for (Character.Item item : this.trader_1.itemTradeOrder) {
							int index = item.index;
							int quantity = item.quantity;
							this.trader_2.character.addItemToBag(item);
							this.trader_1.character.removeItem(index, quantity, true);
						}
					}
					this.trader_1.character.user.service.tradeAccept();
					this.trader_2.character.user.service.tradeAccept();
					return;
				}
				this.timeCount--;
				Thread.sleep(1000L);
			}
			closeMenu();
		} catch (IOException ex) {
			Logger.getLogger(Trade.class.getName()).log(Level.SEVERE, (String) null, ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(Trade.class.getName()).log(Level.SEVERE, (String) null, ex);
		}
	}

	public void tradeItemLock(Trader trader) throws IOException {
		Message ms = new Message(45);
		DataOutputStream ds = ms.writer();
		ds.writeInt(trader.coinTradeOrder);
		ds.writeByte(trader.itemTradeOrder.size());
		for (Character.Item item : trader.itemTradeOrder) {
			ds.writeShort(item.id);
			if (item.entry.isTypeBody() || item.entry.isTypeNgocKham()) {
				ds.writeByte(item.upgrade);
			}
			ds.writeBoolean((item.expire != -1L));
			ds.writeShort(item.quantity);
		}
		ds.flush();
		((trader == this.trader_1) ? this.trader_2 : this.trader_1).character.user.client.sendMessage(ms);
	}

	public void viewItemInfo(Character _char, byte type, byte index) throws IOException {
		Trader trader = (_char == this.trader_1.character) ? this.trader_2 : this.trader_1;
		Character.Item item = trader.itemTradeOrder.get(index);
		Message mss = new Message(42);
		DataOutputStream ds = mss.writer();
		ds.writeByte(type);
		ds.writeByte(index);
		ds.writeLong(item.expire);
		ds.writeInt(item.yen);
		if (item.entry.isTypeBody() || item.entry.isTypeMount() || item.entry.isTypeNgocKham()) {
			ds.writeByte(item.sys);
			for (ItemOption ability : item.options) {
				ds.writeByte(ability.optionTemplate.id);
				ds.writeInt(ability.param);
			}
		} else if (item.id == 233 || item.id == 234 || item.id == 235) {
			byte[] ab = NinjaUtil
					.getFile("Data/Img/Item/" + _char.user.client.zoomLevel + "/Small" + item.entry.icon + ".png");
			ds.writeInt(ab.length);
			ds.write(ab);
		}
		ds.flush();
		_char.sendMessage(mss);
		mss.cleanup();
	}

	public class Trader {
		public Character character;
		public int coinTradeOrder;
		public ArrayList<Character.Item> itemTradeOrder;
		public boolean accept;
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Trade.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */