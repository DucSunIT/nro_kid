package nro.models.boss.broly;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;
import nro.utils.Util;

/**
 *
 * @author Văn Tuấn - 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class SuperBrolyRed extends FutureBoss {

    public SuperBrolyRed() {
        super(BossFactory.SUPER_BROLY_RED, BossData.SUPER_BROLY_RED);
    }

    @Override
    public void rewards(Player pl) {
        if (Util.isTrue(50, 100)) {
            this.dropItemReward(568, (int) pl.id);
            generalRewards(pl);
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {

        textTalkAfter = new String[]{"Mấy con gà này chờ ta đấy, ta sẽ quay trở lại"};
    }

    @Override
    public void leaveMap() {
        BossManager.gI().getBossById(BossFactory.SUPER_BROLY).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }
}
