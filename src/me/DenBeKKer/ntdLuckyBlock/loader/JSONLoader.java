package me.DenBeKKer.ntdLuckyBlock.loader;

import me.DenBeKKer.ntdLuckyBlock.api.PathLoader;
import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyEntry;

public class JSONLoader implements PathLoader {
	
	/*
	 * 
	 *  drops:
	 *    '0':
	 *      chance: HIGH
	 *      items:
	 *        '0':
	 *          class: "ItemDrop"
	 *          json: "{...}"
	 *        '1':
	 *          class: "ItemDrop"
	 *          json: "{...}"
	 *    
	 */
	
	@Override
	public LuckyDrop load(Config config, String path) {
		throw new UnsupportedOperationException("Feature unavailable in free version");
	}
	
	public void save(Config loaded, String path, LuckyEntry entry) {
		throw new UnsupportedOperationException("Feature unavailable in free version");
	}
	
}
