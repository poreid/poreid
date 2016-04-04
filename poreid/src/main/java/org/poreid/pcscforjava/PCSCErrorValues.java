/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.poreid.pcscforjava;

/**
 *
 * @author mleromain
 */

public class PCSCErrorValues 
{
    // PCSC success / error / failure / warning codes
    /**
     * 
     */
    public final static int SCARD_S_SUCCESS             = 0x00000000;
    /**
     * 
     */
    public final static int SCARD_F_INTERNAL_ERROR      = 0x80100001;
    /**
     * 
     */
    public final static int SCARD_E_CANCELLED           = 0x80100002;
    /**
     * 
     */
    public final static int SCARD_E_INVALID_HANDLE      = 0x80100003;
    /**
     * 
     */
    public final static int SCARD_E_INVALID_PARAMETER   = 0x80100004;
    /**
     * 
     */
    public final static int SCARD_E_INVALID_TARGET      = 0x80100005;
    /**
     * 
     */
    public final static int SCARD_E_NO_MEMORY           = 0x80100006;
    /**
     * 
     */
    public final static int SCARD_F_WAITED_TOO_LONG     = 0x80100007;
    /**
     * 
     */
    public final static int SCARD_E_INSUFFICIENT_BUFFER = 0x80100008;
    /**
     * 
     */
    public final static int SCARD_E_UNKNOWN_READER      = 0x80100009;
    /**
     * 
     */
    public final static int SCARD_E_TIMEOUT             = 0x8010000A;
    /**
     * 
     */
    public final static int SCARD_E_SHARING_VIOLATION   = 0x8010000B;
    /**
     * 
     */
    public final static int SCARD_E_NO_SMARTCARD        = 0x8010000C;
    /**
     * 
     */
    public final static int SCARD_E_UNKNOWN_CARD        = 0x8010000D;
    /**
     * 
     */
    public final static int SCARD_E_CANT_DISPOSE        = 0x8010000E;
    /**
     * 
     */
    public final static int SCARD_E_PROTO_MISMATCH      = 0x8010000F;
    /**
     * 
     */
    public final static int SCARD_E_NOT_READY           = 0x80100010;
    /**
     * 
     */
    public final static int SCARD_E_INVALID_VALUE       = 0x80100011;
    /**
     * 
     */
    public final static int SCARD_E_SYSTEM_CANCELLED    = 0x80100012;
    /**
     * 
     */
    public final static int SCARD_F_COMM_ERROR          = 0x80100013;
    /**
     * 
     */
    public final static int SCARD_F_UNKNOWN_ERROR       = 0x80100014;
    /**
     * 
     */
    public final static int SCARD_E_INVALID_ATR         = 0x80100015;
    /**
     * 
     */
    public final static int SCARD_E_NOT_TRANSACTED      = 0x80100016;
    /**
     * 
     */
    public final static int SCARD_E_READER_UNAVAILABLE  = 0x80100017;
    /**
     * 
     */
    public final static int SCARD_P_SHUTDOWN            = 0x80100018;
    /**
     * 
     */
    public final static int SCARD_E_PCI_TOO_SMALL       = 0x80100019;
    /**
     * 
     */
    public final static int SCARD_E_READER_UNSUPPORTED  = 0x8010001A;
    /**
     * 
     */
    public final static int SCARD_E_DUPLICATE_READER    = 0x8010001B;
    /**
     * 
     */
    public final static int SCARD_E_CARD_UNSUPPORTED    = 0x8010001C;
    /**
     * 
     */
    public final static int SCARD_E_NO_SERVICE          = 0x8010001D;
    /**
     * 
     */
    public final static int SCARD_E_SERVICE_STOPPED     = 0x8010001E;
    /**
     * 
     */
    public final static int SCARD_E_UNEXPECTED          = 0x8010001F;
    /**
     * 
     */
    public final static int SCARD_E_ICC_INSTALLATION    = 0x80100020;
    /**
     * 
     */
    public final static int SCARD_E_ICC_CREATEORDER     = 0x80100021;
    /**
     * 
     */
    public final static int SCARD_E_UNSUPPORTED_FEATURE = 0x80100022;
    /**
     * 
     */
    public final static int SCARD_E_DIR_NOT_FOUND       = 0x80100023;
    /**
     * 
     */
    public final static int SCARD_E_FILE_NOT_FOUND      = 0x80100024;
    /**
     * 
     */
    public final static int SCARD_E_NO_DIR              = 0x80100025;
    /**
     * 
     */
    public final static int SCARD_E_NO_FILE             = 0x80100026;
    /**
     * 
     */
    public final static int SCARD_E_NO_ACCESS           = 0x80100027;
    /**
     * 
     */
    public final static int SCARD_E_WRITE_TOO_MANY      = 0x80100028;
    /**
     * 
     */
    public final static int SCARD_E_BAD_SEEK            = 0x80100029;
    /**
     * 
     */
    public final static int SCARD_E_INVALID_CHV         = 0x8010002A;
    /**
     * 
     */
    public final static int SCARD_E_UNKNOWN_RES_MNG     = 0x8010002B;
    /**
     * 
     */
    public final static int SCARD_E_NO_SUCH_CERTIFICATE = 0x8010002C;
    /**
     * 
     */
    public final static int SCARD_E_CERTIFICATE_UNAVAILABLE = 0x8010002D;
    /**
     * 
     */
    public final static int SCARD_E_NO_READERS_AVAILABLE = 0x8010002E;
    /**
     * 
     */
    public final static int SCARD_E_COMM_DATA_LOST      = 0x8010002F;
    /**
     * 
     */
    public final static int SCARD_E_NO_KEY_CONTAINER    = 0x80100030;
    /**
     * 
     */
    public final static int SCARD_E_SERVER_TOO_BUSY     = 0x80100031;
    /**
     * 
     */
    public final static int SCARD_W_UNSUPPORTED_CARD    = 0x80100065;
    /**
     * 
     */
    public final static int SCARD_W_UNRESPONSIVE_CARD   = 0x80100066;
    /**
     * 
     */
    public final static int SCARD_W_UNPOWERED_CARD      = 0x80100067;
    /**
     * 
     */
    public final static int SCARD_W_RESET_CARD          = 0x80100068;
    /**
     * 
     */
    public final static int SCARD_W_REMOVED_CARD        = 0x80100069;
    /**
     * 
     */
    public final static int SCARD_W_SECURITY_VIOLATION  = 0x8010006A;
    /**
     * 
     */
    public final static int SCARD_W_WRONG_CHV           = 0x8010006B;
    /**
     * 
     */
    public final static int SCARD_W_CHV_BLOCKED         = 0x8010006C;
    /**
     * 
     */
    public final static int SCARD_W_EOF                 = 0x8010006D;
    /**
     * 
     */
    public final static int SCARD_W_CANCELLED_BY_USER   = 0x8010006E;
    /**
     * 
     */
    public final static int SCARD_W_CARD_NOT_AUTHENTICATED = 0x8010006F;
    
    // std. Windows invalid handle return code, used instead of SCARD code
    /**
     * 
     */
    public final static int WINDOWS_ERROR_INVALID_HANDLE = 6;
    /**
     * 
     */
    public final static int WINDOWS_ERROR_INVALID_PARAMETER = 87;
    /**
     * 
     */
    public final static int ERROR_IO_DEVICE             = 0x0000045D;
    // end std. Windows
        
    /**
     * Returns a translation of a PCSC exception integer code to PCSC exception 
     * string.
     * @param code the PCSC exception integer code.
     * @return A translation of a PCSC exception integer code to PCSC exception 
     * string.
     */
    public static String toErrorString(int code) {
        switch (code) 
        {
            case PCSCErrorValues.SCARD_S_SUCCESS: 
                return "SCARD_S_SUCCESS";
                
            case PCSCErrorValues.SCARD_F_INTERNAL_ERROR: 
                return "SCARD_F_INTERNAL_ERROR";
                
            case PCSCErrorValues.SCARD_E_CANCELLED: 
                return "SCARD_E_CANCELLED";
                
            case PCSCErrorValues.SCARD_E_INVALID_HANDLE:
                return "SCARD_E_INVALID_HANDLE";

            case PCSCErrorValues.SCARD_E_INVALID_PARAMETER:
                return "SCARD_E_INVALID_PARAMETER";

            case PCSCErrorValues.SCARD_E_INVALID_TARGET:
			return "SCARD_E_INVALID_TARGET";
    
            case PCSCErrorValues.SCARD_E_NO_MEMORY:
		return "SCARD_E_NO_MEMORY";

            case PCSCErrorValues.SCARD_F_WAITED_TOO_LONG:
		return "SCARD_F_WAITED_TOO_LONG";

            case PCSCErrorValues.SCARD_E_INSUFFICIENT_BUFFER:
		return "SCARD_E_INSUFFICIENT_BUFFER";

            case PCSCErrorValues.SCARD_E_UNKNOWN_READER:
		return "SCARD_E_UNKNOWN_READER";
        
            case PCSCErrorValues.SCARD_E_TIMEOUT:
		return "SCARD_E_TIMEOUT";

            case PCSCErrorValues.SCARD_E_SHARING_VIOLATION:
		return "SCARD_E_SHARING_VIOLATION";
    
            case PCSCErrorValues.SCARD_E_NO_SMARTCARD:
		return "SCARD_E_NO_SMARTCARD";

            case PCSCErrorValues.SCARD_E_UNKNOWN_CARD:
		return "SCARD_E_UNKNOWN_CARD";
  
            case PCSCErrorValues.SCARD_E_CANT_DISPOSE:
		return "SCARD_E_CANT_DISPOSE";

            case PCSCErrorValues.SCARD_E_PROTO_MISMATCH:
		return "SCARD_E_PROTO_MISMATCH";

            case PCSCErrorValues.SCARD_E_NOT_READY:
		return "SCARD_E_NOT_READY";

            case PCSCErrorValues.SCARD_E_INVALID_VALUE:
		return "SCARD_E_INVALID_VALUE";

            case PCSCErrorValues.SCARD_E_SYSTEM_CANCELLED:
		return "SCARD_E_SYSTEM_CANCELLED";
    
            case PCSCErrorValues.SCARD_F_COMM_ERROR:
		return "SCARD_F_COMM_ERROR";

            case PCSCErrorValues.SCARD_F_UNKNOWN_ERROR:
		return "SCARD_F_UNKNOWN_ERROR";

            case PCSCErrorValues.SCARD_E_INVALID_ATR:
		return "SCARD_E_INVALID_ATR";

            case PCSCErrorValues.SCARD_E_NOT_TRANSACTED:
		return "SCARD_E_NOT_TRANSACTED";

            case PCSCErrorValues.SCARD_E_READER_UNAVAILABLE:
		return "SCARD_E_READER_UNAVAILABLE";

            case PCSCErrorValues.SCARD_P_SHUTDOWN:
		return "SCARD_P_SHUTDOWN";

            case PCSCErrorValues.SCARD_E_PCI_TOO_SMALL:
		return "SCARD_E_PCI_TOO_SMALL";

            case PCSCErrorValues.SCARD_E_READER_UNSUPPORTED:
		return "SCARD_E_READER_UNSUPPORTED";

            case PCSCErrorValues.SCARD_E_DUPLICATE_READER:
		return "SCARD_E_DUPLICATE_READER";

            case PCSCErrorValues.SCARD_E_CARD_UNSUPPORTED:
		return "SCARD_E_CARD_UNSUPPORTED";

            case PCSCErrorValues.SCARD_E_NO_SERVICE:
		return "SCARD_E_NO_SERVICE";

            case PCSCErrorValues.SCARD_E_SERVICE_STOPPED:
		return "SCARD_E_SERVICE_STOPPED";

            case PCSCErrorValues.SCARD_E_UNEXPECTED: 
                if(System.getProperty("os.name").contains("Windows"))
                    return "SCARD_E_UNEXPECTED";
                else
                    return "SCARD_E_UNSUPPORTED_FEATURE";

            case PCSCErrorValues.SCARD_E_ICC_INSTALLATION:
		return "SCARD_E_ICC_INSTALLATION";

            case PCSCErrorValues.SCARD_E_ICC_CREATEORDER:
		return "SCARD_E_ICC_CREATEORDER";

            case PCSCErrorValues.SCARD_E_UNSUPPORTED_FEATURE:
		return "SCARD_E_UNSUPPORTED_FEATURE";

            case PCSCErrorValues.SCARD_E_DIR_NOT_FOUND:
		return "SCARD_E_DIR_NOT_FOUND";

            case PCSCErrorValues.SCARD_E_FILE_NOT_FOUND:
		return "SCARD_E_FILE_NOT_FOUND";

            case PCSCErrorValues.SCARD_E_NO_DIR:
		return "SCARD_E_NO_DIR";

            case PCSCErrorValues.SCARD_E_NO_FILE:
		return "SCARD_E_NO_FILE";

            case PCSCErrorValues.SCARD_E_NO_ACCESS:
		return "SCARD_E_NO_ACCESS";

            case PCSCErrorValues.SCARD_E_WRITE_TOO_MANY:
		return "SCARD_E_WRITE_TOO_MANY";

            case PCSCErrorValues.SCARD_E_BAD_SEEK:
		return "SCARD_E_BAD_SEEK";

            case PCSCErrorValues.SCARD_E_INVALID_CHV:
		return "SCARD_E_INVALID_CHV";

            case PCSCErrorValues.SCARD_E_UNKNOWN_RES_MNG:
		return "SCARD_E_UNKNOWN_RES_MNG";

            case PCSCErrorValues.SCARD_E_NO_SUCH_CERTIFICATE:
		return "SCARD_E_NO_SUCH_CERTIFICATE";

            case PCSCErrorValues.SCARD_E_CERTIFICATE_UNAVAILABLE:
		return "SCARD_E_CERTIFICATE_UNAVAILABLE";

            case PCSCErrorValues.SCARD_E_NO_READERS_AVAILABLE:
		return "SCARD_E_NO_READERS_AVAILABLE";

            case PCSCErrorValues.SCARD_E_COMM_DATA_LOST:
		return "SCARD_E_COMM_DATA_LOST";

            case PCSCErrorValues.SCARD_E_NO_KEY_CONTAINER:
		return "SCARD_E_NO_KEY_CONTAINER";

            case PCSCErrorValues.SCARD_E_SERVER_TOO_BUSY:
		return "SCARD_E_SERVER_TOO_BUSY";
    
            case PCSCErrorValues.SCARD_W_UNSUPPORTED_CARD:
		return "SCARD_W_UNSUPPORTED_CARD";

            case PCSCErrorValues.SCARD_W_UNRESPONSIVE_CARD:
		return "SCARD_W_UNRESPONSIVE_CARD";

            case PCSCErrorValues.SCARD_W_UNPOWERED_CARD:
		return "SCARD_W_UNPOWERED_CARD";

            case PCSCErrorValues.SCARD_W_RESET_CARD:
		return "SCARD_W_RESET_CARD";

            case PCSCErrorValues.SCARD_W_REMOVED_CARD:
		return "SCARD_W_REMOVED_CARD";

            case PCSCErrorValues.SCARD_W_SECURITY_VIOLATION:
		return "SCARD_W_SECURITY_VIOLATION";

            case PCSCErrorValues.SCARD_W_WRONG_CHV:
		return "SCARD_W_WRONG_CHV";

            case PCSCErrorValues.SCARD_W_CHV_BLOCKED:
		return "SCARD_W_CHV_BLOCKED";

            case PCSCErrorValues.SCARD_W_EOF:
		return "SCARD_W_EOF";

            case PCSCErrorValues.SCARD_W_CANCELLED_BY_USER:
		return "SCARD_W_CANCELLED_BY_USER";

            case PCSCErrorValues.SCARD_W_CARD_NOT_AUTHENTICATED:
		return "SCARD_W_CARD_NOT_AUTHENTICATED";

            case PCSCErrorValues.WINDOWS_ERROR_INVALID_HANDLE: 
                return "WINDOWS_ERROR_INVALID_HANDLE";

            case PCSCErrorValues.WINDOWS_ERROR_INVALID_PARAMETER: 
                return "WINDOWS_ERROR_INVALID_PARAMETER";

            case PCSCErrorValues.ERROR_IO_DEVICE:
		return "ERROR_IO_DEVICE";

            default: 
                return "SCARD_F_UNKNOWN_ERROR Precise unknown error code = 0x" + 
                    Integer.toHexString(code);
        }
    }
    
    /**
     * Returns the detail of a PCSC exception from a PCSC code.
     * @param iCode the PCSC code.
     * @return the detail of the PCSC exception defined by the PCSC code.
     */
    public static String getPcscExceptionDetail(int iCode)
    {
        return getPcscExceptionDetail(toErrorString(iCode));
    }
    
    /**
     * Returns the detail of a PCSC exception from a PCSC string code.
     * @param sCode the PCSC string code.
     * @return the detail of the PCSC exception defined by the PCSC string code.
     */
    public static String getPcscExceptionDetail(String sCode)
    {
        if(sCode.equals("SCARD_S_SUCCESS")) 
            return "Smart card operation succeeded.";

        if(sCode.equals("SCARD_F_INTERNAL_ERROR")) 
            return "An internal consistency check failed.";

        if(sCode.equals("SCARD_E_CANCELLED")) 
            return "The action was cancelled by a SCardCancel request.";

        if(sCode.equals("SCARD_E_INVALID_HANDLE"))
            return "The supplied handle was invalid.";

        if(sCode.equals("SCARD_E_INVALID_PARAMETER"))
            return "One or more of the supplied parameters could not be properly"
                    + " interpreted.";

        if(sCode.equals("SCARD_E_INVALID_TARGET"))
            return "Registry startup information is missing or invalid.";

        if(sCode.equals("SCARD_E_NO_MEMORY"))
            return "Not enough memory available to complete this command.";

        if(sCode.equals("SCARD_F_WAITED_TOO_LONG"))
            return "An internal consistency timer has expired.";

        if(sCode.equals("SCARD_E_INSUFFICIENT_BUFFER"))
            return "The data buffer to receive returned data is too small for "
                    + "the returned data.";

        if(sCode.equals("SCARD_E_UNKNOWN_READER"))
            return "The specified reader name is not recognized.";

        if(sCode.equals("SCARD_E_TIMEOUT"))
            return "The user-specified timeout value has expired.";

        if(sCode.equals("SCARD_E_SHARING_VIOLATION"))
            return "The smart card cannot be accessed because of other "
                    + "connections outstanding.";

        if(sCode.equals("SCARD_E_NO_SMARTCARD"))
            return "The operation requires a smart card, but no smart card is "
                    + "currently in the device.";

        if(sCode.equals("SCARD_E_UNKNOWN_CARD"))
            return "The specified smart card name is not recognized.";

        if(sCode.equals("SCARD_E_CANT_DISPOSE"))
            return "The system could not dispose of the media in the requested "
                    + "manner.";

        if(sCode.equals("SCARD_E_PROTO_MISMATCH"))
            return "The requested protocols are incompatible with the protocol "
                    + "currently in use with the smart card.";

        if(sCode.equals("SCARD_E_NOT_READY"))
            return "The reader or smart card is not ready to accept commands.";

        if(sCode.equals("SCARD_E_INVALID_VALUE"))
            return "One or more of the supplied parameters values could not "
                    + "be properly interpreted.";

        if(sCode.equals("SCARD_E_SYSTEM_CANCELLED"))
            return "The action was cancelled by the system, presumably to log "
                    + "off or shut down.";

        if(sCode.equals("SCARD_F_COMM_ERROR"))
            return "An internal communications error has been detected.";

        if(sCode.equals("SCARD_F_UNKNOWN_ERROR"))
            return "An internal error has been detected, but the source is "
                    + "unknown.";

        if(sCode.equals("SCARD_E_INVALID_ATR"))
            return "An ATR obtained from the registry is not a valid ATR string.";

        if(sCode.equals("SCARD_E_NOT_TRANSACTED"))
            return "An attempt was made to end a non-existent transaction.";

        if(sCode.equals("SCARD_E_READER_UNAVAILABLE"))
            return "The specified reader is not currently available for use.";

        if(sCode.equals("SCARD_P_SHUTDOWN"))
            return "The operation has been aborted to allow the server "
                    + "application to exit.";

        if(sCode.equals("SCARD_E_PCI_TOO_SMALL"))
            return "The PCI Receive buffer was too small.";

        if(sCode.equals("SCARD_E_READER_UNSUPPORTED"))
            return "The reader driver does not meet minimal requirements for "
                    + "support.";

        if(sCode.equals("SCARD_E_DUPLICATE_READER"))
            return "The reader driver did not produce a unique reader name.";

        if(sCode.equals("SCARD_E_CARD_UNSUPPORTED"))
            return "The smart card does not meet minimal requirements for "
                    + "support.";

        if(sCode.equals("SCARD_E_NO_SERVICE"))
            return "The Smart Card Resource Manager is not running.";

        if(sCode.equals("SCARD_E_SERVICE_STOPPED"))
            return "The Smart Card Resource Manager has shut down.";

        if(sCode.equals("SCARD_E_UNEXPECTED")) 
            if(System.getProperty("os.name").contains("Windows"))
                return "An unexpected card error has occurred.";
            else
                return "This smart card does not support the requested feature.";

        if(sCode.equals("SCARD_E_ICC_INSTALLATION"))
            return "No primary provider can be found for the smart card.";

        if(sCode.equals("SCARD_E_ICC_CREATEORDER"))
            return "The requested order of object creation is not supported.";

        if(sCode.equals("SCARD_E_UNSUPPORTED_FEATURE"))
            return "This smart card does not support the requested feature.";

        if(sCode.equals("SCARD_E_DIR_NOT_FOUND"))
            return "The identified directory does not exist in the smart card.";

        if(sCode.equals("SCARD_E_FILE_NOT_FOUND"))
            return "The identified file does not exist in the smart card.";

        if(sCode.equals("SCARD_E_NO_DIR"))
            return "The supplied path does not represent a smart card directory.";

        if(sCode.equals("SCARD_E_NO_FILE"))
            return "The supplied path does not represent a smart card file.";

        if(sCode.equals("SCARD_E_NO_ACCESS"))
            return "Access is denied to this file.";

        if(sCode.equals("SCARD_E_WRITE_TOO_MANY"))
            return "The smart card does not have enough memory to store the "
                    + "information.";

        if(sCode.equals("SCARD_E_BAD_SEEK"))
            return "There was an error trying to set the smart card file "
                    + "object pointer.";

        if(sCode.equals("SCARD_E_INVALID_CHV"))
            return "The supplied PIN is incorrect.";

        if(sCode.equals("SCARD_E_UNKNOWN_RES_MNG"))
            return "An unrecognized error code was returned from a layered "
                    + "component.";

        if(sCode.equals("SCARD_E_NO_SUCH_CERTIFICATE"))
            return "The requested certificate does not exist.";

        if(sCode.equals("SCARD_E_CERTIFICATE_UNAVAILABLE"))
            return "The requested certificate could not be obtained.";

        if(sCode.equals("SCARD_E_NO_READERS_AVAILABLE"))
            return "Cannot find a smart card reader.";

        if(sCode.equals("SCARD_E_COMM_DATA_LOST"))
            return "A communications error with the smart card has been "
                    + "detected. Retry the operation.";

        if(sCode.equals("SCARD_E_NO_KEY_CONTAINER"))
            return "The requested key container does not exist on the smart "
                    + "card.";

        if(sCode.equals("SCARD_E_SERVER_TOO_BUSY"))
            return "The Smart Card Resource Manager is too busy to complete "
                    + "this operation.";

        if(sCode.equals("SCARD_W_UNSUPPORTED_CARD"))
            return "The reader cannot communicate with the card, due to ATR "
                    + "string configuration conflicts.";

        if(sCode.equals("SCARD_W_UNRESPONSIVE_CARD"))
            return "The smart card is not responding to a reset.";

        if(sCode.equals("SCARD_W_UNPOWERED_CARD"))
            return "Power has been removed from the smart card, so that further "
                    + "communication is not possible.";

        if(sCode.equals("SCARD_W_RESET_CARD"))
            return "The smart card has been reset, so any shared state "
                    + "information is invalid.";

        if(sCode.equals("SCARD_W_REMOVED_CARD"))
            return "The smart card has been removed, so further communication "
                    + "is not possible.";

        if(sCode.equals("SCARD_W_SECURITY_VIOLATION"))
            return "Access was denied because of a security violation.";

        if(sCode.equals("SCARD_W_WRONG_CHV"))
            return "The card cannot be accessed because the wrong PIN was "
                    + "presented.";

        if(sCode.equals("SCARD_W_CHV_BLOCKED"))
            return "The card cannot be accessed because the maximum number of "
                    + "PIN entry attempts has been reached.";

        if(sCode.equals("SCARD_W_EOF"))
            return "The end of the smart card file has been reached.";

        if(sCode.equals("SCARD_W_CANCELLED_BY_USER"))
            return "The action was cancelled by the user.";

        if(sCode.equals("SCARD_W_CARD_NOT_AUTHENTICATED"))
            return "No PIN was presented to the smart card.";

        if(sCode.equals("WINDOWS_ERROR_INVALID_HANDLE")) 
            return "Windows error: invalid handle value.";

        if(sCode.equals("WINDOWS_ERROR_INVALID_PARAMETER")) 
            return "Windows error: invalid parameter value.";

        if(sCode.equals("ERROR_IO_DEVICE"))
            return "An input or output communication to the device failed.";

        return "An internal error has been detected, but the source is "
                    + "unknown.";
    }
    
    /**
     * Returns the value of a PCSC exception from a PCSC string code.
     * @param sCode the PCSC string code.
     * @return the value of the PCSC exception defined by the PCSC string code.
     */
    public static int getPcscExceptionValue(String sCode)
    {
        if(sCode.equals("SCARD_S_SUCCESS")) 
            return SCARD_S_SUCCESS;

        if(sCode.equals("SCARD_F_INTERNAL_ERROR")) 
            return SCARD_F_INTERNAL_ERROR;

        if(sCode.equals("SCARD_E_CANCELLED")) 
            return SCARD_E_CANCELLED;

        if(sCode.equals("SCARD_E_INVALID_HANDLE"))
            return SCARD_E_INVALID_HANDLE;

        if(sCode.equals("SCARD_E_INVALID_PARAMETER"))
            return SCARD_E_INVALID_PARAMETER;

        if(sCode.equals("SCARD_E_INVALID_TARGET"))
            return SCARD_E_INVALID_TARGET;

        if(sCode.equals("SCARD_E_NO_MEMORY"))
            return SCARD_E_NO_MEMORY;

        if(sCode.equals("SCARD_F_WAITED_TOO_LONG"))
            return SCARD_F_WAITED_TOO_LONG;

        if(sCode.equals("SCARD_E_INSUFFICIENT_BUFFER"))
            return SCARD_E_INSUFFICIENT_BUFFER;

        if(sCode.equals("SCARD_E_UNKNOWN_READER"))
            return SCARD_E_UNKNOWN_READER;

        if(sCode.equals("SCARD_E_TIMEOUT"))
            return SCARD_E_TIMEOUT;

        if(sCode.equals("SCARD_E_SHARING_VIOLATION"))
            return SCARD_E_SHARING_VIOLATION;

        if(sCode.equals("SCARD_E_NO_SMARTCARD"))
            return SCARD_E_NO_SMARTCARD;

        if(sCode.equals("SCARD_E_UNKNOWN_CARD"))
            return SCARD_E_UNKNOWN_CARD;

        if(sCode.equals("SCARD_E_CANT_DISPOSE"))
            return SCARD_E_CANT_DISPOSE;

        if(sCode.equals("SCARD_E_PROTO_MISMATCH"))
            return SCARD_E_PROTO_MISMATCH;

        if(sCode.equals("SCARD_E_NOT_READY"))
            return SCARD_E_NOT_READY;

        if(sCode.equals("SCARD_E_INVALID_VALUE"))
            return SCARD_E_INVALID_VALUE;

        if(sCode.equals("SCARD_E_SYSTEM_CANCELLED"))
            return SCARD_E_SYSTEM_CANCELLED;

        if(sCode.equals("SCARD_F_COMM_ERROR"))
            return SCARD_F_COMM_ERROR;

        if(sCode.equals("SCARD_F_UNKNOWN_ERROR"))
            return SCARD_F_UNKNOWN_ERROR;

        if(sCode.equals("SCARD_E_INVALID_ATR"))
            return SCARD_E_INVALID_ATR;

        if(sCode.equals("SCARD_E_NOT_TRANSACTED"))
            return SCARD_E_NOT_TRANSACTED;

        if(sCode.equals("SCARD_E_READER_UNAVAILABLE"))
            return SCARD_E_READER_UNAVAILABLE;

        if(sCode.equals("SCARD_P_SHUTDOWN"))
            return SCARD_P_SHUTDOWN;

        if(sCode.equals("SCARD_E_PCI_TOO_SMALL"))
            return SCARD_E_PCI_TOO_SMALL;

        if(sCode.equals("SCARD_E_READER_UNSUPPORTED"))
            return SCARD_E_READER_UNSUPPORTED;

        if(sCode.equals("SCARD_E_DUPLICATE_READER"))
            return SCARD_E_DUPLICATE_READER;

        if(sCode.equals("SCARD_E_CARD_UNSUPPORTED"))
            return SCARD_E_CARD_UNSUPPORTED;

        if(sCode.equals("SCARD_E_NO_SERVICE"))
            return SCARD_E_NO_SERVICE;

        if(sCode.equals("SCARD_E_SERVICE_STOPPED"))
            return SCARD_E_SERVICE_STOPPED;

        if(sCode.equals("SCARD_E_UNEXPECTED")) 
            return SCARD_E_UNEXPECTED;
            
        if(sCode.equals("SCARD_E_ICC_INSTALLATION"))
            return SCARD_E_ICC_INSTALLATION;

        if(sCode.equals("SCARD_E_ICC_CREATEORDER"))
            return SCARD_E_ICC_CREATEORDER;

        if(sCode.equals("SCARD_E_UNSUPPORTED_FEATURE"))
            return SCARD_E_UNSUPPORTED_FEATURE;

        if(sCode.equals("SCARD_E_DIR_NOT_FOUND"))
            return SCARD_E_DIR_NOT_FOUND;

        if(sCode.equals("SCARD_E_FILE_NOT_FOUND"))
            return SCARD_E_FILE_NOT_FOUND;

        if(sCode.equals("SCARD_E_NO_DIR"))
            return SCARD_E_NO_DIR;

        if(sCode.equals("SCARD_E_NO_FILE"))
            return SCARD_E_NO_FILE;

        if(sCode.equals("SCARD_E_NO_ACCESS"))
            return SCARD_E_NO_ACCESS;

        if(sCode.equals("SCARD_E_WRITE_TOO_MANY"))
            return SCARD_E_WRITE_TOO_MANY;

        if(sCode.equals("SCARD_E_BAD_SEEK"))
            return SCARD_E_BAD_SEEK;

        if(sCode.equals("SCARD_E_INVALID_CHV"))
            return SCARD_E_INVALID_CHV;

        if(sCode.equals("SCARD_E_UNKNOWN_RES_MNG"))
            return SCARD_E_UNKNOWN_RES_MNG;

        if(sCode.equals("SCARD_E_NO_SUCH_CERTIFICATE"))
            return SCARD_E_NO_SUCH_CERTIFICATE;

        if(sCode.equals("SCARD_E_CERTIFICATE_UNAVAILABLE"))
            return SCARD_E_CERTIFICATE_UNAVAILABLE;

        if(sCode.equals("SCARD_E_NO_READERS_AVAILABLE"))
            return SCARD_E_NO_READERS_AVAILABLE;

        if(sCode.equals("SCARD_E_COMM_DATA_LOST"))
            return SCARD_E_COMM_DATA_LOST;

        if(sCode.equals("SCARD_E_NO_KEY_CONTAINER"))
            return SCARD_E_NO_KEY_CONTAINER;

        if(sCode.equals("SCARD_E_SERVER_TOO_BUSY"))
            return SCARD_E_SERVER_TOO_BUSY;

        if(sCode.equals("SCARD_W_UNSUPPORTED_CARD"))
            return SCARD_W_UNSUPPORTED_CARD;

        if(sCode.equals("SCARD_W_UNRESPONSIVE_CARD"))
            return SCARD_W_UNRESPONSIVE_CARD;

        if(sCode.equals("SCARD_W_UNPOWERED_CARD"))
            return SCARD_W_UNPOWERED_CARD;

        if(sCode.equals("SCARD_W_RESET_CARD"))
            return SCARD_W_RESET_CARD;

        if(sCode.equals("SCARD_W_REMOVED_CARD"))
            return SCARD_W_REMOVED_CARD;

        if(sCode.equals("SCARD_W_SECURITY_VIOLATION"))
            return SCARD_W_SECURITY_VIOLATION;

        if(sCode.equals("SCARD_W_WRONG_CHV"))
            return SCARD_W_WRONG_CHV;

        if(sCode.equals("SCARD_W_CHV_BLOCKED"))
            return SCARD_W_CHV_BLOCKED;

        if(sCode.equals("SCARD_W_EOF"))
            return SCARD_W_EOF;

        if(sCode.equals("SCARD_W_CANCELLED_BY_USER"))
            return SCARD_W_CANCELLED_BY_USER;

        if(sCode.equals("SCARD_W_CARD_NOT_AUTHENTICATED"))
            return SCARD_W_CARD_NOT_AUTHENTICATED;

        if(sCode.equals("WINDOWS_ERROR_INVALID_HANDLE")) 
            return WINDOWS_ERROR_INVALID_HANDLE;

        if(sCode.equals("WINDOWS_ERROR_INVALID_PARAMETER")) 
            return WINDOWS_ERROR_INVALID_PARAMETER;

        if(sCode.equals("ERROR_IO_DEVICE"))
            return ERROR_IO_DEVICE;

        return SCARD_F_UNKNOWN_ERROR;
    }
}
