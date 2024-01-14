package com.kitakeyos.data;

import com.kitakeyos.object.ItemEntry;
import com.kitakeyos.template.ItemOptionTemplate;
import java.util.Collection;
import java.util.HashMap;

public class ItemData {
	private static HashMap<Integer, ItemEntry> entrys = new HashMap<>();
	private static HashMap<Integer, ItemOptionTemplate> options = new HashMap<>();

	public static ItemEntry getItemEntryById(int id) {
		return entrys.get(Integer.valueOf(id));
	}

	public static void put(int id, ItemEntry entry) {
		entrys.put(Integer.valueOf(id), entry);
	}

	public static Collection<ItemEntry> getEntrys() {
		return entrys.values();
	}

	public static ItemOptionTemplate getItemOptionById(int id) {
		return options.get(Integer.valueOf(id));
	}

	public static void put(int id, ItemOptionTemplate option) {
		options.put(Integer.valueOf(id), option);
	}

	public static Collection<ItemOptionTemplate> getOptions() {
		return options.values();
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * data\ItemData.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */