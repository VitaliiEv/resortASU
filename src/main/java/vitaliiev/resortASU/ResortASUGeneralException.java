package vitaliiev.resortASU;

public class ResortASUGeneralException extends RuntimeException {
    public ResortASUGeneralException() {
    }

    public ResortASUGeneralException(String message) {
        super(message);
    }

    public ResortASUGeneralException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResortASUGeneralException(Throwable cause) {
        super(cause);
    }

    public ResortASUGeneralException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
