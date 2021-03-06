package erogenousbeef.bigreactors.common.tileentity;

import erogenousbeef.bigreactors.api.HeatPulse;
import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.IRadiationModerator;
import erogenousbeef.bigreactors.api.IRadiationPulse;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.core.multiblock.MultiblockControllerBase;
import erogenousbeef.core.multiblock.MultiblockTileEntityBase;
import erogenousbeef.core.multiblock.MultiblockValidationException;

public class TileEntityReactorGlass extends MultiblockTileEntityBase implements IRadiationModerator, IHeatEntity {

	@Override
	public MultiblockControllerBase createNewMultiblock() {
		return new MultiblockReactor(this.worldObj);
	}
	
	@Override
	public Class<? extends MultiblockControllerBase> getMultiblockControllerType() { return MultiblockReactor.class; }

	@Override
	public void isGoodForFrame()  throws MultiblockValidationException {
		throw new MultiblockValidationException(String.format("%d, %d, %d - Reactor glass may only be used on the exterior faces, not as part of a reactor's frame or interior", xCoord, yCoord, zCoord));
	}

	@Override
	public void isGoodForSides() throws MultiblockValidationException {
	}

	@Override
	public void isGoodForTop() throws MultiblockValidationException {
	}

	@Override
	public void isGoodForBottom() throws MultiblockValidationException {
	}

	@Override
	public void isGoodForInterior() throws MultiblockValidationException {
		throw new MultiblockValidationException(String.format("%d, %d, %d - Reactor glass may only be used on the exterior faces, not as part of a reactor's frame or interior", xCoord, yCoord, zCoord));
	}

	@Override
	public void onMachineAssembled(MultiblockControllerBase multiblockControllerBase) {
	}

	@Override
	public void onMachineBroken() {
	}

	@Override
	public void onMachineActivated() {
	}

	@Override
	public void onMachineDeactivated() {
	}

	@Override
	public void receiveRadiationPulse(IRadiationPulse radiation) {
		float freePower = radiation.getSlowRadiation() * 0.25f;
		
		// Convert 25% of incident radiation to power, for balance reasons.
		radiation.addPower(freePower);
		
		// Slow radiation is all lost now
		radiation.setSlowRadiation(0);
		
		// And zero out the TTL so evaluation force-stops
		radiation.setTimeToLive(0);
	}

	@Override
	public float getHeat() {
		if(!this.isConnected()) { return 0f; }
		return ((MultiblockReactor)getMultiblockController()).getHeat();
	}

	@Override
	public float getThermalConductivity() {
		// Using iron so there's no disadvantage to reactor glass.
		return IHeatEntity.conductivityIron;
	}

	@Override
	public float onAbsorbHeat(IHeatEntity source, HeatPulse pulse, int faces, int contactArea) {
		float deltaTemp = source.getHeat() - getHeat();
		// If the source is cooler than the reactor, then do nothing
		if(deltaTemp <= 0.0f) {
			return 0.0f;
		}

		float heatToAbsorb = deltaTemp * getThermalConductivity() * (1.0f/(float)faces) * contactArea;

		pulse.heatChange += heatToAbsorb;

		return heatToAbsorb;
	}

	@Override
	public HeatPulse onRadiateHeat(float ambientHeat) {
		// Ignore, glass doesn't re-radiate heat
		return null;
	}
}
