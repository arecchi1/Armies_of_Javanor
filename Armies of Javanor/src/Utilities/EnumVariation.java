package Utilities;

public final class EnumVariation {

	public static final int SAFE = -1;
	
	public enum TerrainType {PLAIN, FOREST, TRACK, MOUNTAIN, SWAMP, SEA, VILLAGE, TOWER, PORTAL, BURNINGFOREST, GROWINGPLAIN};
	public enum UnitID {NOMADCHIEFTAIN, NOMAD, BULL, CROCODILE, CHOCOBO, AVIANSIE, THIEF, RIFTGAZER,
		SPEARMAN, CLERIC, DRONE, ELF, CRUSADER, VALKYRIE, ARACHNE, FAERY, HERO, ORACLE,
		WEREWOLF, DEATHGUIDE, SKELETON, DARKKNIGHT, LILITH, SORCERER, TOMBWALKER, GARGOYLE, LICH, PYROPHIDIAN,
		GOBLIN, GOBLINPRIEST, DRACONIAN, HOBGOBLIN, DRACONIANLEGIONARY, DRACONIANSHAMAN, HOMUNCULUS, ORK, SEADEVIL, DRAGON};
	public enum UnitType {SKIRMISHER, HELPER, WARRIOR, RANGER, MAGE, FLIER, TITAN};
	public enum TileStatus {SMOKE, FEAR, CONFUSION, MORALE, AURA, INRANGE, BONES};
	public enum UnitStatus {SMOKE, FEAR, CONFUSION, MORALE, POISON};
	public enum God {ASSEMBLY, SWIFT, JAVA, PYTHON};
	public enum Phase {SELECT, MOVE, ATTACK};
	public enum Condition {FULL, HALF, DONE}; //Full can move, Half can attack, Empty is done
	public enum Ability {CAPTURE, HEALBURY, RAISE, FEAR, CONFUSION, SMOKE, ANTIAIR, ROT, FEARLESS, MORALE,
		PYROMANIAC, FORESTATION, OCEANDWELLER, FORESTDWELLER, FORESTPREDATOR, MORTFLESH, NECROPOLIS, BIOSYSTEM, VENOM};
	
}
