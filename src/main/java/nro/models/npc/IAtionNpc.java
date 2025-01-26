package nro.models.npc;

import nro.models.player.Player;

/**
 *
 * @author Văn Tuấn - 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public interface IAtionNpc {

    void openBaseMenu(Player player);

    void confirmMenu(Player player, int select);

}
