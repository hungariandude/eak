package webshop.common;

public class WebshopServerException extends Exception {

    private static final long serialVersionUID = 1L;

    public WebshopServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WebshopServerException(final String message) {
        super(message);
    }

    public WebshopServerException(final Throwable cause) {
        super(cause);
    }

}
