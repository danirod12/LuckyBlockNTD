package me.DenBeKKer.ntdLuckyBlock.api.loader;

import me.DenBeKKer.ntdLuckyBlock.util.Config;
import me.DenBeKKer.ntdLuckyBlock.variables.LuckyDrop;

public interface PathLoader {

    LuckyDrop load(Config loaded, String path);

}
