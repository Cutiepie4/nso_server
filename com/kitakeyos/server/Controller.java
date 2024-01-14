package com.kitakeyos.server;

import com.kitakeyos.io.IMessageHandler;
import com.kitakeyos.io.Message;
import com.kitakeyos.io.Session;
import com.kitakeyos.util.Logger;
import java.io.IOException;

public class Controller implements IMessageHandler {
	private final Session client;
	private Logger logger = new Logger(getClass());

	public Controller(Session client) {
		this.client = client;
	}

	public void onMessage(Message mss) {
		if (mss != null) {
			try {
				switch (mss.getCommand()) {

				case -27:
					this.client.sendKey();
					return;

				case -28:
					messageNotMap(mss);
					return;

				case -29:
					messageNotLogin(mss);
					return;

				case -30:
					messageSubCommand(mss);
					return;

				case -23:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.chatPublic(mss);
					}
					return;

				case -22:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.chatPrivate(mss);
					}
					return;

				case -21:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.chatGlobal(mss);
					}
					return;

				case -17:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.changeMap();
					}
					return;

				case -12:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.throwItem(mss);
					}
					return;

				case -14:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.pickItem(mss);
					}
					return;

				case -9:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.returnTownFromDead(mss);
					}
					return;

				case -10:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.wakeUpFromDead(mss);
					}
					return;

				case 1:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.move(mss);
					}
					return;

				case 25:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.requestCharacterInfo(mss);
					}
					return;

				case 68:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.addCuuSat(mss);
					}
					return;

				case 11:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.useItem(mss);
					}
					return;

				case 12:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.useItemChangeMap(mss);
					}
					return;

				case 13:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.buyItem(mss);
					}
					return;

				case 14:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.sellItem(mss);
					}
					return;

				case 15:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.itemBodyToBag(mss);
					}
					return;

				case 16:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.itemBoxToBag(mss);
					}
					return;

				case 17:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.itemBagToBox(mss);
					}
					return;

				case 21:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.upgradeItem(mss);
					}
					return;

				case 22:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.splitItem(mss);
					}
					return;

				case 19:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.upPearl(mss, true);
					}
					return;

				case 20:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.upPearl(mss, false);
					}
					return;

				case 28:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.changeZone(mss);
					}
					return;

				case 36:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.service.openUIZone();
					}
					return;

				case 40:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.menuId(mss);
					}
					return;

				case 41:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.selectSkill(mss);
					}
					return;

				case 42:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.requestItemInfo(mss);
					}
					return;

				case 43:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.tradeInvite(mss);
					}
					return;

				case 44:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.acceptInviteTrade(mss);
					}
					return;

				case 45:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.tradeItemLock(mss);
					}
					return;

				case 46:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.tradeAccept();
					}
					return;

				case 47:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.menu(mss);
					}
					return;

				case 57:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.tradeClose();
					}
					return;

				case 59:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.addFriend(mss);
					}
					return;

				case 60:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.attackMonster(mss);
					}
					return;

				case 61:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.attackCharacter(mss);
					}
					return;

				case 73:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.attackAllType(mss);
					}
					return;

				case 92:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.input(mss);
					}
					return;

				case 93:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.viewInfo(mss);
					}
					return;

				case 94:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.requestItemCharacter(mss);
					}
					return;

				case 108:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.itemMountToBag(mss);
					}
					return;
				}

				this.logger.log("CMD: " + mss.getCommand());

			} catch (IOException e) {
				this.logger.debug("onMessage", e.getMessage());
			}
		}
	}

	public void messageNotLogin(Message mss) {
		if (mss != null) {
			try {
				byte command = mss.reader().readByte();
				switch (command) {
				case -127:
					if (this.client.user == null) {
						this.client.login(mss);
					}
					return;

				case -125:
					if (this.client.user == null) {
						this.client.setClientType(mss);
					}
					return;
				}

				this.logger.log(String.format("Client %d: messageNotLogin: %d",
						new Object[] { Integer.valueOf(this.client.id), Byte.valueOf(command) }));

			} catch (Exception e) {
				this.logger.debug("messageNotLogin", e.getMessage());
			}
		}
	}

	public void messageNotMap(Message mss) {
		if (mss != null) {
			try {
				byte command = mss.reader().readByte();
				switch (command) {
				case -126:
					if (this.client.user != null && this.client.user.selectedCharacter == null) {
						this.client.user.selectToChar(mss);
					}
					return;

				case -125:
					if (this.client.user != null && this.client.user.selectedCharacter == null) {
						this.client.user.createCharacter(mss);
					}
					return;

				case -122:
					this.client.user.service.sendData();
					return;

				case -121:
					this.client.user.service.sendMap();
					return;

				case -120:
					this.client.user.service.sendSkill();
					return;

				case -119:
					this.client.user.service.sendItem();
					return;

				case -115:
					if (this.client.user != null) {
						this.client.user.service.requestIcon(mss);
					}
					return;

				case -109:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.service.requestMapTemplate(mss);
					}
					return;

				case -108:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.service.requestMobTemplate(mss);
					}
					return;

				case -101:
					if (this.client.user != null) {
						this.client.clientOK = true;
						this.client.user.service.selectChar();
					}
					return;

				case -88:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.convertUpgrade(mss);
					}
					return;

				case -85:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.inputNumberSplit(mss);
					}
					return;

				case -72:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.selectCard(mss);
					}
					return;
				}

				this.logger.log(String.format("Client %d: messageNotMap: %d",
						new Object[] { Integer.valueOf(this.client.id), Byte.valueOf(command) }));

			} catch (Exception e) {
				e.printStackTrace();
				this.logger.error("messageNotMap", e.getMessage());
			}
		}
	}

	public void messageSubCommand(Message mss) {
		if (mss != null) {
			try {
				byte command = mss.reader().readByte();
				switch (command) {

				case -65:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.loadSkillShortcut(mss);
					}
					return;

				case -67:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.saveRms(mss);
					}
					return;

				case -93:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.changePk(mss);
					}
					return;

				case -83:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.removeFriend(mss);
					}
					return;

				case -84:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.requestEnemies();
					}
					return;

				case -85:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.service.requestFriend();
					}
					return;

				case -109:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.upPotential(mss);
					}
					return;

				case -108:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.upSkill(mss);
					}
					return;

				case -107:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.bagSort();
						this.client.user.service.bagSort();
					}
					return;

				case -106:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.boxSort();
						this.client.user.service.boxSort();
					}
					return;

				case -105:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.boxCoinIn(mss);
					}
					return;

				case -104:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.boxCoinOut(mss);
					}
					return;

				case -103:
					if (this.client.user != null && this.client.user.selectedCharacter != null) {
						this.client.user.selectedCharacter.requestItem(mss);
					}
					return;
				}

				this.logger.log(String.format("Client %d: messageSubCommand: %d",
						new Object[] { Integer.valueOf(this.client.id), Byte.valueOf(command) }));

			} catch (Exception e) {
				this.logger.debug("messageSubCommand", e.getMessage());
			}
		}
	}

	public void onConnectionFail() {
		this.logger
				.log(String.format("Client %d: Kết nối thất bại!", new Object[] { Integer.valueOf(this.client.id) }));
	}

	public void onDisconnected() {
		this.logger.log(String.format("Client %d: Mất kết nối!", new Object[] { Integer.valueOf(this.client.id) }));
	}

	public void onConnectOK() {
		this.logger
				.log(String.format("Client %d: Kết nối thành công!", new Object[] { Integer.valueOf(this.client.id) }));
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * server\Controller.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.3
 */