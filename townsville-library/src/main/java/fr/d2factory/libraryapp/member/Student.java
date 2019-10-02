package fr.d2factory.libraryapp.member;

/**
 * 
 * @author Kais Belhareth
 *
 */
public class Student extends Member{

	private static final int daysBeforeLateStudent = 30;
	private static final float bookChargeBeforeLateStudent = 0.10f;
	private static final float ThirtydaysBookCharge = 3.0f;
	private static final float bookChargeAfterLateStudent = 0.15f;
	
    public Student() {
		super();		
	}
    
	public Student(String id, String firstName, String lastName, float wallet, boolean isLate) {
		super(id, firstName, lastName, wallet, isLate);	
	}
	
	public Student(String id, String firstName, String lastName) {
		super(id, firstName, lastName);	
	}

	@Override
	public void payBook(int numberOfDays) {
		float amount = 0;
		if (numberOfDays <= daysBeforeLateStudent) {
			for (int i = 1; i <= numberOfDays; i++) {
				amount += bookChargeBeforeLateStudent;
			}
		} else {
			amount += ThirtydaysBookCharge;
			for (int i = 1; i <= (numberOfDays - daysBeforeLateStudent); i++) {
				amount += bookChargeAfterLateStudent;
			}
		}
		if (this.getWallet() > amount) {
			this.setWallet(this.getWallet() - amount);
		}
	}
}
