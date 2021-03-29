package synchronizer;

import environment.MailBox;

@SuppressWarnings("FieldCanBeLocal")
public class PersonalSynchronizer implements Synchronizer {

    private final int myId;
    private final MailBox inbox;
    private SyncElement[] syncSet;

    private boolean ini = false;
    private boolean reqR = false;
    private boolean reqS = false;
    private boolean ackS = false;
    private boolean ackR = false;
    private boolean comR = false;
    private boolean comS = false;
    private boolean sync = false;

    PersonalSynchronizer(int id) {
        myId = id;
        inbox = new MailBox();
    }

    public int[] getSyncSet(int id) {
        return new int[0];
    }

    public void synchronize(int agent_id, int[] setOfCandidates, int time) {}

    private String getState() {
        if (syncSet.length == 0) {
            return "sync";
        }
        String state = "";
        ini = false;
        reqR = false;
        reqS = false;
        ackR = false;
        ackS = false;
        comR = false;
        comS = false;
        sync = false;
        for (SyncElement syncElement : syncSet) {
            switch (syncElement.getState()) {
                case "ini":
                    ini = true;
                    break;
                case "reqR":
                    reqR = true;
                    break;
                case "reqS":
                    reqS = true;
                    break;
                case "ackR":
                    ackR = true;
                    break;
                case "ackS":
                    ackS = true;
                    break;
                case "comR":
                    comR = true;
                    break;
                case "comS":
                    comS = true;
                    break;
                case "sync":
                    sync = true;
                    break;
            }
        }
        if (ini) {
            state = "ini";
        } else if (reqR) {  // !ini
            state = "reqR";
        } else if (reqS) {  // !ini & !reqR
            state = "reqS";
        } else if (ackR) {  //!ini & !reqR & !reqS
            state = "ackR";
        } else if (ackS) {  // !ini & !reqR & !reqS & !ackR
            state = "ackS";
        } else if (comR) {  // !ini & !reqR & !reqS & !ackR & !ackS
            state = "comR";
        } else if (comS) {  // !ini & !reqR & !reqS & !ackR & !ackS & !comR
            state = "comS";
        } else if (sync) {  // !ini & !reqR & !reqS & !ackR & !ackS & !comR & !comS
            state = "sync";
        }
        return state;
    }

    /* private int agent;
       private int[] sS;
       private String name;
       private MailBox inbox;
       private SyncElement[] syncSet;
       private int syncTime;
       private boolean restart;
       private boolean ini = false;
       private boolean reqR = false;
       private boolean reqS = false;
       private boolean ackS = false;
       private boolean ackR = false;
       private boolean comR = false;
       private boolean comS = false;
       private boolean sync = false;
       public PersonalSynchronizer(int agent) {
       this.agent = agent;
       syncTime = 0;
       }
       public int[] getSyncSet(int agent) {
       return sS;
       }
       public void synchronize(int agent, int[] synchroCandidates, int time) {
       initialize(synchroCandidates);
       procede();
       }
       private void initialize(int[] percSet) {
       syncSet = new SyncElement[percSet.length];
       for (int i = 0; i < percSet.length; i++) {
       syncSet[i] = new SyncElement(percSet[i]);
       }
       }
      private void procede() {
      while (restart | !synchronizationCompleted()) {
      handleMail();
      sendRequests();
      if (blockedToCommit()) {
      unblockCommits();
      }
      sendCommits();
      if (readyToSendSyncs()) {
      sendSyncs();
      }
      if (restart)
      restart = false;
      }
      }
      private String getState() {
      if (syncSet.length == 0) {
      return "sync";
      }
      String state = "";
      ini = false;
      reqR = false;
      reqS = false;
      ackR = false;
      ackS = false;
      comR = false;
      comS = false;
      sync = false;
      for (int i = 0; i < syncSet.length; i++)
      if (syncSet[i].getState().equals("ini"))
      ini = true;
      else if (syncSet[i].getState().equals("reqR"))
      reqR = true;
      else if (syncSet[i].getState().equals("reqS"))
      reqS = true;
      else if (syncSet[i].getState().equals("ackR"))
      ackR = true;
      else if (syncSet[i].getState().equals("ackS"))
      ackS = true;
      else if (syncSet[i].getState().equals("comR"))
      comR = true;
      else if (syncSet[i].getState().equals("comS"))
      comS = true;
      else if (syncSet[i].getState().equals("sync"))
      sync = true;
      if (ini)
      state = "ini";
      else if (!ini & reqR)
      state = "reqR";
       else if ((!ini & !reqR) & reqS)
       state = "reqS";
       else if (((!ini & !reqR) & !reqS) & ackR)
       state = "ackR";
       else if ((((!ini & !reqR) & !reqS) & !ackR) & ackS)
        state = "ackS";
       else if (((((!ini & !reqR) & !reqS) & !ackR) & !ackS) & comR)
        state = "comR";
       else if ((((((!ini & !reqR) & !reqS) & !ackR) & !ackS) & !comR) & comS)
        state = "comS";
       else if (
        ((((((!ini & !reqR) & !reqS) & !ackR) & !ackS) & !comR) & !comS)
         & sync)
        state = "sync";
       return state;
      }*/

}
