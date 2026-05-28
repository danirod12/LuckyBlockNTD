package com.github.danirod12.luckyblock.api.model;

import com.github.danirod12.luckyblock.api.model.random.LuckyCollection;

/**
 * A collection of items that can be dropped as a reward.
 */
public interface ItemsBag extends LuckyCollection<LuckyCollection<LuckyDrop>> {
}
