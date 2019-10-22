package com.mindbox.pe.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PE_InputContextValidator {
    /* ---------------------------------------
     *         Regex Patterns 
     * ---------------------------------------
     */

    private static final String postDeployScriptPatternCnst = "^([a-z]:)?[a-z0-9/_-]+(\\.(bat|sh))$";


    //    private static final String portPatternCnst = "^[0-9]{4}$";
    //
    //    private static final String hostPatternCnst = "^localhost$";
    //
    //    private static final String logPathPatternCnst = "^([a-zA-Z]:)?/(MindBox|opt|projects)/(MindBox|mbx|MBXprojects)/[/a-zA-Z0-9_-]+/log/[0-9]{4}/$";
    //
    //    private static final String loadFilePathAndNamePatternCnst = "^([a-zA-Z]:)?/(MindBox|opt|projects)/(mbx/|mindbox/)?(MBXprojects)/[/_a-zA-Z0-9-]+(/Engine/ae-server/)[/_a-zA-Z0-9-]+/MBX-Server\\.(bat|sh)$";
    //
    //    private static final String loadFileArgsPatternCnst = "^localhost [0-9]{4}( [0-9]{4})?$";


    //==========================================================================================================


    public static boolean isValidLoadFileExecuteArgs(String loadFileExecuteArgs) {
        if (isEmptyAfterTrim(loadFileExecuteArgs)) {
            return true;
        }
        else {
            return false;
        }
    }

    //==========================================================================================================
    private static final String perlScriptNamePatternCnst = "^([a-zA-Z0-9-_])+([\\.](pl|pm))$";

    private static final String engineResponseFileNamePattern = "^([a-zA-Z0-9-_])+(_EngineResponse\\.xml)$";

    // private static final String xsltFileNamePattern = "^[a-zA-Z0-9-_]+\\.xsl$";

    private static final String batchLinkedReportFilePathPattern = "^[0-9]+_[a-zA-Z0-9-_]+[_]%s\\.(pdf|xls|xml)$";

    private static final String outputFileNamePattern = "^(([a-zA-Z0-9-_]+)|([0-9]+_[a-zA-Z0-9-_]+))[_]%s\\.(xls|pdf|xml)$";

    // private static final String perlReportsLogFilePathPattern = "^%s-perl-report\\.log$";

    private static final String simpleFileNamePattern = "^[a-zA-Z0-9-_]+\\.[a-zA-Z0-9-_]+$";

    private static final String baseUpdateQueryWhereClausePattern = "^(status = 'ONHOLD'|requestQueueID = [0-9]+|(processingID=)([0-9]+)|(processingID=)([0-9]+)( AND )(requestID=')([a-zA-Z0-9_-]+)('))$";

    //  private static final String[] batchInputFilePatterns = {
    //          "^([a-zA-Z0-9_-]+)-master-((0[1-9]|1[012])-([0-2][0-9]|3[01])-(20[1-9][0-9]))-(([01][0-9]|2[0-3])-[0-5][0-9]-[0-5][0-9]\\.(xls|zip))$",
    //          "^([a-zA-Z0-9_-]+)_BatchInput\\.(xls|zip)$" };

    //Action Results Patterns


    private static final String[] actionResultMessagePatterns = {
            "^Successfully added customer \\'([a-zA-Z0-9-, \\.&]{1,50})\\' with login id \\'([a-zA-Z0-9-, \\.&]{1,50})\\'\\.$",
            "^Successfully updated customer \\'([a-zA-Z0-9-, \\.&]{1,50})\\'\\.$",
            "^Successfully cloned customer \\'([a-zA-Z0-9-, \\.&]{1,50})\\' to produce new customer id \\'([a-zA-Z0-9-, \\.&]{1,50})\\'\\.$",
            "^Successfully deleted the customer \\'([a-zA-Z0-9-, \\.&]{1,50})\\'\\.$" };


    // test class names pattern
    private static final String testClassNamesPattern = "^(str-[0-9]+)$";

    /* ---------------------------------------
     *         Black Lists 
     * ---------------------------------------
     */

    private static final String[] unallowedFilePathNameCharsBlackList = { "|", "#", "//", "@", " ", "*", "%", "$", "--", "(", ")", "{", "}", "+", ";" };

    /* ---------------------------------------
     *         White Lists 
     * ---------------------------------------
     */

    private static final String[] jasperRptFilesWhitList = { "" };

    private static final String[] perlScriptFilesWhiteList = { "" };

    private static final String[] inputPatternsWhiteList = { "" };

    private static final String[] reportNamesWhiteList = { "" };

    private static final String[] fileExtensionsWhiteList = { "" };

    private static final String[] rptRespTypeWhiteList = { "" };

    private static final String[] actionHandlersTargetUrlsWhiteList = { "" };

    private static final String[] reflectedClassNamesWhiteList = { "" };

    private static List<PE_InputContextWhiteListSet> classNameWhiteList = new ArrayList<PE_InputContextWhiteListSet>();

    private static void setClassNameWhiteList() {
        classNameWhiteList.clear();
        classNameWhiteList.add(new PE_InputContextWhiteListSet("", ""));
        classNameWhiteList.add(new PE_InputContextWhiteListSet("", ""));
        classNameWhiteList.add(new PE_InputContextWhiteListSet("", ""));
    }

    //  private static final String[] rptXformFilesWhiteList = {};
    /* ---------------------------------------
     *         Validation Methods 
     * ---------------------------------------
     */

    /**
     * check that filename and extention conform to expected format
     * @param fileNameWithExt - filename to check
     * @param checkIfSimpleFileName - check if proper format beyond being empty/blank
     * @return boolean
     */
    public static boolean checkFileNameWithExt(String fileNameWithExt, boolean checkIfSimpleFileName) {
        if (isEmptyAfterTrim(fileNameWithExt)) {
            return false;
        }
        if (checkIfSimpleFileName) {
            if (!checkStrMatch(simpleFileNamePattern, fileNameWithExt)) {
                return false;
            }
        }
        return true;
    }

    /**
     *  
     * @param strToChk
     * @return boolean
     */
    public static boolean checkForInvalidCharsInFileName(String strToChk) {
        return containsUnallowedFileNameChars(strToChk);
    }

    /**
     * Check that a method is in the calling stack trace
     * @param validCallerName - name of method to search for within StackTraceElement[]
     * @return
     */
    public static boolean checkForValidCaller(String validCallerName) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean validCallerFound = false;
        for (int i = 0; i < stackTraceElements.length; i++) {
            if (stackTraceElements[i].getMethodName().equals(validCallerName)) {
                validCallerFound = true;
                break;
            }
        }
        return validCallerFound;
    }

    /**
     * Check that a method was called by a valid method in the stack trace up to a given depth within the stack
     * @param validCallerName - name of method to search for within StackTraceElement[]
     * @param callerDepthLimit - StackTraceElement[] index value after which to stop looking (inclusive of this index value)
     * @return
     */
    public static boolean checkForValidCaller(String validCallerName, int callerDepthLimit) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean validCallerFound = false;
        for (int i = 0; i < stackTraceElements.length; i++) {
            if (i > callerDepthLimit) {
                break;
            }
            if (stackTraceElements[i].getMethodName().equals(validCallerName)) {
                validCallerFound = true;
                break;
            }
        }
        return validCallerFound;
    }

    /**
     * check for match on regexPattern (always case insensitive)
     * @param patternString
     * @param matcherString
     * @return boolean
     */
    private static boolean checkStrMatch(String patternString, String matcherString) {
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(matcherString);
        return matcher.find();
    }

    /**
     * check for match on regexPattern (case sensitivity is optional)
     * 
     * @param patternString - regex pattern to use
     * @param matcherString - string to match
     * @param caseSensitive - boolean - true or false
     * @return
     */
    public static boolean checkStrMatch(String patternString, String matcherString, boolean caseSensitive) {
        Pattern pattern = null;
        if (caseSensitive) {
            pattern = Pattern.compile(patternString);
        }
        else {
            pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        }
        Matcher matcher = pattern.matcher(matcherString);
        return matcher.find();
    }

    /**
     * Check for match using string array of regexPatterns (calling matcher logic using case insensitivity)
     * 
     * @param patternStrings
     * @param matcherString
     * @return
     */
    public static boolean checkStrMatches(String[] patternStrings, String matcherString) {
        for (String patternString : patternStrings) {
            if (checkStrMatch(patternString, matcherString)) {
                return true;
            }
        }
        return false;
    }

    /**
     *   
     * @param whiteList
     * @param strToValidate
     * @return boolean
     */
    public static boolean checkWhiteList(String[] whiteList, String strToValidate) {
        return checkWhiteLists(whiteList, strToValidate);
    }

    /**
     * Verify that the string value passed is as expected
     * 
     * @param whiteList - array of valid string values 
     * @param strToValidate - string value to evaluate
     * @return boolean
     */
    private static boolean checkWhiteLists(String[] whiteList, String strToValidate) {
        if (whiteList != null && whiteList.length != 0) {
            for (String nxtWhiteListElem : whiteList) {
                if (nxtWhiteListElem.equals(strToValidate)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check that path string is valid
     * @param pathStr
     * @return boolean
     */
    public static final boolean chkPathStr(String pathStr) {
        String patternString = "/mindbox/artadvisor.*/config";
        return checkStrMatch(patternString, pathStr.replaceAll("\\\\", "/"));
    }

    /**
     * 
     * @param strToChk
     * @return
     */
    private static boolean containsUnallowedFileNameChars(String strToChk) {
        for (String nxtInvalidChr : unallowedFilePathNameCharsBlackList) {
            if (strToChk.indexOf(nxtInvalidChr) != -1) {
                // invalid char found
                return true;
            }
        }
        // only contains expected valid chars 
        return false;
    }

    /**
     * Check that the string passing is only alphanumeric characters A-Z in upper/lower or 0-9
     * @param alphaNumStr
     * @return boolean 
     */
    public static final boolean isAlphaNumericCharsOnly(String alphaNumStr) {
        if (isEmptyAfterTrim(alphaNumStr)) {
            return false;
        }
        String regexPattern = "^[a-zA-Z0-9]+$";
        return checkStrMatch(regexPattern, alphaNumStr);
    }

    /**
     * Check that the string passing is only alphanumeric characters A-Z in upper/lower or 0-9 with possible spacing
     * @param alphaNumStr
     * @return boolean 
     */
    public static final boolean isAlphaNumericCharsWithSpacing(String alphaNumStr) {
        if (isEmptyAfterTrim(alphaNumStr)) {
            return false;
        }
        String regexPattern = "^[ a-zA-Z0-9]+$";
        return checkStrMatch(regexPattern, alphaNumStr);
    }

    /**
     * Checks if a string has any characters other than spaces or is null or empty  
     * @param str - String var to check
     * @return boolean - true if str is null, empty or blank else returns false
     */
    public static final boolean isEmptyAfterTrim(String str) {
        return str == null || str.trim().length() == 0 || str.isEmpty();
    }

    /**
     * Check that the string passing is only alpha characters A-Z in upper or lower or mixed
     * @param alphaStr
     * @return boolean
     */
    public static final boolean isOnlyAlphaChars(String alphaStr) {
        if (isEmptyAfterTrim(alphaStr)) {
            return false;
        }
        String regexPattern = "^[a-zA-Z]+$";
        return checkStrMatch(regexPattern, alphaStr);
    }

    /**
     * Will check if a string is a valid numberic value
     * 
     * @param strNum
     * @return boolean
     */
    public static boolean isStringNumeric(String strNum) {
        if (isEmptyAfterTrim(strNum)) {
            return false;
        }
        String regexPattern = "^[\\+\\-]?\\d*\\.?\\d+(?:[Ee][\\+\\-]?\\d+)?$";
        return checkStrMatch(regexPattern, strNum);
    }

    /**
     * Check that a string only contains numbers
     * @param strNum String composed of only numbers 0 - 9
     * @return boolean
     */
    public static boolean isStringOnlyNumbers(String strNum) {
        if (isEmptyAfterTrim(strNum)) {
            return false;
        }
        String regexPattern = "^\\d*?$";
        return checkStrMatch(regexPattern, strNum);
    }

    /**
     * Check that the string passing is only alphanumeric characters A-Z in upper/lower or 0-9 with possible Dashes and/or Underscores
     * @param batchID
     * @return boolean
     */
    public static final boolean isValidBatchID(String batchID) {
        String regexPattern = "^[a-zA-Z0-9_-]+$";
        return checkStrMatch(regexPattern, batchID);
    }

    public static final boolean isValidPostDeployScriptFileName(String postDeployScriptFileName) {
        postDeployScriptFileName = postDeployScriptFileName.replaceAll("\\\\", "/");
        // check if the file name conforms
        if (checkStrMatch(postDeployScriptPatternCnst, postDeployScriptFileName)) {
            return true;
        }
        return false;
    }

    /**
     * Validate that the engine response file name contains expected format
     * 
     * @param engRespFileName - name of engine response file
     * @return boolean
     */
    public static boolean isValidEngineRespFileName(String engRespFileName) {
        if (containsUnallowedFileNameChars(engRespFileName)) {
            return false;
        }
        return checkStrMatch(engineResponseFileNamePattern, engRespFileName);
    }

    /**
     * Check for valid response/PID/graphname_Graphs.gif fully qualified file name
     * @param grphsImgFilename
     * @return boolean
     */
    public static final boolean isValidGrphsImgFile(String grphsImgFilename) {
        String regexPattern = "response/PID-[0-9]*/.*_Graphs.gif$";
        return checkStrMatch(regexPattern, grphsImgFilename.replaceAll("\\\\", "/"));
    }

    /**
     * Check for valid response/PID/graphname_Graphs.html fully qualified file name
     * @param htmlGrphsUrl
     * @return boolean
     */
    public static final boolean isValidHtmlGrphsUrl(String htmlGrphsUrl) {
        String regexPattern = "response/PID-[0-9]*/.*_Graphs.html$";
        return checkStrMatch(regexPattern, htmlGrphsUrl.replaceAll("\\\\", "/"));
    }

    /**
     * Check that the string passed is a valid combination of BatchIID + "_" + ProcessingID
     * @param batchID_ProcessingID
     * @return boolean
     */
    public static final boolean isValidOutputFileId(String batchID_ProcessingID) {
        String regexPattern = "^[a-zA-Z0-9_-]+[0-9]+$";
        return checkStrMatch(regexPattern, batchID_ProcessingID);
    }

    /**
     * Check that output file name is valid
     * 
     * @param outputFileName - file name to check
     * @return boolean
     */
    public static final boolean isValidOutputFileName(String outputFileName) {
        for (String reportName : reportNamesWhiteList) {
            String validOutputFileNamePattern = String.format(outputFileNamePattern, reportName);
            String validOutputFileNameNoBatchPattern = String.format(outputFileNamePattern, reportName.replaceAll("Batch", ""));
            // check if the output file name conforms
            if (checkStrMatch(validOutputFileNamePattern, outputFileName) || checkStrMatch(validOutputFileNameNoBatchPattern, outputFileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that perl script name is valid and is in an expected root path
     * 
     * @param perlScriptFile - name of perl script which must include the pathing
     * @return boolean
     */
    public static boolean isValidPerlScriptName(String perlScriptFile) {
        String perlScriptFileName = perlScriptFile.replaceAll("\\\\", "/");
        if (perlScriptFileName.indexOf(" ") != -1) {
            // no spaces allowed
            return false;
        }
        if (perlScriptFileName.indexOf("/") != -1) {
            //          if (!checkRootPath(perlScriptFileName)) {
            //              //not in expected root path
            //              return false;
            //          }
            perlScriptFileName = getFileNameWithExt(perlScriptFileName);
        }
        // is it properly formatted perl script file name
        if (checkStrMatch(perlScriptNamePatternCnst, perlScriptFileName)) {
            // is it an expected script file name?
            return checkWhiteList(perlScriptFilesWhiteList, perlScriptFileName);
        }
        return false;
    }

    /**
     * Check that the string passing is only alphanumeric characters A-Z in upper/lower or 0-9 with possible Dashes and/or Underscores
     * @param requestID
     * @return boolean
     */
    public static final boolean isValidRequestID(String requestID) {
        String regexPattern = "^[a-zA-Z0-9_-]+$";
        return checkStrMatch(regexPattern, requestID);
    }

    /**
     * check that script file name is valid
     * @param scriptFile
     * @return
     */
    public static final boolean isValidScriptFile(String scriptFile) {
        if (isEmptyAfterTrim(scriptFile)) {
            return false;
        }
        String regexPattern = "^[ a-zA-Z0-9]+$";
        return checkStrMatch(regexPattern, scriptFile);
    }

    /**
     * check that the test class name is valid
     * @param testClassName
     * @return
     */
    public static final boolean isValidTestClassNamesPattern(String testClassName) {
        if (isEmptyAfterTrim(testClassName)) {
            return false;
        }
        return checkStrMatch(testClassNamesPattern, testClassName, true);
    }

    /**
     * check that the where clause for the update query is valid
     * @param updateWhereClauseStr
     * @return
     */
    public static final boolean isValidUpdateWhereClause(String updateWhereClauseStr) {
        if (isEmptyAfterTrim(updateWhereClauseStr)) {
            return false;
        }
        return checkStrMatch(baseUpdateQueryWhereClausePattern, updateWhereClauseStr);
    }

    /**
     * Return a blank string if the string is empty or null
     * @param strToChk
     * @return String
     */
    public static String nullEmptyStrToBlank(String strToChk) {
        if (isEmptyAfterTrim(strToChk)) {
            return "";
        }
        return strToChk;
    }

    /* ---------------------------------------
     *         Getter Methods 
     * ---------------------------------------
     */

    public static String[] getActionresultmessagepatterns() {
        return actionResultMessagePatterns;
    }

    /**
     * @return the fileextensionswhitelist
     */
    public static String[] getFileextensionswhitelist() {
        return fileExtensionsWhiteList;
    }


    /**
     * returns just the filename.ext portion of the fully pathed file name
     * @param fullFileName - string containing the fully pathed file name
     * @return String - filename.ext
     */
    public static String getFileNameWithExt(String fullFileName) {
        String fileNameWithExt = "";
        fileNameWithExt = fullFileName.replaceAll("\\\\", "/");
        if (fileNameWithExt.indexOf("/") != -1) {
            int lastInx = fileNameWithExt.lastIndexOf("/");
            fileNameWithExt = fileNameWithExt.substring(++lastInx);
        }
        return fileNameWithExt;
    }

    /** 
     * @return the actionHandlersTargetUrlsWhiteList
     */
    public static String[] getActionHandlersTargetUrlWhitelist() {
        return actionHandlersTargetUrlsWhiteList;
    }

    /**
     * @return the inputpatternswhitelist
     */
    public static String[] getInputpatternswhitelist() {
        return inputPatternsWhiteList;
    }

    /**
     * @return the jasperRptFilesWhitList
     */
    public static String[] getJasperRptFilesWhitList() {
        return jasperRptFilesWhitList;
    }

    /**
     * @return the perlscriptfileswhitelist
     */
    public static String[] getPerlscriptfileswhitelist() {
        return perlScriptFilesWhiteList;
    }

    /**
     * 
     * @return the reflectedClassNamesWhiteList
     */
    public static String[] getReflectedclassnameswhitelist() {
        return reflectedClassNamesWhiteList;
    }

    /**
     * @return the reportnameswhitelist
     */
    public static String[] getReportnameswhitelist() {
        return reportNamesWhiteList;
    }

    /**
     * @return rptRespTypeWhiteList
     */
    public static String[] getRptRespTypeWhiteList() {
        return rptRespTypeWhiteList;
    }

    /**
     * Verify that string value passed is found in whitelist and return whitelist value 
     * 
     * @param whiteList - array of valid string values
     * @param strToValidate - string value to evaluate
     * @return String - valid matched string from array
     */
    public static String getValidWhitelistValue(String[] whiteList, String strToValidate) {
        String emptyStr = "";
        if (whiteList != null && whiteList.length != 0) {
            for (String nxtWhiteListElem : whiteList) {
                if (nxtWhiteListElem.equals(strToValidate)) {
                    return nxtWhiteListElem;
                }
            }
        }
        // return empty String if no match found or invalid whitelist passed
        return emptyStr;
    }

    /**
     * Verify that string passed is found in classnamewhitelist key and return classnamewhitelist value 
     * 
     * @param whiteList - array of valid string values
     * @param strClassNameToValidate - string value to evaluate
     * @return String - valid matched string from array
     */
    public static String getValidClassNameWhitelistValue(String strClassNameToValidate) {
        setClassNameWhiteList();
        String emptyStr = "";
        if (!isEmptyAfterTrim(strClassNameToValidate)) {
            for (PE_InputContextWhiteListSet nxtWhiteListElem : classNameWhiteList) {
                if (nxtWhiteListElem.getWhiteListKey().equalsIgnoreCase(strClassNameToValidate)) {
                    return nxtWhiteListElem.getWhiteListValue();
                }
            }
        }
        // return empty String if no match found or invalid class name key passed
        return emptyStr;
    }
}
