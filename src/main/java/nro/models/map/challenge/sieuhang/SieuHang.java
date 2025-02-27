package nro.models.map.challenge.sieuhang;

import java.sql.SQLException;
import nro.consts.ConstPlayer;
import nro.jdbc.daos.GodGK;
import nro.jdbc.daos.PlayerDAO;
import nro.models.boss.Boss;
import nro.models.boss.dhvt.BossDHVT;
import nro.models.player.Player;
import nro.server.Client;
import nro.services.EffectSkillService;
import nro.services.ItemTimeService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;
import lombok.Getter;
import lombok.Setter;
import nro.jdbc.DBService;

/**
 * @author Béo Mập :3
 */
public class SieuHang {
//    @Setter
//    @Getter
    private Player player;
    //    @Setter
    private Boss boss;

    //    @Setter
    private int time;
    //    @Setter
    private int timeWait;

    public void update() {
        try {
        if (time > 0) {
            time--;
            if (player.isDie()) {
                die();
                return;
            }
            if (player.location != null && !player.isDie() && player != null && player.zone != null) {
                if (boss.isDie()) {
                    endChallenge();
                    if (player.rankSieuHang == boss.rankSieuHang) {
                        player.rankSieuHang--;
                    } else if (player.rankSieuHang > boss.rankSieuHang) {
                        int temp = player.rankSieuHang;
                        player.rankSieuHang = boss.rankSieuHang;
                        BossDHVT bossDHVT = (BossDHVT) boss;
                        Player player1 = Client.gI().getPlayer(bossDHVT.idPlayer);
                        if (player1 == null) {
                            player1 = GodGK.loadPlayerbyId(bossDHVT.idPlayer);
                        }
//                        player1.rankSieuHang = temp;
                        PlayerDAO.subUpdateSieuHang(player1, temp);
                    }
                    Service.getInstance().chat(player, "Haha thắng cuộc rồi! Đã thăng lên hạng " + player.rankSieuHang);
                    PlayerDAO.updatePlayer(player, DBService.gI().getConnectionForGame());
                    boss.leaveMap();
                }
                if (player.location.y > 264) {
                    leave();
                }
            } else {
                if (boss != null) {
                    boss.leaveMap();
                }
                SieuHangManager.gI().remove(this);
            }
        } else {
            timeOut();
        }
        if (timeWait > 0) {
            switch (timeWait) {
                case 5:
                    Service.getInstance().chat(boss, "Sẵn sàng chưa");
                    ready();
                    break;
                case 1:
                    Service.getInstance().chat(player, "Ok");
                    break;
            }
            timeWait--;
        }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void ready() {
        EffectSkillService.gI().startStun(boss, System.currentTimeMillis(), 5000);
        EffectSkillService.gI().startStun(player, System.currentTimeMillis(), 5000);
        ItemTimeService.gI().sendItemTime(player, 3779, 5);
        Util.setTimeout(() -> {
            if (boss.effectSkill != null) {
                EffectSkillService.gI().removeStun(boss);
            }
            SieuHangService.gI().sendTypePK(player, boss);
            PlayerService.gI().changeAndSendTypePK(this.player, ConstPlayer.PK_PVP);
            boss.changeStatus(Boss.ATTACK);
        }, 5000);
    }

    public void toTheNextRound(Boss bss) {
        try {
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
            SieuHangService.gI().moveFast(player, 335, 264);
            Service.getInstance().hsChar(player, player.nPoint.hpMax, player.nPoint.mpMax);
            setTimeWait(6);
            setBoss(bss);
            setTime(185);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Boss getBoss() {
        return boss;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTimeWait() {
        return timeWait;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    private void die() {
        Service.getInstance().sendThongBao(player, "Thất bại rồi nhục nhã quá");
        if (player.zone != null) {
            endChallenge();
        }
    }

    private void timeOut() {
        Service.getInstance().sendThongBao(player, "Bạn bị xử thua vì hết thời gian");
        endChallenge();
    }

    public void leave() {
        setTime(0);
        EffectSkillService.gI().removeStun(player);
        Service.getInstance().sendThongBao(player, "Bạn bị xử thua vì rời khỏi võ đài");
        endChallenge();
    }

    public void endChallenge() {
        if (player.zone != null) {
            PlayerService.gI().hoiSinh(player);
        }
        PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
        if (player != null && player.zone != null && player.zone.map.mapId == 113) {
            Util.setTimeout(() -> {
                ChangeMapService.gI().changeMapNonSpaceship(player, 113, player.location.x, 360);
            }, 500);
        }
        if (boss != null) {
            boss.leaveMap();
        }
        SieuHangManager.gI().remove(this);
    }
}
