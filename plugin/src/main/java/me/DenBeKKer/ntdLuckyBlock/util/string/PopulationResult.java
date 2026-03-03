package me.DenBeKKer.ntdLuckyBlock.util.string;

public class PopulationResult {

    private final String result;
    private final ValidationLevel validationLevel;
    private final boolean touchesOriginBlock;

    public PopulationResult(String result, ValidationLevel validationLevel, boolean touchesOriginBlock) {
        this.result = result;
        this.validationLevel = validationLevel;
        this.touchesOriginBlock = touchesOriginBlock;
    }

    public boolean isTouchesOriginBlock() {
        return touchesOriginBlock;
    }

    public String getResult() {
        return result;
    }

    public ValidationLevel getValidationLevel() {
        return validationLevel;
    }

    public enum ValidationLevel {
        SUCCESS,
        NO_PLAYER,
        NO_BLOCK,
        NO_BLOCK_NO_PLAYER;

        public ValidationLevel negate(ValidationLevel level) {
            if (level == SUCCESS) {
                return this;
            }
            if (this == SUCCESS) {
                return level;
            }
            return this == level ? level : NO_BLOCK_NO_PLAYER;
        }
    }
}
