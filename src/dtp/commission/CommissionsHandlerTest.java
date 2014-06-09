package dtp.commission;

public class CommissionsHandlerTest {

	public static void main(String[] args) {

		CommissionsHandler comsHandler = new CommissionsHandler();

		Commission com0 = new Commission(0, 0, 0, 0, 10, 5, 5, 20, 30, 50, 90,
				90);
		CommissionHandler comH0 = new CommissionHandler(com0, 0);

		Commission com1 = new Commission(1, 0, 0, 0, 10, 5, 5, 20, 30, 60, 90,
				90);
		CommissionHandler comH1 = new CommissionHandler(com1, 1);

		Commission com2 = new Commission(2, 0, 0, 0, 10, 5, 5, 20, 30, 70, 90,
				90);
		CommissionHandler comH2 = new CommissionHandler(com2, 2);

		comsHandler.addCommissionHandler(comH0);
		comsHandler.addCommissionHandler(comH1);
		comsHandler.addCommissionHandler(comH2);

		CommissionHandler[] comHandler;
		comHandler = comsHandler.getCommissionsBeforeTime(1);

		for (int i = 0; i < comHandler.length; i++) {

			System.out.println("time = " + comHandler[i].getIncomeTime());
			comHandler[i].getCommission().printCommision();
		}
	}

}
