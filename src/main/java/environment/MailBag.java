package environment;

/**
 *A class facilitating communication between the Spheres and the PostalService.
 */
public class MailBag extends ToHandle {

    private Mail[] mailset;

    public MailBag(Sphere sender) {
        super(sender);
        mailset = new Mail[0];
    }

    public Mail[] getMailSet() {
        return mailset;
    }

    public void putInBag(Mail mail) {
        Mail[] temp = new Mail[mailset.length + 1];
        System.arraycopy(mailset, 0, temp, 0, mailset.length);
        temp[mailset.length] = mail;
        mailset = temp;
    }
}
