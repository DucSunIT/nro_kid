package nro.services;

import nro.consts.ConstMob;
import nro.consts.ConstTask;
import nro.models.boss.BossFactory;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.map.ItemMap;
import nro.models.mob.Mob;
import nro.models.player.Pet;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.utils.Log;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DucSunIT
 * @copyright 💖 GirlkuN 💖
 *
 */
public class MobService {

    private static MobService i;

    private MobService() {

    }

    public static MobService gI() {
        if (i == null) {
            i = new MobService();
        }
        return i;
    }

    public void sendMobStillAliveAffterAttacked(Mob mob, long dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(mob.id);
            msg.writer().writeLong(mob.point.getHP());
            msg.writer().writeLong(dameHit);
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.getInstance().sendMessAllPlayerInMap(mob.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(MobService.class, e);
        }
    }

    public void sendMobDieAffterAttacked(Mob mob, Player plKill, long dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(mob.id);
            msg.writer().writeLong(dameHit);
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(mob, plKill, msg);
            Service.getInstance().sendMessAllPlayerInMap(mob.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
//            Logger.logException(MobService.class, e);
            e.printStackTrace();
        }
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(player, item.itemMapId, true);
                    }
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                    }
                }
            }
        }
    }

    private List<ItemMap> mobReward(Mob mob, Player player, Message msg) {
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            if (player.isBot) return null;
            itemReward = RewardService.gI().getRewardItems(player, mob,
                    mob.location.x + Util.nextInt(-10, 10), mob.zone.map.yPhysicInTop(mob.location.x, mob.location.y));
            msg.writer().writeByte(itemReward.size()); //sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
            }
        } catch (Exception e) {
            Log.error(MobService.class, e);
        }
        return itemReward;
    }

    public long mobAttackPlayer(Mob mob, Player player) {
        long dameMob = mob.point.getDameAttack();
        if (player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        return player.injured(null, dameMob, false, true);
    }

    public void sendMobAttackMe(Mob mob, Player player, long dame) {
        if (!player.isPet) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(mob.id);
                msg.writer().writeLong(dame); //dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(MobService.class, e);
            }
        }
    }

    public void sendMobAttackPlayer(Mob mob, Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(mob.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeLong(player.nPoint.hp);
            Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(MobService.class, e);
        }
    }

    public void hoiSinhMob(Mob mob) {
        boolean isDie = mob.isDie();
        mob.point.hp = mob.point.maxHp;
        mob.setTiemNang();
        if (isDie) {
            Message msg;
            try {
                msg = new Message(-13);
                msg.writer().writeByte(mob.id);
                msg.writer().writeByte(mob.tempId);
                msg.writer().writeByte(0); //level mob
                msg.writer().writeLong(mob.point.hp);
                Service.getInstance().sendMessAllPlayerInMap(mob.zone, msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(MobService.class, e);
            }
        }
    }

    public void hoiSinhMobDoanhTrai(Mob mob) {
        if (mob.tempId == ConstMob.BULON) {
            boolean haveTrungUyTrang = false;
            List<Player> bosses = mob.zone.getBosses();
            for (Player boss : bosses) {
                if (boss.id == BossFactory.TRUNG_UY_TRANG) {
                    haveTrungUyTrang = true;
                    break;
                }
            }
            if (haveTrungUyTrang) {
                hoiSinhMob(mob);
            }
        }
    }

    public void initMobDoanhTrai(Mob mob, Clan clan) {
        for (ClanMember cm : clan.getMembers()) {
            for (Player pl : clan.membersInGame) {
                if (pl.id == cm.id && pl.nPoint.hpMax >= mob.point.clanMemHighestHp) {
                    mob.point.clanMemHighestHp = pl.nPoint.hpMax;
                }
            }
        }
        mob.point.dame = mob.point.clanMemHighestHp / mob.point.xHpForDame;
        for (ClanMember cm : clan.getMembers()) {
            for (Player pl : clan.membersInGame) {
                if (pl.id == cm.id && pl.nPoint.dame >= mob.point.clanMemHighestDame) {
                    mob.point.clanMemHighestDame = pl.nPoint.dame;
                }
            }
        }
        mob.point.hp = mob.point.clanMemHighestDame * mob.point.xDameForHp;
    }

    public void initMobDoanhTrai(Mob mob, long point) {
        mob.point.hp = mob.point.maxHp = (int) (point / 10);
        mob.point.dame = mob.point.dame = (int) (point / 200);
    }

    public void initMobBanDoKhoBau(Mob mob, byte level) {
        mob.point.dame = level * 1250 * mob.level * 400;
        mob.point.maxHp = level * 9472 * mob.level * 200 + level * 4263 * mob.tempId;
    }

    public static void main(String[] args) {
        int level = 110;
        int tn = 100;
        tn += (level / 5 * 50);
        System.out.println(tn);
    }

    public void dropItemTask(Player player, Mob mob) {
        ItemMap itemMap = null;
        switch (mob.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(mob.zone, 73, 1, mob.location.x, mob.location.y, player.id);
                }
                break;
            case ConstMob.CABIRA:
            case ConstMob.TOBI:
                if (Util.isTrue(1, 10) && TaskService.gI().getIdTask(player) == ConstTask.TASK_32_2) {
                    itemMap = new ItemMap(mob.zone, 993, 1, mob.location.x, mob.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            Service.getInstance().dropItemMap(mob.zone, itemMap);
        }
    }

    public boolean isMonterFly(int tempId) {
        return tempId == ConstMob.THAN_LAN_BAY || tempId == ConstMob.PHI_LONG || tempId == ConstMob.QUY_BAY || tempId == ConstMob.THAN_LAN_ME || tempId == ConstMob.PHI_LONG_ME
                 || tempId == ConstMob.QUY_BAY_ME || tempId == ConstMob.ALIEN || tempId == ConstMob.TAMBOURINE || tempId == ConstMob.THAN_LAN_BAY_2
                 || tempId == ConstMob.PHI_LONG_2 || tempId == ConstMob.QUY_BAY_2 || tempId == ConstMob.KHONG_TAC || tempId == ConstMob.QUY_DAU_TO
                 || tempId == ConstMob.QUY_DIA_NGUC || tempId == ConstMob.ROBOT_BAY || tempId == ConstMob.THAN_LAN_XANH || tempId == ConstMob.DOI_DA_XANH
                 || tempId == ConstMob.QUY_CHIM || tempId == ConstMob.DA_XANH || tempId == ConstMob.ARBEE || tempId == ConstMob.TABURINE_DO || tempId == ConstMob.PHU_THUY;
    }
}
