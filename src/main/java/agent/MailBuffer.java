package agent;

import environment.Mail;

/**
 * This is a cloneable container for Mail
 */
public class MailBuffer implements Cloneable {

    private Mail[] buffer;

    /**
     * Creates a new MailBuffer
     */
    public MailBuffer() {
        buffer = new Mail[0];
    }

    /**
     * Adds a new mail to the buffer
     */
    public void addMail(Mail mail) {
        Mail[] temp = new Mail[buffer.length + 1];
        System.arraycopy(buffer, 0, temp, 0, buffer.length);
        temp[buffer.length] = mail;
        buffer = temp;
    }

    /**
     * Returns the mails in an array
     */
    public Mail[] getMails() {
        return buffer;
    }

    /**
     * Removes all mails from this buffer
     */
    public void clear() {
        buffer = new Mail[0];
    }

    /**
     * Clones this MailBuffer
     */
    public Object clone() {
        MailBuffer mbuf = null;
        try {
            mbuf = (MailBuffer)super.clone();
        } catch (CloneNotSupportedException exc) {
            System.err.println("Woeps: illegal cloning");
            System.err.println("at MailBuffer.clone() ");
        }
        mbuf.buffer = new Mail[buffer.length];
        System.arraycopy(buffer, 0, mbuf.buffer, 0, buffer.length);
        return mbuf;
    }
}
