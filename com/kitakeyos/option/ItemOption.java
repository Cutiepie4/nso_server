package com.kitakeyos.option;

import com.kitakeyos.data.ItemData;
import com.kitakeyos.template.ItemOptionTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemOption implements Cloneable {
	public byte active;
	public int param;
	public ItemOptionTemplate optionTemplate;

	public ItemOption(int optionTemplateId, int param) {
		this.param = param;
		this.optionTemplate = ItemData.getItemOptionById(optionTemplateId);
	}

	public ItemOption clone() {
		try {
			return (ItemOption) super.clone();
		} catch (CloneNotSupportedException ex) {
			Logger.getLogger(ItemOption.class.getName()).log(Level.SEVERE, (String) null, ex);

			return null;
		}
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * option\ItemOption.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.3
 */