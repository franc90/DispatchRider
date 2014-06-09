package dtp.commission;

import java.util.HashSet;
import java.util.Iterator;

public class CommissionsHandler {

    HashSet<CommissionHandler> commissions;

    int id;

    public CommissionsHandler() {

        commissions = new HashSet<CommissionHandler>();
        id = 0;
    }

    public void addCommissionHandler(CommissionHandler commissionHandler) {

        // update commission's ID
        commissionHandler.getCommission().setID(id);
        // add com handler to the set
        commissions.add(commissionHandler);
        id++;

        System.out.println("com added: incomingTime = " + commissionHandler.getIncomeTime());
        commissionHandler.getCommission().printCommision();
    }

    public void removeCommissionHandler(CommissionHandler comHandler) {

        commissions.remove(comHandler);
    }

    public CommissionHandler[] getCommissionsBeforeTime(int time) {

        CommissionHandler[] coms = new CommissionHandler[1000];
        CommissionHandler[] comsNew;
        CommissionHandler tempCom;
        Iterator<CommissionHandler> iter = commissions.iterator();
        int count = 0;

        while (iter.hasNext()) {

            tempCom = iter.next();
            if (tempCom.getIncomeTime() <= time) {

                coms[count] = tempCom;
                count++;
            }
        }

        comsNew = new CommissionHandler[count];
        for (int i = 0; i < count; i++)
            comsNew[i] = coms[i];

        return comsNew;
    }

    public CommissionHandler[] getCommissionHandlers() {

        CommissionHandler[] coms;

        coms = new CommissionHandler[commissions.size()];

        return commissions.toArray(coms);
    }

    public int getComsSize() {

        return commissions.size();
    }
}
