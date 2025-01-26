package nro.models.boss.event;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import nro.consts.ConstItem;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.FutureBoss;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.Service;
import nro.utils.Util;

/**
 * @author 💖 Nothing 💖
 */
public class ThanTai extends FutureBoss {

    public ThanTai() {
        super(BossFactory.THAN_TAI_TD, BossData.THAN_TAI_TD);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        int tempId = -1;

        // Kiểm tra Player
        if (pl == null) {
            System.err.println("Player is null. Skipping rewards.");
            return;
        }

        // Kiểm tra Zone
        if (this.zone == null) {
            System.err.println("Zone is null. Cannot drop items.");
            return;
        }

        if (Manager.EVENT_SEVER == 4) {
            // Xác suất 1/30 để lấy vật phẩm từ LIST_ITEM_NLSK_TET_2023
            if (Util.isTrue(1, 30)) {
                tempId = ConstItem.LIST_ITEM_NLSK_TET_2023[
                        Util.nextInt(0, ConstItem.LIST_ITEM_NLSK_TET_2023.length - 1)
                        ];

                // Tạo vật phẩm và không thêm chỉ số
                ItemMap itemMap = new ItemMap(
                        this.zone, tempId, 1,
                        pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id
                );
                Service.getInstance().dropItemMap(this.zone, itemMap);
                return; // Kết thúc phần thưởng nếu vật phẩm thuộc LIST_ITEM_NLSK_TET_2023
            }
        }

        // Phần còn lại: Phần thưởng sự kiện Tết 2025
        int[] tempSKTET1 = new int[]{1489, 1490, 1491}; // Đầu lân
        int[] tempSKTET2 = new int[]{1494, 1495, 1496}; // Du xuân
        int[] tempSKTET3 = new int[]{1499, 1500, 1501}; // Thần tài

        tempId = getTempId(tempSKTET1, tempSKTET2, tempSKTET3);

        if (tempId != -1) {
            ItemMap itemMap = new ItemMap(
                    this.zone, tempId, 1,
                    pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id
            );

            // Thêm chỉ số cho các vật phẩm không thuộc LIST_ITEM_NLSK_TET_2023
            if (Util.isTrue(90, 100)) {
                itemMap.options.add(new ItemOption(93, Util.nextInt(1, 30)));
            }

            int maxValue = getMaxValue(tempId);
            itemMap.options.add(new ItemOption(50, Util.nextInt(1, maxValue)));
            itemMap.options.add(new ItemOption(77, Util.nextInt(1, maxValue)));
            itemMap.options.add(new ItemOption(103, Util.nextInt(1, maxValue)));
            itemMap.options.add(new ItemOption(30, Util.nextInt(1)));

            Service.getInstance().dropItemMap(this.zone, itemMap);
        }

        generalRewards(pl);
    }

    // Hàm lấy tempId dựa trên xác suất
    private int getTempId(int[] tempSKTET1, int[] tempSKTET2, int[] tempSKTET3) {
        if (Util.isTrue(1, 30)) {
            return tempSKTET1[Util.nextInt(0, tempSKTET1.length - 1)];
        } else if (Util.isTrue(1, 100)) {
            return tempSKTET2[Util.nextInt(0, tempSKTET2.length - 1)];
        } else if (Util.isTrue(1, 200)) {
            return tempSKTET3[Util.nextInt(0, tempSKTET3.length - 1)];
        }
        return -1; // Không có tempId hợp lệ
    }

    // Hàm lấy giá trị tối đa cho các tùy chọn dựa trên nhóm tempId
    private int getMaxValue(int tempId) {
        if (tempId == 1494 || tempId == 1495 || tempId == 1496) {
            return 70;
        } else if (tempId == 1499 || tempId == 1500 || tempId == 1501) {
            return 80;
        }
        return 50; // Giá trị mặc định
    }


    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        textTalkMidle = new String[]{"Chúc mừng năm mới 2025"};
        textTalkMidle = new String[]{"Chúc các bạn chơi game vui vẻ !!!"};
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        setJustRest();
    }

}
