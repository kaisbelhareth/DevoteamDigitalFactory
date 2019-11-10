package fr.d2factory.libraryapp.member;

/**
 * 
 * @author Kais Belhareth
 *
 */
public class Resident extends Member{

	private static final int daysBeforeLateResident = 60;
	private static final float bookChargeBeforeLateResident = 0.10f;
	private static final float SixtydaysBookCharge = 6.0f;
	private static final float bookChargeAfterLateResident = 0.20f;
	
    public Resident() {
		super();		
	}
    
	public Resident(String id, String firstName, String lastName, float wallet, boolean isLate) {
		super(id, firstName, lastName, wallet, isLate);	
	}
	
	public Resident(String id, String firstName, String lastName) {
		super(id, firstName, lastName);
	}

	@Override
	public void payBook(int numberOfDays) {
		float amount = 0;
		if (numberOfDays <= daysBeforeLateResident) {
			for (int i = 1; i <= numberOfDays; i++) {
				amount += bookChargeBeforeLateResident;
			}
		} else {
				amount = SixtydaysBookCharge;
			for (int i = 1; i <= (numberOfDays - daysBeforeLateResident); i++) {
				amount += bookChargeAfterLateResident;
			}			
		}
		if (this.getWallet() >= amount) {
			this.setWallet(this.getWallet() - amount);
		}
		else {
			this.setDebt(amount - this.getWallet());
			this.setWallet(0);
		}		
	}
}
