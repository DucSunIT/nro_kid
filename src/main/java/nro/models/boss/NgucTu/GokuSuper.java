package nro.models.boss.NgucTu;

import java.util.logging.Level;
import java.util.logging.Logger;
import nro.consts.ConstItem;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.utils.Util;

import nro.models.boss.BossManager;
import nro.services.SkillService;
import nro.utils.SkillUtil;

import nro.models.boss.BossData;

import nro.models.boss.BossFactory;

import nro.models.boss.Boss;

/**
 * @author DucSunIT
 * @copyright 💖 GirlkuN 💖
 */
public class GokuSuper extends Boss {

    public GokuSuper() {
        super(BossFactory.GOKU_SUPER, BossData.GOKU_SUPER);
    }

    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null, null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                if (!useSpecialSkill()) {
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
                        SkillService.gI().useSkill(this, pl, null, null);
                        checkPlayerDie(pl);
                    } else {
                        goToPlayer(pl, false);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

//    @Override
//    public void rewards(Player pl) {
//        int a = 0;
//        int b = 5;
//        if (Util.isTrue(90, 100)) {
//            for (int i = 0; i < 3; i++) {
//                ItemMap itemMap = new ItemMap(this.zone, 1235, 1,
//                        this.location.x + a, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
//                Service.getInstance().dropItemMap(this.zone, itemMap);
//                a += 15;
//            }
//        } else {
//            for (int i = 0; i < 2; i++) {
//                ItemMap itemMap = new ItemMap(this.zone, 457, 1,
//                        this.location.x + a, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
//                Service.getInstance().dropItemMap(this.zone, itemMap);
//                a += 15;
//            }
//        }
//        for (int i = 0; i < 2; i++) {
//            ItemMap itemMap1 = new ItemMap(this.zone, 1236, 1,
//                    this.location.x + b, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
//            Service.getInstance().dropItemMap(this.zone, itemMap1);
//            b += 15;
//        }
//    }
    @Override
    public void rewards(Player pl) {
        int a = 0;
        boolean dropped = false;  // Biến để kiểm tra xem đã rơi vật phẩm hay chưa
        if (Util.isTrue(1, 2)) {
            ItemMap itemMap = new ItemMap(this.zone, 1235, 1,
                    this.location.x + a, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, itemMap);
            dropped = true; // Đánh dấu đã rơi đồ
        }

        if (Util.isTrue(1, 10)) {
            ItemMap itemMap1 = new ItemMap(this.zone, 1236, 1,
                    this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, itemMap1);
            dropped = true; // Đánh dấu đã rơi đồ
        }

        // Nếu không rơi bất kỳ vật phẩm nào, rơi vật phẩm mặc định (ID 457)
        if (!dropped) {
            for (int i = 0; i < 2; i++) {
                ItemMap itemMap = new ItemMap(this.zone, 457, 1,
                        this.location.x + a, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, itemMap);
                a += 15;
            }
        }

    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl
    ) {

    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{"Vật phẩm hiếm ở chổ ta nè",
            "Đâu có dễ đâu ku", "Thái dương hạ san"};

    }

    @Override
    public void leaveMap() {
        try {
            BossFactory.createBoss(BossFactory.GOKU_SUPER).setJustRest();
            super.leaveMap();
            BossManager.gI().removeBoss(this);
        } catch (Exception ex) {
            Logger.getLogger(GokuSuper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
