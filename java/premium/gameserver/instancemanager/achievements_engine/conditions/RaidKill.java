/*
 * package premium.gameserver.instancemanager.achievements_engine.conditions; import java.util.Map; import premium.gameserver.instancemanager.RaidBossPointsManager; import premium.gameserver.instancemanager.achievements_engine.base.Condition; import premium.gameserver.model.Player; public class RaidKill extends
 * Condition { public RaidKill(Object value) { super(value); setName("Raid Kill"); }
 * @Override public boolean meetConditionRequirements(Player player) { if (getValue() == null) { return false; } int val = Integer.parseInt(getValue().toString()); Map<Integer, Integer> list = RaidBossPointsManager.getList(player); if (list != null) { for (int bid : list.keySet()) { if (bid == val)
 * { if (RaidBossPointsManager.getList(player).get(bid) > 0) { return true; } } } } return false; } }
 */