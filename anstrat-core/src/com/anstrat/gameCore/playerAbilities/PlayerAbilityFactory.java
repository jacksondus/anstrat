package com.anstrat.gameCore.playerAbilities;

import com.anstrat.gameCore.Player;

public class PlayerAbilityFactory {

	public static PlayerAbility createAbility(PlayerAbilityType type, Player player){
		if (type.equals(PlayerAbilityType.THUNDERBOLT))
			return new Thunderbolt(player);
		else if (type.equals(PlayerAbilityType.GLASS_CANNON))
			return new GlassCannon(player);
		else if (type.equals(PlayerAbilityType.COMETSTRIKE))
			return new CometStrike(player);
		else if (type.equals(PlayerAbilityType.SWAP))
			return new Swap(player);
		else if (type.equals(PlayerAbilityType.ZOMBIFY))
			return new Zombify(player);
		else if (type.equals(PlayerAbilityType.THORS_RAGE))
			return new ThorsRage(player);
		else if (type.equals(PlayerAbilityType.ODINS_BLESSING))
			return new OdinsBlessing(player);
		else if (type.equals(PlayerAbilityType.HELS_CURSE))
			return new HelsCurse(player);
		else if (type.equals(PlayerAbilityType.FREEZE))
			return new Freeze(player);
		else if (type.equals(PlayerAbilityType.CONFUSION))
			return new Confusion(player);
		else if (type.equals(PlayerAbilityType.HUGIN_AND_MUNIN))
			return new HuginAndMunin(player);
		else if (type.equals(PlayerAbilityType.UNCLE_LOKI))
			return new UncleLokiWantsYou(player);
		else
			return null;
	}
}
